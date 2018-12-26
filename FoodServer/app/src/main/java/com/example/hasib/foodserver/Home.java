package com.example.hasib.foodserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasib.foodserver.Common.Common;
import com.example.hasib.foodserver.Interface.ItemClickListener;
import com.example.hasib.foodserver.Model.Catagory;
import com.example.hasib.foodserver.Model.Token;
//import com.example.hasib.foodserver.Server.ListenOrder;
import com.example.hasib.foodserver.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView adminText;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    EditText setMenu;
    Button selectbt,uploadbt;




    FirebaseRecyclerAdapter<Catagory,MenuViewHolder> adepter;


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    Catagory catagory;

    Uri imgUri;

    @Override
    protected void onStop() {
        super.onStop();
        adepter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adepter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Food Server");
        setSupportActionBar(toolbar);


        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Category");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView=navigationView.getHeaderView(0);
        adminText=(TextView)headerView.findViewById(R.id.fullTextName);
        adminText.setText(Common.currentUser.getName()) ;

        recyclerView=(RecyclerView)findViewById(R.id.recycular_menu);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        loadMenu();




        updataeToken(FirebaseInstanceId.getInstance().getToken());






    }

    private void updataeToken(String token) {

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference referance=db.getReference("Token");
        Token toke=new Token(token,false);
        referance.child(Common.currentUser.getPhone()).setValue(toke);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Common.PICK_IMAGE_REQUESR && resultCode==RESULT_OK && data != null && data.getData() !=null){
            selectbt.setText("Image Selected");

            imgUri=data.getData();
        }
    }

    private void showDialog() {
        final AlertDialog.Builder alrt=new AlertDialog.Builder(Home.this);
        alrt.setTitle("Add A New Category");
        alrt.setMessage("Enter The Name");

        //final LayoutInflater inflater=this.getLayoutInflater();
        final LayoutInflater inflater=LayoutInflater.from(this);
        View v= inflater.inflate(R.layout.add_item_layout,null);
        setMenu= (EditText) v.findViewById(R.id.setMenuname);
        selectbt=(Button)v.findViewById(R.id.selectBt);
        uploadbt=(Button)v.findViewById(R.id.uploadBt);

        selectbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        uploadbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });




        alrt.setView(v);
        alrt.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if(catagory!=null){
                    reference.push().setValue(catagory);
                }



            }
        });
        alrt.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        alrt.show();


    }

    private void uploadImage() {

      final   ProgressDialog mDialog=new ProgressDialog(this);
        mDialog.setMessage("Uploading.....");
        mDialog.show();
        if(imgUri!=null){

            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child("image/"+imageName);

            imageFolder.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Home.this,"Download Successfully completed",Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {
                         catagory=new Catagory(setMenu.getText().toString(),uri.toString());


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(Home.this,"Fail to Upload"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploading "+progress+"%");
                }
            });
        }

    }

    private void chooseImage() {

        Intent in=new Intent();
        in.setType("image/*");
        in.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(in,"Select Picture"),Common.PICK_IMAGE_REQUESR);


    }

    private void loadMenu() {
        FirebaseRecyclerOptions<Catagory> options=new FirebaseRecyclerOptions.Builder<Catagory>()
                .setQuery(reference,Catagory.class)
                .build();

        adepter=new FirebaseRecyclerAdapter<Catagory, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, final int position, @NonNull Catagory model) {

                viewHolder.titile.setText(model.getName());
                Picasso.with(Home.this).load(model.getImage()).into(viewHolder.imag);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int possition, boolean inClicked) {
                        // Toast.makeText(getApplication(),""+position,Toast.LENGTH_SHORT).show();
                        Intent in=new Intent(Home.this,FoodList.class);
                        in.putExtra("categoryId",adepter.getRef(position).getKey());
                        startActivity(in);

                    }
                });

            }

            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(parent.getContext()).inflate(   R.layout.menu_item,parent,false);

                return new MenuViewHolder(v);
            }
        };


        adepter.startListening();
        adepter.notifyDataSetChanged();
        recyclerView.setAdapter(adepter);


      /*  adepter=new FirebaseRecyclerAdapter<Catagory, MenuViewHolder>(Catagory.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                reference)  {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Catagory model, final int position) {

                viewHolder.titile.setText(model.getName());
                Picasso.with(Home.this).load(model.getImage()).into(viewHolder.imag);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int possition, boolean inClicked) {
                       // Toast.makeText(getApplication(),""+position,Toast.LENGTH_SHORT).show();
                        Intent in=new Intent(Home.this,FoodList.class);
                        in.putExtra("categoryId",adepter.getRef(position).getKey());
                        startActivity(in);

                    }
                });


            }
        };*/

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

        if (id == R.id.home) {
            // Handle the camera action
        }
        if (id==R.id.banner_add){
            startActivity(new Intent(Home.this,BannerClass.class));

        }
        if (id==R.id.shipper){
            startActivity(new Intent(Home.this,JunpToShipper.class));

        }
        if (id == R.id.order) {
            // Handle the camera action
            Intent in=new Intent(Home.this,OrderStatus.class);
            startActivity(in);
        }
        if (id == R.id.sendMessageToTheClient) {
            // Handle the camera action
            Intent in=new Intent(Home.this,SendMessageNotification.class);
            startActivity(in);
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDailog(adepter.getRef(item.getOrder()).getKey(),adepter.getItem(item.getOrder()));
        }

        if(item.getTitle().equals(Common.DELETE)){
            showDeleteDailog(adepter.getRef(item.getOrder()).getKey());
           Toast.makeText(getApplication(),"Deleted!!",Toast.LENGTH_SHORT).show();
        }



        return super.onContextItemSelected(item);
    }

    private void showDeleteDailog(String key) {
        reference.child(key).removeValue();
    }

    private void showUpdateDailog(final String key, final Catagory item) {

        final AlertDialog.Builder alrt=new AlertDialog.Builder(Home.this);
        alrt.setTitle("Update Category");
        alrt.setMessage("Enter The Name");

        final LayoutInflater inflater=this.getLayoutInflater();
        View v= inflater.inflate(R.layout.add_item_layout,null);
        setMenu= (MaterialEditText) v.findViewById(R.id.setMenuname);
        selectbt=(FButton)v.findViewById(R.id.selectBt);
        uploadbt=(FButton)v.findViewById(R.id.uploadBt);
        setMenu.setText(item.getName());

        selectbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        uploadbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });




        alrt.setView(v);
        alrt.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

              item.setName(setMenu.getText().toString());
                reference.child(key).setValue(item);


            }
        });
        alrt.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        alrt.show();

    }
    private void changeImage(final Catagory item) {

        final   ProgressDialog mDialog=new ProgressDialog(this);
        mDialog.setMessage("Uploading.....");
        mDialog.show();
        if(imgUri!=null){

            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child("image/"+imageName);

            imageFolder.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Home.this,"Download Successfully completed",Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {

                            item.setImage(uri.toString());


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(Home.this,"Fail to Upload"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploading "+progress+"%");
                }
            });
        }

    }
}
