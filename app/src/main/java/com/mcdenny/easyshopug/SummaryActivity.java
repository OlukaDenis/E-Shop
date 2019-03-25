
package com.mcdenny.easyshopug;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcdenny.easyshopug.Common.Common;
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

public class SummaryActivity extends AppCompatActivity {

    TextView mName, mPhone, mAddress, mArea, totalPrice, mDeliveryMthd, tvGrandTotal, tvShippingFee;
    Button checkout;
    HashMap<String, List<Cart>> cartItemMap;

    String userKey;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference cart;

    List<Cart> cartOrderList = new ArrayList<>();

    FirebaseRecyclerAdapter<Cart, CartDetailViewHolder> adapter;
    FirebaseRecyclerAdapter<Address, AddressViewHolder> addressAdapter;

    private String mCustomerAddress;
    private String mCustomerArea;
    private String mCustomerName;
    private String mCustomerPhone;
    private int orderTotal;
    private ProgressDialog progressDialog;
    private int shipping_fee = 0, grand_total = 0;

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
                .setDefaultFontPath("fonts/MontserratRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_summary);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        totalPrice = (TextView) findViewById(R.id.tvTotal);
        mAddress = (TextView) findViewById(R.id.tvAddress);
        mArea = (TextView) findViewById(R.id.tvArea);
        mName = (TextView) findViewById(R.id.tvname);
        mPhone = (TextView) findViewById(R.id.tvPhone);
        checkout = (Button) findViewById(R.id.confirm);
        mDeliveryMthd = (TextView) findViewById(R.id.tv_dlvry_method);
        tvGrandTotal = (TextView) findViewById(R.id.tvGrandTotal);
        tvShippingFee = (TextView) findViewById(R.id.tvShippingFee);

        cartItemMap = new HashMap<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Placing Order");

        //initialise firebase
        database = FirebaseDatabase.getInstance();
        orders = database.getReference("Requests");
        cart = database.getReference("Cart");
        final String orderKey = orders.push().getKey();

        final android.app.AlertDialog waitingDialog = new SpotsDialog(SummaryActivity.this);
        waitingDialog.dismiss();

        mCustomerArea = Common.area;
        mCustomerAddress = Common.place;
        mCustomerName = Common.user_Current.getName();
        mCustomerPhone = Common.user_Current.getPhone();

        mAddress.setText(mCustomerAddress);
        mArea.setText(mCustomerArea);
        mPhone.setText(mCustomerPhone);
        mName.setText(mCustomerName);
        mDeliveryMthd.setText(Common.delivery_method);

        orderTotal = Common.cart_item_total;
        totalPrice.setText(Cons.Vals.CURRENCY + Util.formatNumber(String.valueOf(orderTotal)));

       cartOrderList = Common.Current_cart_list;

       //calculating the grand total
        if (Common.chosen_delivery_method == 1){
            shipping_fee += Common.standard_shipping_fees;
            tvShippingFee.setText(Cons.Vals.CURRENCY + Util.formatNumber(String.valueOf(shipping_fee)));
        }

        if(Common.chosen_delivery_method == 2){
            shipping_fee += 0;
            tvShippingFee.setText(Cons.Vals.CURRENCY + Util.formatNumber(String.valueOf(shipping_fee)));
        }

        //shipping_fee = Common.standard_shipping_fees;

        grand_total = orderTotal + shipping_fee;
        tvGrandTotal.setText(Cons.Vals.CURRENCY + Util.formatNumber(String.valueOf(grand_total)));


        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Submitting Order");
                progressDialog.show();
                final Requests requests = new Requests(
                       mCustomerName,
                        mCustomerPhone,
                        mCustomerAddress,
                        grand_total,
                        cartOrderList
                );
                //Sending the above data to firebase database

                orders.child(orderKey).setValue(requests).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            cart.child(Common.user_Current.getPhone()).removeValue();
                            Intent intent = new Intent(SummaryActivity.this, CartDetail.class);
                            startActivity(intent);
                            Toast.makeText(SummaryActivity.this, "Successfully placed your order.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(SummaryActivity.this, "Failed to place the order.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}