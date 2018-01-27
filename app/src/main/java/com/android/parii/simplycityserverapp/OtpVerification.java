package com.android.parii.simplycityserverapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

/**
 * Created by AVINASH on 1/26/2018.
 */

public class OtpVerification extends AppCompatActivity{

    EditText phonNumber;
    Button submit;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_verification);

        phonNumber = (EditText) findViewById(R.id.phone_otp);
        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(OtpVerification.this);
                progressDialog.setMessage("Verifying phoneNumber");
                progressDialog.setCancelable(false);

                if(phonNumber.getText().toString().trim().length()!=0){
                    progressDialog.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(getIntent().getStringExtra("verificationId"),
                            phonNumber.getText().toString().trim());

                    signInWithPhoneAuthCredential(credential);
                }else{
                    Toast.makeText(OtpVerification.this,"Invalid otp",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("PhoneVERifier", "signInWithCredential:success");
                            startActivity(new Intent(OtpVerification.this,Home.class));
                            finish();
                            FirebaseUser user = task.getResult().getUser();
                            //FirebaseAuth.getInstance().signOut();
                            // ...
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(OtpVerification.this,"Invalid  Otp",Toast.LENGTH_SHORT).show();
                            // Sign in failed, display a message and update the UI
                            Log.w("PhoneVERifier", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
