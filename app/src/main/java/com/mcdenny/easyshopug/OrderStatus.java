package com.mcdenny.easyshopug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcdenny.easyshopug.Common.Common;
import com.mcdenny.easyshopug.Interface.ItemClickListener;
import com.mcdenny.easyshopug.Model.Requests;
import com.mcdenny.easyshopug.Utils.Util;
import com.mcdenny.easyshopug.ViewHolder.OrderViewHolder;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatus extends AppCompatActivity {
    public RecyclerView orderRecylerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    FirebaseRecyclerAdapter<Requests, OrderViewHolder> adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MontserratRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_order_status);
        setTitle("Your Orders");

        //init firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        orderRecylerView = findViewById(R.id.list_orders);
        orderRecylerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        orderRecylerView.setLayoutManager(layoutManager);

        loadOrderStatus(Util.cleanEmailKey(Common.current_user_email));

    }

    private void loadOrderStatus(final String phone) {
        adapter = new FirebaseRecyclerAdapter<Requests, OrderViewHolder>(
                Requests.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("contact").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Requests model, final int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderPhone.setText(model.getContact());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderReceived.setText(Common.convertCodeToReceived(model.getRecieved()));
                String transporter = model.getTransporter();
                if(transporter.equals("0")){
                    viewHolder.txtOrderTransporter.setText("Transporter not assigned");
                }else {
                    viewHolder.txtOrderTransporter.setText(model.getTransporter());
                }

                viewHolder.editOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editOrder(adapter.getRef(position).getKey(), model);
                    }
                });

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClicked) {

                    }
                });
            }
        };
        orderRecylerView.setAdapter(adapter);
    }

    private void editOrder(final String key, final Requests model) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Have you received the order?");
        alertDialog.setMessage("Dear customer, please click YES upon receiving your order. Thank you");

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                model.setRecieved("Order received successfully");

                requests.child(key).setValue(model); //add to update size
                adapter.notifyDataSetChanged();
            }
        });
        alertDialog.show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

        return super.onOptionsItemSelected(item);
    }
}
