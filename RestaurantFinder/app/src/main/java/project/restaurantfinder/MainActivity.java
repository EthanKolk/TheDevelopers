package project.restaurantfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    public static final String EXTRA_MESSAGE = "MainActivity";
    private Button signin;
    private EditText emailID;
    private EditText passwordID;
    private TextView signup;
    private ProgressDialog progressDialog;
    FirebaseAuth mAuth;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GetLocations(ImportGooglePlaces());


        mAuth = FirebaseAuth.getInstance();
        emailID = (EditText) findViewById((R.id.username_Text));
        passwordID = (EditText) findViewById((R.id.password_Text));
        signin = (Button) findViewById(R.id.button);
        signup = (TextView) findViewById(R.id.CreateAccount);
        signup.setOnClickListener(this);
        signin.setOnClickListener(this);
    }


    String Encrypt(String plain) {//Does nothing, but could implement encryption
        return plain;//Put encryption scheme here. Preferably a hash. Must be one that always
        //returns the same value

    }



    @Override
    public void onClick(View v) {

        if (v == signup) {
            Intent intent = new Intent(v.getContext(), CreateAccount.class);
            startActivity(intent);
        }

        if (v == signin) {

            String email = emailID.getText().toString();
            String password = passwordID.getText().toString();

            if (email.isEmpty()) {
                emailID.setError("Enter Email");
                emailID.requestFocus();
            } else if (password.isEmpty()) {
                passwordID.setError("Enter password");
                passwordID.requestFocus();
            } else {

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    finish();
                                    Intent j = new Intent(MainActivity.this, Main_Page.class);
                                    j.putExtra(EXTRA_MESSAGE,Locations.get(0).latitude+","+Locations.get(0).longitude);
                                    startActivity(j);

                                } else {
                                    Toast.makeText(MainActivity.this, "Log in failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                     }
            }
         }
    }



