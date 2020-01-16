package com.example.runsical;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

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
                                                 EditText emailText = findViewById(R.id.email);
                                                 String email = emailText.getText().toString();
                                                 if (email.length() == 0)
                                                     emailText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                                                 EditText passwordText = findViewById(R.id.password);
                                                 String password = passwordText.getText().toString();
                                                 if (password.length() == 0) {
                                                     passwordText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                                                 }
                                                 if (email.length() == 0 || password.length() == 0 )
                                                     return;
                                                 if (isValidLogin(email, password)) {
                                                     Intent intent = new Intent(MainActivity.this, StartWorkoutActivity.class);
                                                     startActivity(intent);
                                                 } else {
                                                     emailText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                                                     passwordText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                                                 }
                                             }
                                         }
        );


    }

    /*
     * checks if account exists in database
     */
    public boolean isValidLogin(String email, String password) {
        File filePath = getApplicationContext().getDatabasePath(getResources().getString(R.string.database));
        final SQLiteDatabase mydatabase = SQLiteDatabase.openDatabase(filePath.getAbsolutePath(), null, 0);

        String query = "SELECT email,password FROM Account WHERE email = ? AND password = ?";
        Cursor c = mydatabase.rawQuery(query, new String[]{email, password});
        try {
            if (c.getCount() > 0) {
                return true;
            }
            c.close();
        } finally {
            c.close();
        }
        return false;
    }

    /*
     * Handles start work out button, pulls up screen that has music player and music speed selector.
     */
    public void startWorkout(View view) {
        Intent intent = new Intent(this, StartWorkoutActivity.class);
        startActivity(intent);
    }
}
