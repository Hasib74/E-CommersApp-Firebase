package com.example.dcl.foodshipperapplicaation.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;


import com.example.dcl.foodshipperapplicaation.Common.Common;
import com.example.dcl.foodshipperapplicaation.Helper.NotificationHelper;
import com.example.dcl.foodshipperapplicaation.HomeActivity;
import com.example.dcl.foodshipperapplicaation.MainActivity;
import com.example.dcl.foodshipperapplicaation.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by HASIB on 6/29/2018.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            sendNotificatonAPI26(remoteMessage);
        }else {
            sendNotification(remoteMessage);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotificatonAPI26(RemoteMessage remoteMessage) {


        Map<String,String> notification=remoteMessage.getData();

        String title=notification.get("title");
        String content=notification.get("message");

        Intent in=new Intent(this, HomeActivity.class);


        PendingIntent pendingIntent;
        NotificationHelper helper;
        Notification.Builder builder ;


        if (Common.currentShipper!=null){


          //  in.putExtra(Common.PHONE_TEXT,Common.currentShipper.getNumber());
           in.setFlags(in.FLAG_ACTIVITY_CLEAR_TOP);
           pendingIntent=PendingIntent.getActivity(this,0,in,PendingIntent.FLAG_ONE_SHOT);
           Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
           helper=new NotificationHelper(this);


            builder= helper.getFoodServerlicationNotification(title,content,pendingIntent,defaultSound);

            helper.getManager().notify(new Random().nextInt(),builder.build());

        }else {
            Uri defaultSoundUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            helper=new NotificationHelper(this);
            builder=helper.getFoodServerlicationNotification(title,content,defaultSoundUri);

            helper.getManager().notify(new Random().nextInt(),builder.build());
        }





    }

    private void sendNotification(RemoteMessage remoteMessage) {

        Map<String,String> notification=remoteMessage.getData();

        String title=notification.get("title");
        String content=notification.get("message");


        if (Common.currentShipper!=null){
            Intent in=new Intent(this, MainActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,in,PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_local_shipping_black_24dp)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSound(defaultSound);

            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0,notificationBuilder.build());
        }
        else {


            Uri defaultSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_local_shipping_black_24dp)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSound(defaultSound);

            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0,notificationBuilder.build());
        }
        
        
    }
}
