package com.mcdenny.easyshopug;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcdenny.easyshopug.Model.Address;

import java.util.Timer;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static java.lang.Boolean.getBoolean;

public class ShippingAddress extends AppCompatActivity {
    private static Timer mTimer = new Timer();
    private static ProgressDialog dialog;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase addreess_table;
    EditText etAddress, etCity, etRegion;
    String address, city, region;
    Button address_done;

    
    public static final String PICK_ADDRESS_DETAILS = "addressDetails";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/QuicksandLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_shipping_address);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        // final ProgressDialog mDialog = new ProgressDialog(this);
        // dialog.setMessage("loading");

        etAddress = findViewById(R.id.address_area);
        etCity = findViewById(R.id.address_city);
        etRegion = findViewById(R.id.address_region);
        address_done = findViewById(R.id.done);

        //pointing to the root of database
        addreess_table = FirebaseDatabase.getInstance();
        mDatabaseReference = addreess_table.getReference("Addresses");

        
        address_done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                address = etAddress.getText().toString();
                city = etCity.getText().toString();
                region = etRegion.getText().toString();

                //checking whether the edit text is empty
                if (address.isEmpty()) {
                    etAddress.setError("field can't be empty");
                    etAddress.requestFocus();
                } else if (city.isEmpty()) {
                    etCity.setError("field can't be empty");
                    etCity.requestFocus();
                } else if (region.isEmpty()) {
                    etRegion.setError("field can't be empty");
                    etRegion.requestFocus();
                } else {
                    if(ShippingAddress.this.getIntent().hasExtra(PICK_ADDRESS_DETAILS) &&
                            ShippingAddress.this.getIntent().getExtras().getBoolean(PICK_ADDRESS_DETAILS) ){
                        Intent intent = new Intent();
                        intent.putExtra("customerAddress", address);
                        intent.putExtra("customerCity", city);
                        intent.putExtra("customerRegion", region);
                        ShippingAddress.this.setResult(RESULT_OK, intent);
                        ShippingAddress.this.finish();
                        return;
                    }
                }
            }
        });
    }

}
