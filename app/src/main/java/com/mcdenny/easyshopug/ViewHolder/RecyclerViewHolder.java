package com.mcdenny.easyshopug.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcdenny.easyshopug.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{
    public TextView actionName;
    public ImageView actionPhoto;

    public RecyclerViewHolder(View itemView) {
        super(itemView);

        actionName = itemView.findViewById(R.id.action_name);
        actionPhoto = itemView.findViewById(R.id.action_logo);

    }
}
