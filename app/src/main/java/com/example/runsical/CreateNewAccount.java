package com.example.runsical;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CreateNewAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        //setting log in text view to red
        TextView logIn= findViewById(R.id.login);
        logIn.setTextColor(getResources().getColor(R.color.red));

        //Click listener for going back to main activity to log in
        logIn.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Intent intent = new Intent(CreateNewAccount.this, MainActivity.class);
                                                 startActivity(intent);
                                             }
                                         }
        );
    }


}
