package project.restaurantfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class SearchActivity extends FragmentActivity implements OnMapReadyCallback {

    private boolean mLocationPermissionGranted;

    Polyline polyline;
    PlacesClient pc;
    List<Place.Field> pFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME, Place.Field.ADDRESS);
    AutocompleteSupportFragment pFragment;

    Location destination;

    Location current;
    FusedLocationProviderClient fusedLocationProviderClient;
    final int request_code = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //If passed longitue / lattidude information
        Intent getIntent = getIntent();
        if(getIntent.getExtras() != null)
        {
            destination.setLatitude( getIntent.getFloatExtra("Latitude", 0));
            destination.setLongitude( getIntent.getFloatExtra("Longitude", 0));
        }

        getLocationPermission();

        initPlaces();

        setupAutoComplete();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
             //       .setAction("Action", null).show();

            //Go Back To Main Page
            Intent intent = new Intent(getApplicationContext(), Main_Page.class);
            startActivity(intent);
        });
    }

    private void fetchLastLocation() {

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if(location != null)
            {
                current = location;
                Toast.makeText(getApplicationContext(), current.getLatitude() +""+
                        current.getLongitude(), Toast.LENGTH_SHORT).show();
                SupportMapFragment supportMapFragment = (SupportMapFragment)
                        getSupportFragmentManager().findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(SearchActivity.this);
            }
        });
    }

    private void setupAutoComplete()
    {
        pFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places_autocomplete_fragment);
        assert pFragment != null;
        pFragment.setPlaceFields(pFields);
        pFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Toast.makeText(SearchActivity.this, place.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(SearchActivity.this, ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPlaces()
    {
        String apiKey="AIzaSyCTyRlS0MCx4cQ1jw71jMi_SUcapo_vlg8";
        Places.initialize(this, apiKey);
        pc = Places.createClient(this);
    }

    private void getLocationPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},1);// 1 = Permission Request
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(current.getLatitude(), current.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You Are Here");
        MarkerOptions markerOptions2;

        //If there is a destination
        if(destination != null)
        {
            LatLng newLatLng = new LatLng(destination.getLatitude(), destination.getLongitude());
            markerOptions2 = new MarkerOptions().position(newLatLng).title("You Want To Go Here");

            LatLng avgLatLng = new LatLng(destination.getLatitude() + current.getLatitude()/2,
                    destination.getLongitude() + current.getLongitude()/2);

            googleMap.animateCamera(CameraUpdateFactory.newLatLng(avgLatLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,4));
            googleMap.addMarker(markerOptions);
            googleMap.addMarker(markerOptions2);

            //Create line
            polyline = googleMap.addPolyline(new PolylineOptions()
                    .add(latLng, newLatLng).width(5).color(Color.BLUE));
        }
        else // Just move to current location
        {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,4));
            googleMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == request_code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLastLocation();
            }
        }
    }
}
