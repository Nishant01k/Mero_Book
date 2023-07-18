package com.example.merobook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.merobook.R;
import com.example.merobook.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import xyz.hasnat.sweettoast.SweetToast;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private FirebaseAuth firebaseAuth;

    //private ProgressDialog progressDialog;

    private FirebaseUser user1;

    private SweetAlertDialog progressDialog;


    CheckBox ch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(android.R.id.content).startAnimation(fadeIn);

        ch = findViewById(R.id.ch2);
        // Hide the status bar
        hideStatusBar();


        firebaseAuth = FirebaseAuth.getInstance();

        //progressDialog = new ProgressDialog(this);
        //progressDialog.setTitle("Please wait");
        //progressDialog.setCanceledOnTouchOutside(false);

        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setCancelable(false);



        binding.ani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);

            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch.isChecked()) {
                    validateData();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Accept the Privacy Policy!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showSuccessDialog() {
        SweetAlertDialog successDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        successDialog.setTitleText("Success");
        successDialog.setContentText("Account created successfully!");
        successDialog.setConfirmText("OK");
        successDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        successDialog.setCancelable(false);
        successDialog.show();
    }


    private void hideStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        View decorView = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        decorView.setSystemUiVisibility(flags);
    }

    private String name ="", email ="",password = "",referCode="";
    private int coin = 100;
    
    private void validateData() {
        name = binding.nameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        String cPassword = binding.cpasswordEt.getText().toString().trim();
        coin = binding.coin1.getInputType();
        referCode = binding.nameEt.getText().toString().trim();
        
        if(TextUtils.isEmpty(name)){

            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            Toast.makeText(this, "Invalid email pattern...!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Enter Password...!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cPassword)){

            Toast.makeText(this, "Confirm Password...!", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(cPassword)){
            Toast.makeText(this, "Password does't match...!", Toast.LENGTH_SHORT).show();
        }
        else {

            createUserAccount();
        }

    }

    private void createUserAccount() {
        progressDialog.setTitleText("Creating Account...");
        progressDialog.show();


       firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateUserInfo();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        /**SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", name);
        editor.putString("password", password);
        editor.apply();
        progressDialog.dismiss();**/
    }

    private void updateUserInfo() {

        progressDialog.setTitleText("Saving user information...");

        long timestamp = System.currentTimeMillis();

        String uid = firebaseAuth.getUid();

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date perviousDate = calendar.getTime();

        String dateString = dateFormat.format(perviousDate);

        FirebaseDatabase.getInstance().getReference().child("Daily Check")
                .child(Objects.requireNonNull(uid))
                .child("date")
                .setValue(dateString);


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", "");
        hashMap.put("userType", "user");
        hashMap.put("Password" , password);
        hashMap.put("timestamp", timestamp);
        hashMap.put("coins",coin+100);
        hashMap.put("ReferCode",referCode+"01k");
        hashMap.put("Account created",dateString+dateFormat+1+"/mm/2022");
        hashMap.put("Spins",1);
        hashMap.put("Promo Redeemed",false);
        hashMap.put("FriendRequests",false);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(Objects.requireNonNull(uid))
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void unused) {
                        sendVerificationEmail();

                        //startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void sendVerificationEmail() {
        FirebaseAuth.getInstance().getCurrentUser()
                .sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        showSuccessDialog();
                        Toast.makeText(RegisterActivity.this, "Email Verification link send to your Email...Please Verify and then Login", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Email not sent"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        // Do nothing to disable going back to the previous activity
        // Remove the super.onBackPressed() line
    }
}


//Toast.makeText(RegisterActivity.this, "Account Created...", Toast.LENGTH_SHORT).show();