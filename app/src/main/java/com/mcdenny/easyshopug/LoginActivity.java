package com.mcdenny.easyshopug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button login;
    TextView create, forgot_password;
    LinearLayout login_layout;
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
        setContentView(R.layout.activity_login);
        //getting the reference
        username = (MaterialEditText) findViewById(R.id.phone);
        password = (MaterialEditText) findViewById(R.id.password);
        login = findViewById(R.id.login);
        create = findViewById(R.id.dont_have_account);
        forgot_password = findViewById(R.id.forgot_password);
        login_layout = findViewById(R.id.login_layout);

        //initializing firebase database
         database = FirebaseDatabase.getInstance();
         user_table = database.getReference("User");

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               final AlertDialog waitingDialog = new SpotsDialog(LoginActivity.this);
               //Check for network connection
               if (Common.isNetworkAvailable(getBaseContext())){
                   //Save user and password
                   Paper.book().write(Common.USER_KEY, username.getText().toString());
                   Paper.book().write(Common.PASSWORD_KEY, password.getText().toString());
                   //checking whether the edit text is empty
                   if(username.getText().toString().isEmpty()) {
                       username.setError("You must fill in the phone number!");
                       username.requestFocus();
                   }
                   else if (password.getText().toString().isEmpty()){
                       password.setError("You must fill in the password!");
                       password.requestFocus();
                   }
                   //If the textfields are not empty
                   else {
                       //setting a dialog to tell the user to wait
                       waitingDialog.show();
                       user_table.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                               //checking if the user exists in the database
                               if (dataSnapshot.child(username.getText().toString()).exists()) {
                                   //getting the users information
                                   User user = dataSnapshot.child(username.getText().toString()).getValue(User.class);
                                   user.setPhone(username.getText().toString());
                                   if (user.getPassword().equals(password.getText().toString())) {
                                       Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                       Common.user_Current = user;//the user details are stored in user_current variable
                                       startActivity(intent);
                                       waitingDialog.dismiss();
                                       finish();//stops the login activity
                                   } else {
                                       Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                       waitingDialog.dismiss();
                                   }
                               }
                               //if user doesn't exist
                               else {
                                   Toast.makeText(LoginActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                                   waitingDialog.dismiss();
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                   }//end of else if
               }
               else {
                   //waitingDialog.dismiss();
                   //SnackBar.make(login_layout, "Check your internet connection", SnackBar.LENGTH_SHORT).show();
                   Toast.makeText(LoginActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                   return;
               }
           }
       });
    }

}
