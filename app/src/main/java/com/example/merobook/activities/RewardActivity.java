package com.example.merobook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.merobook.MyApplication;
import com.example.merobook.R;
import com.example.merobook.databinding.ActivityRewardBinding;
import com.example.merobook.databinding.DialogCommentAddBinding;
import com.example.merobook.databinding.DialogPormoBinding;
import com.example.merobook.models.PromoCode;
import com.example.merobook.models.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RewardActivity extends AppCompatActivity {

    ActivityRewardBinding binding;
    FirebaseDatabase database;
    String currentUid;
    int coins = 0;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private static final String TAG = "REWARD_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRewardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();


        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

        database.getReference().child("Users")
                .child(currentUid)
                .child("coins")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        coins = snapshot.getValue(Integer.class);
                        binding.coinTv.setText(String.valueOf(coins));
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        binding.watchCheckcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RewardActivity.this, adEarnActivity.class));
            }
        });

        binding.referCheckcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RewardActivity.this, ReferActivity.class));
            }
        });

        binding.c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogPormoBinding dialogPormoBinding = DialogPormoBinding.inflate(LayoutInflater.from(RewardActivity.this));

                AlertDialog.Builder builder = new AlertDialog.Builder(RewardActivity.this, R.style.CustomDialog);
                builder.setView(dialogPormoBinding.getRoot());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                dialogPormoBinding.backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                dialogPormoBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String promoCode = dialogPormoBinding.commentEt.getText().toString();
                        if (!promoCode.isEmpty()) {
                            // Get a reference to the "promocodes" node in your Firebase Realtime Database
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("promocodes");

                            // Query the database for the promo code with the specified code
                            Query query = databaseRef.orderByChild("code").equalTo(promoCode);

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // Check if the promo code exists in the database
                                    if (dataSnapshot.exists()) {
                                        // Get the first child (there should only be one)
                                        DataSnapshot promoCodeSnapshot = dataSnapshot.getChildren().iterator().next();

                                        // Check if the promo code has already been redeemed
                                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                                        DatabaseReference currentUserRef = usersRef.child(currentUid);
                                        DatabaseReference promoRedeemedRef = currentUserRef.child("Promo Redeemed");

                                        promoRedeemedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Boolean redeemed = dataSnapshot.getValue(Boolean.class);
                                                if (redeemed != null && redeemed) {
                                                    // The promo code has already been redeemed
                                                    Toast.makeText(RewardActivity.this, "This promo code has already been used", Toast.LENGTH_SHORT).show();
                                                    alertDialog.dismiss();
                                                }
                                                else {
                                                    long coinms = (long) promoCodeSnapshot.child("coins").getValue();

                                                    // TODO: Add the coins to the user's balance in your app
                                                    // ...
                                                    coins = (int) (coins + coinms);
                                                    database.getReference().child("Users")
                                                            .child(currentUid)
                                                            .child("coins")
                                                            .setValue(coins);

                                                    Toast.makeText(RewardActivity.this, " Congratulations you have got" + coinms + " coins in Mero Book account", Toast.LENGTH_SHORT).show();

                                                    database.getReference().child("Users")
                                                            .child(currentUid)
                                                            .child("Promo Redeemed")
                                                            .setValue(true);
                                                    alertDialog.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                // Handle database error
                                            }
                                        });

                                        // Get the coins value from the promo code snapshot



                                        // Set the "redeemed" flag for the promo code to true
                                        //promoCodeSnapshot.getRef().child("redeemed").setValue(true);
                                    } else {
                                        // The promo code does not exist in the database
                                        Toast.makeText(RewardActivity.this, "Invalid promo code", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle any errors that occur during the database query
                                    Log.e(TAG, "onCancelled", databaseError.toException());
                                }
                            });

                        } else {
                            Toast.makeText(RewardActivity.this, "Please enter a promo code", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        binding.c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RewardActivity.this, "!!!!!!Comming Soon!!!!!!!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RewardActivity.this, "!!!!!!Comming Soon!!!!!!!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RewardActivity.this, "!!!!!!Comming Soon!!!!!!!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RewardActivity.this, "!!!!!!Comming Soon!!!!!!!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.c6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RewardActivity.this, "!!!!!!Comming Soon!!!!!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void loadUserInfo() {

        Log.d(TAG, "loadUserInfo: Loading User Info of User " + firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = "" + snapshot.child("email").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

                        binding.emailTv.setText(email);
                        binding.nameTv.setText(name);

                        Glide.with(RewardActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.profile)
                                .into(binding.profileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}