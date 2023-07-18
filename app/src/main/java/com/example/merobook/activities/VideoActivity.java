package com.example.merobook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.merobook.R;
import com.example.merobook.adpters.VideoAdapter;
import com.example.merobook.models.Video;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<Video> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video1);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoList = new ArrayList<>();
        adapter = new VideoAdapter(videoList);
        recyclerView.setAdapter(adapter);

        videoList = new ArrayList<>();
        adapter = new VideoAdapter(videoList);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference videosRef = database.getReference("Video");

        videosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                videoList.clear();
                for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                    String title = videoSnapshot.child("title").getValue(String.class);
                    String url = videoSnapshot.child("url").getValue(String.class);
                    Video video = new Video(title, url);
                    videoList.add(video);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e("VideoListActivity", "Database error: " + error.getMessage());
            }

            });
        }
    }