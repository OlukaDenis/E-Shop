package com.mcdenny.easyshopug.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.mcdenny.easyshopug.Model.Cart;
import com.mcdenny.easyshopug.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyHolder>{
    private Context context;
    private List<Cart> listCart;


    public RecyclerViewAdapter(Context context, List<Cart> listCart) {
        this.listCart = listCart;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout,parent,false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }


    public void onBindViewHolder(MyHolder holder, int position) {
        Cart data = listCart.get(position);
        holder.cart_item_name.setText(data.getName());
        holder.cart_item_price.setText(data.getPrice());
        Picasso.with(context).load(data.getImage()).into(holder.cart_item_image);
        holder.cart_number_button.setNumber(data.getQuantity());
    }

    @Override
    public int getItemCount() {
        return listCart.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        TextView cart_item_name, cart_item_price;
        ImageView cart_item_image;
        ElegantNumberButton cart_number_button;

        public MyHolder(View itemView) {
            super(itemView);
            cart_item_name = (TextView) itemView.findViewById(R.id.cart_item_name);
            cart_item_price = (TextView) itemView.findViewById(R.id.cart_item_price);
            cart_item_image = (ImageView) itemView.findViewById(R.id.cart_image);
            cart_number_button = (ElegantNumberButton) itemView.findViewById(R.id.enb_cart_add_subtract);
        }
    }


}
