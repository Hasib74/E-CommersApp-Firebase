package com.example.hasib.foodserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hasib.foodserver.Common.Common;
import com.example.hasib.foodserver.Interface.ItemClickListener;
import com.example.hasib.foodserver.Model.Food;
import com.example.hasib.foodserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class FoodList extends AppCompatActivity {

    MaterialEditText fname,fdiscription,fprice,fdiscount,un,ud,up,udiscount;
    FButton uploadbt,selectbt ;


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference food_referance;
    FirebaseStorage storage;
    StorageReference storageReference;
    FloatingActionButton floatingActionButton;
    RelativeLayout relativeLayout;
    Food newFood;

    FirebaseRecyclerAdapter<Food,FoodViewHolder> adepter;

    String catagoryId="";

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        firebaseDatabase=FirebaseDatabase.getInstance();
        food_referance=firebaseDatabase.getReference("Foods");

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        recyclerView=(RecyclerView)findViewById(R.id.menu_list);
        recyclerView.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        relativeLayout=(RelativeLayout) findViewById(R.id.recycular_menu);

        floatingActionButton=(FloatingActionButton)findViewById(R.id.floting_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addShowDialog();

            }
        });
        if(getIntent() !=null){
            catagoryId=getIntent().getStringExtra("categoryId");
        }
        if(!catagoryId.isEmpty()){
            loadFood(catagoryId);
        }

    }

    private void loadFood(String catagoryId) {


        Query query= food_referance.orderByChild("menuId").equalTo(catagoryId);

        FirebaseRecyclerOptions<Food> options=new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query,Food.class)
                .build();


        adepter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {

                viewHolder.titile.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imag);


                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int possition, boolean inClicked) {

                        addShowDialog();
                    }
                });


            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext()).inflate( R.layout.food_item,parent,false);

                return new FoodViewHolder(v);
            }
        };

       adepter.startListening();
        adepter.notifyDataSetChanged();
        recyclerView.setAdapter(adepter);

   /* adepter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
            R.layout.food_item,FoodViewHolder.class,
            food_referance.orderByChild("menuId").equalTo(catagoryId)) {
        @Override
        protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
            viewHolder.titile.setText(model.getName());
            Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imag);


            viewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int possition, boolean inClicked) {

                  addShowDialog();
                }
            });


        }
    };*/

    }

    private void addShowDialog() {

        final AlertDialog.Builder alrt=new AlertDialog.Builder(FoodList.this);
        alrt.setTitle("Add A New Food");
        alrt.setMessage("Enter The Name");

        final LayoutInflater inflater=this.getLayoutInflater();
         View v= inflater.inflate(R.layout.add_new_food_layout,null);
        fname= (MaterialEditText) v.findViewById(R.id.item_name);
        fdiscription=(MaterialEditText) v.findViewById(R.id.item_discreption);
        fprice=(MaterialEditText) v.findViewById(R.id.item_price);
        fdiscount=(MaterialEditText) v.findViewById(R.id.item_discount);


        uploadbt=(FButton)v.findViewById(R.id.uploadBt);
        selectbt=(FButton)v.findViewById(R.id.selectBt);

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

                if(catagoryId!=null){
                    food_referance.push().setValue(newFood);
                  //  Snackbar.make(relativeLayout,"New Category"+newFood.getName()+"was add",Snackbar.LENGTH_SHORT).show();
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

        final ProgressDialog mDialog=new ProgressDialog(this);
        mDialog.setMessage("Uploading.....");
        mDialog.show();
        if(saveUri!=null){

            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child("image/"+imageName);

            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(FoodList.this,"Download Successfully completed",Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {
                            newFood=new Food();
                            newFood.setName(fname.getText().toString());
                            newFood.setDescription(fdiscription.getText().toString());
                            newFood.setPrice(fprice.getText().toString());
                            newFood.setDiscount(fdiscount.getText().toString());
                            newFood.setMenuId(catagoryId);
                            newFood.setImage(uri.toString());



                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this,"Fail to Upload"+e.getMessage(),Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== Common.PICK_IMAGE_REQUESR && resultCode==RESULT_OK && data != null && data.getData() !=null){
            selectbt.setText("Image Selected");

            saveUri=data.getData();
        }

    }


    //////Update and Delete ...................

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
        food_referance.child(key).removeValue();
    }
    private void showUpdateDailog(final String key, final Food item) {

        final AlertDialog.Builder alrt=new AlertDialog.Builder(FoodList.this);
        alrt.setTitle("Update Category");
        alrt.setMessage("Enter The Name");

        final LayoutInflater inflater=this.getLayoutInflater();
        View v= inflater.inflate(R.layout.add_new_food_layout,null);
        un= (MaterialEditText) v.findViewById(R.id.item_name);
        ud= (MaterialEditText) v.findViewById(R.id.item_discreption);
        up= (MaterialEditText) v.findViewById(R.id.item_price);
        udiscount= (MaterialEditText) v.findViewById(R.id.item_discount);

        selectbt=(FButton)v.findViewById(R.id.selectBt);
        uploadbt=(FButton)v.findViewById(R.id.uploadBt);

        un.setText(item.getName());
        up.setText(item.getPrice());
        ud.setText(item.getDescription());
        udiscount.setText(item.getDiscount());

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

                item.setName(un.getText().toString());
                item.setDescription(ud.getText().toString());
                item.setPrice(up.getText().toString());
                item.setDiscount(udiscount.getText().toString());

                food_referance.child(key).setValue(item);


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




    private void changeImage(final Food item) {

        final   ProgressDialog mDialog=new ProgressDialog(this);
        mDialog.setMessage("Uploading.....");
        mDialog.show();
        if(saveUri!=null){

            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child("image/"+imageName);

            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(),"Download Successfully completed",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(),"Fail to Upload"+e.getMessage(),Toast.LENGTH_SHORT).show();
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


    /////End

}
