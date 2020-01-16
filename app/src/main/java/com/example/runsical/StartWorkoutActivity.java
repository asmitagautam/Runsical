package com.example.runsical;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;

public class StartWorkoutActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;

    private double treadmillSpeed;
    private final double MAX_TREADMILL_SPEED = 11.9;
    private final double MIN_TREADMILL_SPEED = 3.0;
    private SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
        //connect to database
        File filePath = getApplicationContext().getDatabasePath(getResources().getString(R.string.database));
        mydatabase = SQLiteDatabase.openDatabase(filePath.getAbsolutePath(), null, 0);

        //default treadmill speed
        treadmillSpeed = 3.0;

        //click listeners for up and down button for treadmill speed
        speedUpButton();
        speedDownButton();
    }

    @Override
    /*
     * Listener for successful initialization.
     * If successful, play video.
     */
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo("fhWaJi1Hsfo"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    /*
     * Listener for failure to initialize.
     * If fail, check to see if error is recoverable by user's action.
     * If true, opens dialog box to allow user to recover.
     */
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
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

    private String getRandomSong(ArrayList<String> songList){
        return null;
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }


    // References:
    // - Only start search once the user has finished selecting a running speed:
    // https://stackoverflow.com/questions/12142021/how-can-i-do-something-0-5-second-after-text-changed-in-my-edittext


}