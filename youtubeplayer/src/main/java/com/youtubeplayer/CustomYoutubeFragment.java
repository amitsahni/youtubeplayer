package com.youtubeplayer;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.util.Locale;

public class CustomYoutubeFragment extends YouTubePlayerFragment {

    private YouTubePlayer youTubePlayer;
    private PopupWindow popupWindow;
    private int height, width = 0;
    private int viewX, viewY = 0;
    private boolean isFullScreen;
    private final String TAG = "CustomYoutubeFragment";
    private boolean isDefault, isError;
    private OnConfigChanged listener;
    private String videoId = "";

    // Player Control
    private View view, gradient, childView;
    private ImageView playPause;
    private ImageView rewind;
    private ImageView forward;
    private SeekBar seekBar;
    private ImageView fullScreen;
    private TextView playTime;
    private Handler handler, handler1 = null;
    private int milli;
    private Configuration configuration;
    private int statusBarHeight;

    public static CustomYoutubeFragment newInstance() {
        CustomYoutubeFragment customYoutubeFragment = new CustomYoutubeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", "");
        customYoutubeFragment.setArguments(bundle);
        return customYoutubeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        //initialize(key, this);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.view_popup_window, null, false);
        configuration = getResources().getConfiguration();

        view.post(new Runnable() {
            @Override
            public void run() {
                Rect rectangle = new Rect();
                Window window = getActivity().getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                statusBarHeight = rectangle.top;
                Log.d(TAG, "statusBarHeight = " + statusBarHeight);
            }
        });
        return super.onCreateView(layoutInflater, viewGroup, bundle);

    }

    public @Nullable
    YouTubePlayer getYouTubePlayer() {
        return youTubePlayer;
    }

    public @Nullable
    PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public int getViewX() {
        return viewX;
    }

    public int getViewY() {
        return viewY;
    }

    public void setViewX(int viewX) {
        this.viewX = viewX;
    }

    public void setViewY(int viewY) {
        this.viewY = viewY;
    }

    private void setFullscreen(boolean b) {
        if (youTubePlayer != null) {
            youTubePlayer.setFullscreen(b);
        }
    }

