package com.example.dcl.foodshipperapplicaation.Helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.dcl.foodshipperapplicaation.R;


/**
 * Created by HASIB on 6/29/2018.
 */

public class NotificationHelper extends ContextWrapper {

    private static  final String FOOD_APPLICATION_SERVER_CHANEL_ID="com.example.hasib.foodshipperapp.FoodApplication";
    private static  final String FOOD_APPLICATION_SERVER_CHANEL_NAME="Food Application User";

    public NotificationManager notificationManager;


    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            createChanel();

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChanel() {

        android.app.NotificationChannel notificationChannel=new android.app.NotificationChannel(FOOD_APPLICATION_SERVER_CHANEL_ID,FOOD_APPLICATION_SERVER_CHANEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        //new NotificationChannel(FOOD_APPLICATION_SERVER_CHANEL_ID,FOOD_APPLICATION_SERVER_CHANEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(false);

        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {
        if (notificationManager==null ){
            notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  Notification.Builder getFoodServerlicationNotification(String title, String body,
                                                                               PendingIntent pendingIntent,
                                                                               Uri notificationSound){

        return  new Notification.Builder(getApplicationContext(),FOOD_APPLICATION_SERVER_CHANEL_ID)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_local_shipping_black_24dp)
                .setContentText(body)
                .setSound(notificationSound)
                ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  Notification.Builder getFoodServerlicationNotification(String title, String body,

                                                                               Uri notificationSound){

        return  new Notification.Builder(getApplicationContext(),FOOD_APPLICATION_SERVER_CHANEL_ID)

                .setAutoCancel(false)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_local_shipping_black_24dp)
                .setContentText(body)
                .setSound(notificationSound)
                ;

    }




}
