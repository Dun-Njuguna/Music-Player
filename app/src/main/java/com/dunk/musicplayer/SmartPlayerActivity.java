package com.dunk.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;

public class SmartPlayerActivity extends AppCompatActivity {

    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";
    private ImageView pausePlayBtn, nextBtn, previousBtn, logo;
    private TextView songName;
    private Button voiceEnable;
    private RelativeLayout lowerRelativeLayout;
    private String mode = "OFF";


    private MediaPlayer myMediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mSongName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_player);

        checkVoiceCommanDPermission();

        pausePlayBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous);
        voiceEnable = findViewById(R.id.voiceEnabled);
        songName = findViewById(R.id.songName);
        lowerRelativeLayout = findViewById(R.id.lower);
        logo = findViewById(R.id.logo);


        parentRelativeLayout= (RelativeLayout)findViewById(R.id.parentRelativeLayout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matchesFound != null)
                {
                    if (mode.equals("ON"))
                    {
                        keeper = matchesFound.get(0);
                        if (keeper.equals("pause"))
                        {
                            playPauseSong();
                        }
                        else if (keeper.equals("play"))
                        {
                            playPauseSong();
                        }
                        else if (keeper.equals("previous"))
                        {
                            playPreviousSong();
                        }
                        else if (keeper.equals("next"))
                        {
                            playNextSong();
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        receiveValuesandStartPlaying();
        logo.setImageResource(R.drawable.logo);

        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper = "";
                        break;

                    case MotionEvent.ACTION_UP:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        break;
                }
                return false;
            }
        });


        voiceEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals("OFF"))
                    {
                        mode = "ON";
                        speechRecognizer.startListening(speechRecognizerIntent);
                        voiceEnable.setText("Voice Enable Mode -ON");
                        lowerRelativeLayout.setVisibility(View.GONE);
                    }
                    else
                    {
                        mode = "OFF";
                        speechRecognizer.stopListening();
                        voiceEnable.setText("Voice Enable Mode -OFF");
                        lowerRelativeLayout.setVisibility(View.VISIBLE);
                    }
            }
        });

        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseSong();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMediaPlayer.getCurrentPosition() > 0)
                    playNextSong();
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMediaPlayer.getCurrentPosition() > 0)
                    playPreviousSong();
            }
        });

    }


    private void receiveValuesandStartPlaying()
    {
        if (myMediaPlayer != null)
        {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList)bundle.getParcelableArrayList("song");
        mSongName = mySongs.get(position).getName();
        String mySongName =  intent.getStringExtra("songName");

        songName.setText(mySongName);
        songName.setSelected(true);

        position = bundle.getInt("position");
        Uri uri = Uri.parse(mySongs.get(position).toString());
        myMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);
        myMediaPlayer.start();

    }


    private void checkVoiceCommanDPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
        {
            if (!(ContextCompat.checkSelfPermission(SmartPlayerActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))
            {
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS, Uri.parse("package" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void playPauseSong()
    {
        if (myMediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();
        }
        else
        {
            pausePlayBtn.setImageResource(R.drawable.pause);
            myMediaPlayer.start();
        }
    }

    public void playNextSong()
    {
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position + 1) %mySongs.size());
        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);
        mSongName = mySongs.get(position).toString();
        songName.setText(mSongName);
        myMediaPlayer.start();

        if (myMediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.pause);
        }
        else
        {
            pausePlayBtn.setImageResource(R.drawable.play);
        }

    }

    public void playPreviousSong()
    {
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position - 1)< 0? (mySongs.size() -1) : (position -1));
        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);
        mSongName = mySongs.get(position).toString();
        songName.setText(mSongName);
        myMediaPlayer.start();

        if (myMediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.pause);
        }
        else
        {
            pausePlayBtn.setImageResource(R.drawable.play);
        }

    }

}
