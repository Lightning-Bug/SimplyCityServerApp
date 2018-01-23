package com.android.parii.simplycityserverapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.parii.simplycityserverapp.Common.Common;
import com.android.parii.simplycityserverapp.Interface.ItemClickListener;
import com.android.parii.simplycityserverapp.R;

/**
 * Created by parii on 1/14/18.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener,
        View.OnCreateContextMenuListener{


    public TextView food_name;
    public ImageView food_image;

    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView){
        super(itemView);

        food_name = (TextView) itemView.findViewById(R.id.food_name);
        food_image = (ImageView) itemView.findViewById(R.id.food_image);


        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void onClick(View view){
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Selcet Action");

        menu.add(0,0,getAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}
