package com.example.hasib.foodserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.hasib.foodserver.Common.Common;
import com.example.hasib.foodserver.ViewHolder.OrderDetailsAdepter;

public class OrderDetails extends AppCompatActivity {
    TextView order_id,order_status,oder_number,order_adress,commentok;
    public String getKey="";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        recyclerView=(RecyclerView)findViewById(R.id.foodList);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        order_id=(TextView)findViewById(R.id.order_id);
        order_status=(TextView)findViewById(R.id.order_status);
        oder_number=(TextView)findViewById(R.id.phone_number);
        order_adress=(TextView)findViewById(R.id.adress);
        commentok=(TextView)findViewById(R.id.comments);


        if(getIntent()!=null){

            getKey=getIntent().getStringExtra("OrderId");


        }

        order_id.setText(getKey);
        order_status.setText(Common.currentRequest.getStatus());
        oder_number.setText(Common.currentRequest.getPhone());
        order_adress.setText(Common.currentRequest.getAddress());
       // commentok.setText(Common.currentRequest.getComment());

        OrderDetailsAdepter orderDetailsAdepter=new OrderDetailsAdepter(Common.currentRequest.getFoods());
        recyclerView.setAdapter(orderDetailsAdepter);


    }
}
