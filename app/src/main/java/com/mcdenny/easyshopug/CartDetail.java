package com.mcdenny.easyshopug;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcdenny.easyshopug.Common.Common;
import com.mcdenny.easyshopug.Model.Cart;
import com.mcdenny.easyshopug.Model.Requests;
import com.mcdenny.easyshopug.ViewHolder.CartDetailViewHolder;
import com.mcdenny.easyshopug.adapters.RecyclerViewAdapter;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CartDetail extends AppCompatActivity {
   TextView totalPrice;
    Button placeOrder;
    int total;//cart total
    List<Cart> list;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private HashMap<String, Cart> cartItemMap;

    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference cart;
    String cartKey;
    public  String currentUser;

    List<Cart> cartOrder = new ArrayList<>();
    //CartAdapter adapter;
    FirebaseRecyclerAdapter<Cart, CartDetailViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle("Your Cart");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        cartItemMap = new HashMap<>();
        totalPrice = findViewById(R.id.total);
        placeOrder = findViewById(R.id.place_order_btn);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(CartDetail.this, ShippingAddress.class));
              //  viewAlertDialog();
            }
        });

        //initialise firebase
        database = FirebaseDatabase.getInstance();
        orders = database.getReference("Requests");
        cart = database.getReference("Cart");
        cartKey = cart.getKey();

        currentUser = Common.user_Current.getPhone();

        recyclerView = findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        //loadCartOrders();
        loadCartDetails();

    }
/**
    //method that is called when place order button is clicked
    private void viewAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartDetail.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your address: ");

        final EditText editAddress = new EditText(CartDetail.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        );
        editAddress.setLayoutParams(layoutParams);
        alertDialog.setView(editAddress);//This adds the edit text to alertdialog
        alertDialog.setIcon(R.drawable.ic_cart);

        alertDialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Creating a new request
                Requests requests = new Requests(
                        Common.user_Current.getName(),
                        Common.user_Current.getPhone(),
                        editAddress.getText().toString(),
                        totalPrice.getText().toString(),
                        cartOrder
                );

                //Sending the above data to firebase database
                orders.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(requests);

                //Deletes the order cart
                Toast.makeText(getBaseContext(), "Order successfuly submitted", Toast.LENGTH_LONG).show();
                finish();

                cart.setValue(null);
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }
    */

    private void loadCartDetails(){
        //cartItemMap.clear();
        cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                total =0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userType=snapshot.child("phone").getValue().toString();
                    if (userType.equals(currentUser)){
                        Cart mCartItem = snapshot.getValue(Cart.class);
                        Cart listCart = new Cart();
                        //cartItemMap.put(dataSnapshot.getKey(), mCartItem);
                        listCart.setName(mCartItem.getName());
                        listCart.setPrice(mCartItem.getPrice());
                        listCart.setQuantity(mCartItem.getQuantity());
                        list.add(listCart);

                        //calculating the total price
                        total += (Integer.parseInt(mCartItem.getPrice()))*(Integer.parseInt(mCartItem.getQuantity()));
                        Locale locale = new Locale("en", "UG");
                        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                        String totals = String.valueOf(total);
                        totalPrice.setText(numberFormat.format(total));

                        RecyclerViewAdapter recycler = new RecyclerViewAdapter(list);
                        recyclerView.setAdapter(recycler);
                    }
                    else {
                        Toast.makeText(CartDetail.this, "Nothing in your Cart", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Connect with me on facebook via: www.facebook.com/denislucaz");
            startActivity(Intent.createChooser(shareIntent, "Send Invite Via"));
            return true;
        } else if(id == R.id.nav_settings){
            startActivity(new Intent(CartDetail.this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
