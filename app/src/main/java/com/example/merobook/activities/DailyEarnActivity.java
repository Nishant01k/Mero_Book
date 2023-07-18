package com.example.merobook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.example.merobook.R;
import com.example.merobook.databinding.ActivityDailyEarnBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DailyEarnActivity extends AppCompatActivity {
    
    ActivityDailyEarnBinding binding;
    int coins = 0;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    FirebaseDatabase database;
    String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDailyEarnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();

        database.getReference().child("Users")
                .child(currentUid)
                .child("coins")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        coins = snapshot.getValue(Integer.class);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
        
        binding.collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coins = coins + 10;
                database.getReference().child("Users")
                        .child(currentUid)
                        .child("coins")
                        .setValue(coins);
                binding.collect.setVisibility(View.INVISIBLE);
                Toast.makeText(DailyEarnActivity.this, " Congratulations you have got" + coins + " coins in Mero Book account", Toast.LENGTH_SHORT).show();
                starttime();
            }
        });
    }

    private void starttime() {
        long duration = TimeUnit.MINUTES.toMillis(1);

        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long l) {
                String sDuration = String.format(Locale.ENGLISH,"%02d : %02d"
                       ,TimeUnit.MILLISECONDS.toMinutes(1)
                ,TimeUnit.MILLISECONDS.toSeconds(1) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(1)));

                binding.time.setText(sDuration);
            }

            @Override
            public void onFinish() {

                binding.collect.setVisibility(View.VISIBLE);

            }
        }.start();
    }
}