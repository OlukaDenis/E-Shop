package com.mcdenny.easyshopug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
    private MaterialEditText usrphone, usrname, usrpassword, usremail;
    private ImageView backLogin;
    private Button signup;
    private TextView terms_service, login;
    private DatabaseReference user_table;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private UserProfileChangeRequest profileUpdates;
    private User user;
    private FirebaseUser firebaseUser;
    AlertDialog waitingDialog;
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
        backLogin = findViewById(R.id.img_back_to_login);

        SpannableString content = new SpannableString("Terms of service");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        terms_service.setText(content);

        //initializing firebase database
        database = FirebaseDatabase.getInstance();
        user_table = database.getReference("User");
        mAuth = FirebaseAuth.getInstance();

        waitingDialog = new SpotsDialog(this);

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

        backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isNetworkAvailable(getBaseContext())) {
                    createUser();

                } else {
                    final AlertDialog noNetworkDialog = new AlertDialog.Builder(SignupActivity.this)
                            .setCancelable(false)
                            .setTitle("Connection failed")
                            .setMessage("Please check your internet connection")
                            .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    createUser();
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
                    //Toast.makeText(SignupActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if (com.mcdenny.easyshopug.Utils.Permissions.isUserVerified(this)) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }// end of onCreate method

    private void createUser() {
        //checking whether the edit text is empty
        final String  password = usrpassword.getText().toString();
        final String email = usremail.getText().toString();
        final String phone = usrphone.getText().toString().trim();
        final String  name = usrname.getText().toString().trim();
        final String admin = "0";


        final boolean valid_pass = Util.isValidPassword(password);
        final boolean valid_email = Util.isValidEmail(email);
        final boolean valid_name = Util.isValidName(name);

        //empty field validations
        if (usrphone.getText().toString().isEmpty()) {
            usrphone.setError("Phone number field must not be empty");
            usrphone.requestFocus();
        } else if (usrname.getText().toString().isEmpty()) {
            usrname.setError("Name field must not be empty");
            usrname.requestFocus();
        } else if (usrpassword.getText().toString().isEmpty()) {
            usrpassword.setError("Password field must not be empty");
            usrpassword.requestFocus();
        } else if (usremail.getText().toString().isEmpty()) {
            usremail.setError("Email field must not be empty");
            usremail.requestFocus();
        }

        //typical validations
        else if (!valid_pass) {
            usrpassword.setError("Password must be at least six characters");
        }
        else if (!valid_email) {
            usremail.setError("Invalid email format");
        }
        else if(!valid_name){
            usrname.setError("Name must not contain symbols or numbers");
        }
        else {
            waitingDialog.show();

            user = new User(name, password, phone, admin);

            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            //Creating a user in firebase authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                firebaseUser = mAuth.getCurrentUser();
                                assert firebaseUser != null;
                                firebaseUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                waitingDialog.dismiss();
                                                if (task.isSuccessful()) {
                                                    Log.d("REG", "User profile updated.");
                                                    saveCustomerDetails();
                                                }
                                            }
                                        });
                                Log.v(TAG, "User authenticated successfully");
                                //Toast.makeText(SignupActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            } else {
                                waitingDialog.dismiss();
                                String message = task.getException().getMessage();
                                Log.e(TAG, message);
                                Toast.makeText(SignupActivity.this, "Account creation failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }//end of else
    }


    //saving the user to the firebase database
    private void saveCustomerDetails() {
        final String userEmail = usremail.getText().toString();
        String userId = Util.cleanEmailKey(userEmail);
        user_table.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "User successfully created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "User creation failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveCustomerDisplayDetails(userId);
    }

    private void saveCustomerDisplayDetails(String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference saveDetailsDatabase = database.getReference();
        saveDetailsDatabase.child("users_list").child(userId).setValue(firebaseUser.getDisplayName())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {

                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (com.mcdenny.easyshopug.Utils.Permissions.isUserVerified(this)) {
            finish();
        }
    }

}
