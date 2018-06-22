package com.example.master.soundprocessingapp.Recording;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.widget.Toast;

import com.example.master.soundprocessingapp.RecordingActivity;


import java.util.concurrent.LinkedBlockingQueue;

public class Record implements Runnable {
    AudioRecord audioRecord;
    int bufferSize;
    LinkedBlockingQueue<byte[]> queue;

    public Record(AudioRecord ar, int buf, LinkedBlockingQueue<byte[]> q){
        audioRecord = ar;
        bufferSize = buf;
        queue = q;
    }

    @Override
    public void run() {
        writeAudioDataToQueue();
    }

    private void writeAudioDataToQueue(){
        byte data[] = new byte[bufferSize];
        int read;

            while(RecordingActivity.isRecording){
                read = audioRecord.read(data, 0, bufferSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        queue.put(data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    }
}
