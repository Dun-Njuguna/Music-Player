package com.dunk.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private String[] itemsAll;
    @BindView(R.id.list_item)ListView songsList;
    private MediaPlayer myMediaPlayer;

    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        appExternalStoragePermission();

    }


    public final void appExternalStoragePermission()
    {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        displayAudioSongsName();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
                    {
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.cancelPermissionRequest();
                    }
                }).check();

    }



    public ArrayList<File> readAudioFiles(File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] allFiles = file.listFiles();
        for (File individualFile: allFiles)
        {
            if (individualFile.isDirectory() && !individualFile.isHidden())
            {
               arrayList.addAll(readAudioFiles(individualFile));
            }
            else
            {
                if (individualFile.getName().endsWith(".mp3") ||
                        individualFile.getName().endsWith(".aac") ||
                        individualFile.getName().endsWith(".wav") ||
                        individualFile.getName().endsWith(".wma")
                )
                {
                    arrayList.add(individualFile);
                }
            }
        }

        return arrayList;
    }


    private void  displayAudioSongsName()
    {
        final ArrayList<File> audioSongs = readAudioFiles(Environment.getExternalStorageDirectory());

        itemsAll = new String[audioSongs.size()];
        for (int songCounter = 0; songCounter<audioSongs.size(); songCounter++)
        {
            itemsAll[songCounter] = audioSongs.get(songCounter).getName();
        }

        arrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_expandable_list_item_1, itemsAll);

        if (arrayAdapter !=null)
            songsList.setAdapter(arrayAdapter);

        songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (myMediaPlayer !=null)
                {
                    myMediaPlayer.start();
                    myMediaPlayer.release();
                }

                String songName = songsList.getItemAtPosition(position).toString();
                Intent intent = new Intent(MainActivity.this, SmartPlayerActivity.class);
                intent.putExtra("song", audioSongs);
                intent.putExtra("songName", songName);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

    }


}
