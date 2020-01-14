package com.example.runsical;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import java.io.IOException;

public class StartWorkoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);
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
