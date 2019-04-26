package com.mcdenny.easyshopug;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mcdenny.easyshopug.Common.Common;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForgotPasswordActivity extends AppCompatActivity {
    private MaterialEditText etEmail;
    private Button btSend_mail;
    private FirebaseAuth mAuth;
    AlertDialog waitingDialog;

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
        setContentView(R.layout.activity_forgot_password);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_ic);

        etEmail = (MaterialEditText)findViewById(R.id.reset);
        btSend_mail = (Button) findViewById(R.id.send_mail);

        //init firebase
        mAuth = FirebaseAuth.getInstance();

        waitingDialog = new SpotsDialog(this);

        btSend_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isNetworkAvailable(getBaseContext())) {
                    resetPassword();

                } else {
                    final AlertDialog noNetworkDialog = new AlertDialog.Builder(ForgotPasswordActivity.this)
                            .setCancelable(false)
                            .setTitle("Connection failed")
                            .setMessage("Please check your internet connection")
                            .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resetPassword();
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
    }

    private void resetPassword() {
        String userEmail = etEmail.getText().toString();
        if (TextUtils.isEmpty(userEmail)){
            etEmail.setError("Please insert your email");
            etEmail.requestFocus();
        }
        else{
            waitingDialog.show();
            mAuth.sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                final AlertDialog alertDialog = new AlertDialog.Builder(ForgotPasswordActivity.this)
                                        .setCancelable(false)
                                        .setTitle("Message")
                                        .setMessage("Password has been sent to your email. Please check your email")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        })
                                        .create();
                                alertDialog.show();
                                //Toast.makeText(ForgotPasswordActivity.this, "Password has been sent your email", Toast.LENGTH_SHORT).show();
                                waitingDialog.dismiss();
                            }
                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this, "Error!"+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
