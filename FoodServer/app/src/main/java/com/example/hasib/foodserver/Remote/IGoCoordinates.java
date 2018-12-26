package com.example.hasib.foodserver.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by HASIB on 12/3/2017.
 */

public interface IGoCoordinates {
@GET("maps/api/geocode/json")
    Call<String>getGeoCode(@Query("address")String address);

    @GET("maps/api/directions/json")
    Call<String>getDirection(@Query("origin")String origin,@Query("destination")String destination);

}
