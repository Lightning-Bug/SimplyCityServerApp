package com.android.parii.simplycityserverapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Freezable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.parii.simplycityserverapp.Common.Common;
import com.android.parii.simplycityserverapp.Interface.ItemClickListener;
//import com.android.parii.simplycityserverapp.Model.Category;
import com.android.parii.simplycityserverapp.Model.Food;
import com.android.parii.simplycityserverapp.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodList extends AppCompatActivity {



    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab;

    //fb
    FirebaseDatabase db;
    DatabaseReference foodRef;
    FirebaseStorage storage;
    StorageReference storageReference;

    //new food
    MaterialEditText edtName,edtDescription,edtPrice,edtDiscount;
    Button btnSelect,btnUpload;
    Food newFood;

    Uri saveUri, updateImageURI;

    String categoryId = "";
    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        //fb
        db = FirebaseDatabase.getInstance();
        foodRef = db.getReference("Restaurant");
        storage = FirebaseStorage.getInstance();
        storageReference= storage.getReference();



        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showAddFoodDialog();
            }
        });

        loadListFood();

    }

    private void showAddFoodDialog() {
        //code from home show dialog

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Add New Food in This");
        alertDialog.setMessage("Please Fill Full info");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout,null);

        edtName =add_menu_layout.findViewById(R.id.edtName);
        edtDescription =add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice =add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount =add_menu_layout.findViewById(R.id.edtDiscount);


        btnSelect= add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload= add_menu_layout.findViewById(R.id.btnUpload);


        //events for buttons
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        btnUpload.setVisibility(View.GONE);


        alertDialog.setView(add_menu_layout);
        //alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        //buttons
        alertDialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //creating new category

                if(saveUri!=null){
                    uploadImage();
                    dialog.dismiss();
                }else{
                    Toast.makeText(FoodList.this,"Select Image",Toast.LENGTH_SHORT).show();
                }

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

    private void uploadImage() {

        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading.....");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);

            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            mDialog.dismiss();
                            Toast.makeText(FoodList.this,"Uploaded....",Toast.LENGTH_LONG).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //set value of category if image is uploaded , can get download link

                                    //added line
                                    mDialog.dismiss();

                                    newFood = new Food();
                                    newFood.setName(edtName.getText().toString());
                                    newFood.setDescription(edtDescription.getText().toString());
                                    newFood.setPrice(edtPrice.getText().toString());
                                    newFood.setDiscount(edtDiscount.getText().toString());
                                    newFood.setImage(uri.toString());
                                    newFood.setMenuId("abcdef");

                                    if(newFood != null)
                                    {
                                        foodRef.child(getIntent().getStringExtra("res_key")).child("restrauntMenuList").
                                                child(getIntent().getStringExtra("menu_key")).
                                                child("foodList")
                                                .child(""+adapter.getItemCount()).setValue(newFood);

                                    }

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(FoodList.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //no error
                            double progress = (100.0 + taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uplaoding...");
                        }
                    });



        }
    }

    private void chooseImage() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        startActivityForResult(intent, 5);
    }

    private void loadListFood() {

        DatabaseReference databaseReference = foodRef.child(getIntent().getStringExtra("res_key")).child("restrauntMenuList").
                child(getIntent().getStringExtra("menu_key")).
                child("foodList");

        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, final Food model, int position) {

                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(viewHolder.food_image);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //late
                        Intent intent = new Intent(FoodList.this,FoodDetail.class);
                        intent.putExtra("foodName",model.getName());
                        intent.putExtra("foodDescription",model.getDescription());
                        intent.putExtra("foodDiscount",model.getDiscount());
                        intent.putExtra("foodPrice",model.getPrice()+"");
                        intent.putExtra("foodImage",model.getImage());
                        startActivity(intent);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Hi","Iam here");
        if(requestCode == 5 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            saveUri = data.getData();
            btnSelect.setText("Image Selected");
        }

        if(requestCode == 4 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            updateImageURI = data.getData();
            btnSelect.setText("Image Selected");
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
       if(item.getTitle().equals(Common.UPDATE))
       {

           showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));


       }else if(item.getTitle().equals(Common.DELETE))
       {

           deleteFood(adapter.getRef(item.getOrder()).getKey());

       }

       return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {
        //foodList.child(key).removeValue();
        foodRef.child(getIntent().getStringExtra("res_key")).child("restrauntMenuList").
                child(getIntent().getStringExtra("menu_key")).
                child("foodList")
                .child(key).removeValue();
    }

    private void changeImage(final String key) {

        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading.....");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);

            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            mDialog.dismiss();
                            Toast.makeText(FoodList.this,"Uploaded....",Toast.LENGTH_LONG).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Food item = new Food();
                                    item.setName(edtName.getText().toString());
                                    item.setPrice(edtPrice.getText().toString());
                                    item.setDiscount(edtDiscount.getText().toString());
                                    item.setDescription(edtDescription.getText().toString());
                                    item.setImage(uri.toString());
                                    //added
                                    Log.d("im at menu", "id");
                                    item.setMenuId("ghg");


                                    Log.d("im at menu", "id " + item.getMenuId() + ";" + key);
                                    //foodList.child(key).setValue(item);
                                    foodRef.child(getIntent().getStringExtra("res_key")).child("restrauntMenuList").
                                            child(getIntent().getStringExtra("menu_key")).
                                            child("foodList")
                                            .child(key).setValue(item);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(FoodList.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //no error
                            double progress = (100.0 + taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uplaoding ");
                        }
                    });

        }
    }


    private void updateImage() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        startActivityForResult(intent, 4);
    }


    private void showUpdateFoodDialog(final String key, final Food item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Edit Food");
        alertDialog.setMessage("Please Fill Full info");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout,null);

        edtName =add_menu_layout.findViewById(R.id.edtName);
        edtDescription =add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice =add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount =add_menu_layout.findViewById(R.id.edtDiscount);

        //set default valur for view
        edtName.setText(item.getName());
        edtDiscount.setText(item.getDiscount());
        edtPrice.setText(item.getPrice());
        edtDescription.setText(item.getDescription());

        //added
        //item.setMenuid(categoryId);

        btnSelect= add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload= add_menu_layout.findViewById(R.id.btnUpload);

        btnUpload.setVisibility(View.GONE);


        //events for buttons
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        //alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        //buttons
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //creating new category


                if(btnSelect.getText().toString().equalsIgnoreCase("Image Selected")){
                    if(updateImageURI!=null){
                        saveUri = updateImageURI;
                        changeImage(key);
                    }
                }else {


                    item.setName(edtName.getText().toString());
                    item.setPrice(edtPrice.getText().toString());
                    item.setDiscount(edtDiscount.getText().toString());
                    item.setDescription(edtDescription.getText().toString());

                    //added
                    Log.d("im at menu", "id");
                    item.setMenuId("ghg");


                    Log.d("im at menu", "id " + item.getMenuId() + ";" + key);
                    //foodList.child(key).setValue(item);
                    foodRef.child(getIntent().getStringExtra("res_key")).child("restrauntMenuList").
                            child(getIntent().getStringExtra("menu_key")).
                            child("foodList")
                            .child(key).setValue(item);

                }

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
