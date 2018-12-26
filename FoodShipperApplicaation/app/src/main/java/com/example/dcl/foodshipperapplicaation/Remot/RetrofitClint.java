package com.example.dcl.foodshipperapplicaation.Remot;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClint {
    public static Retrofit retrofit=null;
    public static Retrofit getClint(String baseUrl){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
