package project.restaurantfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import android.widget.TextView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Use to authenticate user creds on login
    public boolean Authenticate(String user,String pass){


        //Replace this statement with some other authentication code
        if(user.equals("user")&&pass.equals("pass")){
            return true;

        }

        return false;
    }
    public void LogIn(View view) {
        Intent intent = new Intent(this, Main_Page.class);
        EditText usernameT = (EditText) findViewById(R.id.username_Text);
        String username = usernameT.getText().toString();
        EditText passwordT = (EditText) findViewById(R.id.password_Text);
        String password = passwordT.getText().toString();

        if(Authenticate(username,password)) {
            startActivity(intent);
        } else {
            TextView error = findViewById(R.id.error_text);
            error.setVisibility(View.VISIBLE);
        }
    }


}
