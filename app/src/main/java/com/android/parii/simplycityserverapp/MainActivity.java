package com.android.parii.simplycityserverapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    Button btnSignIn;
    TextView txtSlogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnSignIn = (Button)findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this,PhoneVerification.class);
                startActivity(i);
                finish();
            }
        });

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Authanticating");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){

            Intent i = new Intent(MainActivity.this,Home.class);
            startActivity(i);
            finish();
        }else{
            progressDialog.dismiss();
        }

    }
}
