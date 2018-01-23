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
 * Created by parii on 1/6/18.
 */

public class FoodMenuViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener,
                   View.OnCreateContextMenuListener{


    public TextView txtMenuName;

    private ItemClickListener itemClickListener;

    public FoodMenuViewHolder(View itemView){
        super(itemView);

        txtMenuName = (TextView) itemView.findViewById(R.id.menu_name);

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
