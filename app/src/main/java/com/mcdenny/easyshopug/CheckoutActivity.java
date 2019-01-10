package com.mcdenny.easyshopug;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcdenny.easyshopug.Model.Address;
import com.mcdenny.easyshopug.Model.Cart;
import com.mcdenny.easyshopug.ViewHolder.AddressViewHolder;
import com.mcdenny.easyshopug.ViewHolder.CartDetailViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    TextView totalPrice;
    Button checkout;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference orders, address;
    DatabaseReference cart;

    List<Cart> cartOrder = new ArrayList<>();

    FirebaseRecyclerAdapter<Cart, CartDetailViewHolder> adapter;
    FirebaseRecyclerAdapter<Address, AddressViewHolder> addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //totalPrice = (TextView) findViewById(R.id.total);

        checkout = findViewById(R.id.confirm);
        //initialise firebase
        database = FirebaseDatabase.getInstance();
        //addressDb = FirebaseDatabase.getInstance();

        // address reference
        address = database.getReference("Addresses");
        //requests reference
        orders = database.getReference("Requests");
        //cart reference
        cart = database.getReference("Cart");

       // recyclerView = (RecyclerView) findViewById(R.id.confirm_cartList);
        //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);

      //  loadCartOrders();
      //  populateAddress();

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //authorize mobile money


            }
        });

    }


    private void populateAddress() {
        addressAdapter = new FirebaseRecyclerAdapter<Address, AddressViewHolder>(
                Address.class,
                R.layout.activity_checkout,
                AddressViewHolder.class,
                address
        ) {
            @Override
            protected void populateViewHolder(AddressViewHolder viewHolder, Address addressModel, int position) {
                    viewHolder.destination.setText(R.string.destination);
                    viewHolder.name.setText(addressModel.getAddressName());
                    viewHolder.area.setText(addressModel.getAddressArea());
                    viewHolder.division.setText(addressModel.getAddressDivision());
                    viewHolder.district.setText(addressModel.getAddressDistrict());
                    viewHolder.phone.setText(addressModel.getAddressPhone());

            }
        };
    }
/*
    //retrieving the orders from the cart
    private void loadCartOrders() {
        adapter = new FirebaseRecyclerAdapter<Cart, CartDetailViewHolder>(
                Cart.class,
                R.layout.cart_layout,
                CartDetailViewHolder.class,
                cart
        ) {
            @Override
            protected void populateViewHolder(CartDetailViewHolder viewHolder, Cart model, int position) {
                cartOrder = new ArrayList<Cart>();
                viewHolder.cart_item_name.setText(model.getName());
                viewHolder.cart_item_price.setText(model.getPrice());
                TextDrawable textDrawable = TextDrawable.builder()
                        .buildRound("" + model.getQuantity(), Color.RED);
                viewHolder.cart_item_count.setImageDrawable(textDrawable);

                //calculating the total price
                int total = 0;
                for (Cart mCart : cartOrder)
                    total += (Integer.parseInt(mCart.getPrice())) * (Integer.parseInt(mCart.getQuantity()));
                Locale locale = new Locale("en", "UG");
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

                totalPrice.setText(numberFormat.format(total));
            }
        };
        adapter.notifyDataSetChanged();//Refresh data if it has been changed
      //  recyclerView.setAdapter(adapter);

    }
*/
}