package com.mcdenny.easyshopug;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcdenny.easyshopug.Common.Common;

import java.util.Timer;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShippingAddress extends AppCompatActivity {
    private static Timer mTimer = new Timer();
    private static ProgressDialog dialog;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase addreess_table;
    EditText etArea, etPlace, etPhone;
    String add_area, add_place, add_phone;
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
                .setDefaultFontPath("fonts/MontserratRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_shipping_address);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        // final ProgressDialog mDialog = new ProgressDialog(this);
        // dialog.setMessage("loading");

        etArea = findViewById(R.id.address_area);
        etPlace = findViewById(R.id.address_place);
        etPhone = findViewById(R.id.address_phone);
        address_done = (Button) findViewById(R.id.btn_proceed);
        etPhone.setText(Common.user_Current.getPhone());

        //pointing to the root of database
        addreess_table = FirebaseDatabase.getInstance();
        mDatabaseReference = addreess_table.getReference("Addresses");

        
        address_done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                add_area = etArea.getText().toString();
                add_place = etPlace.getText().toString();
                add_phone = etPhone.getText().toString();

                //send the address to the common activity
                Common.area = add_area;
                Common.place = add_place;

                //checking whether the edit text is empty
                if (add_area.isEmpty()) {
                    etArea.setError("field can't be empty");
                    etArea.requestFocus();
                } else if (add_place.isEmpty()) {
                    etPlace.setError("field can't be empty");
                    etPlace.requestFocus();
                } else if (add_phone.isEmpty()) {
                    etPhone.setError("field can't be empty");
                    etPhone.requestFocus();
                } else {
                    if(ShippingAddress.this.getIntent().hasExtra(PICK_ADDRESS_DETAILS) &&
                            ShippingAddress.this.getIntent().getExtras().getBoolean(PICK_ADDRESS_DETAILS) ){
                        Intent intent = new Intent();
                        intent.putExtra("customerArea", add_area);
                        intent.putExtra("customerPlace", add_place);
                        intent.putExtra("customerPhone", add_phone);
                        ShippingAddress.this.setResult(RESULT_OK, intent);
                        ShippingAddress.this.finish();
                        return;
                    }
                }
            }
        });
    }

}
