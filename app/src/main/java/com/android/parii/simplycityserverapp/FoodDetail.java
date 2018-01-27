package com.android.parii.simplycityserverapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class FoodDetail extends AppCompatActivity {

    TextView food_name, food_price, food_description,food_disc,food_sp;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;

    FloatingActionButton btnCart;
    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference foods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        food_image = (ImageView) findViewById(R.id.img_food);
        food_description = (TextView) findViewById(R.id.food_description);
        food_name = (TextView) findViewById(R.id.food_name);
        food_price = (TextView) findViewById(R.id.food_price);
        food_disc = (TextView) findViewById(R.id.discount);
        food_sp = (TextView) findViewById(R.id.sellingPrice);

        food_name.setText(getIntent().getStringExtra("foodName"));
        food_description.setText(getIntent().getStringExtra("foodDescription"));
        food_price.setText("Original Price: "+getIntent().getStringExtra("foodPrice"));
        food_disc.setText("Discount: "+getIntent().getStringExtra("foodDiscount"));
        food_sp.setText("Selling Price: "+(Integer.parseInt(getIntent().getStringExtra("foodPrice"))
                -Integer.parseInt(getIntent().getStringExtra("foodDiscount")))+"");

        Picasso.with(getBaseContext())
                .load(getIntent().getStringExtra("foodImage"))
                .into(food_image);


    }

}