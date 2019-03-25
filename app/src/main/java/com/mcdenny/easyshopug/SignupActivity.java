package com.mcdenny.easyshopug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignupActivity extends AppCompatActivity {
    MaterialEditText usrphone, usrname, usrpassword, usremail;
    Button signup;
    TextView terms_service, login;
    DatabaseReference user_table;
    FirebaseDatabase database;
    private FirebaseAuth mAuth;
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
        setContentView(R.layout.activity_signup);

        usrphone = (MaterialEditText) findViewById(R.id.phone);
        usrname = (MaterialEditText) findViewById(R.id.name);
        usrpassword = (MaterialEditText) findViewById(R.id.password);
        usremail = (MaterialEditText) findViewById(R.id.email);
        signup = (Button) findViewById(R.id.signup);
        terms_service = findViewById(R.id.terms_of_service);
        login = findViewById(R.id.Login_user);

        SpannableString content = new SpannableString("Terms of service");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        terms_service.setText(content);

        //initializing firebase database
         database = FirebaseDatabase.getInstance();
         user_table = database.getReference("User");
         mAuth = FirebaseAuth.getInstance();

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
                    final String pass = usrpassword.getText().toString();
                    final String email = usremail.getText().toString();
                    final boolean valid_pass = Util.isValidPassword(pass);
                    final boolean valid_email = Util.isValidEmail(email);
                    if(usrphone.getText().toString().isEmpty()) {
                        usrphone.setError("Invalid Phone number");
                        usrphone.requestFocus();
                    }
                    else if(usrname.getText().toString().isEmpty()) {
                        usrname.setError("Invalid Name");
                        usrname.requestFocus();
                    }
                    else if(usrpassword.getText().toString().isEmpty()){
                        usrpassword.setError("Please enter password");
                        usrpassword.requestFocus();
                    }
                    else if(usremail.getText().toString().isEmpty()){
                        usremail.setError("Invalid email");
                        usremail.requestFocus();
                    }
                    else if(!valid_pass){
                        usrpassword.setError("Password is too short(At least 6 characters)");
                    }
                    else if(!valid_email){
                        usremail.setError("Invalid email format");
                    }
                    else {
                        final AlertDialog waitingDialog = new SpotsDialog(SignupActivity.this);
                        waitingDialog.show();

                        //Creating a user in firebase database
                        user_table.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    User user = new User(usrname.getText().toString(), usrpassword.getText().toString(), usremail.getText().toString());
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

                        //Creating a user in firebase authentication
                        mAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Log.v(TAG, "User authenticated successfully");
                                           // Toast.makeText(SignupActivity.this, "Sucess", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            String message = task.getException().getMessage();
                                            Log.e(TAG, message);
                                            //Toast.makeText(SignupActivity.this, message, Toast.LENGTH_LONG).show();
                                        }
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
