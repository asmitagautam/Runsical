package com.example.runsical;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting create account textView to red
        TextView createAccount = findViewById(R.id.createaccount);
        createAccount.setTextColor(getResources().getColor(R.color.red));

        //Click listener for create new account activity
        createAccount.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent(MainActivity.this, CreateNewAccountActivity.class);
                                           startActivity(intent);
                                       }
                                   }
        );


        //Click listener for sign in button
        Button signIn = findViewById(R.id.signin);
        signIn.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Intent intent = new Intent(MainActivity.this, StartWorkoutActivity.class);
                                                 startActivity(intent);
                                             }
                                         }
        );


    }

    /*
     * Handles start work out button, pulls up screen that has music player and music speed selector.
     */
    public void startWorkout(View view) {
        Intent intent = new Intent(this, StartWorkoutActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
