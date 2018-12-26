package com.example.dcl.foodshipperapplicaation.Remot;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoCoordinates {
    @GET("maps/api/geocode/json")
    Call<String> getGeoCode(@Query("address")String address);

    @GET("maps/api/directions/json")
    Call<String>getDirection(@Query("origin")String origin,@Query("destination")String destination);

}
