package com.mcdenny.easyshopug.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mcdenny.easyshopug.Interface.ItemClickListener;
import com.mcdenny.easyshopug.R;


public class AddressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView destination, name, area, division, district, phone, total;
    public ItemClickListener itemClickListener;

    public AddressViewHolder(@NonNull View itemView) {
        super(itemView);

        //total = (TextView) itemView.findViewById(R.id.cart_item_count);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
