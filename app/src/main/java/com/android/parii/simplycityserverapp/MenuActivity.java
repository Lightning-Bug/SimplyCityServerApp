package com.android.parii.simplycityserverapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.android.parii.simplycityserverapp.Common.Common;
import com.android.parii.simplycityserverapp.Interface.ItemClickListener;
import com.android.parii.simplycityserverapp.Model.Food;
import com.android.parii.simplycityserverapp.Model.Restaurant;
import com.android.parii.simplycityserverapp.Model.RestrauntMenu;
import com.android.parii.simplycityserverapp.ViewHolder.FoodMenuViewHolder;
import com.android.parii.simplycityserverapp.ViewHolder.FoodViewHolder;
import com.android.parii.simplycityserverapp.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    Uri saveUri, updateImageURI;

    String resID = "";
    FirebaseRecyclerAdapter<RestrauntMenu,MenuViewHolder> adapter;

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
        adapter = new FirebaseRecyclerAdapter<RestrauntMenu, MenuViewHolder>(RestrauntMenu.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                databaseReference) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, RestrauntMenu model, int position) {
                viewHolder.txtMenuName.setText(model.getMenueName());
                Picasso.with(MenuActivity.this).load(model.getImageURL())
                        .into(viewHolder.imageView);

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

        btnSelect.setVisibility(View.VISIBLE);
        btnUpload.setVisibility(View.GONE);

        alertDialog.setView(add_menu_layout);
        //alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        //buttons
        alertDialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(saveUri!=null) {
                    dialog.dismiss();
                    uploadImage();
                }else{
                    Toast.makeText(MenuActivity.this,"Select image",Toast.LENGTH_SHORT).show();
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

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        startActivityForResult(intent, 5);
    }

    private void uploadImage() {

        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);

            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            mDialog.dismiss();
                            //Toast.makeText(Home.this,"Uploaded",Toast.LENGTH_LONG).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    mDialog.dismiss();
                                    RestrauntMenu restrauntMenu = new RestrauntMenu("PIZZA","ffd", "abcdefghijkl");
                                    restrauntMenu.setMenueName("" + edtName.getText().toString());
                                    restrauntMenu.setImageURL(uri.toString());
                                    resDetails.child(getIntent().getStringExtra("key")).child("restrauntMenuList").child("" +
                                            adapter.getItemCount()).
                                            setValue(restrauntMenu);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(MenuActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //no error
                            double progress = (100.0 + taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uplaoding... ");
                        }
                    });

        }
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
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else  if(item.getTitle().equals(Common.DELETE))
        {
            deleteMenuItem(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);

    }

    private void deleteMenuItem(String key) {
        resDetails.child(getIntent().getStringExtra("key")).child("restrauntMenuList").child(key).removeValue();
        Toast.makeText(MenuActivity.this,"Item Deleted",Toast.LENGTH_LONG).show();
    }

    private void updateImage() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        startActivityForResult(intent, 4);
    }


    private void showUpdateDialog(final String key, final RestrauntMenu restrauntMenu) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
        alertDialog.setTitle("Update Menu");
        alertDialog.setMessage("Please Fill Full info");


        final LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout,null);

        edtName =add_menu_layout.findViewById(R.id.edtName);
        btnSelect= add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload= add_menu_layout.findViewById(R.id.btnUpload);


        //set name
        edtName.setText(restrauntMenu.getMenueName());

        //events for buttons
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage();
            }
        });

        btnUpload.setVisibility(View.GONE);

        alertDialog.setView(add_menu_layout);
        //alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        //buttons
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                if(btnSelect.getText().toString().equalsIgnoreCase("Image Selected")){
                    if(updateImageURI!=null){
                        saveUri = updateImageURI;
                        uploadImage(key);
                    }
                }else{
                    restrauntMenu.setMenueName(edtName.getText().toString());
                    resDetails.child(getIntent().getStringExtra("key")).child("restrauntMenuList").child(key).
                            setValue(restrauntMenu);
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

    private void uploadImage(final String key) {

        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);

            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            mDialog.dismiss();
                            //Toast.makeText(Home.this,"Uploaded",Toast.LENGTH_LONG).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    mDialog.dismiss();
                                    RestrauntMenu restrauntMenu = new RestrauntMenu("PIZZA","ffd", "abcdefghijkl");
                                    restrauntMenu.setMenueName("" + edtName.getText().toString());
                                    restrauntMenu.setImageURL(uri.toString());
                                    resDetails.child(getIntent().getStringExtra("key")).child("restrauntMenuList").child(key).
                                            setValue(restrauntMenu);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(MenuActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //no error
                            double progress = (100.0 + taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uplaoding... ");
                        }
                    });

        }
    }


}
