package com.example.dcl.foodshipperapplicaation.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.dcl.foodshipperapplicaation.Model.Request;
import com.example.dcl.foodshipperapplicaation.Model.Shipper;
import com.example.dcl.foodshipperapplicaation.Model.ShipperInformation;
import com.example.dcl.foodshipperapplicaation.Remot.IGoCoordinates;
import com.example.dcl.foodshipperapplicaation.Remot.RetrofitClint;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by HASIB on 6/29/2018.
 */

public class Common {
    public static final String PHONE_TEXT = "phoneNumber";
    public static String databaseref="Shipper";
    public static String ORDER_NEED_SHIP_TABLE="OrderNeedToShip";
    public static String SHIPPING_INFO_TABLE="ShippingOrderTable";
    public static final String baseUrl="https://maps.googleapis.com/";

    public static Shipper currentShipper;
    public static Request currentRequest;
    public static String currentKey;

    public final static int REQUEST_CODE=1000;
    //public static Request setCurrentShipper;

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
    public static IGoCoordinates getGeoCodeService(){
        return RetrofitClint.getClint(baseUrl).create(IGoCoordinates.class);
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
    public static  Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){
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

    public static void createShipperOrder(String key, String number, Double lat,Double lon) {
        ShipperInformation shipperInformation =new ShipperInformation();
        shipperInformation.setOrderId(key);
        shipperInformation.setShipperPhone(number);
        shipperInformation.setLat(lat);
        shipperInformation.setLng(lon);

        FirebaseDatabase.getInstance()
                .getReference(SHIPPING_INFO_TABLE)
                .child(key)
                .setValue(shipperInformation)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR_TABLE_INFO",e.getMessage());
                    }
                });
    }

    public static void updateShippingInformation(String currentKey, Location mlocation) {
        Map<String,Object> update_location=new HashMap<>();
        update_location.put("lat",mlocation.getLatitude());
        update_location.put("lng",mlocation.getLongitude());

        FirebaseDatabase.getInstance()
                .getReference(SHIPPING_INFO_TABLE)
                .child(currentKey)
                .updateChildren(update_location)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR",e.getMessage());
                    }
                });
    }





}
