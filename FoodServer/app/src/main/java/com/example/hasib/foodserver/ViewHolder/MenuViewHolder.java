package com.example.hasib.foodserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hasib.foodserver.Common.Common;
import com.example.hasib.foodserver.Interface.ItemClickListener;
import com.example.hasib.foodserver.R;

/**
 * Created by HASIB on 11/30/2017.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {


    public TextView titile;
    public ImageView imag;
    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);
        titile=(TextView)itemView.findViewById(R.id.menu_name);
        imag=(ImageView)itemView.findViewById(R.id.menu_image);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);

    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("select the acrtion");
        menu.add(0,0,getAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAdapterPosition(),Common.DELETE);
    }
}
