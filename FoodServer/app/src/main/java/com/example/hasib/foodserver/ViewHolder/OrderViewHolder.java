package com.example.hasib.foodserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.example.hasib.foodserver.Common.Common;
import com.example.hasib.foodserver.Interface.ItemClickListener;
import com.example.hasib.foodserver.R;

/**
 * Created by HASIB on 12/2/2017.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener,View.OnLongClickListener {
    public  TextView OrderId,OrderStatus,OrderNumber,OrderAdress,OrderComment,OrderTimeAndDate,OrderShift,OrderDilete,OrderLocation;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);
        OrderId=(TextView)itemView.findViewById(R.id.order_id);
        OrderNumber=(TextView) itemView.findViewById(R.id.phone_number);
        OrderAdress=(TextView) itemView.findViewById(R.id.adress);
        OrderStatus=(TextView) itemView.findViewById(R.id.order_status);
        OrderComment=(TextView) itemView.findViewById(R.id.comments);
        OrderTimeAndDate=(TextView)itemView.findViewById(R.id.timeAnddata_show);
        OrderShift=(TextView)itemView.findViewById(R.id.shift);
        OrderLocation=(TextView)itemView.findViewById(R.id.location);
        OrderDilete=(TextView)itemView.findViewById(R.id.delete);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

//        itemClickListener.onClick(v,getAdapterPosition(),false);

    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("select the acrtion");
        menu.add(0,0,getAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAdapterPosition(),Common.DELETE);
    }

    /**
     * Called when a view has been clicked and held.
     *
     * @param v The view that was clicked and held.
     * @return true if the callback consumed the long click, false otherwise.
     */
    @Override
    public boolean onLongClick(View v) {
        return true;
    }
}