    public void initialize(String key, final OnInitializedListener listener) {
        initialize(BuildConfig.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, final boolean b) {
                Log.d(TAG, "onInitializationSuccess");
                getView().post(new Runnable() {
                    @Override
                    public void run() {
                        height = getView().getHeight();
                        width = getView().getWidth();
                        viewX = (int) getView().getX();
                        viewY = (int) getView().getY() + statusBarHeight;
                        Log.d(TAG, "Heigth = " + height);
                        Log.d(TAG, "width = " + width);
                        Log.d(TAG, "viewX = " + viewX);
                        Log.d(TAG, "viewY = " + viewY);
                        listener.onSuccess(b);
                    }
                });
                CustomYoutubeFragment.this.youTubePlayer = youTubePlayer;
                handler = new Handler(Looper.getMainLooper());
                handler1 = new Handler(Looper.getMainLooper());
                displayCurrentTime();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e(getTag(), "onInitializationFailure = " + youTubeInitializationResult.name());
                listener.onError(youTubeInitializationResult);
            }
        });

        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isFullScreen) {
                    viewX = (int) getView().getX();
                    viewY = (int) getView().getY() + statusBarHeight;
                    Log.d(TAG, "viewX = " + viewX);
                    Log.d(TAG, "viewY = " + viewY);
                    if (getPopupWindow() != null) {
                        getPopupWindow().update(viewX, viewY, width, height);
                    }
                }
            }
        });

    }

    public void setOnConfigurationChanged(OnConfigChanged l) {
        listener = l;
    }


    private void setPlaybackEventListener() {
        setPlaybackEventListener(null);
    }


    private void setPlayerStateChangeListener() {
        setPlayerStateChangeListener(null);
    }


    private void setPlaylistEventListener() {
        setPlaylistEventListener(null);
    }

    private void setFullScreenListener() {
        setFullScreenListener(null);
    }

    public void setFullScreenListener(final YouTubePlayer.OnFullscreenListener l) {
        if (youTubePlayer != null) {
            youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(final boolean b) {
                    isFullScreen = b;
                    Log.d(TAG, "onFullscreen = " + b);
                    dismissPopUp();
                    layoutChange();
                    if (l != null) {
                        l.onFullscreen(b);
                    }
                }
            });
        }
    }


    public void showDefaultPopUp() {
        setFullScreenListener();
        setPlaybackEventListener();
        setPlayerStateChangeListener();
        setPlaylistEventListener();
        setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        isDefault = true;
        if (popupWindow == null) {
            popupWindow = new PopupWindow(view, width, height);
        }
        showPopUp();
        defaultViewControl(view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gradient != null)
                    gradient.callOnClick();
            }
        }, 1000);
    }

    public void setPopupWindow(@NonNull final View v) {
        isDefault = false;
        if (popupWindow == null) {
            popupWindow = new PopupWindow(v, width, height);
        }
        popupWindow.update(v, width, height);
    }

    public void showPopUp() {
        if (popupWindow != null &&
                getView() != null) {
            Log.d(TAG, "showPopUp");
            Log.d(TAG, "viewX = " + viewX);
            Log.d(TAG, "viewY = " + viewY);
            popupWindow.showAtLocation(getView(), Gravity.NO_GRAVITY, viewX, viewY);
        }
    }

    public void dismissPopUp() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    Log.d(TAG, "dismissedPopUp");
                }
            });
        }
    }

    private void setPlayerStyle(YouTubePlayer.PlayerStyle style) {
        if (youTubePlayer != null) {
            youTubePlayer.setPlayerStyle(style);
        }
    }

    private void layoutChange() {
        if (getView() == null) return;
        ViewGroup.LayoutParams params = getView().getLayoutParams();
        if (isFullScreen) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            params.height = displayMetrics.heightPixels;
            params.width = displayMetrics.widthPixels;
            if (getPopupWindow() != null) {
                getPopupWindow().update(0, 0, params.width, params.height);
            }
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (getPopupWindow() != null) {
                getPopupWindow().update(viewX, viewY, width, height);
            }
            params.height = height;
            params.width = width;
        }
        getView().setLayoutParams(params);
        showPopUp();

    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dismissPopUp();
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (configuration.orientation == newConfig.orientation) {
                    Log.d(TAG, "onConfigurationChanged");
                    Log.d(TAG, "No Change");
                    showPopUp();
                } else {
                    viewX = (int) getView().getX();
                    viewY = (int) getView().getY();
                    Log.d(TAG, "onConfigurationChanged");
                    Log.d(TAG, "viewX = " + viewX);
                    Log.d(TAG, "viewY = " + viewY);
                    if (listener != null) {
                        listener.onConfigurationChanged(newConfig);
                    }
                    layoutChange();
                }
            }
        }, 2000);


    }


    /************************************************************************************/

    public void setPlaybackEventListener(@NonNull final YouTubePlayer.PlaybackEventListener l) {
        if (youTubePlayer != null) {
            youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                @Override
                public void onPlaying() {
                    Log.d(TAG, "PlaybackEventListener : onPlaying");
                    handler.postDelayed(runnable, 100);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //gradient.callOnClick();
                        }
                    }, 1000);
                    if (playPause != null) {
                        playPause.setImageResource(android.R.drawable.ic_media_pause);
                    }
                    if (rewind != null) {
                        rewind.setVisibility(View.VISIBLE);
                    }
                    if (forward != null) {
                        forward.setVisibility(View.VISIBLE);
                    }
                    if (l != null)
                        l.onPlaying();
                }

                @Override
                public void onPaused() {
                    Log.d(TAG, "PlaybackEventListener : onPaused");
                    handler.removeCallbacks(runnable);
                    if (playPause != null) {
                        playPause.setClickable(true);
                        playPause.setImageResource(android.R.drawable.ic_media_play);
                    }
                    if (l != null)
                        l.onPaused();
                }

                @Override
                public void onStopped() {
                    Log.d(TAG, "PlaybackEventListener : onStopped");
                    handler.removeCallbacks(runnable);
                    if (l != null)
                        l.onStopped();
                }

                @Override
                public void onBuffering(boolean b) {
                    Log.d(TAG, "PlaybackEventListener : onBuffering = " + b);
                    if (playPause != null) {
                        playPause.setClickable(!b);
                    }
                    if (l != null)
                        l.onBuffering(b);
                }

                @Override
                public void onSeekTo(int i) {
                    Log.d(TAG, "PlaybackEventListener : onSeekTo = " + i);
                    handler.removeCallbacks(runnable);
                    displayCurrentTime();
                    if (l != null)
                        l.onSeekTo(i);
                }
            });
        }
    }

    public void setPlayerStateChangeListener(@NonNull final YouTubePlayer.PlayerStateChangeListener l) {
        if (youTubePlayer != null) {
            youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {
                    Log.d(TAG, "PlayerStateChangeListener : onLoading");
                    if (l != null)
                        l.onLoading();
                }

                @Override
                public void onLoaded(String s) {
                    Log.d(TAG, "PlayerStateChangeListener : onLoaded = " + s);
                    videoId = s;
                    if (isError && playPause != null) {
                        isError = false;
                        youTubePlayer.loadVideo(videoId, milli);
                        showPopUp();
                    }
                    if (l != null)
                        l.onLoaded(s);
                }

                @Override
                public void onAdStarted() {
                    Log.d(TAG, "PlayerStateChangeListener : onAdStarted");
                    if (l != null)
                        l.onAdStarted();
                }

                @Override
                public void onVideoStarted() {
                    Log.d(TAG, "PlayerStateChangeListener : onVideoStarted");
                    if (isDefault) {

                    }
                    if (l != null)
                        l.onVideoStarted();
                }

                @Override
                public void onVideoEnded() {
                    Log.d(TAG, "PlayerStateChangeListener : onVideoEnded");
                    if (playPause != null) {
                        playPause.setClickable(true);
                        playPause.setImageResource(android.R.drawable.ic_menu_rotate);
                    }
                    if (rewind != null) {
                        rewind.setVisibility(View.INVISIBLE);
                    }
                    if (forward != null) {
                        forward.setVisibility(View.INVISIBLE);
                    }
                    if (l != null)
                        l.onVideoStarted();
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {
                    Log.d(TAG, "PlayerStateChangeListener : onError = " + errorReason.name());
                    if (playPause != null) {
                        isError = true;
                        dismissPopUp();
                    }
//                    switch (errorReason) {
//                        case NETWORK_ERROR:
//                        case UNKNOWN:
//                            isError = true;
//                            if (youTubePlayer != null &&
//                                    playPause != null) {
//                                playPause.setClickable(true);
//                                playPause.setImageResource(android.R.drawable.ic_media_play);
//                            }
//                            if (rewind != null) {
//                                rewind.setVisibility(View.INVISIBLE);
//                            }
//                            if (forward != null) {
//                                forward.setVisibility(View.INVISIBLE);
//                            }
//                            break;
//                        default:
//                            break;
//                    }
                    switch (errorReason) {
                        case UNAUTHORIZED_OVERLAY:
                        case PLAYER_VIEW_NOT_VISIBLE:
                        case PLAYER_VIEW_TOO_SMALL:
                            showPopUp();
                            break;
                    }
                    if (l != null)
                        l.onError(errorReason);
                }
            });
        }
    }

    public void setPlaylistEventListener(@NonNull final YouTubePlayer.PlaylistEventListener l) {
        if (youTubePlayer != null) {
            youTubePlayer.setPlaylistEventListener(new YouTubePlayer.PlaylistEventListener() {
                @Override
                public void onPrevious() {
                    Log.d(TAG, "PlaylistEventListener : onPrevious");
                    if (l != null)
                        l.onPrevious();
                }

                @Override
                public void onNext() {
                    Log.d(TAG, "PlaylistEventListener : onNext");
                    if (l != null)
                        l.onNext();
                }

                @Override
                public void onPlaylistEnded() {
                    Log.d(TAG, "PlaylistEventListener : onPlaylistEnded");
                    if (l != null)
                        l.onPlaylistEnded();
                }
            });
        }
    }

    private void defaultViewControl(final View v) {
        playPause = v.findViewById(R.id.play_pause_btn);
        rewind = v.findViewById(R.id.video_rewind_btn);
        forward = v.findViewById(R.id.video_forward_btn);
        seekBar = v.findViewById(R.id.video_seekbar);
        fullScreen = v.findViewById(R.id.full_screen_btn);
        playTime = v.findViewById(R.id.play_time);
        gradient = v.findViewById(R.id.gradient);
        childView = v.findViewById(R.id.relative);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (youTubePlayer == null) return;
                if (!youTubePlayer.isPlaying()) {
                    if (isError) {
                        isError = false;
                        youTubePlayer.loadVideo(videoId, milli);
                    } else {
                        youTubePlayer.play();
                    }
                    playPause.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    youTubePlayer.pause();
                    playPause.setImageResource(android.R.drawable.ic_media_play);
                }
//                handler1.removeCallbacks(runnable1);
                handler1.postDelayed(runnable1, 5000);
                runnable1.start();
            }
        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (youTubePlayer != null) {
                    youTubePlayer.seekRelativeMillis(-(10 * 1000));
                }
