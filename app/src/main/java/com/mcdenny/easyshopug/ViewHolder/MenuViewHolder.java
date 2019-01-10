package com.mcdenny.easyshopug.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcdenny.easyshopug.Interface.ItemClickListener;
import com.mcdenny.easyshopug.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productName;
    public ImageView productImage;
    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView){
        super(itemView);

        productName = (TextView) itemView.findViewById(R.id.product_name);
        productImage = (ImageView) itemView.findViewById(R.id.product_image);
        itemView.setOnClickListener(this);
    }


   public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
