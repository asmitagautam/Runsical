package com.example.runsical;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StartWorkoutActivity extends AppCompatActivity {

    private double treadmillSpeed;
    private final double MAX_TREADMILL_SPEED = 11.9;
    private final double MIN_TREADMILL_SPEED = 3.0;
    private SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);

        //connect to database
        File filePath = getApplicationContext().getDatabasePath(getResources().getString(R.string.database));
        mydatabase = SQLiteDatabase.openDatabase(filePath.getAbsolutePath(), null, 0);

        //default treadmill speed
        treadmillSpeed = 3.0;

        //click listeners for up and down button for treadmill speed
        speedUpButton();
        speedDownButton();

    }

    private double getBPM(double speed){
        String query = "SELECT bpm FROM BPM WHERE speed = ?";
        Cursor c = mydatabase.rawQuery(query, new String[]{String.format("%.1f", treadmillSpeed)});
        int bpm = 0;
        if (c.getCount() > 0)
        {
            c.moveToFirst();
            do {
                bpm = c.getInt(c.getColumnIndex("bpm"));
            } while (c.moveToNext());
            c.close();
        }
        return bpm;
    }

    private ArrayList<String> getSongList(double BPM){
        String query = "SELECT Performer, Song FROM Music WHERE tempo = ?";
        Cursor c = mydatabase.rawQuery(query, new String[]{String.format("%.1f", BPM)});
        ArrayList<String> songs = new ArrayList<>();
        StringBuilder songName = new StringBuilder();
        if (c.getCount() > 0)
        {
            c.moveToFirst();
            do {
                songName.append(c.getString(c.getColumnIndex("Song")));
                songName.append(" - ");
                songName.append(c.getString(c.getColumnIndex("Performer")));
                songs.add(songName.toString());
                songName.setLength(0);
            } while (c.moveToNext());
            c.close();
        }
        return songs;
    }

    public void speedUpButton(){
        //Text view for treadmill speed
        final TextView speedView = findViewById(R.id.speed);

        //increase treadmill speed by 0.1 everytime up button pressed
        ImageButton up = findViewById(R.id.up);
        up.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      //only update speed if it is less than max speed possible
                                      if (treadmillSpeed < MAX_TREADMILL_SPEED) {
                                          treadmillSpeed += 0.1;
                                          //format double to display only one decimal point
                                          speedView.setText(String.format("%.1f", treadmillSpeed));
                                      }
                                  }
                              }
        );
    }

    public void speedDownButton(){
        //Text view for treadmill speed
        final TextView speedView = findViewById(R.id.speed);

        //decrease treadmill speed by 0.1 everytime up button pressed
        ImageButton down = findViewById(R.id.down);
        down.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      //only update speed if it is more than min speed possible
                                      if (treadmillSpeed > MIN_TREADMILL_SPEED) {
                                          treadmillSpeed -= 0.1;
                                          //format double to display only one decimal point and remove negative sign from 0.0
                                          speedView.setText(String.format("%.1f", treadmillSpeed).replaceAll("^-(?=0(\\.0*)?$)", ""));
                                      }
                                  }
                              }
        );
        
    }

    private void playMusic() {
        String url = "http://........"; // your URL here
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync(); // might take long! (buffering, fetching and decoding media data)
        } catch (IOException e) {
            // TODO: display error message in app
        }
        mediaPlayer.start();
    }
}
