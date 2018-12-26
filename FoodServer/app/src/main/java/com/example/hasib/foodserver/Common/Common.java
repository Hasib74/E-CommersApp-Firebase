package com.example.hasib.foodserver.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.example.hasib.foodserver.Model.Request;
import com.example.hasib.foodserver.Model.User;
import com.example.hasib.foodserver.Remote.FCMretrofitClient;
import com.example.hasib.foodserver.Remote.IGoCoordinates;
import com.example.hasib.foodserver.Remote.RetrofitClint;
import com.example.hasib.foodserver.Remote.APIservice;

import java.util.Calendar;
import java.util.Locale;


/**
 * Created by HASIB on 11/28/2017.
 */

public class Common {
    public static String topicName="News";
    public static String oderShip="OrderNeedToShip";

    public static User currentUser;
    public static Request currentRequest;
    public static final String BaseUrl="https://fcm.googleapis.com/";
    public static final String baseUrl="https://maps.googleapis.com/";

   /* public static APIservice getFCMserver(){
        return RetrofitClint.getClint(BaseUrl).create(APIservice.class);
    }*/


    public static  final String UPDATE="Update";
    public static  final String DELETE="Delete";


    public static  int PICK_IMAGE_REQUESR=7;
    public static String PHONE_TEXT="userPhone";


    public static IGoCoordinates getGeoCodeService(){
        return RetrofitClint.getClint(baseUrl).create(IGoCoordinates.class);
    }
    public static APIservice getFCMClient(){
        return FCMretrofitClient.getClint(BaseUrl).create(APIservice.class);
    }



    public static final Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap scalerBitmap=Bitmap.createBitmap(newWidth,newHeight, Bitmap.Config.ARGB_8888);

        float scaleX=newWidth/(float)bitmap.getWidth();
        float scaleY=newHeight/(float)bitmap.getHeight();

        float pivotX=0;
        float pivotY=0;

        Matrix mMatrix=new Matrix();
        mMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas=new Canvas(scalerBitmap);
        canvas.setMatrix(mMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return  scalerBitmap;



    }


    @NonNull
    public  static  String ConvertCodeToStatus(String code){

        if(code.equals("0"))
            return "Placed";
        else if(code.equals("1"))
            return "On my way";
        else if(code.equals("2"))
            return "Shipping";
        else
            return "Shipped";


    }
    public static String getdate(long time){
        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);

        calendar.setTimeInMillis(time);

        StringBuilder date=new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yyyy HH:mm",
                        calendar).toString()
        );

        return  date.toString();
    }
}
