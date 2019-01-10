package com.mcdenny.easyshopug;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    TextView check_update, about_us, guide, invite_text,shipping_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        check_update = findViewById(R.id.update);
        about_us = findViewById(R.id.about);
        invite_text = findViewById(R.id.invite);
        shipping_address = findViewById(R.id.shipping_address);

        shipping_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,ShippingAddress.class));
            }
        });
        check_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri playstoreuri1 = Uri.parse("market://details?id=" + getPackageName());
                    //or you can add
                    //var playstoreuri:Uri=Uri.parse("market://details?id=com.quickDuuka")
                    Intent playstoreIntent1 = new Intent(Intent.ACTION_VIEW, playstoreuri1);
                    startActivity(playstoreIntent1);

                    //it generate exception when devices do not have playstore
                } catch (Exception E) {
                    Uri playstoreuri2 = Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName());

                    Intent playstoreIntent2 = new Intent(Intent.ACTION_VIEW, playstoreuri2);
                    startActivity(playstoreIntent2);

                }
            }
        });
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AboutUs.class));
            }
        });

        invite_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Connect with me on facebook via: www.facebook.com/denislucaz");
                startActivity(Intent.createChooser(shareIntent, "Invite Via"));
            }
        });
    }
}
