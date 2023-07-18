package com.example.merobook;


import static com.android.volley.VolleyLog.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookNotificationManager {
    private static final String CHANNEL_ID = "Book Update";
    private static final int NOTIFICATION_ID = 1;

    public static void sendBookAddedNotificationToAllUsers(Context context, String bookTitle) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop through all user IDs and send a notification to each user
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    sendBookAddedNotification(context, userId, bookTitle);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read users.", databaseError.toException());
            }
        });
    }

    public static void sendBookAddedNotification(Context context, String userId, String bookTitle) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text, bookTitle))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(userId.hashCode(), builder.build());
    }

    private static void createNotificationChannel (Context context){

            // Create the notification channel only for Android 8.0 (API level 26) and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create a new notification channel with a unique ID and name
                String channelId = "my_channel_id";
                String channelName = "My Channel Name";
                String channelDescription = "My Channel Description";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

                // Set the channel description (optional)
                channel.setDescription(channelDescription);

                // Get the system notification manager and create the channel
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }