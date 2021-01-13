/*
 * Copyright (c) 2017, Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package com.qualcomm.qti.qmmi.testcase.Hdmiout;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.qualcomm.qti.qmmi.R;
import com.qualcomm.qti.qmmi.framework.BaseActivity;

import java.io.IOException;

public class HdmioutActivity extends BaseActivity {
    private static final String TAG = HdmioutActivity.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMediaPlayer();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hdmi_out_act;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void initMediaPlayer()
    {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.reset();
        mSurfaceView = (SurfaceView)findViewById(R.id.playview);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(new SurfaceCallback());

        try{
//            AssetFileDescriptor afd = getAssets().openFd("SampleVideo_1280x720_10mb.mp4");
//            String path = afd.getFileDescriptor().toString();
//            Log.d(TAG,"path:"+path);
//            mMediaPlayer.setDataSource(afd.getFileDescriptor());//设置播放视频路径
            String storageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Qmmi";
            AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.hdmi_out_test_1280x720);
            //String path = storageDirectory + "/video_test_1280x720.mp4";

            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());//设置播放视频路径
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mMediaPlayer.prepareAsync();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepared()");
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(HdmioutActivity.this, "播放完毕！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "surfaceCreated()");
            mMediaPlayer.setDisplay(holder);// surfaceView被创建
            mMediaPlayer.start(); //开始播放
        }
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

    }

    @Override
    protected void onDestroy() {
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();  //停止播放视频
        }
        mMediaPlayer.release();   //释放资源
        super.onDestroy();
    }
}
