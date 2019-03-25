package com.mcdenny.easyshopug.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.mcdenny.easyshopug.Interface.ItemClickListener;
import com.mcdenny.easyshopug.R;

public class CartDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView cart_item_name, cart_item_price;
    public ImageView cart_item_image;
    public ElegantNumberButton cart_number_button;
    public Button remove_cart;
    public ItemClickListener itemClickListener;

    public CartDetailViewHolder(@NonNull View itemView) {
        super(itemView);
        cart_item_name = (TextView) itemView.findViewById(R.id.cart_item_name);
        cart_item_price = (TextView) itemView.findViewById(R.id.cart_item_price);
        cart_item_image = (ImageView) itemView.findViewById(R.id.cart_image);
        cart_number_button = (ElegantNumberButton) itemView.findViewById(R.id.enb_cart_add_subtract);
        remove_cart = (Button) itemView.findViewById(R.id.remove_cart_item);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
