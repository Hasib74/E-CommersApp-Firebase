package com.example.hasib.foodserver.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by HASIB on 4/2/2018.
 */

public class FCMretrofitClient {
    public static Retrofit retrofit=null;
    public static  Retrofit getClint(String baseUrl){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
