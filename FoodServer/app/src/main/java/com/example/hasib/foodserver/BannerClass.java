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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hasib.foodserver.Common.Common;
import com.example.hasib.foodserver.Interface.ItemClickListener;
import com.example.hasib.foodserver.Model.Bannar;
import com.example.hasib.foodserver.ViewHolder.bannerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class BannerClass extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference food_referance;
    FirebaseStorage storage;
    StorageReference storageReference;
    FloatingActionButton floatingActionButton;
    RelativeLayout relativeLayout;
    EditText updateName,updateId;
    Button selectbt,uploadbt;
    Bannar bn;


    FirebaseRecyclerAdapter<Bannar,bannerViewHolder> adepter;
    private Uri saveUri;

    @Override
    protected void onStop() {
        super.onStop();
        adepter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adepter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_class);


        firebaseDatabase=FirebaseDatabase.getInstance();
        food_referance=firebaseDatabase.getReference("Bannar");

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        recyclerView=(RecyclerView)findViewById(R.id.banner_recycularview);
        recyclerView.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);




        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showaddialog();
            }
        });


      loadFood();

    }

    private void showaddialog() {

        AlertDialog.Builder alrt=new AlertDialog.Builder(this);
        alrt.setTitle("Add Banner");
        alrt.setMessage("You will add banner to set your ui");
        View v=LayoutInflater.from(this).inflate(R.layout.add_banner,null);
    }

    private void loadFood() {


        FirebaseRecyclerOptions<Bannar> options = new FirebaseRecyclerOptions.Builder<Bannar>()
                .setQuery(food_referance, Bannar.class)
                .build();


        adepter = new FirebaseRecyclerAdapter<Bannar, bannerViewHolder>(options) {



            @Override
            public bannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item,parent,false);

                return new bannerViewHolder(v) ;
            }


            @Override
            protected void onBindViewHolder(@NonNull bannerViewHolder viewHolder, final int position, @NonNull Bannar model) {
                viewHolder.banner_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.banner_image);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int possition, boolean inClicked) {
                        Toast.makeText(getApplicationContext(),"Possition :-"+adepter.getRef(position),Toast.LENGTH_SHORT).show();
                    }
                });

            }



        };
        adepter.startListening();
        adepter.notifyDataSetChanged();

        recyclerView.setAdapter(adepter);
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
        food_referance.child(key).removeValue();
        adepter.notifyDataSetChanged();

    }

    private void showUpdateDailog(final String key, final Bannar item) {

      


        final AlertDialog.Builder alrt=new AlertDialog.Builder(BannerClass.this);
        alrt.setTitle("Update banner");
        alrt.setMessage("Enter The Name");


        final LayoutInflater inflater=this.getLayoutInflater();
        View v= inflater.inflate(R.layout.update_banner,null);

        updateName= (EditText) v.findViewById(R.id.update_banner_name);
        updateId= (EditText) v.findViewById(R.id.update_banner_id);


        selectbt=(Button) v.findViewById(R.id.selectBt);
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
                uploadImage(item);
            }
        });


        alrt.setView(v);
        alrt.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                item.setName(updateName.getText().toString());
                item.setId(updateId.getText().toString());



                food_referance.child(key).setValue(item);
                    //  Snackbar.make(relativeLayout,"New Category"+newFood.getName()+"was add",Snackbar.LENGTH_SHORT).show();




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

    private void uploadImage(final Bannar item) {
        final ProgressDialog mDialog=new ProgressDialog(this);
        mDialog.setMessage("Uploading.....");
        mDialog.show();

        if(saveUri!=null){

            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child("image/"+imageName);

            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(BannerClass.this,"Download Successfully completed",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(BannerClass.this,"Fail to Upload"+e.getMessage(),Toast.LENGTH_SHORT).show();
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
            saveUri = data.getData();
        }

    }
}
