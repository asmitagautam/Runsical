package com.example.runsical;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting app name textView to red
        TextView appName = findViewById(R.id.runsical);
        appName.setTextColor(getResources().getColor(R.color.red));

        //setting create account textView to red
        TextView createAccount = findViewById(R.id.createaccount);
        createAccount.setTextColor(getResources().getColor(R.color.red));

        //Click listener for create new account activity
        createAccount.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent(MainActivity.this, CreateNewAccount.class);
                                           startActivity(intent);
                                       }
                                   }
        );

    }

}
