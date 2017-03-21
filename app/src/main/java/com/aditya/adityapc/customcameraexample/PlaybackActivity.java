package com.aditya.adityapc.customcameraexample;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;

import com.sprylab.android.widget.TextureVideoView;

public class PlaybackActivity extends AppCompatActivity {

    public static final String INTENT_NAME_VIDEO_PATH = "INTENT_NAME_VIDEO_PATH";

    private TextureVideoView mVvPlayback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
        mVvPlayback = (TextureVideoView) findViewById(R.id.vv_playback);

        final String path = getIntent().getStringExtra(INTENT_NAME_VIDEO_PATH);
        if (path == null) {
            finish();
        }

        //tvVideoPath.setText(path);
//        mVvPlayback.setVideoPath(path);
        mVvPlayback.setKeepScreenOn(true);
        mVvPlayback.canSeekForward();
        mVvPlayback.canSeekBackward();
        mVvPlayback.canPause();
        mVvPlayback.setRotation(90);
        mVvPlayback.setVideoPath(path);
        mVvPlayback.showContextMenu();

        //to show the pause, forward and previous buttons(media functions)
        mVvPlayback.setMediaController(new MediaController(this));


        mVvPlayback.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                mVvPlayback.start();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVvPlayback.stopPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVvPlayback.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVvPlayback.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
