package com.vamsigutha.atomichabitstracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class RemainderBroadcast extends BroadcastReceiver {
    String title ="";

    @Override
    public void onReceive(Context context, Intent intent) {
        title = intent.getExtras().getString("title");
        int code = intent.getIntExtra("code",0);
        Boolean notificationSound = intent.getBooleanExtra("notificationSound",false);
        String content = intent.getStringExtra("content");

        PendingIntent i = PendingIntent.getActivity(context,
                code,
                new Intent(context,MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify")
                .setSmallIcon(R.drawable.displayicon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(i)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if(notificationSound){
            builder.setSound(Uri.parse("android.resource://"
                    + context.getPackageName() + "/" + R.raw.waterdrop));
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(code, builder.build());
    }
}
