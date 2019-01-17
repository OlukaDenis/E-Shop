package com.mcdenny.easyshopug;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mcdenny.easyshopug.Common.Common;
import com.mcdenny.easyshopug.Interface.ItemClickListener;
import com.mcdenny.easyshopug.Model.Product;
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
    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;

    String categoryId = "";
    String toolbarTitle ;

    //search functionality
    FirebaseRecyclerAdapter<Product,ProductViewHolder> searchAdapter;
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
                .setDefaultFontPath("fonts/QuicksandLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        //firebase init
        database = FirebaseDatabase.getInstance();
        productItemList = database.getReference("Products");

        recyclerView = findViewById(R.id.recycler_product);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        progressDialog = new ProgressDialog(this);

        //getting the intent from the home activity
        if(getIntent() != null){
            categoryId = getIntent().getStringExtra("CategoryID");
        }
        if(!categoryId.isEmpty() && categoryId != null) {
            if (Common.isNetworkAvailable(getBaseContext())){
                loadProductItemList(categoryId);
            }
            else {
                Toast.makeText(ProductList.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        //search products
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Search Product");
        
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //when user searches something, we change suggestions
                List<String> suggest = new ArrayList<String>();
                for(String search:suggestList)
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when search bar is closed, restore original suggest adapter
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search is done, show results of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class,
                R.layout.product_list_layout,
                ProductViewHolder.class,
                productItemList.orderByChild("Name").equalTo(text.toString())//compare the names
        ) {
            @Override
            protected void populateViewHolder(ProductViewHolder viewHolder, Product model, int position) {
                progressDialog.setTitle("Loading "+toolbarTitle);
                progressDialog.show();
                viewHolder.productItemName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.productItemImage);
                //the name on the toolbar
                Bundle bundle = getIntent().getExtras();
                toolbarTitle = bundle.getString("Title_key");
                setTitle(toolbarTitle);
                progressDialog.dismiss();

                final Product productItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClicked) {
                        Intent productDetail = new Intent(ProductList.this, ProductDetail.class);
                        //Getting the category key id and sending it to the product list activity
                        productDetail.putExtra("ProductListID", searchAdapter.getRef(position).getKey());
                        startActivity(productDetail);
                    }
                });
            }
        };

        recyclerView.setAdapter(searchAdapter);//set adapter for recycler view is search result
    }

    private void loadSuggest() {
        productItemList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Product product = postSnapshot.getValue(Product.class);
                            suggestList.add(product.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadProductItemList(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class,
                R.layout.product_list_layout,
                ProductViewHolder.class,
                productItemList.orderByChild("menuid").equalTo(categoryId)//getting product items where menuID equals to category id
        ) {
            @Override
            protected void populateViewHolder(ProductViewHolder viewHolder, Product model, int position) {
                viewHolder.productItemName.setText(model.getName());
                Locale locale = new Locale("en", "UG");
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                int thePrice = (Integer.parseInt(model.getPrice()));
                viewHolder.productItemPrice.setText(numberFormat.format(thePrice));
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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
        }
        if (id == R.id.view_cart) {
            Intent cartIntent = new Intent(ProductList.this, CartDetail.class);
            startActivity(cartIntent);
        }
        if (id == R.id.nav_settings){
            Intent settingIntent = new Intent(ProductList.this, SettingsActivity.class);
            startActivity(settingIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
