package com.android.parii.simplycityserverapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.android.parii.simplycityserverapp.Interface.ItemClickListener;
import com.android.parii.simplycityserverapp.Model.Food;
import com.android.parii.simplycityserverapp.Model.Restaurant;
import com.android.parii.simplycityserverapp.Model.RestrauntMenu;
import com.android.parii.simplycityserverapp.ViewHolder.FoodMenuViewHolder;
import com.android.parii.simplycityserverapp.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parii on 1/22/18.
 */

public class MenuActivity extends AppCompatActivity {



    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab;

    //fb
    FirebaseDatabase db;
    DatabaseReference resDetails;
    FirebaseStorage storage;
    StorageReference storageReference;

    //new food
    MaterialEditText edtName,edtDescription,edtPrice,edtDiscount;
    Button btnSelect,btnUpload;
    Food newFood;

    Uri saveUri;

    String resID = "";
    FirebaseRecyclerAdapter<RestrauntMenu,FoodMenuViewHolder> adapter;

    ArrayList<RestrauntMenu> restrauntMenuArrayList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);


        //fb
        db = FirebaseDatabase.getInstance();
        resDetails = db.getReference("Restaurant");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        restrauntMenuArrayList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showAddFoodDialog();
                addMenu();
            }
        });


        if(getIntent().getStringExtra("resID") != null)
            resID=getIntent().getStringExtra("resID");

        //loadMenu();
        showRecycler();
    }

    public void loadMenu(){
        resDetails.child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showRecycler(){
        DatabaseReference databaseReference = resDetails.child(getIntent().getStringExtra("key")).
                child("restrauntMenuList");
        adapter = new FirebaseRecyclerAdapter<RestrauntMenu, FoodMenuViewHolder>(RestrauntMenu.class,
                R.layout.menu_name_layout,FoodMenuViewHolder.class,
                databaseReference) {
            @Override
            protected void populateViewHolder(FoodMenuViewHolder viewHolder, RestrauntMenu model, int position) {
                viewHolder.txtMenuName.setText(model.getMenueName());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //TODO:food items
                        Intent intent = new Intent(MenuActivity.this,FoodList.class);
                        intent.putExtra("res_key",getIntent().getStringExtra("key"));
                        intent.putExtra("menu_key",adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public void addMenu(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
        alertDialog.setTitle("Add Menu");
        alertDialog.setMessage("Please Fill Full info");

        final LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout,null);

        edtName =add_menu_layout.findViewById(R.id.edtName);
        btnSelect= add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload= add_menu_layout.findViewById(R.id.btnUpload);

        btnSelect.setVisibility(View.GONE);
        btnUpload.setVisibility(View.GONE);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        //buttons
        alertDialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                RestrauntMenu restrauntMenu = new RestrauntMenu("PIZZA","abcdefghijkl");
                restrauntMenu.setMenueName(""+edtName.getText().toString());
                resDetails.child(getIntent().getStringExtra("key")).child("restrauntMenuList").child(""+
                        adapter.getItemCount()).
                        setValue(restrauntMenu);

            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.show();


        }

}
