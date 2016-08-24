package com.wheat.mobile.smallvideoapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Administrator on 2016/8/24.
 */
public class FirstActivity extends Activity {

    private Button btnRecordAudio;
    private ImageButton btnPlay;
    private String path="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btnRecordAudio=(Button)findViewById(R.id.btn_record_audio);
        btnPlay=(ImageButton)findViewById(R.id.play);


    }
}
