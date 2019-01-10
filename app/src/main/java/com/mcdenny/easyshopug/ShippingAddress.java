package com.mcdenny.easyshopug;

import android.app.ProgressDialog;
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

public class ShippingAddress extends AppCompatActivity {
    private static Timer mTimer = new Timer();
    private static ProgressDialog dialog;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase addreess_table;
    EditText nameEditText, areaEditText, districtEditText, countyEditText, phoneEditText, divisionEditText;
    String name, area, district, county, phone, division;
    Button address_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        // final ProgressDialog mDialog = new ProgressDialog(this);
        // dialog.setMessage("loading");

        nameEditText = findViewById(R.id.address_name);
        areaEditText = findViewById(R.id.address_area);
        districtEditText = findViewById(R.id.address_district);
        countyEditText = findViewById(R.id.address_county);
        phoneEditText = findViewById(R.id.address_phone);
        divisionEditText = findViewById(R.id.address_division);
        address_done = findViewById(R.id.done);

        //pointing to the root of database
        addreess_table = FirebaseDatabase.getInstance();
        mDatabaseReference = addreess_table.getReference("Addresses");

        address_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mDialog.show();

                name = nameEditText.getText().toString();
                area = areaEditText.getText().toString();
                district = districtEditText.getText().toString();
                county = countyEditText.getText().toString();
                phone = phoneEditText.getText().toString();
                division = divisionEditText.getText().toString();

                //checking whether the edit text is empty
                if (name.isEmpty()) {
                    nameEditText.setError("field can't be empty");
                    nameEditText.requestFocus();
                } else if (area.isEmpty()) {
                    areaEditText.setError("field can't be empty");
                    areaEditText.requestFocus();
                } else if (district.isEmpty()) {
                    districtEditText.setError("field can't be empty");
                    districtEditText.requestFocus();
                } else if (county.isEmpty()) {
                    countyEditText.setError("field can't be empty");
                    countyEditText.requestFocus();
                } else if (phone.isEmpty() || phone.length() < 10) {
                    phoneEditText.setError("Invalid number");
                    phoneEditText.requestFocus();
                } else if (division.isEmpty()) {
                    divisionEditText.setError("field can't be empty");
                    divisionEditText.requestFocus();
                } else {

                    dialog = ProgressDialogWithTimeout.show(ShippingAddress.this, null, "processing.....");
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //check if the user exists
                            Address address = new Address(name, area, district, county, phone, division);
                            if (dataSnapshot.child(phone).exists()) {
                                //dialog.dismiss();

                                phoneEditText.setError("phone number already exists");
                                phoneEditText.requestFocus();
                            }
                                if (phone.startsWith("0") && phone.length() == 10 || phone.startsWith("256") && phone.length() == 12 ) {

                                    mDatabaseReference.child(phone).setValue(address);
                                    //Toast.makeText(getApplicationContext(), "Succeeded", Toast.LENGTH_SHORT).show();
                                    nameEditText.getText().clear();
                                    areaEditText.getText().clear();
                                    countyEditText.getText().clear();
                                    districtEditText.getText().clear();
                                    divisionEditText.getText().clear();
                                    phoneEditText.getText().clear();

                                    startActivity(new Intent(getApplicationContext(), CheckoutActivity.class));

                                } else {
                                    phoneEditText.setError("invalid phone number");
                                    phoneEditText.requestFocus();
                                }
                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }

}
