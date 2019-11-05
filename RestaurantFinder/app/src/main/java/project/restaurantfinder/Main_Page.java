package project.restaurantfinder;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.content.Context;
//import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
//For debug
import android.util.Log;

import android.app.ActivityManager;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.content.Intent;

import java.util.Arrays;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Main_Page extends AppCompatActivity {


    //This is to import the google places api
    PlacesClient ImportGooglePlaces(){
        //We should no technically hardcode the apiKey
        String apiKey="AIzaSyCTyRlS0MCx4cQ1jw71jMi_SUcapo_vlg8";
        Places.initialize(getApplicationContext(), apiKey);
        return Places.createClient(this);
        //

    }

    //public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback){}


    void GoogleStuff(PlacesClient placesClient) {

        // Use fields to define the data types to return.
        List<Field> placeFields = Arrays.asList(Field.NAME,Field.ADDRESS_COMPONENTS);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request=FindCurrentPlaceRequest.builder(placeFields).build();
        final List<String> restaurantNames=new ArrayList<String>();
        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            placesClient.findCurrentPlace(request).addOnSuccessListener(((response) -> {


                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    restaurantNames.add(placeLikelihood.getPlace().getName());
            }
                }));
        } else {
            int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=100;
            ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            GoogleStuff(placesClient);
        }

        //for (String i : restaurantNames){
        //Log.i("Place",placeFields.get(1));

        //}


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Places.initialize(getApplicationContext(), apiKey);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__page);

        Log.i("Place","1");
        //Activity currentActivity = ((MyApp)getApplicationContext()).getCurrentActivity() ;
        Log.i("Place","2");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        PlacesClient googlePlaces=ImportGooglePlaces();
        GoogleStuff(googlePlaces);

    }

}
