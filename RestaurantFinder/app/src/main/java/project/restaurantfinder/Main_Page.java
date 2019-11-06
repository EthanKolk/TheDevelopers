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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
//For debug
import android.os.StrictMode;
import android.util.Log;

import android.app.ActivityManager;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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


    String ReadURL(){//Method to get information from a google places url
        //This method may cause lock up in low internet areas (can instead use async)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String urlText="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyCTyRlS0MCx4cQ1jw71jMi_SUcapo_vlg8";
        URL url;
        String Data="";
        InputStream inputStream=null;
        HttpURLConnection urlConnection=null;
        try {
            url=new URL(urlText);
            urlConnection=(HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream=urlConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer=new StringBuffer();
            String line="";
            while((line=bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }

            Data=stringBuffer.toString();
            inputStream.close();
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Error";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
        return Data;
    }

    class Restaurant{//Holds data about one restaurant
        String lat,lng,icon,name,id,price,rate,address;
        Boolean open;
        List<String> types;
        Restaurant(String lat,String lng,String icon,String name,String id,String price
                ,String rate,String address,Boolean open,List<String> types){

            this.lat=lat;this.lng=lng;this.icon=icon;this.name=name;this.id=id;
            this.price=price;this.rate=rate;this.address=address;this.open=open;
            this.types=types;
        }

    }

    class RestaurantManager{//Holds all restaurants given

        List<Restaurant> All;
        void AddPage(String lat,String lng,String icon,String name,String id,String price
                ,String rate,String address,Boolean open,List<String> types){

            All.add(new Restaurant(lat,lng,icon,name,id,price,rate,address,open,types));

        }

        void Clear(){
            All.clear();

        }

        void FromData(String data){//Creates a restaurant object from a string
            String current="";
            Boolean Start=false;
            String lat="",lng="",icon="",name="",id="",price="",rate="",address="";
            Boolean open=false;
            List<String> types=new ArrayList<String>();
            int len=data.length();
            Boolean firstLat=true;
            Boolean firstLng=true;
            for (int i =0;i<len;i++){
                char t=data.charAt(i);
                if(t=='\"'){
                    String curHead="";
                    i++;
                    while(i<len&&data.charAt(i)!='\"'){
                        curHead+=data.charAt(i);
                        i++;

                    }
                    //Log.i("SuperGreat",curHead);
                    //This one is the big oof
                    if(curHead.equals("lat")&&firstLat){
                        firstLat=false;
                        i+=3;
                        while(data.charAt(i)!=','){
                            lat+=data.charAt(i);
                            i++;
                        }


                    }else if(curHead.equals("lng")&&firstLng){
                        firstLng=false;
                        i+=3;
                        i+=3;
                        while(data.charAt(i)!='}'){
                            lng+=data.charAt(i);
                            i++;
                        }


                    }else if (curHead.equals("icon")){
                        i+=4;
                        while(data.charAt(i)!='\"'){
                            icon+=data.charAt(i);
                            i++;
                        }
                    }else if (curHead.equals("id")){
                        i+=4;
                        while(data.charAt(i)!='\"'){
                            icon+=data.charAt(i);
                            i++;
                        }
                    }else if (curHead.equals("name")){
                        i+=4;
                        while(data.charAt(i)!='\"'){
                            name+=data.charAt(i);
                            i++;
                        }
                    }else if (curHead.equals("open_now")){
                        i+=4;
                        if (data.charAt(i)=='t'){
                            open=true;
                        }else{
                            open=false;
                        }
                    }else if (curHead=="place_id"){
                        i+=4;
                        while(data.charAt(i)!='\"'){
                            id+=data.charAt(i);
                            i++;
                        }
                    }else if (curHead=="rating"){
                        i+=3;
                        while(data.charAt(i)!=','){
                            rate+=data.charAt(i);
                            i++;
                        }
                    }else if (curHead=="types"){
                        i+=5;
                        String temp="";
                        while (data.charAt(i)!=']') {
                            if (data.charAt(i) == '\"'){
                                while (data.charAt(i) != '\"') {
                                    temp += data.charAt(i);
                                    i++;
                                }
                                i++;
                                types.add(temp);
                        }
                            i++;
                    }
                    }else if (curHead=="vicinity"){
                        firstLat=true;
                        firstLng=true;
                        i+=4;
                        while(data.charAt(i)!='\"'){
                            address+=data.charAt(i);
                            i++;
                        }
                        //Make a new node
                    }
                    //

                }
            }
        }

    }

    //This is to import the google places api
    PlacesClient ImportGooglePlaces(){
        //We should no technically hardcode the apiKey
        String apiKey="AIzaSyCTyRlS0MCx4cQ1jw71jMi_SUcapo_vlg8";
        Places.initialize(getApplicationContext(), apiKey);
        return Places.createClient(this);
        //

    }

    //public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback){}


    //This gets the user location as a google place
    void GooglePlacesPlace(PlacesClient placesClient) {

        // Use fields to define the data types to return.
        List<Field> placeFields = Arrays.asList(Field.NAME);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request=FindCurrentPlaceRequest.builder(placeFields).build();
        final List<String> restaurantNames=new ArrayList<String>();
        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Log.i("Help","Have permission");
            placesClient.findCurrentPlace(request).addOnSuccessListener(((response) -> {


                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Place place =placeLikelihood.getPlace();
                    String name=place.getName();
                    if(name!=null) {
                        Log.i("Help", name);
                    }
            }
                })).addOnFailureListener((exception)-> {

                }
            );
        } else {
            int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=100;
            ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            //GoogleStuff(placesClient);
            //Log.i("Help","Error");
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Places.initialize(getApplicationContext(), apiKey);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__page);

        //Activity currentActivity = ((MyApp)getApplicationContext()).getCurrentActivity() ;

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

        //PlacesClient googlePlaces=ImportGooglePlaces();
        String data=ReadURL();
        RestaurantManager restaurantManager= new RestaurantManager();
        restaurantManager.FromData(data);

    }

}
