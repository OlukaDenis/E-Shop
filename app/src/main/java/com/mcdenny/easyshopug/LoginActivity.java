package com.mcdenny.easyshopug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcdenny.easyshopug.Common.Common;
import com.mcdenny.easyshopug.Model.User;
import com.mcdenny.easyshopug.Utils.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button login;
    TextView create, forgot_password;
    LinearLayout login_layout;
    DatabaseReference user_table;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    AlertDialog waitingDialog;
    User user;

    private static final String TAG = SignupActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_login);
        //getting the reference
        email = (MaterialEditText) findViewById(R.id.et_login_email);
        password = (MaterialEditText) findViewById(R.id.password);
        login = findViewById(R.id.login);
        create = findViewById(R.id.dont_have_account);
        forgot_password = findViewById(R.id.forgot_password);
        login_layout = findViewById(R.id.login_layout);

        waitingDialog = new SpotsDialog(this);

        //initializing firebase database
        database = FirebaseDatabase.getInstance();
        user_table = database.getReference("User");
        mAuth = FirebaseAuth.getInstance();

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
                //Check for network connection
                if (Common.isNetworkAvailable(getBaseContext())) {
                    loginUser();
                } else {
                    final AlertDialog noNetworkDialog = new AlertDialog.Builder(LoginActivity.this)
                            .setCancelable(false)
                            .setTitle("Connection failed")
                            .setMessage("Please check your internet connection")
                            .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    loginUser();
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    noNetworkDialog.show();
                    waitingDialog.dismiss();
                    //SnackBar.make(login_layout, "Check your internet connection", SnackBar.LENGTH_SHORT).show();
                    // Toast.makeText(LoginActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUser() {
        final String userEmail = email.getText().toString();
        final String pass = password.getText().toString();
        final boolean valid_pass = Util.isValidPassword(pass);
        final boolean valid_email = Util.isValidEmail(userEmail);

        //Save user and password
        //Paper.book().write(Common.USER_KEY, email);
        //Paper.book().write(Common.PASSWORD_KEY, pass);
        //checking whether the edit text is empty
        if (userEmail.isEmpty()) {
            email.setError("You must fill in the phone number!");
            email.requestFocus();
        } else if (pass.isEmpty()) {
            password.setError("You must fill in the password!");
            password.requestFocus();
        }

        //typical validations
        else if (!valid_pass) {
            password.setError("Short password");
        }
        else if (!valid_email) {
            email.setError("Invalid email format");
        }
        //If the textfields are not empty
        else {

            //save current user's details
            final String cleanEmail = Util.cleanEmailKey(userEmail);
            getCurrentUserDetails(cleanEmail);

            //setting a dialog to tell the user to wait
            waitingDialog.show();
            //signing the user to the firebase authentication
            mAuth.signInWithEmailAndPassword(userEmail, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUser = mAuth.getCurrentUser();

                                if (currentUser != null) {
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    // Common.user_Current = currentUser;//the user details are stored in user_current variable
                                    startActivity(intent);
                                    waitingDialog.dismiss();

                                    finish();
                                }
                                Log.v(TAG, "Successfully logged in");
                            } else {
                                waitingDialog.dismiss();
                                String message = task.getException().getMessage();
                                Log.e(TAG, message);
                                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }//end of else if
    }

    private void getCurrentUserDetails(final String email) {

        //save users info
        user_table.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 user = dataSnapshot.getValue(User.class);
                 user = new User();
                // Common.user_Current = user;
                final String normalEmail = Util.normalEmailKey(email);
                Common.clean_current_user_email = email;
                //user.setEmail(normalEmail);

                String phone = dataSnapshot.child("phone").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String pass = dataSnapshot.child("password").getValue().toString();

                Common.current_user_email = normalEmail;
                Common.current_user_name = name;
                Common.current_user_phone = phone;

                //Common.user_Current = user;
                //Toast.makeText(LoginActivity.this, normalEmail, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
