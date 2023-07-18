package com.example.merobook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.merobook.R;
import com.example.merobook.databinding.ActivityAdEarnBinding;
import com.example.merobook.databinding.ActivityRewardBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class adEarnActivity extends AppCompatActivity {


    ActivityAdEarnBinding binding;
    private RewardedAd mRewardedAd;
    FirebaseDatabase database;
    String currentUid;

    int coins = 0;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdEarnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();
        loadAd();

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

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.video1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedAd != null) {
                    Activity activityContext = adEarnActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            loadAd();
                            coins = coins + 200;
                            database.getReference().child("Users")
                                    .child(currentUid)
                                    .child("coins")
                                    .setValue(coins);
                            binding.video1.setVisibility(View.INVISIBLE);
                            Toast.makeText(activityContext, " Congratulations you have got" + coins + " coins in Mero Book account", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                }
            }
        });


        binding.video2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedAd != null) {
                    Activity activityContext = adEarnActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            loadAd();
                            coins = coins + 300;
                            database.getReference().child("Users")
                                    .child(currentUid)
                                    .child("coins")
                                    .setValue(coins);
                            binding.video2.setVisibility(View.INVISIBLE);
                            Toast.makeText(activityContext, " Congratulations you have got" + coins + " coins in Mero Book account", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                }
            }
        });

        binding.video3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedAd != null) {
                    Activity activityContext = adEarnActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            loadAd();
                            coins = coins + 400;
                            database.getReference().child("Users")
                                    .child(currentUid)
                                    .child("coins")
                                    .setValue(coins);
                            binding.video3.setVisibility(View.INVISIBLE);
                            Toast.makeText(activityContext, " Congratulations you have got" + coins + " coins in Mero Book account", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                }
            }
        });

        binding.video4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedAd != null) {
                    Activity activityContext = adEarnActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            loadAd();
                            coins = coins + 500;
                            database.getReference().child("Users")
                                    .child(currentUid)
                                    .child("coins")
                                    .setValue(coins);
                            binding.video4.setVisibility(View.INVISIBLE);
                            //binding.video4Icon.setImageResource(R.drawable.check);
                            Toast.makeText(activityContext, " Congratulations you have got" + coins + " coins in Mero Book account", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                }
            }
        });

        binding.video5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedAd != null) {
                    Activity activityContext = adEarnActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            loadAd();
                            coins = coins + 1000;
                            database.getReference().child("Users")
                                    .child(currentUid)
                                    .child("coins")
                                    .setValue(coins);
                            binding.video5.setVisibility(View.INVISIBLE);
                            Toast.makeText(activityContext, " Congratulations you have got" + coins + " coins in Mero Book account", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                }
            }
        });

    }
        private void loadAd () {
            AdRequest adRequest = new AdRequest.Builder().build();

            RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.

                            mRewardedAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;
                        }
                    });
        }


    }