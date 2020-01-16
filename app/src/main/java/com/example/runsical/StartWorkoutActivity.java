package com.example.runsical;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class StartWorkoutActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;

    private double treadmillSpeed;
    private final double MAX_TREADMILL_SPEED = 11.9;
    private final double MIN_TREADMILL_SPEED = 3.0;
    private SQLiteDatabase mydatabase;
    private Stack<String> songsPlayed;
    private ArrayList<String> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
        //connect to database
        File filePath = getApplicationContext().getDatabasePath(getResources().getString(R.string.database));
        mydatabase = SQLiteDatabase.openDatabase(filePath.getAbsolutePath(), null, 0);

        //initialize stack that keeps track of songs played for the previous button
        songsPlayed = new Stack<>();

        //initialize songList for the current BPM
        songList = new ArrayList<>();

        //default treadmill speed
        treadmillSpeed = 3.0;
        //get a random song at that treadmill speed
        double bpm = getBPM(treadmillSpeed);
        songList = getSongList(bpm);
        String song = getRandomSong(songList);
        songsPlayed.push(song);
        TextView songInfo = findViewById(R.id.songInfo);
        songInfo.setText(song);

        //click listeners for up and down button for treadmill speed
        speedUpButton();
        speedDownButton();

        //click listeners for next and previous button for music
        nextSongButton();
        previousSongButton();
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

    /*
     * Queries BPM table in database for the bpm at specified treadmill speed
     */
    private double getBPM(double speed){
        String query = "SELECT bpm FROM BPM WHERE speed = ?";
        Cursor c = mydatabase.rawQuery(query, new String[]{String.format("%.1f", speed)});
        int bpm = 0;
        try {
            if (c.getCount() > 0)
        {
            c.moveToFirst();
            do {
                bpm = c.getInt(c.getColumnIndex("bpm"));
            } while (c.moveToNext());
        }
        } finally {
            c.close();
        }
        return bpm;
    }

    /*
     * Queries Music table in database for all songs at a specified tempo/BPM
     * loops over result set and adds to arraylist in the format SongName - Artist
     */
    private ArrayList<String> getSongList(double BPM) {
        String query = "SELECT Performer, Song FROM Music WHERE tempo = ?";
        Cursor c = mydatabase.rawQuery(query, new String[]{String.format("%.1f", BPM)});
        ArrayList<String> songs = new ArrayList<>();
        StringBuilder songName = new StringBuilder();
        try {
            if (c.getCount() > 0) {
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
        } finally {
            c.close();
        }
        return songs;
    }

    /*
     * Get a random song from the arraylist of songs
     */
    private String getRandomSong(ArrayList<String> songList){
        double value = 0.1;
        while (songList.size() == 0){
            if (treadmillSpeed-value > 3.0) {
                double bpm1 = getBPM(treadmillSpeed - value);
                ArrayList<String> songs1 = getSongList(bpm1);
                if (songs1.size() != 0) {
                    songList = songs1;
                    break;
                }
            }
            if (treadmillSpeed+value < 12.0) {
                double bpm2 = getBPM(treadmillSpeed + value);
                Log.d("ASMITA", String.format("%.1f", bpm2));
                Log.d("ASMITA", String.format("%.1f", treadmillSpeed + value));
                ArrayList<String> songs2 = getSongList(bpm2);
                songList = songs2;
            }
            value += 0.1;
        }
        Random r = new Random();
        Integer random = r.nextInt(songList.size());
        Log.d("ASMITA", songList.get(random));
        return songList.get(random);
    }

    /*
     * click listener for speed up button
     */
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

                                          //get a random song at that treadmill speed
                                          double bpm = getBPM(treadmillSpeed);
                                          songList = getSongList(bpm);
                                          String song = getRandomSong(songList);
                                          songsPlayed.empty();
                                          songsPlayed.push(song);
                                          TextView songInfo = findViewById(R.id.songInfo);
                                          songInfo.setText(song);
                                      }
                                  }
                              }
        );

    }

    /*
     * click listener for speed down button
     */
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

                                            //get a random song at that treadmill speed
                                            double bpm = getBPM(treadmillSpeed);
                                            songList = getSongList(bpm);
                                            String song = getRandomSong(songList);
                                            songsPlayed.empty();
                                            songsPlayed.push(song);
                                            TextView songInfo = findViewById(R.id.songInfo);
                                            songInfo.setText(song);
                                        }
                                    }
                                }
        );
        
    }

    /*
     * click listener for next song button
     */
    public void nextSongButton(){
        ImageButton next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String song = getRandomSong(songList);
                                        songsPlayed.push(song);
                                        TextView songInfo = findViewById(R.id.songInfo);
                                        songInfo.setText(song);
                                    }
                                }
        );
    }

    /*
     * click listener for previous song button
     */
    public void previousSongButton(){
        ImageButton previous = findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        TextView songInfo = findViewById(R.id.songInfo);
                                        if (!songsPlayed.isEmpty()) {
                                            songInfo.setText(songsPlayed.pop());
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