package com.wheat.mobile.smallvideoapplication;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MovieRecorderView mRecordView;
    private Button mShootBtn;
    private boolean isFinish=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecordView=(MovieRecorderView)findViewById(R.id.movieRecorderView);
        mShootBtn=(Button)findViewById(R.id.shoot_button);
        mShootBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    mRecordView.record(new MovieRecorderView.OnRecordFinishListener() {
                        @Override
                        public void onRecordFinish() {
                            handler.sendEmptyMessage(1);
                        }
                    });
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    if(mRecordView.getTimeCount()>1){
                        handler.sendEmptyMessage(1);
                    }else{
                        if(mRecordView.getRecordFile()!=null){
                            mRecordView.getRecordFile().delete();
                        }
                        mRecordView.stop();
                        Toast.makeText(MainActivity.this,"录制时间太短",Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFinish=true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFinish=false;
        mRecordView.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            finishActivity();
        }
    };

    private void finishActivity(){
        if(isFinish){
            mRecordView.stop();
            Intent intent=new Intent();
            Log.d("TAG",mRecordView.getRecordFile().getAbsolutePath());
            intent.putExtra("path",mRecordView.getRecordFile().getAbsolutePath());
            setResult(RESULT_OK,intent);
        }
        finish();
    }
}
