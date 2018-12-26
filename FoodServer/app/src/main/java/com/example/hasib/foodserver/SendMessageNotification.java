package com.example.hasib.foodserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hasib.foodserver.Common.Common;
import com.example.hasib.foodserver.Model.Myresponce;
import com.example.hasib.foodserver.Model.Notification;
import com.example.hasib.foodserver.Model.Sender;
import com.example.hasib.foodserver.Remote.APIservice;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessageNotification extends AppCompatActivity {

    EditText getTitle,getMessage;
    Button sendButton;
    APIservice mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_notification);
        mService=Common.getFCMClient();

        getTitle=(EditText)findViewById(R.id.sendtitle);
        getMessage=(EditText)findViewById(R.id.sendMessage);

        sendButton=(Button)findViewById(R.id.sendNotificationbutton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=getTitle.getText().toString();
                String message=getMessage.getText().toString();


                Map<String,String> content=new HashMap<>();

                String to=new StringBuilder("/topics/").append(Common.topicName).toString();

                content.put("title",title);
                content.put("message",message);

                Notification notification=new Notification(message,title);
                Sender toTopics=new Sender(to,content);




                //toTopics.to=new StringBuilder("/topics/").append(Common.topicName).toString();
               // toTopics.notification=notification;

                mService.sendNotification(toTopics).enqueue(new Callback<Myresponce>() {
                    @Override
                    public void onResponse(Call<Myresponce> call, Response<Myresponce> response) {

                        if (response.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Successfully send the message",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<Myresponce> call, Throwable t) {

                        Toast.makeText(getApplicationContext(),"Fail to send Message "+call.toString(),Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }
}
