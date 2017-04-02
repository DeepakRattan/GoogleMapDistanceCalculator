package com.example.deepakrattan.googlemapdistancecalculator;


import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener, View.OnClickListener {

    private GoogleMap mMap;
    //To store latitude and longitude from map
    private double latitude, longitude;

    //from - the first coordinates from where we need to calculate the distance
    private double fromLatitude, fromLongitude;

    //to - the second coordinates to where we need to calculate the distance
    private double toLatitude, toLongitude;

    //GoogleApiClient
    private GoogleApiClient googleApiClient;

    //Buttons
    private Button btnTo, btnFrom, btnDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //initialize GoogleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        //findViewbyID
        btnFrom = (Button) findViewById(R.id.btnFrom);
        btnTo = (Button) findViewById(R.id.btnTo);
        btnDistance = (Button) findViewById(R.id.btnDistance);

        //Listener
        btnFrom.setOnClickListener(this);
        btnTo.setOnClickListener(this);
        btnDistance.setOnClickListener(this);


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
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        moveMap();


    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        moveMap();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFrom:
                fromLongitude = longitude;
                fromLatitude = latitude;
                Toast.makeText(MapsActivity.this, "Form Set", Toast.LENGTH_LONG).show();
                break;
            case R.id.btnTo:
                toLatitude = latitude;
                toLongitude = longitude;
                Toast.makeText(MapsActivity.this, "To Set", Toast.LENGTH_LONG).show();
                break;
            case R.id.btnDistance:
                calculateDistance();

        }

    }

    public void calculateDistance() {
        LatLng from = new LatLng(fromLatitude, fromLongitude);
        LatLng to = new LatLng(toLatitude, toLongitude);

        double distance = SphericalUtil.computeDistanceBetween(from, to);

        Toast.makeText(this, "Distance in Meters = " + distance, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Distance in Km = " + distance / 1000, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    public void getCurrentLocation() {
        mMap.clear();

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        //Move to location
        moveMap();
    }

    public void moveMap() {
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Current Location"));
        //Move Camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //Animate Camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
}
