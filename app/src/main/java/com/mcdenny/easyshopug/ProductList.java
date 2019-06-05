package com.mcdenny.easyshopug;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mcdenny.easyshopug.Common.Common;
import com.mcdenny.easyshopug.Interface.ItemClickListener;
import com.mcdenny.easyshopug.Model.Product;
import com.mcdenny.easyshopug.Utils.Cons;
import com.mcdenny.easyshopug.Utils.Util;
import com.mcdenny.easyshopug.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference productItemList;
    DatabaseReference favoriteItem;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;

    String categoryId = "";
    String toolbarTitle;

    //search functionality
    FirebaseRecyclerAdapter<Product, ProductViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    private ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set the fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MontserratRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_ic);
        //customize font
        //changeToolbarFont(findViewById(R.id.app_bar_layout), this);

        //firebase init
        database = FirebaseDatabase.getInstance();
        productItemList = database.getReference("Products");
        favoriteItem = database.getReference("Favorites");

        recyclerView = findViewById(R.id.recycler_product);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        progressDialog = new ProgressDialog(this);

        //getting the intent from the home activity
        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryID");
        }
        if (!categoryId.isEmpty() && categoryId != null) {
            if (Common.isNetworkAvailable(getBaseContext())) {
                loadProductItemList(categoryId);
            } else {
                Toast.makeText(ProductList.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    private void loadProductItemList(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class,
                R.layout.product_list_layout,
                ProductViewHolder.class,
                productItemList.orderByChild("menuid").equalTo(categoryId)//getting product items where menuID equals to category id

        ) {
            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, final Product model, final int position) {
                String available = model.getAvailable();
                //  if (available.equals("1"))
                viewHolder.productItemName.setText(model.getName());
                int thePrice = (Integer.parseInt(model.getPrice()));
                viewHolder.productItemPrice.setText(Cons.Vals.CURRENCY + Util.formatNumber(String.valueOf(thePrice)));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.productItemImage);

                //the name on the toolbar
                Bundle bundle = getIntent().getExtras();
                toolbarTitle = bundle.getString("Title_key");
                setTitle(toolbarTitle);

                final Product productItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClicked) {
                        Intent productDetail = new Intent(ProductList.this, ProductDetail.class);
                        //Getting the category key id and sending it to the product list activity
                        productDetail.putExtra("ProductListID", adapter.getRef(position).getKey());
                        startActivity(productDetail);
                    }
                });
            }
        };

        //Setting the adapter
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Connect with me on facebook via: www.facebook.com/denislucaz");
            startActivity(Intent.createChooser(shareIntent, "Send Invite Via"));
            return true;
        }
        if (id == R.id.view_cart) {
            Intent cartIntent = new Intent(ProductList.this, CartDetail.class);
            startActivity(cartIntent);
        }
        if (id == R.id.nav_settings) {
            Intent settingIntent = new Intent(ProductList.this, SettingsActivity.class);
            startActivity(settingIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    //customize toolbar font
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void changeToolbarFont(Toolbar toolbar, Activity context) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    applyFont(tv, context);
                }
            }
        }
    }

    private static void applyFont(TextView tv, Activity context) {
        tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/montserratRegular"));
    }
}
