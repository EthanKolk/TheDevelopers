package project.restaurantfinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText emailID, passwordID, passwordID2;
    Button signup;
    TextView signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Replace this statement with some other authentication code


        mAuth = FirebaseAuth.getInstance();
        emailID = findViewById((R.id.editText));
        passwordID = findViewById((R.id.editText2));
        passwordID2 = findViewById((R.id.editText3));

        signup = findViewById(R.id.button2);
        signin = findViewById(R.id.textView);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailID.getText().toString();
                String password = passwordID.getText().toString();
                String password2 = passwordID2.getText().toString();
                if (email.isEmpty()) {
                    emailID.setError("Enter Email");
                    emailID.requestFocus();
                } else if (password.isEmpty()) {
                    passwordID.setError("Enter password");
                    passwordID.requestFocus();
                } else if (password2.isEmpty()) {
                    passwordID2.setError("Enter password again");
                    passwordID2.requestFocus();
                } else if (!(password2.equals(password))) {
                    {
                        passwordID2.setError("The password do not match, enter again");
                        passwordID2.requestFocus();
                    }

                } else if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(CreateAccount.this, "Fields Are Empty!", Toast.LENGTH_SHORT).show();
                } else if (!email.isEmpty() && !password.isEmpty()) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccount.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(CreateAccount.this, "Sign up Unsuccessful, Please Enter Again", Toast.LENGTH_SHORT);
                                //  updateUI(null);

                            } else {
                                Toast.makeText(CreateAccount.this, "Welcome, Account created", Toast.LENGTH_SHORT);
                                startActivity(new Intent(CreateAccount.this, MainActivity.class));

                            }

                        }
                    });

                } else {
                    Toast.makeText(CreateAccount.this, "An Error Has Occurred", Toast.LENGTH_SHORT).show();
                }
            }

        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(CreateAccount.this, MainActivity.class);
                startActivity(j);
            }

        });
    }
}