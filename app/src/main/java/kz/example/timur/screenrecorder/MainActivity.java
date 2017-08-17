package kz.example.timur.screenrecorder;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;


public class MainActivity extends Activity {
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String fileName;
    private String filepath;
    ArrayList<File> files;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filepath = Environment.getExternalStorageDirectory() + "/Records";
        new File(filepath).mkdir();
        listView = (ListView)findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        refreshList();
    }

    public void recordStart(View v) {
        java.sql.Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        fileName = filepath + "/Record_"+ timestamp.getTime() + ".mp4";

        try {
            releaseRecorder();
            File outFile = new File(fileName);
            if (outFile.exists()) {
                outFile.delete();
            }
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioChannels(2);
            mediaRecorder.setAudioEncodingBitRate(96000);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setOutputFile(fileName);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recordStop(View v) {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            refreshList();
        }
    }

    public void playStart(View v) {
        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();

            if ( listView.getCheckedItemPosition() != -1){
                mediaPlayer.setDataSource(filepath + "/" + files.get(listView.getCheckedItemPosition()).getName());
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playStop(View v) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        releaseRecorder();
    }

    public void deleteFile(View v){
        System.out.println(listView.getCheckedItemPosition());
        if ( listView.getCheckedItemPosition() != -1) {
            files.get(listView.getCheckedItemPosition()).delete();
        }
        refreshList();
    }
    private void refreshList(){
        files = new ArrayList<File>();
        File file1 = new File(filepath);
        for (File file : file1.listFiles()) {
            if (file.getName().contains("Record") == true && file.getName().contains(".mp4") == true)
                files.add(file);
        }
        ArrayAdapter<File> adapter = new ArrayAdapter<File>(
                this, R.layout.support_simple_spinner_dropdown_item,files);
        listView.setAdapter(adapter);
    }
}