package com.example.merobook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merobook.R;
import com.example.merobook.models.User;
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

import org.w3c.dom.Text;

import java.util.HashMap;

public class ReferActivity extends AppCompatActivity {

    private FirebaseUser user;
    private String oppositeUID;

    private TextView referCodeTv;
    private Button shareBtn, redeemBtn;
    private ImageView back;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        init();

        back = findViewById(R.id.backBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("Users");

        loadData();
        redeemAvailability();
        clickListener();


    }

    private  void redeemAvailability(){

        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists() && snapshot.hasChild("redeemed")) {

                            boolean isAvailable = snapshot.child("redeemed").getValue(Boolean.class);

                            if (isAvailable) {
                                redeemBtn.setVisibility(View.GONE);
                                redeemBtn.setEnabled(false);
                            } else {
                                redeemBtn.setVisibility(View.VISIBLE);
                                redeemBtn.setEnabled(true);

                            }
                        }
                    }

                        @Override
                        public void onCancelled (@NonNull DatabaseError error){

                        }
                });


    }


    private void init(){
        referCodeTv=findViewById(R.id.referCodeTv);
        shareBtn = findViewById(R.id.share);
        redeemBtn = findViewById(R.id.redeemBtn);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void loadData(){

        reference.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String referCode = snapshot.child("ReferCode").getValue(String.class);
                        referCodeTv.setText(referCode);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ReferActivity.this, "Error:"+error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
    private void clickListener(){
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String referCode = referCodeTv.getText().toString();

                String shareBody = "Hey, I am using the best learning app. Join using my invite code to instantly get 100"
                        +" coins. My invite code is "+referCode+"\n"+
                        "Download from Galaxy Store\n"+
                        "https://galaxy.store/01k"+
                        getPackageName();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(intent);
            }
        });

        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = new EditText(ReferActivity.this);
                editText.setHint("Mero Book");

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                editText.setLayoutParams(layoutParams);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReferActivity.this);

                alertDialog.setTitle("Redeem Code");

                alertDialog.setView(editText);

                alertDialog.setPositiveButton("Redeem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String inputCode = editText.getText().toString();

                        if (TextUtils.isEmpty(inputCode)) {
                            Toast.makeText(ReferActivity.this, "Input valid code", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (inputCode.equals(referCodeTv.getText().toString())) {
                            Toast.makeText(ReferActivity.this, "You can not input your own code",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        redeemQuery(inputCode, dialog);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
                }
        });
    }

    private void redeemQuery(String inputCode, final DialogInterface dialog) {

        Query query = reference.orderByChild("ReferCode").equalTo(inputCode);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot  : snapshot.getChildren()){
                    oppositeUID = dataSnapshot.getKey();

                    reference
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User users = snapshot.child(oppositeUID).getValue(User.class);
                                    User user1 = snapshot.child(user.getUid()).getValue(User.class);

                                    int coins = (int) users.getCoins();
                                    int updateCoins = coins+ 100;

                                    int mycoins = (int) user1.getCoins();
                                    int myupdate = mycoins+ 100;

                                    HashMap<String,Object>map = new HashMap<>();
                                    map.put("coins",updateCoins);

                                    HashMap<String,Object>mymap = new HashMap<>();
                                    mymap.put("coins",myupdate);
                                    mymap.put("redeemed",true);

                                    reference.child(oppositeUID).updateChildren(map);
                                    reference.child(user.getUid()).updateChildren(mymap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    dialog.dismiss();
                                                    Toast.makeText(ReferActivity.this, "Congrats you got 100 Coin in your Mero Book Wallet", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReferActivity.this, "Error:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}