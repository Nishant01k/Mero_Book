package com.example.merobook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.merobook.MyApplication;
import com.example.merobook.R;
import com.example.merobook.adpters.AdapterPdfFavortie;
import com.example.merobook.databinding.ActivityGoogleProfileBinding;
import com.example.merobook.databinding.ActivityProfileBinding;
import com.example.merobook.models.ModelPdf;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GoogleProfile extends AppCompatActivity {

    private ActivityGoogleProfileBinding binding;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelPdf> pdfArrayList;

    private AdapterPdfFavortie adapterPdfFavortie;

    private static final String TAG = "PROFILE_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoogleProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();
        loadFavortieBooks();

        binding.profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Create the object of
                // AlertDialog Builder class
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(GoogleProfile.this);


                // Set Alert Title
                builder.setTitle("Do you want Log Out ?");

                // Set Cancelable false
                // for when the user clicks on the outside
                // the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name
                // OnClickListener method is use of
                // DialogInterface interface.

                builder
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface
                                        .OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {
                                        startActivity(new Intent(GoogleProfile.this ,MainActivity.class));
                                    }
                                });

                // Set the Negative button with No name
                // OnClickListener method is use
                // of DialogInterface interface.
                builder
                        .setNegativeButton(
                                "No",
                                new DialogInterface
                                        .OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {

                                        // If user click no
                                        // then dialog box is canceled.
                                        dialog.cancel();
                                    }
                                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();

                // Show the Alert Dialog box
                alertDialog.show();

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void loadUserInfo(){

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            binding.emailTv.setText(signInAccount.getEmail());
            binding.nameTv.setText(signInAccount.getDisplayName());
            binding.memberDateTV.setText(signInAccount.getId());
            binding.accountTypeTv.setText("User");
            Glide.with(GoogleProfile.this)
                    .load(signInAccount.getPhotoUrl())
                    .placeholder(R.drawable.ic_baseline_person_white)
                    .into(binding.profileTv);
        }

    }
    private void loadFavortieBooks(){
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String bookId = ""+ds.child("bookId").getValue();

                            ModelPdf modelPdf = new ModelPdf();
                            modelPdf.setId(bookId);

                            pdfArrayList.add(modelPdf);
                        }

                        binding.favoriteBookCountTv.setText(""+pdfArrayList.size());

                        adapterPdfFavortie = new AdapterPdfFavortie(GoogleProfile.this, pdfArrayList);

                        binding.booksRv.setAdapter(adapterPdfFavortie);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}