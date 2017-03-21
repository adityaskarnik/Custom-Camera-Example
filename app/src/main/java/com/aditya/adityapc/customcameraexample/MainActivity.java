package com.aditya.adityapc.customcameraexample;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;

import com.sprylab.android.widget.TextureVideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    @SuppressWarnings("deprecation")
    Camera camera; // camera class variable
    SurfaceView camView; // drawing camera preview using this variable
    SurfaceHolder surfaceHolder; // variable to hold surface for surfaceView which means display
    boolean camCondition = false;  // conditional variable for camera preview checking and set to false
    Button cap,preview;    // image capturing button
    MediaRecorder mediaRecorder; //video player controls
    boolean recording; //boolean value to check if recording has started or not
    String filePath = "/sdcard/" + Constants.STORAGE_PATH  + "/"; //save the video to specified path
    String finalPath;
    private ProgressDialog mProgressDialog;
    private String TAG;


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG = this.getResources().getString(R.string.app_name);

        createDirectory();
        permissionGrantedSetupView();

        cap = (Button) findViewById(R.id.recordButton);
        cap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startAction();
            }
        });

        preview = (Button) findViewById(R.id.btn_done);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAndPreview();
            }
        });


    }

    private void permissionGrantedSetupView() {
        // getWindow() to get window and set it's pixel format which is UNKNOWN
        getWindow().setFormat(PixelFormat.UNKNOWN);
        // refering the id of surfaceView
        camView = (SurfaceView) findViewById(R.id.surfaceView);
        // getting access to the surface of surfaceView and return it to surfaceHolder
        surfaceHolder = camView.getHolder();
        // adding call back to this context means MainActivity
        surfaceHolder.addCallback(this);
        // to set surface type
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        recording = false;

    }

    private void startAction() {

        cap.setEnabled(false);
        preview.setEnabled(false);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        cap.setEnabled(true);
                        preview.setEnabled(true);

                    }
                });
            }
        }).start();

        // TODO Auto-generated method stub
        // calling a method of camera class take picture by passing one picture callback interface parameter
        camera.takePicture(null, null, null, mPictureCallback);

        // TODO Auto-generated method stub
        if(recording){
            stopRecording();
        }else{
            startRecording();
        }

    }

    private void stopAndPreview() {

        //disable the stop and preview buttons for 4 seconds, so as to record min 4 seconds of video
        //for this purpose thread is used with sleep method
        // after 4 second the buttons are automatically re enabled.
        cap.setEnabled(false);
        preview.setEnabled(false);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        cap.setEnabled(true);
                        preview.setEnabled(true);

                    }
                });
            }
        }).start();
        new FinishRecordingTask().execute();

    }

    private void startRecording() {

        camera.stopPreview();
        camera.unlock();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        //different quality videos can recorded as pe user requirement
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        mediaRecorder.setProfile(profile);

        mediaRecorder.setMaxDuration(12000);
        Log.d("Max Duration",""+(12000/1000)+" Sec");
        File file = new File("/sdcard/" + Constants.STORAGE_PATH + "/snaps/" +
                Constants.STORAGE_PATH +
                System.currentTimeMillis()+".jpg");
        if (file.exists()) {
            file.delete();
        }
        file = new File("/sdcard/" + Constants.STORAGE_PATH + "/snaps/" +
                Constants.STORAGE_PATH +
                System.currentTimeMillis()+".jpg");

        try
        {
            file.createNewFile();
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            Log.e("File Exception", "*** first catch ***");
        }

        String recordedTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        finalPath = filePath + "/videos/" + recordedTime + ".mp4";
        mediaRecorder.setOutputFile(finalPath);
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

        mediaRecorder.setVideoFrameRate(20);
        mediaRecorder.setMaxFileSize(15000000);
        Log.d("Max Size",""+(15000000/1024)/1024+" MB");

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
        recording = true;
        cap.setText(MainActivity.this.getResources().getString(R.string.stop));

    }

    private void stopRecording() {

        mediaRecorder.stop();
        mediaRecorder.release();
        recording = false;
        cap.setText(MainActivity.this.getResources().getString(R.string.start));

    }

    // camera picture taken image and store in directory
    @SuppressWarnings("deprecation")
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera c) {

            FileOutputStream outStream = null;
            try {

                // Directory and name of the photo. We put system time
                // as a postfix, so all photos will have a unique file name.
                outStream = new FileOutputStream("/sdcard/" + Constants.STORAGE_PATH + "/snaps/" +
                        Constants.STORAGE_PATH +
                        System.currentTimeMillis()+".jpg");
                outStream.write(data);
                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }

        }
    };


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        // stop the camera
        if(camCondition){
            camera.stopPreview(); // stop preview using stopPreview() method
            camCondition = false; // setting camera condition to false means stop
        }
        // condition to check whether your device have camera or not
        if (camera != null){
            try {
                camera.setDisplayOrientation(90);   // setting camera preview orientation
                Camera.Parameters parameters = camera.getParameters();
                // JPEG quality of captured picture. The range is 1 to 100, with 100 being the best.
                parameters.setJpegQuality(80); // picture quality to be set to 80.

                parameters.setColorEffect(Camera.Parameters.EFFECT_NONE); //applying effect on camera
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE); //focus of camera auto
                camera.setParameters(parameters); // setting camera parameters
                camera.setPreviewDisplay(surfaceHolder); // setting preview of camera
                camera.startPreview();  // starting camera preview

                camCondition = true; // setting camera to true which means having camera
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera = Camera.open();   // opening camera
        camera.setDisplayOrientation(90);   // setting camera preview orientation
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setPreviewFrameRate(20);
        camera.setParameters(parameters);
//        prepareMediaRecorder();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();  // stopping camera preview
        camera.release();       // releasing camera
        camera = null;          // setting camera to null when left
        camCondition = false;   // setting camera condition to false also when exit from application
    }

    private void prepareMediaRecorder(){
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class FinishRecordingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(MainActivity.this, null, "Please Wait..", true);
            mProgressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            if(recording) {
                mediaRecorder.stop();
                mediaRecorder.release();
                recording = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();

            Intent intent = new Intent(MainActivity.this,PlaybackActivity.class);
            intent.putExtra(PlaybackActivity.INTENT_NAME_VIDEO_PATH, finalPath);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            System.gc();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mProgressDialog!=null) {
            mProgressDialog.dismiss();
        }
    }

    private void createDirectory() {
        File myDirectory = new File(Environment.getExternalStorageDirectory(), this.getResources().getString(R.string.folderName));
        File myDirectory1 = new File(Environment.getExternalStorageDirectory(), this.getResources().getString(R.string.folderName) + "/videos");
        File myDirectory2 = new File(Environment.getExternalStorageDirectory(), this.getResources().getString(R.string.folderName) + "/snaps");

        if(!myDirectory.exists()) {
            myDirectory.mkdir();
            Log.v(TAG+"","Dir created");
        }
        else {
            Log.v(TAG+"","Directory already exists");
        }

        if(!myDirectory1.exists()) {
            myDirectory1.mkdir();
            Log.v(TAG+" videos","Directory created");
            // Crashlytics.log(Log.ERROR, "--->","Created For first time directory in android");
        }
        else {
            Log.v(TAG+" videos","Directory already exists");
        }

        if(!myDirectory2.exists()) {
            myDirectory2.mkdir();
            Log.v(TAG+" snaps","Directory created");
            // Crashlytics.log(Log.ERROR, "--->","Created For first time directory in android");
        }
        else {
            Log.v(TAG+" snaps","Directory already exists");
        }
    }
}