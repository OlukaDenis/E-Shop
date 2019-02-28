package com.mcdenny.easyshopug;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcdenny.easyshopug.Common.Common;
import com.mcdenny.easyshopug.Model.Cart;
import com.mcdenny.easyshopug.ViewHolder.CartDetailViewHolder;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.mcdenny.easyshopug.ShippingAddress.PICK_ADDRESS_DETAILS;

public class CartDetail extends AppCompatActivity {
    TextView totalPrice, noCart;
    ImageView noCartImg;
    LinearLayout checkoutLayout;
    Button placeOrder;
    public int total;//cart total
    List<Cart> list = new ArrayList<>();
    HashMap<String, List<Cart>> cartItemMap;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference cart;
    public String cartItemKey;

    public String currentUserPhone;
    public String currentUserName;

    private static final int PICK_ADDRESS = 23;
    public static final String CUSTOMER_ADDRESS = "CUSTOMER_ADDRESS";
    public static final String CUSTOMER_CITY = "CUSTOMER_CITY";
    public static final String CUSTOMER_REGION = "CUSTOMER_REGION";
    public static final String TOTAL_AMOUNT = "TOTAL_AMOUNT";
    public static final String CURRENT_USER_PHONE = "CURRENT_USER_PHONE";
    public static final String CURRENT_USER_NAME = "CURRENT_USER_NAME";
    public static final String ITEMS_LIST = "ITEMS_LIST";

    private String mCustomerAddress;
    private String mCustomerCity;
    private String mCustomerRegion;
    //CartAdapter adapter;
    //FirebaseRecyclerAdapter<Cart, CartDetailViewHolder> adapter;
    android.app.AlertDialog waitingDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/QuicksandRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_cart);
        setTitle("Your Cart");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        cartItemMap = new HashMap<>();
        totalPrice = findViewById(R.id.total);
        placeOrder = findViewById(R.id.place_order_btn);
        noCart = findViewById(R.id.noCartItem);
        noCartImg = findViewById(R.id.noCartWaarning);
        checkoutLayout = findViewById(R.id.checkOutLayout);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startActivity(new Intent(CartDetail.this, ShippingAddress.class));
                //  viewAlertDialog();
                pickAddress();
            }
        });

        //initialise firebase
        database = FirebaseDatabase.getInstance();
        orders = database.getReference("Requests");
        cart = database.getReference("Cart");
        //cartKey = cart.getKey();

        currentUserPhone = Common.user_Current.getPhone();
        currentUserName = Common.user_Current.getName();

        recyclerView = findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //loadCartOrders();
        loadCartDetails();
    }

    private void pickAddress() {

        Intent intent = new Intent(this, ShippingAddress.class);
        intent.putExtra(PICK_ADDRESS_DETAILS, true);
        startActivityForResult(intent, PICK_ADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_ADDRESS && resultCode == RESULT_OK) {

            mCustomerAddress = data.getStringExtra("customerAddress");
            mCustomerCity = data.getStringExtra("customerCity");
            mCustomerRegion = data.getStringExtra("customerRegion");
            viewAlertDialog();
        } else {
            Toast.makeText(this, "No Customer address found!", Toast.LENGTH_SHORT).show();
        }
    }

    //method that is called when place order button is clicked
    private void viewAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartDetail.this);
        alertDialog.setTitle("Confirm your details");
        alertDialog.setMessage("Name: " + currentUserName +
                "\nContact: " + currentUserPhone +
                "\nAddress: " + mCustomerRegion + "," + mCustomerCity + "," + mCustomerAddress);


        alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final android.app.AlertDialog waitingDialog = new SpotsDialog(CartDetail.this);
                waitingDialog.show();
                Intent intent = new Intent(CartDetail.this, CheckoutActivity.class);
                intent.putExtra(CartDetail.CURRENT_USER_NAME, currentUserName);
                intent.putExtra(CartDetail.CURRENT_USER_PHONE, currentUserPhone);
                intent.putExtra(CartDetail.CUSTOMER_ADDRESS, mCustomerAddress);
                intent.putExtra(CartDetail.CUSTOMER_CITY, mCustomerCity);
                intent.putExtra(CartDetail.CUSTOMER_REGION, mCustomerRegion);
                intent.putExtra(CartDetail.ITEMS_LIST, (Serializable) list);
                intent.putExtra(CartDetail.TOTAL_AMOUNT, total);
                startActivity(intent);
                finish();
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

    private void loadCartDetails() {
        waitingDialog = new SpotsDialog(CartDetail.this);
        waitingDialog.show();
        cart.child(Common.user_Current.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                total = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cart mCartItem = snapshot.getValue(Cart.class);
                    Cart listCart = new Cart();
                    //cartItemMap.put(dataSnapshot.getKey(), mCartItem);
                    listCart.setName(mCartItem.getName());
                    listCart.setPrice(mCartItem.getPrice());
                    listCart.setImage(mCartItem.getImage());
                    listCart.setQuantity(mCartItem.getQuantity());
                    list.add(listCart);
                    cartItemMap.put(snapshot.getKey(), list);

                    //calculating the total price
                    total += (Integer.parseInt(mCartItem.getPrice())) * (Integer.parseInt(mCartItem.getQuantity()));
                    Locale locale = new Locale("en", "UG");
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                    String totals = String.valueOf(total);
                    totalPrice.setText(numberFormat.format(total));
                    //default view when the cart is empty
                    checkoutLayout.setVisibility(View.VISIBLE);
                    noCart.setVisibility(View.GONE);
                    noCartImg.setVisibility(View.GONE);
                    cartItemKey = snapshot.getKey();
                    RecyclerViewAdapter recycler = new RecyclerViewAdapter(getApplicationContext(), list);
                    recyclerView.setAdapter(recycler);
                    //recycler.notifyDataSetChanged();
                }
                waitingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //waitingDialog.dismiss();
            }
        });
    }

    //remove from cart
    private void removeCart(final String key) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Removing Product from Cart...");

        progressDialog.show();
        cart.child(Common.user_Current.getPhone()).child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CartDetail.this, "Removed item from cart.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(CartDetail.this, "Failed to remove from cart.", Toast.LENGTH_SHORT).show();
                }

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
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(CartDetail.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyHolder>{
        private Context context;
        private List<Cart> listCart;


        public RecyclerViewAdapter(Context context, List<Cart> listCart) {
            this.listCart = listCart;
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerViewAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout,parent,false);
            RecyclerViewAdapter.MyHolder myHolder = new RecyclerViewAdapter.MyHolder(view);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.MyHolder holder, final int position) {
            Cart data = listCart.get(position);
            holder.cart_item_name.setText(data.getName());
            holder.cart_item_price.setText(data.getPrice());
            Picasso.with(context).load(data.getImage()).into(holder.cart_item_image);
            holder.cart_number_button.setNumber(data.getQuantity());
            holder.remove_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeCart(cartItemKey);//removing the item frome the database
                    listCart.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, listCart.size());
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listCart.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView cart_item_name, cart_item_price;
            ImageView cart_item_image;
            ElegantNumberButton cart_number_button;
            Button remove_cart;

            public MyHolder(View itemView) {
                super(itemView);
                cart_item_name = (TextView) itemView.findViewById(R.id.cart_item_name);
                cart_item_price = (TextView) itemView.findViewById(R.id.cart_item_price);
                cart_item_image = (ImageView) itemView.findViewById(R.id.cart_image);
                cart_number_button = (ElegantNumberButton) itemView.findViewById(R.id.enb_cart_add_subtract);
                remove_cart = (Button) itemView.findViewById(R.id.remove_cart_item);
            }

        }
    }
}



