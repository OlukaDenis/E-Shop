package com.mcdenny.easyshopug;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcdenny.easyshopug.Common.Common;

import java.util.Timer;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShippingAddress extends AppCompatActivity implements View.OnTouchListener,
        AdapterView.OnItemClickListener {
    private static Timer mTimer = new Timer();
    private static ProgressDialog dialog;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase addreess_table;
    EditText etArea, etPlace, etPhone;
    String add_area, add_place, add_phone;
    Button address_done;

    private ListPopupWindow areaLPW, placeLPW;
    private String[] areaList, placeList;
    boolean listChooser = true;

    
    public static final String PICK_ADDRESS_DETAILS = "addressDetails";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressLint("ClickableViewAccessibility")
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
        etArea.setOnTouchListener(this);
        areaList = new String[] { "Ndejje Lady Irene", "Ndejje Main Campus", "Ndejje Trading Center" };
        areaLPW = new ListPopupWindow(this);
        areaLPW.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, areaList
        ));
        areaLPW.setAnchorView(etArea);
        areaLPW.setModal(true);
        areaLPW.setOnItemClickListener(this);
        etArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                areaLPW.show();
                return false;
            }
        });

        etPlace = findViewById(R.id.address_place);
        etPlace.setOnTouchListener(this);
        placeList = new String[] { "Noah\'s Ark", "Victoria Hall", "Muteesa Ladies", "Kyabazinga Hall",
                "Yokaana Hall", "Sports Complex", "Science Complex", "Kape Villa", "Dungu Hostels"};
        placeLPW = new ListPopupWindow(this);
        placeLPW.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, placeList
        ));
        placeLPW.setAnchorView(etPlace);
        placeLPW.setModal(true);
        placeLPW.setOnItemClickListener(this);
        etPlace.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                placeLPW.show();
                return false;
            }
        });

        etPhone = findViewById(R.id.address_phone);
        address_done = (Button) findViewById(R.id.btn_proceed);
        etPhone.setText(Common.current_user_phone);

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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      try {
          if(listChooser){
              String item = areaList[position];
              etArea.setText(item);
              areaLPW.dismiss();
              listChooser = false;
          }
          else {
              String placeItem = placeList[position];
              etPlace.setText(placeItem);
              placeLPW.dismiss();
              listChooser = true;
          }
      } catch (Exception ex){
          ex.printStackTrace();
      }
      catch (Throwable e) {
          e.printStackTrace();
      }



    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int DRAWABLE_RIGHT = 2;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() >= (v.getWidth() - ((EditText) v)
                    .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                areaLPW.show();
                return true;
            }
        }
        return false;
    }


}
