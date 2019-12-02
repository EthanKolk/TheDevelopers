package project.restaurantfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "MainActivity";

    //This is to import the google places api
    PlacesClient ImportGooglePlaces(){
        //We should no technically hardcode the apiKey
        String apiKey="AIzaSyCTyRlS0MCx4cQ1jw71jMi_SUcapo_vlg8";
        Places.initialize(getApplicationContext(), apiKey);
        return Places.createClient(this);
        //

    }

    List<LatLng> Locations=new ArrayList<LatLng>();
    void GetLocations(PlacesClient placesClient) {


        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request=FindCurrentPlaceRequest.builder(placeFields).build();
        //final List<String> restaurantNames=new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            placesClient.findCurrentPlace(request).addOnSuccessListener((response) -> {


                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Place place =placeLikelihood.getPlace();
                    LatLng LL=place.getLatLng();
                    if(LL!=null) {
                        Locations.add(LL);
                    }
                }
            }).addOnFailureListener((exception)-> {

                    }
            );
        } else {
            int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=100;
            ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GetLocations(ImportGooglePlaces());
    }
/*
    //Use to authenticate user creds on login
    public boolean Authenticate(String user,String pass){


        //Replace this statement with some other authentication code
      private FirebaseAuth mAuth;
        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });


        //Replace this statement with some other authentication code
//        if(user.equals("user")&&pass.equals("pass")){
//            return true;
//
//        }

        return false;
    } */

    Boolean Authenticate(String user,String pass){
        return true;
    }
    public void LogIn(View view) {
        Intent intent = new Intent(this, Main_Page.class);
        EditText usernameT = (EditText) findViewById(R.id.username_Text);
        String username = usernameT.getText().toString();
        EditText passwordT = (EditText) findViewById(R.id.password_Text);
        String password = passwordT.getText().toString();

        if(Authenticate(username,password)) {
            intent.putExtra(EXTRA_MESSAGE,Locations.get(0).latitude+","+Locations.get(0).longitude);
            startActivity(intent);
        } else {
            TextView error = findViewById(R.id.error_text);
            error.setVisibility(View.VISIBLE);
        }
    }


}
