package com.example.master.soundprocessingapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.master.soundprocessingapp.Recording.Recording;
import com.example.master.soundprocessingapp.Recording.Utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static List<Recording> recordings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordings = new ArrayList<>();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        load();
    }

    public void goRec(View view){
        Intent i = new Intent(this, RecordingActivity.class);
        startActivity(i);
    }

    public void goMan(View view){
        Intent i = new Intent(this, ListActivity.class);
        startActivity(i);
    }

    public void load(){
        try {
            ObjectInputStream x = new ObjectInputStream(new FileInputStream(Utility.getMetaFilename()));
            recordings = (ArrayList<Recording>) (x.readObject());
            x.close();
        }
        catch (Exception e){
            Toast.makeText(this,"Failed to load", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Activity", "Granted!");

                } else {
                    Log.d("Activity", "Denied!");
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            ObjectOutputStream x = new ObjectOutputStream(new FileOutputStream(Utility.getMetaFilename()));
            x.writeObject(recordings);
            x.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
