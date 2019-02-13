package com.mcdenny.easyshopug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcdenny.easyshopug.Common.Common;
import com.mcdenny.easyshopug.Model.User;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    Button signin, signup;

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

        setContentView(R.layout.activity_main);
        //init paper
        Paper.init(this);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        //Check whether user and password exists
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PASSWORD_KEY);
        if(user!=null && pwd != null){
            if (!user.isEmpty() && !pwd.isEmpty()){
                login(user,pwd);
            }
        }
    }

    //signs in the user automatically
    private void login(final String phone, final String pwd) {
        final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
        if (Common.isNetworkAvailable(getBaseContext())){
            //initializing firebase database
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference user_table = database.getReference("User");

            //setting a dialog to tell the user to wait
            waitingDialog.show();

            user_table.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //checking if the user exists in the database
                    if (dataSnapshot.child(phone).exists()) {

                        //getting the users information
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPassword().equals(pwd)) {
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Common.user_Current = user;//the user details are stored in user_current variable
                            startActivity(intent);
                            finish();//stops the login activity
                        } else {
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //if user doesn't exist
                    else {
                        Toast.makeText(MainActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            waitingDialog.show();
            Toast.makeText(MainActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

    }
}
