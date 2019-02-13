package com.mcdenny.easyshopug;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mcdenny.easyshopug.Common.Common;
import com.mcdenny.easyshopug.Interface.ItemClickListener;
import com.mcdenny.easyshopug.Model.Category;
import com.mcdenny.easyshopug.Service.ListenOrder;
import com.mcdenny.easyshopug.ViewHolder.MenuViewHolder;
import com.mcdenny.easyshopug.ViewHolder.RecyclerGrid;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;
import mehdi.sakout.fancybuttons.FancyButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseDatabase database;
    DatabaseReference category;
    FirebaseStorage storage;
    StorageReference storageReference;

    RecyclerView recyclerMenu;

    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    //for the alert dialog
    MaterialEditText edtName;
    FancyButton btnSelectImage, btnUploadImage;
    Category newCategory;
    Uri saveUri;

    private final int PICK_IMAGE_REQUEST = 71;
    public RecyclerView rView;
    private GridLayoutManager lLayout;
    private RecyclerGrid rcAdapter;
    TextView usrFullName;
    private boolean allowBackButtonExit = false;

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
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        toolbar.setTitle("Quick Duuka");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(this);

        //showing the users full name on the header
        View headerView = navigationView.getHeaderView(0);
        usrFullName = headerView.findViewById(R.id.userFullName);
        usrFullName.setText(Common.user_Current.getName());

        //initialising the firebase database
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Loading the product details to the menu
        recyclerMenu = findViewById(R.id.recycler_menu);
        recyclerMenu.setHasFixedSize(false);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerMenu.setLayoutManager(layoutManager);

        if (Common.isNetworkAvailable(getApplicationContext())){
            loadMenu();
            Intent service = new Intent(HomeActivity.this, ListenOrder.class);
            startService(service);
        }
        else {
            Toast.makeText(HomeActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void loadMenu(){
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class,
                R.layout.product_category_list,
                MenuViewHolder.class,
                category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, final Category model, int position) {
                viewHolder.productName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getLink()).into(viewHolder.productImage);
                final String titleName = model.getName();

                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClicked) {
                        Intent productList = new Intent(HomeActivity.this, ProductList.class);
                        //Getting the category key id and sending it to the product list activity
                        productList.putExtra("CategoryID", adapter.getRef(position).getKey());
                        Bundle bundle = new Bundle();
                        bundle.putString("Title_key", titleName);
                        productList.putExtras(bundle);
                        startActivity(productList);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();//Refresh data if it has been changed
        recyclerMenu.setAdapter(adapter);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.home) {
            // Home index activity
        }
        else if(id == R.id.order_history){
            startActivity(new Intent(HomeActivity.this, OrderStatus.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(getApplicationContext(), AboutUs.class));

        } else if (id == R.id.nav_share) {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");

            String shareBodyText = "Quick Duuka :" +
                    "Check out on this application. you can download it from " +
                    "https://play.google.com/store/apps/details?id=com.mcdenny.easyshopug";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Quick Duuka");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        } else if (id == R.id.nav_logout) {
            //delete remember password
            Paper.book().destroy();

            //firebaseAuth.signOut();
            intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Toast.makeText(HomeActivity.this, "Thanks for visiting!\nSee you soon.", Toast.LENGTH_SHORT)
                    .show();
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout1);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout1);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            //super.onBackPressed();
            if (!allowBackButtonExit) {
                allowBackButtonExit = true;
                Toast.makeText(this, "Press again to close app.", Toast.LENGTH_SHORT).show();
                CountDownTimer timer = new CountDownTimer(1000, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        allowBackButtonExit = false;
                    }
                }.start();
            } else {
                System.exit(0);
            }
        }
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
            Intent cartIntent = new Intent(HomeActivity.this, CartDetail.class);
            startActivity(cartIntent);
        }
        if (id == R.id.nav_settings){
            Intent settingIntent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(settingIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
