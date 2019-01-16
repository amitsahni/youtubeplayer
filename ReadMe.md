------
* Add fragment in xml
```xml
  <fragment
            android:id="@+id/customFragment"
            class="com.youtubeplayer.CustomYoutubeFragment"
            android:layout_width="300dp"
            android:layout_height="300dp"
            android:layout_centerInParent="true" />
```
* Use in Activity

```
CustomYoutubeFragment customFragment = (CustomYoutubeFragment) getFragmentManager().findFragmentById(R.id.customFragment);

customFragment.initialize("key", new OnInitializedListener() {
            @Override
            public void onSuccess(boolean wasRestored) {
                customFragment.getYouTubePlayer().setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION |
                        YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                customFragment.getYouTubePlayer().loadVideo("Tnj9E5I3DkM");
                float px = convertDpToPx(ExampleActivity.this, getResources().getDimension(R.dimen.popupheight));
                customFragment.setViewY((int) (customFragment.getViewY() + px));
                customFragment.showDefaultPopUp();
                customFragment.setFullScreenListener(new YouTubePlayer.OnFullscreenListener() {
                    @Override
                    public void onFullscreen(boolean b) {
                        if (b) {
                            Full Screen youTube player
                            Make other view gone
                        } else {
                            Return youTube player to normal view
                            Make other view visible
                        }
                    }
                });
            }

            @Override
            public void onError(YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
        
customFragment.setOnConfigurationChanged(new OnConfigChanged() {
            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                // If need to set your Y postion of popup when screen rotates
                customFragment.setViewY(customFragment.getViewY() + 110);
            }
        });
```

* Pass Any custom View
```
customFragment.setPopupWindow(view);
// Handle your view in your class 
```

Download
--------
Add the JitPack repository to your root build.gradle

```groovy
	allprojects {
		repositories {
			maven { url "https://jitpack.io" }
		}
	}
```
Add the Gradle dependency:
```groovy
	dependencies {
		compile 'com.github.amitsahni:youtubeplayer:1.0.0'
	}
```