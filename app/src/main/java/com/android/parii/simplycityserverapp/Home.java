package com.android.parii.simplycityserverapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.parii.simplycityserverapp.Common.Common;
import com.android.parii.simplycityserverapp.Interface.ItemClickListener;
import com.android.parii.simplycityserverapp.Model.Food;
import com.android.parii.simplycityserverapp.Model.Restaurant;
import com.android.parii.simplycityserverapp.Model.RestrauntMenu;
import com.android.parii.simplycityserverapp.ViewHolder.MenuViewHolder;
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

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtFullName;

    //firebase
    FirebaseDatabase database;
    DatabaseReference categories;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseRecyclerAdapter<Restaurant,MenuViewHolder> adapter;

    //view
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    MaterialEditText edtName;
    Button btnUpload,btnSelect;

    Restaurant newRestaurant;

    Uri saveUri;
    private  final int PICK_IMAGE_REQUEST = 71;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);

        //init fb
        database=FirebaseDatabase.getInstance();
        categories= database.getReference("Restaurant");

        storage= FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
                
                showDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //name in header
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());


        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
        loadMenu();
    }

    private void showDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Add New");
        alertDialog.setMessage("Please Fill Full info");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout,null);

        edtName =add_menu_layout.findViewById(R.id.edtName);
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


        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        //buttons
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //creating new category

                if(newRestaurant != null)
                {
                    categories.push().setValue(newRestaurant);
                }

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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
                            Toast.makeText(Home.this,"Uploaded....",Toast.LENGTH_LONG).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //set value of category if image is uploaded , can get download link

                                    //added line
                                    mDialog.dismiss();
                                    //ArrayList<Food> foodArrayList = new ArrayList<>();
                                    //foodArrayList.add(new Food("afa","wefwae","faf","efw","wegfwae","efgaw"));
                                    ArrayList<RestrauntMenu> restrauntMenus = new ArrayList<>();
                                    //RestrauntMenu restrauntMenu = new RestrauntMenu();
                                    //restrauntMenu.setFoodList(foodArrayList);
                                    //restrauntMenu.setMenueID("01");
                                    //restrauntMenu.setMenueName("kulcha");
                                    //restrauntMenus.add(restrauntMenu);
                                    newRestaurant = new Restaurant(edtName.getText().toString()+"","01",uri.toString(),restrauntMenus);

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(Home.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //no error
                            double progress = (100.0 + taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uplaoded "+progress+"%");
                        }
                    });



        }
    }




    //ctrl+o
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

    }

    private void chooseImage() {

       /* Intent intent = new Intent();
        intent.setType("images/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);

*/

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        startActivityForResult(intent, 5);
    }

    private void loadMenu() {

        adapter = new FirebaseRecyclerAdapter<Restaurant, MenuViewHolder>(
                Restaurant.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                categories) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, final Restaurant model, int position) {

                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(Home.this).load(model.getImageURL())
                        .into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        // send category id and start new activity
                        Intent menuActivity = new Intent(Home.this, MenuActivity.class);
                        //TODO? to put menuID as string extras
                        menuActivity.putExtra("resID",model.getResID());
                        menuActivity.putExtra("imageURL",model.getImageURL());
                        menuActivity.putExtra("Name",model.getName());
                        menuActivity.putExtra("key",adapter.getRef(position).getKey());
                        startActivity(menuActivity);
                        Log.e("details:",model.toString()+":"+adapter.getRef(position).getKey());
                        //TODO?model.getResID();
                    }
                });

            }
        };

        adapter.notifyDataSetChanged(); //Refresh data if data have changed
        recycler_menu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            Intent j = new Intent(Home.this,OrderStatus.class);
            startActivity(j);

        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    //Update /Delete

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE))
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else  if(item.getTitle().equals(Common.DELETE))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }


        return super.onContextItemSelected(item);

    }

    private void deleteCategory(String key) {

       DatabaseReference foods = database.getReference("Foods");
        Query foodInCategory = foods.orderByChild("menuId").equalTo(key);
        foodInCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for( DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    postSnapshot.getRef().removeValue();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        categories.child(key).removeValue();
        Toast.makeText(Home.this,"Deleted Broo :( !!!",Toast.LENGTH_LONG).show();
    }

    private void showUpdateDialog(final String key, final Restaurant restaurant) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Update Info");
        alertDialog.setMessage("Please Fill Full info");

        final LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout,null);

        edtName =add_menu_layout.findViewById(R.id.edtName);
        btnSelect= add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload= add_menu_layout.findViewById(R.id.btnUpload);


        //set name
        edtName.setText(restaurant.getName());

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
                changeImage(restaurant);
            }
        });


        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        //buttons
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                restaurant.setName(edtName.getText().toString());
                categories.child(key).setValue(restaurant);


                //creating new category

                /*if(newCategory != null)
                {
                    categories.push().setValue(newCategory);
                }
                */

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void changeImage(final Restaurant restaurant) {

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
                            Toast.makeText(Home.this,"Uploaded....",Toast.LENGTH_LONG).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //set value of category if image is uploaded , can get download link

                                    //newCategory = new Category(edtName.getText().toString(),uri.toString());

                                    restaurant.setImageURL(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(Home.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //no error
                            double progress = (100.0 + taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uplaoded "+progress+"%");
                        }
                    });



        }
    }
}
