package com.example.master.soundprocessingapp;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.master.soundprocessingapp.Recording.Recording;
import com.example.master.soundprocessingapp.Recording.Utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    String [] titles;
    String [] datesAndTime;
    String [] names;
    myAdapter adapter;
    View actualFocusView;
    int actualFocusPosition = -1;
    boolean concatenate = false;
    MediaPlayer soundPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        prepareList();

        ListView list1 = findViewById(R.id.Itemslist);
        list1.setOnItemClickListener(this);

        adapter = new myAdapter();
        list1.setAdapter(adapter);
    }
    private void prepareList(){
        titles = new String[MainActivity.recordings.size()];
        datesAndTime = new String[MainActivity.recordings.size()];
        names = new String[MainActivity.recordings.size()];

        for (int i = 0; i < MainActivity.recordings.size(); i++){
            titles[i] = MainActivity.recordings.get(i).getTitle();
            datesAndTime[i] = MainActivity.recordings.get(i).getDateAndTime();
            names[i] = MainActivity.recordings.get(i).getFullName();
        }
    }
    public void onResume(){
        super.onResume();
    }
    public boolean onCreateOptionsMenu(Menu myMenu){
        super.onCreateOptionsMenu(myMenu);
        MenuInflater minf = getMenuInflater();
        minf.inflate(R.menu.options_menu, myMenu);
        return true;
    }


    public void startPlaying(){
        soundPlayer = new MediaPlayer();
        try {
            soundPlayer.setDataSource(Utility.getFilename(titles[actualFocusPosition]));
            soundPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            soundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    soundPlayer = null;
                }
            });
            soundPlayer.prepareAsync();
        }
        catch (IOException e){

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                deleteItem(titles[actualFocusPosition], actualFocusPosition);
                finish();
                startActivity(getIntent());
                break;
            case R.id.play:
                if(soundPlayer == null || !soundPlayer.isPlaying())
                    startPlaying();
                break;
            case R.id.stop:
                if (soundPlayer != null) {
                    soundPlayer.stop();
                    soundPlayer.release();
                    soundPlayer = null;
                }
                break;
            case R.id.pause:
                if(soundPlayer.isPlaying())
                    soundPlayer.pause();
                else
                    soundPlayer.start();
                break;
            case R.id.concat:
                concatenate = !concatenate;
                Toast.makeText(this, "Click which item you want to concatenate with green one", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!concatenate) {
            if (actualFocusView != null) {
                actualFocusView.setBackgroundColor(Color.WHITE);
            }

            actualFocusView = view;
            actualFocusPosition = position;

            view.setBackgroundColor(Color.GREEN);
        }
        else {
            if (actualFocusPosition != position) {
                Utility.concatFiles(titles[actualFocusPosition], titles[position]);
                concatenate = false;
                String desc = MainActivity.recordings.get(actualFocusPosition).getDesc();
                MainActivity.recordings.remove(new Recording(null, null, titles[position], null));
                MainActivity.recordings.remove(new Recording(null, null, titles[actualFocusPosition], null));
                Utility.deleteFilee(titles[position]);
                Utility.deleteFilee(titles[actualFocusPosition]);

                Recording con = new Recording(names[actualFocusPosition],
                        "", titles[actualFocusPosition] + " " + titles[position], desc);
                MainActivity.recordings.add(con);
                finish();
                startActivity(getIntent());
            }
        }
    }
    public void deleteItem(String title, int position){
        MainActivity.recordings.remove(position);
        Utility.deleteFilee(title);
    }
    class myAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        myAdapter() {
            super();
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public View getView(int position,
                            View convertView,
                            ViewGroup parent) {

            View mV;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.my_list_item, null);
            }
            mV = convertView;

            TextView tv1 = mV.findViewById(R.id.row1);
            TextView tv2 = mV.findViewById(R.id.row2);
            TextView tv3 = mV.findViewById(R.id.row3);

            tv1.setText((position+1) + ". " +titles[position]);
            tv2.setText(datesAndTime[position]);
            tv3.setText(names[position]);
            if (position == 0)
            {
                actualFocusView = mV;
                actualFocusPosition = position;
                mV.setBackgroundColor(Color.GREEN);
            }
            return mV;
        }
    }
    protected void onPause() {
        super.onPause();
        Utility.saveAllData();
    }
}
