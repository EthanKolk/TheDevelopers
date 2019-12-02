package project.restaurantfinder;

import android.content.pm.PackageManager;
import android.os.Bundle;

//import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
//For debug
import android.os.StrictMode;
import android.util.Log;

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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import java.util.Arrays;
import java.util.Random;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class Main_Page extends AppCompatActivity {


    public static final String EXTRA_MESSAGE ="Main_Page";

    String ReadURL(String latlng, int radius){//Method to get information from a google places url
        //This method may cause lock up in low internet areas (can instead use async)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //String urlText="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1000&type=restaurant&keyword=cruise&key=AIzaSyCTyRlS0MCx4cQ1jw71jMi_SUcapo_vlg8";
        String urlText="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+latlng+"&radius="+radius+"&type=restaurant&key=AIzaSyCTyRlS0MCx4cQ1jw71jMi_SUcapo_vlg8";
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
        String lat,lng,icon,name,price,rate,address;
        Boolean open;
        List<String> types;
        Restaurant(String lat,String lng,String icon,String name,String price
                ,String rate,String address,Boolean open,List<String> types){

            this.lat=lat;this.lng=lng;this.icon=icon;this.name=name;
            this.price=price;this.rate=rate;this.address=address;this.open=open;
            this.types=types;
        }
        String GetName(){
            return name;
        }

        String GetRaw(){
            String re;
            re="Name: "+name+",\nAddress: "+address+",\nPrice: "+price+",\nRating: "+rate+",\nOpen: ";
            if(open){
                re=re+"Yes";
            }else{
                re=re+"No";
            }
            re=re+",\nTypes:\n";
            if(types.size()>0) {
                for (String s : types) {
                    re = re + s + "\n";

                }
            }
            return re;
        }

    }

    class RestaurantManager{//Holds all restaurants given

        List<Restaurant> All=new ArrayList<>();
        void AddPage(String lat,String lng,String icon,String name,String price
                ,String rate,String address,Boolean open,List<String> types){
            All.add(new Restaurant(lat,lng,icon,name,price,rate,address,open,types));

        }

        void Clear(){
            All.clear();

        }

        Restaurant ByName(String name){
            for (Restaurant r:All){
                if(r.GetName()==name){
                    return r;
                }
            }
            return null;

        }

        void FromData(String data){//Creates a restaurant object from a string
            Clear();
            String lat="",lng="",icon="",name="",price="",rate="",address="";
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

                    //This one is the big oof
                    if(curHead.equals("lat")&&firstLat){
                        firstLat=false;
                        i+=3;
                        while(data.charAt(i)!=','){
                            lat+=data.charAt(i);
                            i++;
                        }
                        //Log.i("Super", "Lat"+lat);


                    }else if(curHead.equals("lng")&&firstLng){
                        firstLng=false;
                        i+=3;
                        while(data.charAt(i)!='}'){
                            lng+=data.charAt(i);
                            i++;
                        }
                        //Log.i("Super", "Lng"+lng);


                    }else if (curHead.equals("icon")){
                        i+=5;
                        while(data.charAt(i)!='\"'){
                            icon+=data.charAt(i);
                            i++;
                        }
                        //Log.i("Super", "Icon"+icon);

                    }else if (curHead.equals("name")){
                        i+=5;
                        while(data.charAt(i)!='\"'){
                            name+=data.charAt(i);
                            i++;
                        }
                        //Log.i("Super", "Name"+name);
                    }else if (curHead.equals("open_now")){
                        i+=4;
                        if (data.charAt(i)=='t'){
                            open=true;
                        }else{
                            open=false;
                        }
                        //Log.i("Super", "open"+data.charAt(i));
                    }else if (curHead.equals("price_level")){
                        i+=4;
                        while(data.charAt(i)!=','){
                            price+=data.charAt(i);
                            i++;
                        }
                        //Log.i("Super", "Price "+price);

                    } else if (curHead.equals("rating")){
                        i+=4;
                        while(data.charAt(i)!=','){
                            rate+=data.charAt(i);
                            i++;
                        }
                        //Log.i("Super", "rate"+rate);
                    }else if (curHead.equals("types")){
                        i+=1;
                        String temp="";
                        while (data.charAt(i)!=']') {
                            if (data.charAt(i) == '\"'){
                                i++;
                                while (data.charAt(i) != '\"') {
                                    temp += data.charAt(i);
                                    i++;
                                }
                                i++;
                                types.add(temp);
                                //Log.i("Super", "Type"+temp);
                        }
                            i++;
                    }
                    }else if (curHead.equals("vicinity")){
                        firstLat=true;
                        firstLng=true;
                        i+=5;
                        while(data.charAt(i)!='\"'){
                            address+=data.charAt(i);
                            i++;
                        }
                        //Make a new node
                        //Log.i("Super","Ad "+ address);
                        AddPage(lat,lng,icon,name,price,rate,address,open,new ArrayList(types));
                        lat="";lng="";icon="";name="";price="";rate="";address="";
                        types.clear();


                    }
                    //

                }
            }
            if(All.size()==0){
                AddPage("0","0","0","No Restaurants In This Area","0","0","0",false,null);
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

    List<LatLng> Locations=new ArrayList<LatLng>();
    //This gets the user location as a google place
    void GooglePlacesPlace(PlacesClient placesClient) {


        // Use fields to define the data types to return.
        List<Field> placeFields = Arrays.asList(Field.LAT_LNG);

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
                        Log.i("Superr", LL.toString());


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

    RestaurantManager restaurantManager= new RestaurantManager();

    int on=0;
    public void Right(View view){
        on=((on+1)%restaurantManager.All.size());
        Button button=(Button)findViewById(R.id.CurRest);
        button.setText(restaurantManager.All.get(on).name);
    }
    public void Left(View view){
        on=((on-1)%restaurantManager.All.size());
        if (on==-1){
            on=restaurantManager.All.size()-1;
        }
        Button button=(Button)findViewById(R.id.CurRest);
        button.setText(restaurantManager.All.get(on).name);
    }
    public void RandomRest(View view){
        Random random = new Random();
        int size=restaurantManager.All.size();
        int r=random.nextInt(size);

        Button button=(Button)findViewById(R.id.Random);
        button.setText(restaurantManager.All.get(r).name);
    }
    public void RandomRest(){
        Random random = new Random();
        int size=restaurantManager.All.size();
        int r=random.nextInt(size);

        Button button=(Button)findViewById(R.id.Random);
        button.setText(restaurantManager.All.get(r).name);
    }
    public void GoRest(String name){
        Restaurant r= restaurantManager.ByName(name);
        if (r==null){
            return;
        }
        Intent intent = new Intent(this,RestaurantInfo.class);
        String ex=r.GetRaw();
        intent.putExtra(EXTRA_MESSAGE,ex);
        startActivity(intent);
    }
    public void GoRest(View view){
        Button button=(Button)view;
        String rest=button.getText().toString();
        GoRest(rest);
    }

    String currentLatLng="";
    void ChangeRadius(int radius){
        on=0;
        String data=ReadURL(currentLatLng,radius);
        restaurantManager.FromData(data);
        RandomRest();
        Button button=(Button)findViewById(R.id.CurRest);
        button.setText(restaurantManager.All.get(on).name);

    }

    public void ChangeRadius(View view){
        EditText editText=(EditText) findViewById(R.id.Radius);
        int radius=Integer.parseInt(editText.getText().toString());
        ChangeRadius(radius);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String LL=intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        currentLatLng=LL;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();

                //Go To Map
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        //Get Location
        //GooglePlacesPlace(ImportGooglePlaces());
        //while(Locations.size()==0){

        //}
        String data=ReadURL(LL,10000);
        restaurantManager.FromData(data);
        RandomRest();
        Button button=(Button)findViewById(R.id.CurRest);
        button.setText(restaurantManager.All.get(on).name);

    }

}
