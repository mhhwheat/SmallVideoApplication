package com.wheat.mobile.smallvideoapplication.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.wheat.mobile.smallvideoapplication.R;

/**
 * Created by Administrator on 2016/8/25.
 */
public class VideoFragment extends Fragment {

    private static final String VIDEO_PATH="video_path";
    private String videoPath;
    private VideoView mVideoView;
    private Button btnClose;


    public static VideoFragment newInstance(String videoPath) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(VIDEO_PATH, videoPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            videoPath=getArguments().getString(VIDEO_PATH);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_video,container,false);
        mVideoView=(VideoView)view.findViewById(R.id.video_view);
        btnClose=(Button)view.findViewById(R.id.btn_close);

        mVideoView.setMediaController(new MediaController(getActivity()));
        mVideoView.setVideoURI(Uri.parse(videoPath));
        mVideoView.start();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(VideoFragment.this).commit();
            }
        });
        return view;
    }
}
