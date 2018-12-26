package com.example.dcl.foodshipperapplicaation;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dcl.foodshipperapplicaation.Common.Common;
import com.example.dcl.foodshipperapplicaation.Common.DirectionJSONParser;
import com.example.dcl.foodshipperapplicaation.Model.Request;
import com.example.dcl.foodshipperapplicaation.Remot.IGoCoordinates;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    IGoCoordinates mServer;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallBack;
    LocationRequest locationRequest;
    Location mlocation;
    Marker mCurrentMarker;
    Polyline polyline;
    Button call, shipped;
    String lats,lang;

    /*@Override
    protected void onStart() {
        super.onStart();
        drawRoute(new LatLng(mlocation.getLatitude(),mlocation.getLongitude()),Common.currentRequest);

    }*/

    @Override
    protected void onRestart() {
        super.onRestart();
        drawRoute(new LatLng(mlocation.getLatitude(),mlocation.getLongitude()),Common.currentRequest);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (lats!=null && lang!=null){
            Toast.makeText(getApplicationContext(),"Lat :-"+lats+"lang :-"+lang,Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"Not found",Toast.LENGTH_LONG).show();
        }
        if (Common.currentRequest.getAddress()!=null){
            Toast.makeText(getApplicationContext(),Common.currentRequest.getAddress(),Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getApplicationContext(),"No address ",Toast.LENGTH_LONG).show();

        }


        buildLocationRequest();
        buildLocationCallBack();
        mServer=Common.getGeoCodeService();


        call = (Button) findViewById(R.id.phoneCallToClient);
        shipped = (Button) findViewById(R.id.btnShipped);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Intent.ACTION_CALL);
                in.setData(Uri.parse("tel:"+Common.currentRequest.getPhone()));
                if (ActivityCompat.checkSelfPermission(TrackingOrder.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(in);
            }
        });

        shipped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance()
                        .getReference(Common.SHIPPING_INFO_TABLE)
                        .child(Common.currentShipper.getNumber())
                        .child(Common.currentKey)
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Map<String,Object> order_update=new HashMap<>();
                                order_update.put("status","03");

                                FirebaseDatabase.getInstance()
                                        .getReference("Request")
                                        .updateChildren(order_update)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseDatabase.getInstance()
                                                        .getReference(Common.SHIPPING_INFO_TABLE)
                                                        .child(Common.currentKey)
                                                        .removeValue()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(getApplicationContext(),"Shipped",Toast.LENGTH_LONG).show();
                                                            }
                                                        });

                                            }
                                        });


                                                            }
                        });
            }
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper());
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mlocation = location;
                LatLng yourLocation = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
                mCurrentMarker= mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
            }
        });

    }

    private void buildLocationCallBack() {

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mlocation = locationResult.getLastLocation();

                if (mCurrentMarker!=null){
                    mCurrentMarker.setPosition(new LatLng(mlocation.getLatitude(),mlocation.getLongitude()));
                }
                Common.updateShippingInformation(Common.currentKey,mlocation);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mlocation.getLatitude(),mlocation.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                drawRoute(new LatLng(mlocation.getLatitude(),mlocation.getLongitude()),Common.currentRequest);

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
    protected void onStop() {
        if (fusedLocationProviderClient!=null){
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
        }

        super.onStop();
    }


    private void drawRoute(final LatLng yourLocation, Request request) {

        try {


            if (polyline != null) {

                polyline.remove();
            }
            if (request.getAddress() != null && !request.getAddress().isEmpty()) {


                Toast.makeText(getApplicationContext(), "Address :- " + request.getAddress(), Toast.LENGTH_LONG).show();
                mServer.getGeoCode(request.getAddress()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            String lat = ((JSONArray) jsonObject.get("results"))
                                    .getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONObject("location")
                                    .get("lat").toString();
                            String lng = ((JSONArray) jsonObject.get("results"))
                                    .getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONObject("location")
                                    .get("lng").toString();
                            lats = lat;
                            lang = lng;

                            Toast.makeText(getApplicationContext(), "Order possition:-" + lat + "," + lng, Toast.LENGTH_LONG).show();
                            LatLng orderLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.download);
                            bitmap = Common.scaleBitmap(bitmap, 70, 70);

                            MarkerOptions marker = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                    .title("Order of " + Common.currentRequest.getPhone())
                                    .position(orderLocation);
                            mMap.addMarker(marker);


                            //////derection

                            mServer.getDirection(yourLocation.latitude + "," + yourLocation.longitude, orderLocation.latitude + "," + orderLocation.longitude)
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            new ParserTask().execute(response.body().toString());
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {

                                        }
                                    });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            } else {
                if (request.getLatLng() != null && !request.getLatLng().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Lat-lng :-" + request.getLatLng(), Toast.LENGTH_LONG).show();

                    String[] latalang = request.getLatLng().split(",");
                    LatLng orderLocation = new LatLng(Double.parseDouble(latalang[0]), Double.parseDouble(latalang[1]));

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.download);
                    bitmap = Common.scaleBitmap(bitmap, 70, 70);

                    MarkerOptions marker = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .title("Order of " + Common.currentRequest.getPhone())
                            .position(orderLocation);
                    mMap.addMarker(marker);
                    mServer.getDirection(mlocation.getLatitude() + "," + mlocation.getLongitude(), orderLocation.latitude + "," + orderLocation.longitude)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    new ParserTask().execute(response.body().toString());
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }
                            });


                }
            }
        }catch (Exception e){}
        finally {
            onRestart();
        }

    }


   /* public static  Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap scalerBitmap=Bitmap.createBitmap(newWidth,newHeight, Bitmap.Config.ARGB_8888);

        float scaleX=newWidth/(float)bitmap.getWidth();
        float scaleY=newHeight/(float)bitmap.getHeight();

        float pivotX=0;
        float pivotY=0;

        Matrix mMatrix=new Matrix();
        mMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas=new Canvas(scalerBitmap);
        canvas.setMatrix(mMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return  scalerBitmap;



    }*/



  private   class  ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {


        ProgressDialog mProgressDialog=new ProgressDialog(TrackingOrder.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage("Please waiting....");
            mProgressDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... params) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes=null;
            try {
                jsonObject=new JSONObject(params[0]);

                DirectionJSONParser parser=new DirectionJSONParser();
                routes= parser.parse(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mProgressDialog.dismiss();;
            ArrayList points=null;
            PolylineOptions lineOperation=null;
            for(int i=0; i<lists.size();i++){
                points=new ArrayList();
                lineOperation=new PolylineOptions();
                List<HashMap<String,String>> path= lists.get(i);
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point=path.get(j);
                    double lat=Double.parseDouble(point.get("lat"));
                    double lng=Double.parseDouble(point.get("lng"));
                    LatLng location=new LatLng(lat,lng);
                    points.add(location);
                }
                lineOperation.addAll(points);
                lineOperation.width(12);
                lineOperation.color(Color.RED);
                lineOperation.geodesic(true);

            }
            mMap.addPolyline(lineOperation);
        }
    }


    }





