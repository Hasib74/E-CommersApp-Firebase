package com.example.dcl.foodshipperapplicaation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dcl.foodshipperapplicaation.Common.Common;
import com.example.dcl.foodshipperapplicaation.Model.ShipperInformation;
import com.example.dcl.foodshipperapplicaation.Model.Token;

import com.example.dcl.foodshipperapplicaation.Model.Request;
import com.example.dcl.foodshipperapplicaation.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class HomeActivity extends AppCompatActivity {

    //  fusedLocationClient;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallBack;
    LocationRequest locationRequest;
    Location mlocation;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Request request;
    String adepterPossition;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adepter;




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        recyclerView=(RecyclerView)findViewById(R.id.orderRecycular);
        layoutManager=new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference(Common.ORDER_NEED_SHIP_TABLE);

        updateTokenShipper(FirebaseInstanceId.getInstance().getToken());
        loadorderNeedShip(Common.currentShipper.getNumber());





        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

       && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, Common.REQUEST_CODE
            );
        } else {
            buildLocationRequest();
            buildLocationCallBack();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper());
        }

        //if (Applica)





    }



    private void loadorderNeedShip(String number) {
        DatabaseReference phone=databaseReference.child(number);

        FirebaseRecyclerOptions<Request> options=new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(phone,Request.class)
                .build();

        adepter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull final Request model) {

                viewHolder.OrderId.setText(adepter.getRef(position).getKey());
                viewHolder.OrderStatus.setText(Common.ConvertCodeToStatus((model.getStatus())));
                viewHolder.OrderAdress.setText(model.getAddress());
                viewHolder.OrderNumber.setText(model.getPhone());
                viewHolder.OrderTimeAndDate.setText(Common.getdate(Long.parseLong(adepter.getRef(position).getKey())));

                viewHolder.OrderShift.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        request =model;
                        adepterPossition=adepter.getRef(position).getKey();







                        // Toast.makeText(getApplicationContext(),"Lat"+mlocation.getLatitude(),Toast.LENGTH_LONG).show();
                      //  Toast.makeText(getApplicationContext(),"Ok",Toast.LENGTH_SHORT).show();
                        createShipperOrder(adepter.getRef(position).getKey(),Common.currentShipper.getNumber()
                        ,mlocation.getLatitude(),mlocation.getLongitude());

                        Common.currentRequest=model;
                        Common.currentKey=adepter.getRef(position).getKey();
                        startActivity(new Intent(HomeActivity.this,TrackingOrder.class));

                    }
                });

            }




            @Override
            public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_style,parent,false);


                return new OrderViewHolder(v);
            }
        };

        adepter.startListening();
        recyclerView.setAdapter(adepter);
        adepter.notifyDataSetChanged();
    }

    private void updateTokenShipper(String token) {

        DatabaseReference tokenref=FirebaseDatabase.getInstance().getReference("Token");

        Token data=new Token(token,false);

        tokenref.child(Common.currentShipper.getNumber())
                .setValue(data);



    }

    private void buildLocationCallBack() {

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mlocation = locationResult.getLastLocation();
                //Double lat=mlocation.getLatitude();
                Toast.makeText(getApplicationContext(),"Your location :"+mlocation.getLatitude()+"/"+mlocation.getLongitude(),Toast.LENGTH_LONG).show();

            }
        };
    }

    @SuppressLint("RestrictedApi")
    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(3000);
        locationRequest.setInterval(5000);
        locationRequest.setSmallestDisplacement(10f);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        loadorderNeedShip(Common.currentShipper.getNumber());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fusedLocationProviderClient!=null){
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
        }
        if (adepter!=null) {
            adepter.stopListening();
        }
    }
    public static void createShipperOrder(String key, String number, Double lat,Double lon) {
        ShipperInformation shipperInformation =new ShipperInformation();
        shipperInformation.setOrderId(key);
        shipperInformation.setShipperPhone(number);
        shipperInformation.setLat(lat);
        shipperInformation.setLng(lon);

        FirebaseDatabase.getInstance()
                .getReference(Common.SHIPPING_INFO_TABLE)
                .child(key)
                .setValue(shipperInformation)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR_TABLE_INFO",e.getMessage());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        final int a = 5;
        //a =String.valueOf(10);
        switch (requestCode) {
            case Common.REQUEST_CODE :
                {

                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                     //   buildLocationRequest();
                     //   buildLocationCallBack();

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper());

                    }
                    else {
                        Toast.makeText(getApplicationContext(),"You Sould Assign Permission",Toast.LENGTH_SHORT).show();
                    }
                }

                break;


            }
            default:
                break;
        }
    }

    public class  startAnotherActivity extends AsyncTask<Void,Void,Void>{

      //  ProgressDialog progressBar =new ProgressDialog(HomeActivity.this);

        @Override
        protected Void doInBackground(Void... voids) {



          /*  progressBar.setMessage("Please Wait...");

            progressBar.show();
*/
            createShipperOrder(adepterPossition,Common.currentShipper.getNumber()
                    ,mlocation.getLatitude(),mlocation.getLongitude());

            Common.currentRequest=request;
            Common.currentKey=adepterPossition;


            return  null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
          //  progressBar.dismiss();

            startActivity(new Intent(HomeActivity.this,TrackingOrder.class));
        }
    }
}
