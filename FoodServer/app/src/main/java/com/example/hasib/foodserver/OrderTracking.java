package com.example.hasib.foodserver;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.hasib.foodserver.Common.Common;
import com.example.hasib.foodserver.Common.DirectionJSONParser;
import com.example.hasib.foodserver.Remote.IGoCoordinates;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderTracking extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;

    private static final int PLAY_SERVICE_RESULATION_REQUEST = 1000;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final int UPDATE_INTERVAAL = 5000;
    private static final int FATEST_INTERVAL = 5001;
    private static final int DISPLAY_ACMENT = 5002;


    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClint;
    private LocationRequest mLocationRequest;

  //  private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private IGoCoordinates mServer;
    private Marker mMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        mServer = Common.getGeoCodeService();
      //  fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
     //   fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestRunTimePermission();

        } else {

            if (checkPlayService()) {
                builGoogleApiClint();
                createLocationRequst();
            }
        }

       // displayLocation();

        if (Common.currentRequest.getAddress()!= null) {
            Toast.makeText(getApplicationContext(), "Client Address " + Common.currentRequest.getAddress(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Halai rohinga", Toast.LENGTH_SHORT).show();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestRunTimePermission();

        } else {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClint);

            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longtitude = mLastLocation.getLongitude();
                LatLng yourLocation = new LatLng(latitude, longtitude);

                mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your Position"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));


                drowRout(yourLocation, Common.currentRequest.getAddress());
            } else {
                Toast.makeText(getApplicationContext(), "Your location do  not found", Toast.LENGTH_SHORT).show();


            }

        }


    }

   /* @Override
    protected void onStop() {

        if (fusedLocationProviderClient!=null){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        super.onStop();
    }
*/

    private void drowRout(final LatLng yourLocation, String address) {

        if (address != null) {
            Toast.makeText(getApplicationContext(), "" + address, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Address not Available ", Toast.LENGTH_SHORT).show();
        }

        mServer.getGeoCode(address).enqueue(new Callback<String>() {
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
                    LatLng orderLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location);
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

    }

    private void createLocationRequst() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLAY_ACMENT);
    }

    protected synchronized void builGoogleApiClint() {

        mGoogleApiClint = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClint.connect();

    }

    private boolean checkPlayService() {

        int requestCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (requestCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(requestCode)) {
                GooglePlayServicesUtil.getErrorDialog(requestCode, this, PLAY_SERVICE_RESULATION_REQUEST).show();

            } else {
                Toast.makeText(getApplicationContext(), "This device dose not support", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }

        return true;
    }


    private void requestRunTimePermission() {
        ActivityCompat.requestPermissions(this, new String[]{

                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION

                }, LOCATION_PERMISSION_REQUEST
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (checkPlayService()) {
                        builGoogleApiClint();
                        createLocationRequst();
                        displayLocation();
                    }


                }
                break;

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


   /*     if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
     /*   fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                mLastLocation=location;

                LatLng yourLocation=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                mMarker=mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));

            }
        });*/

        // Add a marker in Sydney and move the camera

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdate();

    }

    private void startLocationUpdate() {

        ////

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            return;

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClint,mLocationRequest,this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClint.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        displayLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClint!=null){
            mGoogleApiClint.connect();
        }
    }

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>{


        ProgressDialog mProgressDialog=new ProgressDialog(OrderTracking.this);

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
            ArrayList<LatLng> points=new ArrayList<LatLng>();
            PolylineOptions lineOperation = new PolylineOptions();;
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
