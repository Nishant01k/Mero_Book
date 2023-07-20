package com.example.merobook.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.merobook.BlurDialogFragment;
import com.example.merobook.BooksUserFragment;
import com.example.merobook.R;
import com.example.merobook.databinding.ActivityDashboardUserBinding;
import com.example.merobook.models.Bookc;
import com.example.merobook.models.ModelCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DashboardUserActivity extends AppCompatActivity {

    public ArrayList<ModelCategory> categoryArrayList;
    public ViewPagerAdapter viewPagerAdapter;

    private ActivityDashboardUserBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(android.R.id.content).startAnimation(fadeIn);


        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();

        setupViewPagerAdapter(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Create the object of

                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(DashboardUserActivity.this,SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitleText("Do you Want to Log Out!!");
                sweetAlertDialog.setConfirmText("Yes");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(DashboardUserActivity.this,MainActivity.class));
                    }
                });
                sweetAlertDialog.setCancelButton("No" , new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
               // BlurDialogFragment blurDialogFragment = new BlurDialogFragment();
               // blurDialogFragment.show(getSupportFragmentManager(), "BlurDialogFragment");

            }
        });
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardUserActivity.this, ProfileActivity.class));
            }
        });

        binding.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardUserActivity.this, UsersActivity.class));
            }
        });
    }




    private void checkUser() {

            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser==null){
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
            else{
                String email = firebaseUser.getEmail();
                binding.subTitleTv.setText(email);
                Glide.with(DashboardUserActivity.this)
                        .load(firebaseUser.getPhotoUrl())
                        .placeholder(R.drawable.profile)
                        .into(binding.profileImage);
            }
        }

    private void setupViewPagerAdapter(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this);
        categoryArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                categoryArrayList.clear();
                ModelCategory modelAll = new ModelCategory("01", "All", "", 1);
                ModelCategory modelMostViewed = new ModelCategory("02", "Most Viewed", "", 1);
                ModelCategory modelMostDownloaded = new ModelCategory("03", "Most Downloaded", "", 1);
                categoryArrayList.add(modelAll);
                categoryArrayList.add(modelMostViewed);
                categoryArrayList.add(modelMostDownloaded);

                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        "" + modelAll.getId(),
                        "" + modelAll.getCategory(),
                        "" + modelAll.getUid()
                ), modelAll.getCategory());
                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        "" + modelMostViewed.getId(),
                        "" + modelMostViewed.getCategory(),
                        "" + modelMostViewed.getUid()
                ), modelMostViewed.getCategory());
                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        "" + modelMostDownloaded.getId(),
                        "" + modelMostDownloaded.getCategory(),
                        "" + modelMostViewed.getUid()
                ), modelMostDownloaded.getCategory());

                viewPagerAdapter.notifyDataSetChanged();
                ref.keepSynced(true);

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelCategory model = ds.getValue(ModelCategory.class);
                    categoryArrayList.add(model);

                    viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                            "" + model.getId(),
                            "" + model.getCategory(),
                            "" + model.getUid()), model.getCategory());

                    viewPagerAdapter.notifyDataSetChanged();
                    ref.keepSynced(true);
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        viewPager.setAdapter(viewPagerAdapter);

    }


    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<BooksUserFragment> fragmentsList = new ArrayList<>();
        private ArrayList<String> fragmentTitleList = new ArrayList<>();
        private Context context;

        public ViewPagerAdapter(FragmentManager fm, int behavior, Context context) {
            super(fm, behavior);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        void addFragment(BooksUserFragment fragment, String title) {
            fragmentsList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing to disable going back to the previous activity
        // Remove the super.onBackPressed() line
    }
}