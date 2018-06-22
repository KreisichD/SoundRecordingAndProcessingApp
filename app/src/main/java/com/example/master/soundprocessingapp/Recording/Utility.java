package com.example.master.soundprocessingapp.Recording;

import android.os.Environment;

import com.example.master.soundprocessingapp.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by Masyter on 22.06.2018.
 */

public class Utility {
    public static final String AUDIO_RECORDER_FOLDER = "SoundRecorder";
    public static final String AUDIO_RECORDER_META_FILE = "metadata";
    public static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    public static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    public static final int SAMPLE_RATE = 44100;
    public static final int BUFFER_SIZE = 8192;
    public static final int HEADER_SIZE = 44;

    public static void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[HEADER_SIZE];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2*16/8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, HEADER_SIZE);
    }

    public static String getMetaFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        File tempFile = new File(filepath,AUDIO_RECORDER_META_FILE);

        if(tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_META_FILE);
    }

    public static String getFilename(String title){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + title + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    public static void deleteFilee(String title){
        File x = new File(getFilename(title));
        x.delete();
    }

    public static String getTempFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

        if(tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    public static void concatFiles(String title1, String title2){
        FileInputStream is1 = null;
        FileInputStream is2 = null;
        FileOutputStream res = null;
        String title = title1 + " " + title2;

        try {
            is1 = new FileInputStream(getFilename(title1));
            is2 = new FileInputStream(getFilename(title2));
            res = new FileOutputStream(getFilename(title));
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        byte[] header = new byte[HEADER_SIZE];
        byte[] block = new byte[BUFFER_SIZE];
        try {
            is1.read(header, 0, HEADER_SIZE);

            long totalAudioLen;
            long totalDataLen;
            long longSampleRate = SAMPLE_RATE;
            int channels = 1;
            long byteRate = 16 * SAMPLE_RATE * channels/8;
            totalAudioLen = is1.getChannel().size() + is2.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(res, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);

            while (is1.read(block) != -1){
                res.write(block);
            }
            is2.read(new byte[HEADER_SIZE], 0, HEADER_SIZE); //going after header
            while (is2.read(block) != -1){
                res.write(block);
            }
            is1.close();
            is2.close();
            res.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void saveAllData(){
        try {
            ObjectOutputStream x = new ObjectOutputStream(new FileOutputStream(Utility.getMetaFilename()));
            x.writeObject(MainActivity.recordings);
            x.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
