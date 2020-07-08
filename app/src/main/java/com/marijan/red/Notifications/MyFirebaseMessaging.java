package com.marijan.red.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.marijan.red.MainActivity;
import com.marijan.red.Profile;
import com.marijan.red.StoryCommentActivity;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");
        String userId = remoteMessage.getData().get("receiverId");

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && sented.equals(firebaseUser.getUid())){
            if (!currentUser.equals(userId)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage);
                }

                else  {
                    sendNotification(remoteMessage);
                }
            }
        }
    }

    private void sendOreoNotification(RemoteMessage remoteMessage){
        String userId = remoteMessage.getData().get("receiverId");
        String userName = remoteMessage.getData().get("receiverName");
        String userImage = remoteMessage.getData().get("receiverImage");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String messageKey = remoteMessage.getData().get("messageKey");
        String type  = remoteMessage.getData().get("type");
        String senderId = remoteMessage.getData().get("senderId");
        //story comments
        String postid = remoteMessage.getData().get("postid");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String user = firebaseUser.getUid();

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(userId.replaceAll("[\\D]", ""));
        if(type.equals("other")){
            Intent intent = new Intent(this, Profile.class);
            Bundle bundle = new Bundle();
            bundle.putString("idProfile", senderId);


            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                    defaultSound, icon);

            int i = 0;
            if (j > 0){
                i = j;
            }

            oreoNotification.getManager().notify(i, builder.build());

        }
        else if(type.equals("StoryComment")){
            Intent intent = new Intent(this, StoryCommentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("from", "notification");
            bundle.putString("receiverId", userId);
            bundle.putString("receiverName", userName);
            bundle.putString("receiverImage", userImage);
            bundle.putString("messageKey", messageKey);
            bundle.putString("storyid", postid);
            bundle.putString("id", user);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                    defaultSound, icon);

            int i = 0;
            if (j > 0){
                i = j;
            }

            oreoNotification.getManager().notify(i, builder.build());
        }


    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String userId = remoteMessage.getData().get("receiverId");
        String userName = remoteMessage.getData().get("receiverName");
        String userImage = remoteMessage.getData().get("receiverImage");
        String messageKey = remoteMessage.getData().get("messageKey");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String type = remoteMessage.getData().get("type");
        String senderId = remoteMessage.getData().get("senderId");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(userId.replaceAll("[\\D]", ""));
        if (type.equals("other")) {
            Intent intent = new Intent(this, Profile.class);
            Bundle bundle = new Bundle();
            bundle.putString("idProfile", senderId);

            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ID")
                    .setSmallIcon(Integer.parseInt(icon))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setPriority(10)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);
            NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int i = 0;
            if (j > 0) {
                i = j;
            }

            noti.notify(i, builder.build());
        }

    }

}