package project.restaurantfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class RestaurantInfo extends AppCompatActivity {
    String raw="";

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
    }
}
