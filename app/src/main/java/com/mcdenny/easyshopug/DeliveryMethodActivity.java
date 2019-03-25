package com.mcdenny.easyshopug;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mcdenny.easyshopug.Common.Common;
import com.mcdenny.easyshopug.Utils.Cons;
import com.mcdenny.easyshopug.Utils.Util;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DeliveryMethodActivity extends AppCompatActivity {
    private Button btnProceed;
    private TextView tvShippingFee;
    //private RadioGroup rgShipping, rgPickup;
    private RadioButton rbShipping, rbPickup;
    private String delivery_method = " ";
    private int shipping_true, pickup_true;

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
        setContentView(R.layout.activity_delivery_method);
        setTitle("Delivery Method");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //initialize views
        btnProceed = (Button) findViewById(R.id.btn_proceed_summary);
        tvShippingFee =(TextView) findViewById(R.id.tv_shipping_fee);
        //rgPickup = (RadioGroup) findViewById(R.id.rg_pickup);
        //rgShipping = (RadioGroup) findViewById(R.id.rg_shipping);
        rbShipping = (RadioButton) findViewById(R.id.rb_shipping);
        rbPickup = (RadioButton) findViewById(R.id.rb_pickup);

        tvShippingFee.setText(Cons.Vals.CURRENCY + Util.formatNumber("1000"));

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent summaryIntent = new Intent(DeliveryMethodActivity.this, SummaryActivity.class);
                startActivity(summaryIntent);
            }
        });
        //pickDeliveryMethod();
    }

    public void chooseDelivery(View view) {
        final boolean mShipping = rbShipping.isChecked();
        final boolean mPickup = rbPickup.isChecked();
        if(mShipping){
            delivery_method = getString(R.string.shipping_delivery);
            rbPickup.setChecked(false);
            shipping_true = 1;
            Common.chosen_delivery_method = shipping_true;
            //rgPickup.clearCheck();
        }
        if(mPickup){
            delivery_method = getString(R.string.pickup_delivery);
            //rgShipping.clearCheck();
            rbShipping.setChecked(false);
            pickup_true = 2;
            Common.chosen_delivery_method = pickup_true;
        }

        //store the user's selection to the common activity
        Common.delivery_method = delivery_method;
    }
}
