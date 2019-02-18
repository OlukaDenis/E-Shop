package com.mcdenny.easyshopug;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcdenny.easyshopug.Common.Common;
import com.mcdenny.easyshopug.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.security.Permissions;

import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignupActivity extends AppCompatActivity {
    MaterialEditText usrphone, usrname, usrpassword;
    Button signup;
    TextView terms_service, login;
    DatabaseReference user_table;
    FirebaseDatabase database;

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
        setContentView(R.layout.activity_signup);

        usrphone = (MaterialEditText) findViewById(R.id.phone);
        usrname = (MaterialEditText) findViewById(R.id.name);
        usrpassword = (MaterialEditText) findViewById(R.id.password);
        signup = (Button) findViewById(R.id.signup);
        terms_service = findViewById(R.id.terms_of_service);
        login = findViewById(R.id.Login_user);

        SpannableString content = new SpannableString("Terms of service");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        terms_service.setText(content);

        //initializing firebase database
         database = FirebaseDatabase.getInstance();
         user_table = database.getReference("User");

        terms_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TermsActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isNetworkAvailable(getBaseContext())){
                    //checking whether the edit text is empty
                    if(usrphone.getText().toString().isEmpty()) {
                        usrphone.setError("Invalid Phone number");
                        usrphone.requestFocus();
                    }
                    else if(usrname.getText().toString().isEmpty()) {
                        usrname.setError("Invalid Name");
                        usrname.requestFocus();
                    }
                    else if(usrpassword.getText().toString().isEmpty()){
                        usrpassword.setError("Invalid Password");
                        usrpassword.requestFocus();
                    }
                    else {
                        final AlertDialog waitingDialog = new SpotsDialog(SignupActivity.this);
                        waitingDialog.show();

                        user_table.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //check if the user exists
                                if (dataSnapshot.child(usrphone.getText().toString()).exists()) {
                                    waitingDialog.dismiss();
                                    Toast.makeText(SignupActivity.this, "User phone already exists", Toast.LENGTH_SHORT).show();
                                }

                                //if user does not exist, creates new
                                else {
                                    waitingDialog.dismiss();
                                    User user = new User(usrname.getText().toString(), usrpassword.getText().toString());
                                    user_table.child(usrphone.getText().toString()).setValue(user);
                                    Toast.makeText(SignupActivity.this, "Succesfully registered!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }//end of else
                }
                else {
                    Toast.makeText(SignupActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        if (com.mcdenny.easyshopug.Utils.Permissions.isUserVerified(this)) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }// end of onCreate method
    @Override
    protected void onResume() {
        super.onResume();

        if (com.mcdenny.easyshopug.Utils.Permissions.isUserVerified(this)) {
            finish();
        }
    }

}
