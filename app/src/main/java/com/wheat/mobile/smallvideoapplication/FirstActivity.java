package com.wheat.mobile.smallvideoapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.wheat.mobile.smallvideoapplication.fragment.VideoFragment;
import com.wheat.mobile.smallvideoapplication.utils.Utils;

/**
 * Created by Administrator on 2016/8/24.
 */
public class FirstActivity extends FragmentActivity {

    private Button btnRecordAudio;
    private ImageButton btnPlay;
    private String path="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btnRecordAudio=(Button)findViewById(R.id.btn_record_audio);
        btnPlay=(ImageButton)findViewById(R.id.play);

        btnRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirstActivity.this,MainActivity.class);
                FirstActivity.this.startActivityForResult(intent,200);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoFragment bigPic=VideoFragment.newInstance(path);
                FragmentManager mFragmentManager=getSupportFragmentManager();
                FragmentTransaction transaction=mFragmentManager.beginTransaction();
                transaction.replace(R.id.main_menu,bigPic);
                transaction.commit();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 200:
                if(resultCode==RESULT_OK){
                    // 成功
                    path = data.getStringExtra("path");
                    Toast.makeText(FirstActivity.this,"存储路径为:"+path,Toast.LENGTH_SHORT).show();
                    // 通过路径获取第一帧的缩略图并显示
                    Bitmap bitmap = Utils.createVideoThumbnail(path);
                    BitmapDrawable drawable = new BitmapDrawable(bitmap);
                    drawable.setTileModeXY(Shader.TileMode.REPEAT , Shader.TileMode.REPEAT);
                    drawable.setDither(true);
                    btnPlay.setBackgroundDrawable(drawable);
                }else{

                }
        }
    }
}
