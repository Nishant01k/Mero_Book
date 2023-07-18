package com.example.merobook.adpters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.merobook.R;
import com.example.merobook.models.Video;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private List<Video> videoList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public VideoView videoView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.title_text_view);
            videoView = view.findViewById(R.id.video_view);
        }
    }

    public VideoAdapter(List<Video> videoList) {
        this.videoList = videoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.titleTextView.setText(video.getTitle());

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(video.getUrl());
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                holder.videoView.setVideoURI(uri);
                holder.videoView.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }
}

