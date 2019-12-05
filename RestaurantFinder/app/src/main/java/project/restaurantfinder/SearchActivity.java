package project.restaurantfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
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

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends FragmentActivity implements OnMapReadyCallback {

    private boolean mLocationPermissionGranted;

    static GoogleMap map;
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        //initPlaces();

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
        /*pFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places_autocomplete_fragment);
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
        });*/
    }

    //initializing stuff
    private void initPlaces()
    {
        //String apiKey="AIzaSyCTyRlS0MCx4cQ1jw71jMi_SUcapo_vlg8";
        //Places.initialize(this, apiKey);
       // pc = Places.createClient(this);
    }

    //Before anything else, make sure we have permissions
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

    //Will be automatically called
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        /*//LatLng latLng = new LatLng(current.getLatitude(), current.getLongitude());
        //MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You Are Here");
        //MarkerOptions markerOptions2;

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

            String url = getUrl(latLng, newLatLng);

            FetchUrl fetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            fetchUrl.execute(url);

            //Create line
            polyline = googleMap.addPolyline(new PolylineOptions()
                    .add(latLng, newLatLng).width(5).color(Color.BLACK));
        }
        else // Just move to current location
        {
           // googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
           // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,4));
            //googleMap.addMarker(markerOptions);
        }*/
    }

    // Code for fetching, parsing, and drawing routes from:
    // https://www.specbee.com/blogs/android-tutorials-google-map-drawing-routes-between-two-points
    // https://acadgild.com/blog/how-to-draw-route-on-google-maps-in-android

    //Fetch from the provided url - Basic AsycTask implementation
    private static class FetchUrl extends AsyncTask<String, Void, String>
    {
        //fetch
        @Override
        protected String doInBackground(String... url)
        {
            String strUrl = "";
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL realUrl = new URL(strUrl);
                urlConnection = (HttpURLConnection) realUrl.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuilder sb = new StringBuilder();

                String line = "";

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                br.close();

            } catch (Exception e) {
                //Nothing
            } finally {
                try {
                    iStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
            return data;
        }

        //parse
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }


    //Parse json data from the url
    private static class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
    {
        // Parsing asynchronously to avoid slow down
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in main thread

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result)
        {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++)
            {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++)
                {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
            }

            // Drawing poly lines on the map
            if(lineOptions != null)
            {
                map.addPolyline(lineOptions);
            }
        }
    }

    //Get this url for routes
    private String getUrl(LatLng current, LatLng destination)
    {
        String str_origin = "origin=" + current.latitude + "," + current.longitude;
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";

        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&mode=driving";
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    //Result of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == request_code)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                fetchLastLocation();
            }
        }
    }
}

//Outside of search activity class
class DataParser {

    List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {
            jRoutes = jObject.getJSONArray("routes");
            for(int i=0;i<jRoutes.length();i++)
            {
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();
                for(int j=0;j<jLegs.length();j++)
                {
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                    for(int k=0;k<jSteps.length();k++)
                    {
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);
                        for(int l=0;l<list.size();l++)
                        {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude) );
                            hm.put("lng", Double.toString((list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

            //If there is a problem
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return routes;
    }


    //Method to decode polyline points
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len)
        {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }
            while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }
            while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}