//                handler1.removeCallbacks(runnable1);
                handler1.postDelayed(runnable1, 5000);
                runnable1.start();
//                handler1.removeCallbacks(runnable1, "handler1");
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (youTubePlayer != null) {
                    youTubePlayer.seekRelativeMillis(10 * 1000);
                }
                handler1.removeCallbacks(runnable1);
                handler1.postDelayed(runnable1, 5000);
                runnable1.start();
//                handler1.removeCallbacks(runnable1, "handler1");
            }
        });

        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (youTubePlayer != null) {
                    setFullscreen(!isFullScreen);
                }

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (youTubePlayer != null
                        && fromUser) {
                    int millis = (youTubePlayer.getDurationMillis() * progress) / 100;
                    youTubePlayer.seekToMillis(millis);
                }
//                handler1.removeCallbacks(runnable1);
//                runnable1.kill();
                handler1.postDelayed(runnable1, 5000);
                runnable1.start();
//                handler1.removeCallbacks(runnable1, "handler1");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch = " + seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch = " + seekBar.getProgress());
            }
        });
        gradient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = gradient.getBackground();
                if (drawable == null) {
                    gradient.setBackgroundColor(Color.parseColor("#80000000"));
                    childView.setVisibility(View.VISIBLE);
                    handler1.postDelayed(runnable1, 2000);
                    runnable1.start();
                } else {
                    gradient.setBackground(null);
                    childView.setVisibility(View.INVISIBLE);
                    handler1.removeCallbacks(runnable1);
                    runnable1.kill();
                }
            }
        });
    }

    public KillableRunnable runnable1 = new KillableRunnable() {
        @Override
        public void doWork() {
            Log.d(TAG, "runnable1");
            if (!youTubePlayer.isPlaying()) return;
            gradient.setBackground(null);
            if (childView != null) childView.setVisibility(View.INVISIBLE);
            kill();
        }
    };

    private void displayCurrentTime() {
        if (youTubePlayer != null &&
                playTime != null &&
                seekBar != null) {
            milli = youTubePlayer.getCurrentTimeMillis();
            String formattedTime = formatTime(youTubePlayer.getDurationMillis() - youTubePlayer.getCurrentTimeMillis());
            playTime.setText(formattedTime);
            seekBar.setProgress(getProgressPercentage(youTubePlayer.getCurrentTimeMillis(), youTubePlayer.getDurationMillis()));
        }
    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        String time;
        if (hours == 0) {
            time = String.format(Locale.getDefault(), "%02d:%02d", minutes % 60, seconds % 60);
        } else {
            time = hours + ":" + String.format(Locale.getDefault(), "%02d:%02d", minutes % 60, seconds % 60);
        }
        return time;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            displayCurrentTime();
            handler.postDelayed(this, 100);
        }
    };

    public int getProgressPercentage(long currentDuration1, long totalDuration1) {
        if (currentDuration1 == 0 || totalDuration1 == 0)
            return 0;

        long currentSeconds = (int) (currentDuration1 / 1000);
        long totalSeconds = (int) (totalDuration1 / 1000);
        // calculating percentage
        Double percentage = (((double) currentSeconds) / totalSeconds) * 100;
        // return percentage
        return percentage.intValue();
    }

}
