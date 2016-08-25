package com.wheat.mobile.smallvideoapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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
        mProgressBar.setMax(mRecordMaxTime);

        mSurfaceHolder=mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        a.recycle();
    }

    private class CustomCallBack implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if(!isOpenCamera)
                return;

            try {
                initCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if(!isOpenCamera)
                return;
            freeCameraResource();
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

    private void createRecordDir(){
        File sampleDir=new File(Environment.getExternalStorageDirectory()+File.separator+"im/video");
        if(!sampleDir.exists()){
            sampleDir.mkdirs();
        }
        File vecordDir=sampleDir;
        try {
            mRecordFile=File.createTempFile("recording",".mp4",vecordDir);
            Log.i("TAG",mRecordFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initRecord() throws IOException {
        mMediaRecorder=new MediaRecorder();
        mMediaRecorder.reset();
        if(mCamera!=null){
            mMediaRecorder.setCamera(mCamera);
        }
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setVideoSize(mWidth,mHeight);

        mMediaRecorder.setVideoEncodingBitRate(1*1280*720);
        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

        mMediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
        mMediaRecorder.prepare();

        try {
            mMediaRecorder.start();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (RuntimeException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void record(final OnRecordFinishListener onRecordFinishListener){
        this.mOnRecordFinishListener=onRecordFinishListener;
        createRecordDir();
        try {
            if (!isOpenCamera)
                initCamera();

            initRecord();
            mTimeCount=0;
            mTimer=new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTimeCount++;
                    mProgressBar.setProgress(mTimeCount);
                    if(mTimeCount==mRecordMaxTime){
                        stop();

                        if(mOnRecordFinishListener!=null)
                            mOnRecordFinishListener.onRecordFinish();
                    }
                }
            },0,1000);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void stop(){
        stopRecord();
        releaseRecord();
        freeCameraResource();
    }

    public void stopRecord(){
        mProgressBar.setProgress(0);
        if(mTimer!=null)
            mTimer.cancel();

        if(mMediaRecorder!=null){
            mMediaRecorder.setOnErrorListener(null);
            try{
                mMediaRecorder.stop();
            }catch (IllegalStateException e){
                e.printStackTrace();
            }catch (RuntimeException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
            mMediaRecorder.setPreviewDisplay(null);
        }
    }

    private void releaseRecord(){
        if(mMediaRecorder!=null){
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            }catch (IllegalStateException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        mMediaRecorder=null;
    }

    public int getTimeCount(){
        return mTimeCount;
    }

    public File getRecordFile(){
        return mRecordFile;
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try{
            if(mr!=null)
                mr.reset();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public interface OnRecordFinishListener{
        void onRecordFinish();
    }
}
