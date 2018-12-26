package com.example.hasib.foodserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hasib.foodserver.Common.Common;
import com.example.hasib.foodserver.Interface.ItemClickListener;
import com.example.hasib.foodserver.Model.Myresponce;
import com.example.hasib.foodserver.Model.Notification;
import com.example.hasib.foodserver.Model.Request;
import com.example.hasib.foodserver.Model.Sender;
import com.example.hasib.foodserver.Model.Token;
import com.example.hasib.foodserver.Remote.APIservice;
import com.example.hasib.foodserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    FirebaseRecyclerAdapter<Request,OrderViewHolder>adepter;
    MaterialSpinner spinner;
    APIservice mService;

    @Override
    protected void onStop() {
        super.onStop();
        adepter.stopListening();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference("Request");
        recyclerView=(RecyclerView)findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        loadOrder();

        mService=Common.getFCMClient();



    }

    private void loadOrder() {

        FirebaseRecyclerOptions<Request> options=new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(reference,Request.class)
                .build();

        adepter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull final Request model) {

                viewHolder.OrderId.setText(adepter.getRef(position).getKey());
                viewHolder.OrderStatus.setText(Common.ConvertCodeToStatus((model.getStatus())));
                viewHolder.OrderAdress.setText(model.getAddress());
                viewHolder.OrderNumber.setText(model.getPhone());
                viewHolder.OrderTimeAndDate.setText(Common.getdate(Long.parseLong(adepter.getRef(position).getKey())));
               // viewHolder.OrderComment.setText(model.getComment());

                viewHolder.OrderLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent in = new Intent(OrderStatus.this, OrderTracking.class);
                        in.putExtra("OrderId",adepter.getRef(position).getKey());

                        Common.currentRequest = model;
                        startActivity(in);

                    }
                });

                viewHolder.OrderDilete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reference.child(adepter.getRef(position).getKey()).removeValue();
                    }
                });

                viewHolder.OrderShift.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showShiftDialog(adepter.getRef(position).getKey(),model);
                    }
                });

               /* viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int possition, boolean inClicked) {
                        if (inClicked) {
                            Intent in = new Intent(OrderStatus.this, OrderTracking.class);
                            // in.putExtra("OrderId",adepter.getRef(position).getKey());
                            Common.currentRequest = model;

                            Common.currentRequest = model;
                            startActivity(in);


                        }
                    }

                });*/


            }





            @Override
            public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(view);
            }
        };
        adepter.startListening();
        adepter.notifyDataSetChanged();
        recyclerView.setAdapter(adepter);


       /* adepter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                reference) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, final int position) {

                viewHolder.OrderId.setText(adepter.getRef(position).getKey());
                viewHolder.OrderStatus.setText(Common.ConvertCodeToStatus((model.getStatus())));
                viewHolder.OrderAdress.setText(model.getAdress());
                viewHolder.OrderNumber.setText(model.getPhone());
               // viewHolder.OrderComment.setText(model.getComment());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int possition, boolean inClicked) {
                       if(inClicked) {
                           Intent in = new Intent(OrderStatus.this, OrderTracking.class);
                          // in.putExtra("OrderId",adepter.getRef(position).getKey());
                           Common.currentRequest=model;

                           Common.currentRequest = model;
                           startActivity(in);


                      }
                       *//*else {
                          Intent in = new Intent(OrderStatus.this, OrderTracking.class);

                           Common.currentRequest = model;
                           startActivity(in);

                       }*//*

                    }
                });






            }
        };*/

    }

    private void showShiftDialog(final String key, final Request model) {

        AlertDialog.Builder alrt=new AlertDialog.Builder(this);
        final MaterialSpinner  status,shipper;

        alrt.setTitle("Shift To The Shipper");

        alrt.setMessage("Shift the customer order location to the shipper");

        View v=LayoutInflater.from(getBaseContext()).inflate(R.layout.update_order_layout,null);
        alrt.setView(v);
        status=(MaterialSpinner) v.findViewById(R.id.spinnerSatus);
        shipper=(MaterialSpinner) v.findViewById(R.id.spinnerNumber);

        status.setItems("Placed","On my way","Shipped");



        final List<String> shipperList=new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Shipper")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot shipperlist:dataSnapshot.getChildren()){
                            shipperList.add(shipperlist.getKey());

                            shipper.setItems(shipperList);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


       alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
               model.setStatus(String.valueOf(status.getSelectedIndex()));


               if (model.getStatus().equals("2")){

                   FirebaseDatabase.getInstance().getReference(Common.oderShip)
                           .child(shipper.getItems().get(shipper.getSelectedIndex()).toString())
                           .child(key)
                           .setValue(model);

                   reference.child(key).setValue(model);
                   adepter.notifyDataSetChanged();
               }

               sendShipperRequest(shipper.getItems().get(shipper.getSelectedIndex()).toString(),model);


           }
       });




