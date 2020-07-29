package com.example.voiceregsample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity
implements RecognitionListener  {
    private GridView gridview;
    private GridAdapter gridAdapter;
    private AudioManager audioManager;


    private static final int REQUEST_RECORD_PERMISSION = 100;

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridview = findViewById(R.id.grid);
        gridAdapter = new GridAdapter(this);
        gridview.setAdapter(gridAdapter);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_PERMISSION);
        }



    }
    private  void mute(boolean isMute){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flag;
            if(isMute)
            {   flag=AudioManager.ADJUST_MUTE;
            }else{
                flag=AudioManager.ADJUST_UNMUTE;
            }
            audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, flag, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, flag, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, flag, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, flag, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, flag, 0);
        } else {
            audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, isMute);
            audioManager.setStreamMute(AudioManager.STREAM_ALARM, isMute);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, isMute);
            audioManager.setStreamMute(AudioManager.STREAM_RING, isMute);
            audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, isMute);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            speech.startListening(recognizerIntent);
        }


    }
    @Override
    protected void onPause() {
        speech.stopListening();
        super.onPause();


    }
    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            mute(false);
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }

    @Override
    protected void onDestroy() {
        mute(false);
        speech.destroy();
        super.onDestroy();
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        mute(true);

    }
    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }
    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");

    }
    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        speech.stopListening();
        speech.startListening(recognizerIntent);
    }
    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }
    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }
    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");

    }
    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        for (String s: matches){
            if(s.contains("left")){
                changeFocus(false);

            }else if(s.contains("right")){
                changeFocus(true);
            }
        }
        speech.stopListening();
        speech.startListening(recognizerIntent);

    }
    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);

    }
    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    private void changeFocus(Boolean b) {
        View v=null;
        if(b==true){
            GridAdapter.currentPosition =GridAdapter.currentPosition +  1;
            v = gridview.getChildAt( GridAdapter.currentPosition);
        }
        else{
            if(GridAdapter.currentPosition!=0)
            GridAdapter.currentPosition =GridAdapter.currentPosition -  1;
            v = gridview.getChildAt( GridAdapter.currentPosition );
        }


        v.findViewById(R.id.editText).requestFocus();
    }

}