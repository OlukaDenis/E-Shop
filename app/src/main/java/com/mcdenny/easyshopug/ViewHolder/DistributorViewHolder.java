package com.mcdenny.easyshopug.ViewHolder;

import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcdenny.easyshopug.Interface.ItemClickListener;
import com.mcdenny.easyshopug.R;

public class DistributorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView distName, distAddress;
    public ImageView distImage;
    public ItemClickListener itemClickListener;

    public DistributorViewHolder(@NonNull View itemView) {
        super(itemView);
        distName = itemView.findViewById(R.id.distributor_name);
        distAddress = itemView.findViewById(R.id.distributor_address);
        distImage = itemView.findViewById(R.id.distributor_image);
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
