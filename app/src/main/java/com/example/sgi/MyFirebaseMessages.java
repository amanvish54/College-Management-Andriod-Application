package com.example.sgi;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import static com.example.sgi.App.FCM_CHANNEL_ID;


public class MyFirebaseMessages extends FirebaseMessagingService {

    private static final String TAG = "MyTag";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: Callled ");
        Log.d(TAG, "onMessageReceived: receive message from: "+remoteMessage.getFrom());
        if (remoteMessage.getNotification() != null){
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            generateNotification(title,body);
        }
    }


    private void generateNotification(String title, String body) {
        Notification notification = new NotificationCompat.Builder(this,FCM_CHANNEL_ID)
                .setSmallIcon(R.drawable.sgi_logo)
                .setContentTitle(title)
                .setContentText(body)
                .build();

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(1002,notification);


    }



    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d(TAG, "TokenRefreshed: " + s);
    }
}
