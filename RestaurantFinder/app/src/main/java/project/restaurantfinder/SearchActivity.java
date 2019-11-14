package project.restaurantfinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private boolean mLocationPermissionGranted;

    PlacesClient pc;
    List<Place.Field> pFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME, Place.Field.ADDRESS);
    AutocompleteSupportFragment pFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getLocationPermission();

        initPlaces();

        setupAutoComplete();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setupAutoComplete()
    {
        pFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places_autocomplete_fragment);
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
}
