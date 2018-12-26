package com.example.dcl.foodshipperapplicaation.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.example.dcl.foodshipperapplicaation.Interface.ItemClickListener;
import com.example.dcl.foodshipperapplicaation.R;


/**
 * Created by HASIB on 6/30/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener,View.OnLongClickListener{


    public TextView OrderId,OrderStatus,OrderNumber,OrderAdress,OrderComment,OrderTimeAndDate,OrderShift,OrderDilete,OrderLocation;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OrderViewHolder(View itemView) {
        super(itemView);
        OrderId=(TextView)itemView.findViewById(R.id.order_id);
        OrderNumber=(TextView) itemView.findViewById(R.id.phone_number);
        OrderAdress=(TextView) itemView.findViewById(R.id.adress);
        OrderStatus=(TextView) itemView.findViewById(R.id.order_status);
        ;
        OrderTimeAndDate=(TextView)itemView.findViewById(R.id.timeAnddata_show);
        OrderShift=(TextView)itemView.findViewById(R.id.shift);



    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * Called when the context menu for this view is being built. It is not
     * safe to hold onto the menu after this method returns.
     *
     * @param menu     The context menu that is being built
     * @param v        The view for which the context menu is being built
     * @param menuInfo Extra information about the item for which the
     *                 context menu should be shown. This information will vary
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    /**
     * Called when a view has been clicked and held.
     *
     * @param v The view that was clicked and held.
     * @return true if the callback consumed the long click, false otherwise.
     */
    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
