package com.example.master.soundprocessingapp.Recording;

import com.example.master.soundprocessingapp.RecordingActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;



public class Process implements Runnable{
    public static int VOLUME_THRESHOLD = 250;
    LinkedBlockingQueue<byte[]> queue;
    FileOutputStream os;
    public Process(LinkedBlockingQueue<byte[]> q, FileOutputStream fos){
        queue = q;
        os = fos;
    }
    private boolean isTooQuiet(byte[] rawData) {
        short[] shorts = getShorts(rawData);
        shorts = abs(shorts);
        short max = max(shorts);
        return max < VOLUME_THRESHOLD;
    }
    private short[] getShorts(byte[] block){
        short[] shorts = new short[block.length / 2];
        ByteBuffer.wrap(block).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        return shorts;
    }
    private short[] abs(short[] shorts){
        for(int i = 0; i < shorts.length; i++){
            if(shorts[i] < 0){
                shorts[i] *= -1;
            }
        }
        return shorts;
    }
    public short max(short[] shorts){
        short s = shorts[0];
        for (int i = 1; i < shorts.length; i++) {
            if(shorts[i] > s){
                s = shorts[i];
            }
        }
        return s;
    }
    @Override
    public void run() {
       byte[] block;
       while (RecordingActivity.isRecording || !queue.isEmpty()){
           if (!queue.isEmpty())
           {
               try {
                   //processing dzwieku
                   block = queue.take();
                   if(!isTooQuiet(block))
                        os.write(block);
               }
               catch (InterruptedException e){
                   e.printStackTrace();
               }
               catch (IOException e){
                   e.printStackTrace();
               }
           }
       }
    }
}

