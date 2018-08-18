package com.dreamwalker.diabetesfoodypilot.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

import com.dreamwalker.diabetesfoodypilot.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";

    @BindView(R.id.btnFacebookLogin)
    MaterialButton materialButton;

    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.text_view)
    TextView textView;

    MediaPlayer mediaPlayer;
    int currentVideoPosition;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        
        Paper.init(this);

        if (Paper.book().read("firstRun") != null){
            boolean flag = Paper.book().read("firstRun");
            if (flag){
                startActivity(new Intent(StartActivity.this, InitAppCheckActivity.class));
                finish();
            }
        }

        // TODO: 2018-08-16 TextView가 안보인다. 해결이 필요하다.
        textView.bringToFront();
        textView.setZ(10.0f);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.start_edit);
//        Uri uriZero = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.start_steak_3);
//        Uri uriOne = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.start_steak_2);
//        Uri uriTwo = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.start_steak);
//
//        List<Uri> videoPathes = new ArrayList<>();
//        videoPathes.add(uriOne);
//        videoPathes.add(uriTwo);
//        videoPathes.add(uriZero);

        videoView.setVideoURI(uri);
        videoView.start();

        videoView.setOnCompletionListener(mediaPlayer -> {
            // TODO: 2018-08-16 동영상을 자동적으로 연속재생할 필요가 있음 - 박제창
//            i = (i + 1) % videoPathes.size();
//            videoView.setVideoURI(videoPathes.get(i));
//            videoView.start();
        });

        videoView.setOnPreparedListener(mp -> {
            mediaPlayer = mp;
            mediaPlayer.setLooping(true);
            if (currentVideoPosition != 0) {
                mediaPlayer.seekTo(currentVideoPosition);
                mediaPlayer.start();
            }
        });
    }

    @OnClick(R.id.btnFacebookLogin)
    public void  onClickedStartButton(){
        Log.e(TAG, "onClickedStartButton: ");
        Paper.book().write("firstRun", true);
        startActivity(new Intent(StartActivity.this, InitAppCheckActivity.class));
        finish();

    }

    @Override
    protected void onPause() {
        super.onPause();
        currentVideoPosition = mediaPlayer.getCurrentPosition();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mediaPlayer.stop();
        //mediaPlayer.release();
        mediaPlayer = null;
    }

  
}
