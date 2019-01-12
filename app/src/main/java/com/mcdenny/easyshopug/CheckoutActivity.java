package com.mcdenny.easyshopug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcdenny.easyshopug.Model.Address;
import com.mcdenny.easyshopug.Model.Cart;
import com.mcdenny.easyshopug.Model.Requests;
import com.mcdenny.easyshopug.Utils.Cons;
import com.mcdenny.easyshopug.Utils.Util;
import com.mcdenny.easyshopug.ViewHolder.AddressViewHolder;
import com.mcdenny.easyshopug.ViewHolder.CartDetailViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckoutActivity extends AppCompatActivity {

    TextView mName, mPhone, mAddress, mCity, totalPrice;
    Button checkout;
    HashMap<String, List<Cart>> cartItemMap;

    String userKey;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference cart;

    List<Cart> cartOrder = new ArrayList<>();

    FirebaseRecyclerAdapter<Cart, CartDetailViewHolder> adapter;
    FirebaseRecyclerAdapter<Address, AddressViewHolder> addressAdapter;

    private String mCustomerAddress;
    private String mCustomerCity;
    private String mCustomerName;
    private String mCustomerPhone;
    private int orderTotal;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/QuicksandLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_checkout);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        totalPrice = (TextView) findViewById(R.id.tvTotal);
        mAddress = (TextView) findViewById(R.id.tvAddress);
        mCity = (TextView) findViewById(R.id.tvCity);
        mName = (TextView) findViewById(R.id.tvname);
        mPhone = (TextView) findViewById(R.id.tvPhone);
        checkout = (Button) findViewById(R.id.confirm);

        cartItemMap = new HashMap<>();

        //initialise firebase
        database = FirebaseDatabase.getInstance();
        orders = database.getReference("Requests");
        cart = database.getReference("Cart");

        final android.app.AlertDialog waitingDialog = new SpotsDialog(CheckoutActivity.this);
        waitingDialog.dismiss();

        if (getIntent().hasExtra(CartDetail.CUSTOMER_ADDRESS)) {
            mCustomerAddress = getIntent().getStringExtra(CartDetail.CUSTOMER_ADDRESS);
            mAddress.setText(mCustomerAddress);
        }
        if (getIntent().hasExtra(CartDetail.CUSTOMER_CITY)) {
            mCustomerCity = getIntent().getStringExtra(CartDetail.CUSTOMER_CITY);
            mCity.setText(mCustomerCity);
        }
        if (getIntent().hasExtra(CartDetail.CURRENT_USER_PHONE)) {
            mCustomerPhone = getIntent().getStringExtra(CartDetail.CURRENT_USER_PHONE);
            mPhone.setText(mCustomerPhone);
        }
        if (getIntent().hasExtra(CartDetail.CURRENT_USER_NAME)) {
            mCustomerName = getIntent().getStringExtra(CartDetail.CURRENT_USER_NAME);
            mName.setText(mCustomerName);
        }
        if (getIntent().hasExtra(CartDetail.TOTAL_AMOUNT)) {
            orderTotal = getIntent().getIntExtra(CartDetail.TOTAL_AMOUNT, 0);
            totalPrice.setText(Cons.Vals.CURRENCY + Util.formatNumber(String.valueOf(orderTotal)));
        }
        if (getIntent().hasExtra(CartDetail.ITEMS_LIST)) {
            cartOrder = ( List<Cart>) getIntent().getSerializableExtra(CartDetail.ITEMS_LIST);
        }

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Requests requests = new Requests(
                       mCustomerName,
                        mCustomerPhone,
                        mCustomerAddress,
                        orderTotal,
                        cartOrder
                );
                //Sending the above data to firebase database
                orders.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(requests);
                Intent intent = new Intent(CheckoutActivity.this, CartDetail.class);
                startActivity(intent);
                finish();
                /*
                cart.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userType = snapshot.child("phone").getValue().toString();
                            if (userType.equals(mCustomerPhone)) {

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                    */


                /* TODO Add mobile money integration
                 */
                //TODO Delete from Cart
            }
        });
    }

    /*
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
    }*/

}