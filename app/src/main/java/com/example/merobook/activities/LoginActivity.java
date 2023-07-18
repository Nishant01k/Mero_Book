package com.example.merobook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.merobook.R;
import com.example.merobook.databinding.ActivityLoginBinding;
import com.example.merobook.models.Google_user;
import com.example.merobook.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import xyz.hasnat.sweettoast.SweetToast;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";
    int RC_SIGN_IN = 11;
    GoogleSignInClient mGoogleSignInClient;


    private ActivityLoginBinding binding;

    private FirebaseAuth firebaseAuth;


    FirebaseDatabase database;

   //private ProgressDialog progressDialog;

    private SweetAlertDialog progressDialog;
    
    CheckBox ch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(android.R.id.content).startAnimation(fadeIn);
        
        ch = findViewById(R.id.ch2);

        // Hide the status bar
        hideStatusBar();



        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setCancelable(false);
        //progressDialog = new ProgressDialog(this);
        //progressDialog.setTitle("Please wait");
        //progressDialog.setCanceledOnTouchOutside(false);

        binding.ani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
            }
        });

        binding.noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch.isChecked()) {

                    validateData();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Accept the Privacy Policy!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, PricyActivity.class));
            }
        });

        binding.forgotTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    private void showSuccessDialog() {
        SweetAlertDialog successDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        successDialog.setTitleText("Success");
        successDialog.setContentText("Welcome!");
        successDialog.setConfirmText("Thank You");
        successDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                startActivity(new Intent(LoginActivity.this,LoginActivity.class));
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

    private String email = "" , password = "";

    private void validateData() {

        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            //Toast.makeText(this, "Invalid email pattern...!", Toast.LENGTH_SHORT).show();

            SweetToast.custom(this, "Invalid email pattern...!", R.drawable.logo, R.color.white, R.drawable.toasr_backgroung, 10000);
        }
        else if (TextUtils.isEmpty(password)) {

            Toast.makeText(this, "Enter Password...!", Toast.LENGTH_SHORT).show();
        }
        else {
            loginUser();
        }
    }

    private void loginUser() {
        progressDialog.setTitleText("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        checkUser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
        // check if username and password are correct
        /**if (email.equals(getUsername()) && password.equals(getPassword())) {
            // login successful, start main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // login failed, show error message
            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }**/
    }

    private String getUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("username", "");
    }

    private String getPassword() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("password", "");
    }


    private void checkUser() {
        progressDialog.setTitleText("Checking User...");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        String userType =""+snapshot.child("userType").getValue();
                        if (userType.equals("user")){
                            if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()){
                            progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Successfully...", Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(LoginActivity.this, DashboardUserActivity.class));
                                finish();
                           }
                            else {
                                Toast.makeText(LoginActivity.this, "Please Verify your Email First", Toast.LENGTH_SHORT).show();
                            }
                        }

                        else if (userType.equals("admin")){
                            if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()){
                            progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Successfully....", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, DashboardAdminActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Please Verify your Email First", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    @Override
    public void onBackPressed() {
        // Do nothing to disable going back to the previous activity
        // Remove the super.onBackPressed() line
    }
}