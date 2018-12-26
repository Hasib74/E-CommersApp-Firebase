package com.example.hasib.foodserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.example.hasib.foodserver.Interface.ItemClickListener;
import com.example.hasib.foodserver.R;

/**
 * Created by HASIB on 6/28/2018.
 */

public class ShipperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener,View.OnLongClickListener {
    public TextView Shippername,ShipperNumber,ShipperPassword;
    public TextView Delete,Update;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private ItemClickListener itemClickListener;

    public ShipperViewHolder(View itemView) {
        super(itemView);
        Shippername=(TextView) itemView.findViewById(R.id.shepper_name);
        ShipperPassword=(TextView)itemView.findViewById(R.id.shipper_password);
        ShipperNumber=(TextView)itemView.findViewById(R.id.shepper_number) ;
        Update=(TextView) itemView.findViewById(R.id.edit);
        Delete=(TextView) itemView.findViewById(R.id.delete);

        itemView.setOnLongClickListener(this);
        itemView.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }


    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
