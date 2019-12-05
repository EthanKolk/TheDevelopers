package project.restaurantfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class RestaurantInfo extends AppCompatActivity {
    String raw="";
    public static final String EXTRA_MESSAGE = "MainActivity";

    //This is to import the google places api
    PlacesClient ImportGooglePlaces() {
        //We should no technically hardcode the apiKey
        String apiKey = "AIzaSyCTyRlS0MCx4cQ1jw71jMi_SUcapo_vlg8";
        Places.initialize(getApplicationContext(), apiKey);
        return Places.createClient(this);
        //

    }

    List<LatLng> Locations = new ArrayList<LatLng>();

    void GetLocations(PlacesClient placesClient) {


        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();
        //final List<String> restaurantNames=new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            placesClient.findCurrentPlace(request).addOnSuccessListener((response) -> {


                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Place place = placeLikelihood.getPlace();
                    LatLng LL = place.getLatLng();
                    if (LL != null) {
                        Locations.add(LL);
                    }
                }
            }).addOnFailureListener((exception) -> {

                    }
            );
        } else {
            int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }


    }




    public void AddToFavs(View view){//Implement add to favorites

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);

        Intent intent = getIntent();
        raw = intent.getStringExtra(Main_Page.EXTRA_MESSAGE);
        TextView textView = findViewById(R.id.RestInfo);
        textView.setText(raw);

        Button button;
        Button button1;
        button = findViewById(R.id.Back);
        button1 = findViewById(R.id.Log_out);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(RestaurantInfo.this, Main_Page.class);
                j.putExtra(EXTRA_MESSAGE,Locations.get(0).latitude+","+Locations.get(0).longitude);
                startActivity(j);
            }

        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(RestaurantInfo.this, MainActivity.class);
                startActivity(j);
            }

        });

    }
}
