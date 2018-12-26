package com.example.hasib.foodserver.Remote;

import com.example.hasib.foodserver.Model.Myresponce;
import com.example.hasib.foodserver.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by HASIB on 12/16/2017.
 */

public interface APIservice {
    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAQ2Qrg9I:APA91bGUAplD_GlJHwwXmDOJc5xlyK52-F4_3c2eM8yXJvcUw-5yU1fm240T4ObqD2NRUegpBblluFAxEO1dOZLq69NsO6K9G8DFXauVV-dlPxanSl37xUYhfM0JwLmscqV9hJYP8rMY"
            }
    )
    @POST("fcm/send")
    Call<Myresponce> sendNotification(@Body Sender body);



    //Call<Myresponce>

}
