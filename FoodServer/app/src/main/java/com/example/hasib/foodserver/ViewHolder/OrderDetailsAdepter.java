package com.example.hasib.foodserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hasib.foodserver.Model.Order;
import com.example.hasib.foodserver.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by HASIB on 2/16/2018.
 */

class MyViewHolder extends RecyclerView.ViewHolder{
    TextView id,name,price,quantity,discounte,comment;


    public MyViewHolder(View itemView) {
        super(itemView);
        id=(TextView)itemView.findViewById(R.id.id);
        name=(TextView)itemView.findViewById(R.id.name);
        price=(TextView)itemView.findViewById(R.id.price);
        quantity=(TextView)itemView.findViewById(R.id.quantity);
        discounte=(TextView)itemView.findViewById(R.id.discoute);
        comment=(TextView)itemView.findViewById(R.id.comments);




    }
}


public class OrderDetailsAdepter extends RecyclerView.Adapter<MyViewHolder>  {

    public OrderDetailsAdepter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    List<Order> myOrders;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_layout,parent,false);


        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order order=myOrders.get(position);
        holder.id.setText(String.format("ID : %s",order.getProductId()));
        holder.name.setText(String.format("NAME : %s",order.getProductName()));
        holder.price.setText(String.format("PRICE : %s",order.getProductPrice()));
        holder.quantity.setText(String.format("QUANTITY : %s",order.getQuantity()));

        holder.discounte.setText(String.format("DISCOUNT : %s",order.getDiscount()));
      //  holder.comment.setText(String.format("Comment : %s",order.get));



    }


    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}

