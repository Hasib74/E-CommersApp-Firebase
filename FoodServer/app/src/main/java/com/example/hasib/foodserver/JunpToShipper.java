package com.example.hasib.foodserver;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hasib.foodserver.Interface.ItemClickListener;
import com.example.hasib.foodserver.Model.Shipper;
import com.example.hasib.foodserver.ViewHolder.ShipperViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JunpToShipper extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    List<Shipper> shipper_list=new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton additem;

    FirebaseRecyclerAdapter<Shipper,ShipperViewHolder> adepter;

    @Override
    protected void onStop() {
        super.onStop();
        adepter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_junp_to_shipper);

        recyclerView=(RecyclerView)findViewById(R.id.shipper_list);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        additem=(FloatingActionButton)findViewById(R.id.addShpper);

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("Shipper");

        Query query=databaseReference;

        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemForShipper();
            }
        });

        FirebaseRecyclerOptions<Shipper> options =new FirebaseRecyclerOptions.Builder<Shipper>()
                .setQuery(query,Shipper.class)
                .build();


        adepter=new FirebaseRecyclerAdapter<Shipper, ShipperViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ShipperViewHolder holder, final int position, @NonNull Shipper model) {
                //Shipper shipper=shipper_list.get(position);

                holder.Shippername.setText(model.getName());
                holder.ShipperPassword.setText(model.getPassword());
                holder.ShipperNumber.setText(model.getNumber());

                holder.Update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adepter.getRef(position).getKey(),holder);
                    }
                });
                holder.Delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteDialog(adepter.getRef(position).getKey());
                    }
                });

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int possition, boolean inClicked) {
                        //showSetEditDialog();
                    }
                });



            }

            @Override
            public ShipperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.sheipper_layout_style,parent,false);

                return new ShipperViewHolder(v);
            }
        };

        recyclerView.setAdapter(adepter);
        adepter.startListening();

    }

    private void showDeleteDialog(String key) {

        databaseReference.child(key)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Successfull",Toast.LENGTH_SHORT).show();

                    }
                });
        adepter.notifyDataSetChanged();
    }

    private void showUpdateDialog(final String key, ShipperViewHolder holder) {
        AlertDialog.Builder alrt=new AlertDialog.Builder(this);

        alrt.setTitle("Update");
        alrt.setMessage("Update  data");

        View v=LayoutInflater.from(getBaseContext()).inflate(R.layout.update_shipper_style,null);

        alrt.setView(v);

        final EditText name,number,password;

        name=(EditText)v.findViewById(R.id.editName) ;
        number=(EditText)v.findViewById(R.id.editNumber);
        password=(EditText)v.findViewById(R.id.edtPassword);


        name.setText(holder.Shippername.getText().toString());
        number.setText(holder.ShipperNumber.getText().toString());
        password.setText(holder.ShipperPassword.getText().toString());








        alrt.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


               String Uname=name.getText().toString();
               String Unumber=number.getText().toString();
               String Upassword=password.getText().toString();

                Map<String,Object> update=new HashMap<>();

                update.put("name",Uname);
                update.put("number",Unumber);
                update.put("password",Upassword);

                databaseReference.child(key)
                        .updateChildren(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"Update Successfully",Toast.LENGTH_SHORT).show();
                            }
                        });


                dialog.dismiss();



            }
        });


        alrt.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
alrt.show();

    }



    private void addItemForShipper() {

        final EditText Shippername,Shippernumber,ShipperPassword;

        final AlertDialog.Builder alrt=new AlertDialog.Builder(this);
        alrt.setTitle("Add Shipper");
        alrt.setMessage("Shipper will handover the order");

        View v=LayoutInflater.from(this).inflate(R.layout.add_shipper,null);

        alrt.setView(v);

        Shippername=(EditText)v.findViewById(R.id.addName);
        Shippernumber =(EditText)v.findViewById(R.id.addPhoneNumber);
        ShipperPassword=(EditText)v.findViewById(R.id.addPassword);

        alrt.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String name=Shippername.getText().toString();
                String number=Shippernumber.getText().toString();
                String password=ShipperPassword.getText().toString();

                Shipper shipper=new Shipper(name,number,password);


                databaseReference.child(number).setValue(shipper)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"Successfully data Add to the Firebase",Toast.LENGTH_SHORT).show();
                            }
                        });

                alrt.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                    }
                });

            }

        });



alrt.show();

    }


}
