package project.restaurantfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import static android.provider.AlarmClock.EXTRA_MESSAGE;



public class RestaurantInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);

        Intent intent = getIntent();
        String raw = intent.getStringExtra(Main_Page.EXTRA_MESSAGE);
        TextView textView = findViewById(R.id.RestInfo);
        textView.setText(raw);
    }
}
