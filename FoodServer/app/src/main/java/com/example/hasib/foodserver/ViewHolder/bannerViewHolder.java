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
 * Created by HASIB on 6/24/2018.
 */

public class bannerViewHolder  extends  RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

    public TextView banner_name;
    public ImageView banner_image;
    private ItemClickListener itemClickListener;
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public bannerViewHolder(View itemView) {

        super(itemView);

        banner_name=(TextView)itemView.findViewById(R.id.banner_name);
        banner_image=(ImageView)itemView.findViewById(R.id.banner_image);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
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