alrt.show();



    }

    private void sendShipperRequest(String shipperPhone, Request model) {

        DatabaseReference tokens=firebaseDatabase.getReference("Token");

        tokens.child(shipperPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshort:dataSnapshot.getChildren()){


                           Token token=new Token();

                            for (DataSnapshot ds:postSnapshort.getChildren()) {

                               token.setToken(ds.child("token").toString());
                               //token.setServerToken((Boolean) ds.child("serverToken").getValue());


                            }

                          //  Token token=postSnapshort.getValue(Token.class) ;

                          /*  for ( DataSnapshot ds:dataSnapshot.getChildren()) {
                                token.setToken(ds.child("token").toString());
                                token.setServerToken((Boolean)ds*//*.child("serverToken")*//*.getValue());
                            }
*/
                          //  Token token=postSnapshort.getValue(Token.class) ;

                         //   Toast.makeText(getApplicationContext(),""+token.getToken()+" "+token.isServerToken(),Toast.LENGTH_SHORT).show();

                           /* Map<String,String> connect=new HashMap<>();
                            connect.put("title","Food Application");
                            connect.put("message","You Have new Order And ship");*/

                            String title="Food Application";
                            String message="You Have new Order And ship";
                            Map<String,String> content=new HashMap<>();
                            content.put("title",title);
                            content.put("message",message);

                          Sender send=new Sender(token.getToken(),content);

                          mService.sendNotification(send)
                                  .enqueue(new Callback<Myresponce>() {
                                      @Override
                                      public void onResponse(Call<Myresponce> call, Response<Myresponce> response) {
                                         /* if (response.body().success==1){
                                              Toast.makeText(getApplicationContext(),"Successs",Toast.LENGTH_SHORT).show();
                                          }*/
                                      }

                                      @Override
                                      public void onFailure(Call<Myresponce> call, Throwable t) {
                                          Toast.makeText(getApplicationContext(),"Faile to send",Toast.LENGTH_SHORT).show();

                                      }
                                  });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

  /*  @Override
    public boolean onContextItemSelected(MenuItem item) {


        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDailog(adepter.getRef(item.getOrder()).getKey(),adepter.getItem(item.getOrder()));
        }

        if(item.getTitle().equals(Common.DELETE)){
            showDeleteDailog(adepter.getRef(item.getOrder()).getKey());
            Toast.makeText(getApplication(),"Deleted!!",Toast.LENGTH_SHORT).show();
        }

        return super.onContextItemSelected(item);

        }*/



    private void showUpdateDailog(String key, final Request item) {
        final AlertDialog.Builder alrt=new AlertDialog.Builder(this);
        alrt.setTitle("Update Order");
        alrt.setMessage("Please Chooice Status");
        LayoutInflater inflater=this.getLayoutInflater();
       // inflater.inflate(R.layout.)

       View v=inflater.inflate(R.layout.update_order_layout,null);
        spinner= (MaterialSpinner) v.findViewById(R.id.spinnerSatus);
        spinner.setItems("Placed","On my wat","Shipped");

        alrt.setView(v);

      final String locakKey=key;

        alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();;
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                reference.child(locakKey).setValue(item);
              sendOrderStatusToUser(locakKey,item);

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

    private void sendOrderStatusToUser(final String key,Request item) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        final DatabaseReference referance=db.getReference("Token");
        referance.orderByKey().equalTo(item.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshort:dataSnapshot.getChildren()){
                    Token token=dataSnapshort.getValue(Token.class);

                    HashMap<String,String> contenr=new HashMap<>();

                    contenr.put("title","Food Application");
                    contenr.put("message","Your order "+key+"has been updated");

                   // Notification notification=new Notification("Akon Resturent","Your Order"+key+"was updeted");

                    Sender content=new Sender(token.getToken(),contenr);
                    mService.sendNotification(content)
                            .enqueue(new Callback<Myresponce>() {
                                @Override
                                public void onResponse(Call<Myresponce> call, Response<Myresponce> response) {

                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(getApplicationContext(), "Your Order is updeted", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed!!!", Toast.LENGTH_SHORT).show();


                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<Myresponce> call, Throwable t) {
                                    Log.e("Error",t.getMessage());


                                }
                            });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showDeleteDailog(String key ){

        reference.child(key).removeValue();


    }

}
