package com.youtube.example;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.youtubeplayer.CustomYoutubeFragment;
import com.youtubeplayer.OnConfigChanged;
import com.youtubeplayer.OnInitializedListener;

import java.util.ArrayList;
import java.util.List;

public class ExampleActivity extends AppCompatActivity {

    CustomYoutubeFragment customFragment;
    private View mainContent;
    private Button button1, button2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        customFragment = (CustomYoutubeFragment) getFragmentManager().findFragmentById(R.id.customFragment);
        mainContent = findViewById(R.id.content_main);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        customFragment.initialize("AIzaSyDDRyqAf3cU76BOhPjssMzEs8M2-hC8Vig", new OnInitializedListener() {
            @Override
            public void onSuccess(boolean wasRestored) {
                customFragment.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                customFragment.getYouTubePlayer().setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION |
                        YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                customFragment.setFullScreenListener(new YouTubePlayer.OnFullscreenListener() {
                    @Override
                    public void onFullscreen(boolean b) {
                        if (b) {
                            button1.setVisibility(View.GONE);
                            button2.setVisibility(View.GONE);
                        } else {
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
                        }
                    }
                });
                List<String> arrayList = new ArrayList<>();
                arrayList.add("uvMYRIRUS2U");
                arrayList.add("PLy4xTWVDquWO0_rRHbCksHRYglZMmSJdV");
                customFragment.getYouTubePlayer().loadVideo("Tnj9E5I3DkM");
                customFragment.setViewY(customFragment.getViewY() + 50);
                customFragment.showDefaultPopUp();
            }

            @Override
            public void onError(YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

        customFragment.setOnConfigurationChanged(new OnConfigChanged() {
            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                customFragment.setViewY(customFragment.getViewY() + 50);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (customFragment.isFullScreen()) {
            customFragment.getYouTubePlayer().setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }
}
