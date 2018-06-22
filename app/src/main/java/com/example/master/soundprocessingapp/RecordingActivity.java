package com.example.master.soundprocessingapp;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.master.soundprocessingapp.Recording.Process;
import com.example.master.soundprocessingapp.Recording.Record;
import com.example.master.soundprocessingapp.Recording.Recording;
import com.example.master.soundprocessingapp.Recording.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.LinkedBlockingQueue;

public class RecordingActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{
    private static LinkedBlockingQueue<byte[]> audioBlocks;
    private boolean recordingStarted = false;

    private AudioRecord recorder = null;
    public static boolean isRecording = false;

    private int bufferSize = Utility.BUFFER_SIZE;

    private Thread recordingThread = null;
    private Thread processingThread = null;

    private FileOutputStream os;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        //requestRecordAudioPermission();

        audioBlocks = new LinkedBlockingQueue<>();
        Process.VOLUME_THRESHOLD = 0;
        ((SeekBar)findViewById(R.id.seekBar)).setOnSeekBarChangeListener(this);
        try {
            os = new FileOutputStream(Utility.getTempFilename());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public void start(View view){
        recordingStarted = true;
        recordAndProcess();

        view.setEnabled(false);
        findViewById(R.id.button4).setEnabled(true);
    }
    public void stop(View view){
        pause();
    }
    public void reset(View view){
        pause();
        clear();
        deleteTempFile();
        recordingStarted = false;
    }
    public void save(View view){
        if (recordingStarted) {
            pause();
            saveToFile();
            clear();
        }
        recordingStarted = false;
    }
    public void goManager(View view){
        Intent i = new Intent(this, ListActivity.class);
        startActivity(i);
    }
    private void clear(){
        recorder = null;
        audioBlocks = new LinkedBlockingQueue<>();

        ((EditText)findViewById(R.id.editText)).setText("");
        ((EditText)findViewById(R.id.editText2)).setText("");
        ((EditText)findViewById(R.id.editText3)).setText("");
        ((EditText)findViewById(R.id.editText4)).setText("");

        try {
            if (os!=null)
                os.close();
            os = new FileOutputStream(Utility.getTempFilename());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void pause(){
        isRecording = false;
        if (recorder != null) {
            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                recorder.stop();
            recorder.release();
            recorder = null;

            try {
                recordingThread.join();
                recordingThread = null;
                processingThread.join();
                processingThread = null;
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        findViewById(R.id.button4).setEnabled(false);
        findViewById(R.id.button3).setEnabled(true);
    }

    public void recordAndProcess(){
        recorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                Utility.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        int i = recorder.getState();

        if(i==1)
            recorder.startRecording();
        isRecording = true;

        recordingThread = new Thread(new Record(recorder, bufferSize, audioBlocks));

        recordingThread.start();

        processingThread = new Thread(new Process(audioBlocks, os));

        processingThread.start();
    }
    public void saveToFile(){
        try {
            os.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String title = saveMeta();
        copyWaveFile(Utility.getTempFilename(), Utility.getFilename(title));
        deleteTempFile();
        Toast.makeText(this, "File saved", Toast.LENGTH_SHORT).show();
    }


    private void deleteTempFile() {
        File file = new File(Utility.getTempFilename());

        file.delete();
    }
    private void copyWaveFile(String inFilename,String outFilename){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = Utility.SAMPLE_RATE;
        int channels = 1;
        long byteRate = 16 * Utility.SAMPLE_RATE * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;


            Utility.WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStop(){
        super.onStop();
        deleteTempFile();
    }

    private String saveMeta(){
        String title = ((EditText)findViewById(R.id.editText)).getText().toString();
        String desc = ((EditText)findViewById(R.id.editText2)).getText().toString();
        String firstName = ((EditText)findViewById(R.id.editText3)).getText().toString();
        String secondName = ((EditText)findViewById(R.id.editText4)).getText().toString();

        Recording rec = new Recording(firstName, secondName, title, desc);
        int i = 1;
        while (MainActivity.recordings.contains(rec)){
            rec.setTitle(title + "(" + i + ")");
            i++;
        }

        MainActivity.recordings.add(rec);
        return rec.getTitle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            ObjectOutputStream x = new ObjectOutputStream(new FileOutputStream(Utility.getMetaFilename()));
            x.writeObject(MainActivity.recordings);
            x.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        ((TextView)findViewById(R.id.textView9)).setText(Integer.toString(progress));
        Process.VOLUME_THRESHOLD = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
