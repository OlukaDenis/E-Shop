package com.mcdenny.easyshopug.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.mcdenny.easyshopug.Model.Cart;
import com.mcdenny.easyshopug.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyHolder>{

    List<Cart> listCart;

    public RecyclerViewAdapter(List<Cart> listCart) {
        this.listCart = listCart;
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
        TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(""+data.getQuantity(), Color.RED);
        holder.cart_item_count.setImageDrawable(textDrawable);
    }

    @Override
    public int getItemCount() {
        return listCart.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        TextView cart_item_name, cart_item_price;
        ImageView cart_item_count;

        public MyHolder(View itemView) {
            super(itemView);
            cart_item_name = (TextView) itemView.findViewById(R.id.cart_item_name);
            cart_item_price = (TextView) itemView.findViewById(R.id.cart_item_price);
            cart_item_count = (ImageView) itemView.findViewById(R.id.cart_item_count);

        }
    }


}
