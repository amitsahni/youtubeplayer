package com.youtubeplayer;

import com.google.android.youtube.player.YouTubeInitializationResult;

public interface OnInitializedListener {
    void onSuccess(boolean wasRestored);

    void onError(YouTubeInitializationResult youTubeInitializationResult);
}
