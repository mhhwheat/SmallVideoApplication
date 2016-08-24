package com.wheat.mobile.smallvideoapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

/**
 * Created by Administrator on 2016/8/24.
 */
public class MovieRecorderView extends LinearLayout implements OnErrorListener{

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ProgressBar mProgressBar;

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Timer mTimer;
    private OnRecordFinishListener mOnRecordFinishListener;

    private int mWidth;
    private int mHeight;
    private boolean isOpenCamera;
    private int mRecordMaxTime;
    private int mTimeCount;
    private File mRecordFile=null;


    public MovieRecorderView(Context context){
        this(context,null);
    }

    public MovieRecorderView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.MovieRecorderView,defStyle,0);
        mWidth=a.getInteger(R.styleable.MovieRecorderView_video_width,320);
        mHeight=a.getInteger(R.styleable.MovieRecorderView_video_height,240);

        isOpenCamera=a.getBoolean(R.styleable.MovieRecorderView_is_open_camera,true);
        mRecordMaxTime=a.getInteger(R.styleable.MovieRecorderView_record_max_time,10);

        LayoutInflater.from(context).inflate(R.layout.movie_recorder_view,this);
        mSurfaceView=(SurfaceView)findViewById(R.id.surfaceView);
        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);

        a.recycle();
    }

    private class CustomCallBack implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if(!isOpenCamera)
                return;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if(!isOpenCamera)
                return;

        }
    }

    private void initCamera() throws IOException{
        if(mCamera!=null){
            freeCameraResource();
        }
        try {
            mCamera = Camera.open();
        }catch (Exception e){
            e.printStackTrace();
            freeCameraResource();
        }

        if(mCamera==null)
            return;

        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewDisplay(mSurfaceHolder);
        mCamera.startPreview();
        mCamera.unlock();
    }

    private void freeCameraResource(){
        if(mCamera!=null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera=null;
        }
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {

    }

    public interface OnRecordFinishListener{
        void onRecordFinish();
    }
}
