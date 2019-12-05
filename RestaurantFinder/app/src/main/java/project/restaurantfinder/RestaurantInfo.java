package project.restaurantfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class RestaurantInfo extends AppCompatActivity {
    String raw="";
    double lat = 0;
    double lng = 0;

    public void AddToFavs(View view){//Implement add to favorites

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);

        Intent intent = getIntent();
        raw = intent.getStringExtra(Main_Page.EXTRA_MESSAGE);
        lat = Double.parseDouble(intent.getStringExtra("Latitude"));
        lng = Double.parseDouble(intent.getStringExtra("Longitude"));
        System.out.println(lat);
        System.out.println(lng);

        TextView textView = findViewById(R.id.RestInfo);
        textView.setText(raw);

        Button setRouteBtn = findViewById(R.id.ToMap);
        setRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();

                //Go To Map
                Intent newIntent = new Intent(RestaurantInfo.this, SearchActivity.class);
                newIntent.putExtra("Latitude", Double.toString(lat));
                newIntent.putExtra("Longitude", Double.toString(lng));
                startActivity(newIntent);
            }
        });
    }
}
