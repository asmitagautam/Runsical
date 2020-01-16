package com.example.runsical;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.File;

public class CreateNewAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        //useful for copying things from our own db to android db
        /*DataBaseHelper help = new DataBaseHelper(getApplicationContext());
        try {
            help.createDataBase();
        } catch (Exception e){

        }*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        //setting log in text view to red
        TextView logIn= findViewById(R.id.login);
        logIn.setTextColor(getResources().getColor(R.color.red));

        //Click listener for going back to main activity to log in
        logIn.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Intent intent = new Intent(CreateNewAccountActivity.this, MainActivity.class);
                                                 startActivity(intent);
                                             }
                                         }
        );
        //Click listener for the sign up button
        signUp();
    }

    /**
     * Click listener for the sign up button
     */
    public void signUp(){
        File filePath = getApplicationContext().getDatabasePath(getResources().getString(R.string.database));
        final SQLiteDatabase mydatabase = SQLiteDatabase.openDatabase(filePath.getAbsolutePath(), null, 0);
        TextView signUp = findViewById(R.id.signup);
        signUp.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          //if any of the text boxes are empty
                                          //highlight border in red to indicate it needs to be filled
                                          //do not let them continue to sign up
                                          EditText emailText = findViewById(R.id.emailForm);
                                          String email = emailText.getText().toString();
                                          if (email.length() == 0)
                                              emailText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                                          EditText fullNameText = findViewById(R.id.fullname);
                                          String fullName = fullNameText.getText().toString();
                                          if (fullName.length() == 0)
                                              fullNameText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                                          EditText passwordText = findViewById(R.id.passwordForm);
                                          String password = passwordText.getText().toString();
                                          if (password.length() == 0) {
                                              passwordText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                                          }
                                          //confirm password must be the same as password
                                          EditText confirmPasswordText = findViewById(R.id.confirmPassword);
                                          String confirmPassword = confirmPasswordText.getText().toString();
                                          if (confirmPassword.length() == 0 || !password.equals(confirmPassword))
                                              confirmPasswordText.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                                          if (email.length() == 0 || fullName.length() == 0 || password.length() == 0 || confirmPassword.length() == 0 || !password.equals(confirmPassword))
                                              return;
                                          mydatabase.execSQL("INSERT INTO Account(fullName, email, password) VALUES ('"+fullName+"','"+email+"','"+password+"')");
                                          Intent intent = new Intent(CreateNewAccountActivity.this, StartWorkoutActivity.class);
                                          startActivity(intent);
                                      }
                                  }
        );
    }

}
