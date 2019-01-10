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

        destination = itemView.findViewById(R.id.destinationTextView);
        name = itemView.findViewById(R.id.set_name);
        area = itemView.findViewById(R.id.set_area);
        division = itemView.findViewById(R.id.set_division);
        district = itemView.findViewById(R.id.set_district);
        phone = itemView.findViewById(R.id.set_phone);
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
