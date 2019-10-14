package tv.superawesome.lib.savideoplayer;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;

public class SAVideoPlayer extends Fragment {

    // subviews in the Fragment
    private SAMediaController           controller = null;
    private FrameLayout                 videoHolder = null;
    private VideoView                   videoView = null;
    private MediaPlayer                 mediaPlayer = null;

    // state vars set by the user
    private int                         oldWidth = 0;
    private int                         oldHeight = 0;
    private boolean                     shouldShowSmallClickButton = false;

    // bool vars for each step
    private int                         mCurrentSeekPos = 0;
    private boolean                     isStartHandled = false;
    private boolean                     is2SHandled = false;
    private boolean                     isFirstQuartileHandled = false;
    private boolean                     isMidpointHandled = false;
    private boolean                     isThirdQuartileHandled = false;
    private boolean                     isCompleteHandled = false;
    private boolean                     is15sHandled = false;
    private boolean                     isErrorHandled = false;

    // current time
    private int                         current = 0;
    private int                         duration = 0;

    // listeners & other configuration variables
    private SAVideoPlayerEventInterface eventListener = null;
    private SAVideoPlayerClickInterface clickListener = null;

    /**
     * Main fragment constructor that inits the event and click listeners so you don't get null
     * pointer errors all the time
     *
     */
    public SAVideoPlayer(){
        eventListener = new SAVideoPlayerEventInterface() { @Override public void saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent event, int time, int duration) {} };
        clickListener = new SAVideoPlayerClickInterface() { @Override public void onClick(View v) {}};
    }

    /**
     * Overridden "onCreate" method of the Fragment, that gets called only once.
     * Main role is to setRetainInstance to true so the fragment doesn't get recreated all the time
     * and to start the media player
     *
     * @param savedInstanceState previous saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Log.d("SuperAwesome", "SAVideoPlayer - On Create");

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /* don't continue if completed */
                if (isCompleteHandled) return;

                if (mediaPlayer != null) {
                    current = mediaPlayer.getCurrentPosition();
                    duration = mediaPlayer.getDuration();

                    if (current >= 1 && !isStartHandled) {
                        isStartHandled = true;
                        eventListener.saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent.Video_Start, current, duration);
                    }
                    if (current >= 2000 && !is2SHandled) {
                        is2SHandled = true;
                        eventListener.saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent.Video_2s, current, duration);
                    }
                    if (current >= duration / 4 && !isFirstQuartileHandled) {
                        isFirstQuartileHandled = true;
                        eventListener.saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent.Video_1_4, current, duration);
                    }
                    if (current >= duration / 2 && !isMidpointHandled) {
                        isMidpointHandled = true;
                        eventListener.saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent.Video_1_2, current, duration);
                    }
                    if (current >= 3 * duration / 4 && !isThirdQuartileHandled) {
                        isThirdQuartileHandled = true;
                        eventListener.saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent.Video_3_4, current, duration);
                    }
                    if (current >= 15000 && !is15sHandled) {
                        is15sHandled = true;
                        eventListener.saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent.Video_15s, current, duration);
                    }

                    if (controller != null) {
                        int remaining = (duration - current) / 1000;
                        controller.setChronographText("Ad: " + remaining);
                    }
                }

                handler.postDelayed(this, 500);
            }
        }, 500);
    }

    /**
     * Overridden "onCreateView" fragment method that gets called each time the view gets
     * recreated
     *
     * @param inflater           current inflater
     * @param container          fragment container (a view of sorts)
     * @param savedInstanceState previous saved instance
     * @return                   the main view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        Log.d("SuperAwesome", "SAVideoPlayer - On Create View");

        // if this is the first time "onCreateView" is called then:
        if (videoHolder == null) {

            // start creating a video holder
            videoHolder = new FrameLayout(getActivity());
            videoHolder.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            videoHolder.setBackgroundColor(Color.BLACK);

            // start creating a video view
            videoView = new VideoView(getActivity());
            // add a callback for when the video view's holder is actually created
            videoView.getHolder().addCallback(new SurfaceHolder.Callback() {
                /**
                 * Method that gets called when the video view's surface is first created
                 *
                 * @param holder the current holder
                 */
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    Log.d("SuperAwesome", "Video View - Surface Created with Media Player " + mediaPlayer);

                    if (mediaPlayer == null) return;

                    // set the media player's display and start it
                    mediaPlayer.setDisplay(holder);

                    try {
                        mediaPlayer.prepare();
                        Log.d("SuperAwesome", "Video View - Set holder for Media Player and started Preparing");
                    } catch (Exception e) {
                        Log.w("SuperAwesome", "Non fatal exception happened whilst preparing the media player " + e.getMessage());
                    }

                    // scale the video view properly
                    int mpWidth = mediaPlayer.getVideoWidth();
                    int mpHeight = mediaPlayer.getVideoHeight();
                    int cWidth = videoHolder.getMeasuredWidth();
                    int cHeight = videoHolder.getMeasuredHeight();
                    videoView.setLayoutParams(getVideoViewLayoutParams(mpWidth, mpHeight, cWidth, cHeight));

                    Log.d("SuperAwesome", "Container " + cWidth + ", " + cHeight);
                }

                /**
                 * Method that gets called when the video view's surface is changed
                 *
                 * @param holder current holder
                 * @param format current format
                 * @param width  new width
                 * @param height new height
                 */
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    Log.d("SuperAwesome", "Video View - Surface changed");
                    // do nothing
                }

                /**
                 * Method that gets called when the video view's surface is destroyed
                 *
                 * @param holder the current holder
                 */
                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    Log.d("SuperAwesome", "Video View - Surface destroyed with Media Player " + mediaPlayer);
                    if (mediaPlayer != null) {
                        mCurrentSeekPos = mediaPlayer.getCurrentPosition();
                        mediaPlayer.stop();
                    }
                }
            });

            // add the video view as a subview to the FrameLayout video holder
            videoHolder.addView(videoView);

            // and an observer to re-scale the video view each time the video holder's layout
            // changes - so that all the time the video has the correct aspect ratio
            videoHolder.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    int newWidth = videoHolder.getMeasuredWidth();
                    int newHeight = videoHolder.getMeasuredHeight();

                    if ((newWidth != oldWidth || newHeight != oldHeight)  && mediaPlayer != null) {
                        oldWidth = newWidth;
                        oldHeight = newHeight;

                        int mpWidth = mediaPlayer.getVideoWidth();
                        int mpHeight = mediaPlayer.getVideoHeight();
                        int cWidth = videoHolder.getMeasuredWidth();
                        int cHeight = videoHolder.getMeasuredHeight();
                        videoView.setLayoutParams(getVideoViewLayoutParams(mpWidth, mpHeight, cWidth, cHeight));

                    }
                }
            });

            // create the media controller, which basically holds all video UI elements
            controller = new SAMediaController(getActivity());
            controller.setShouldShowSmallClickButton(shouldShowSmallClickButton);
            controller.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            controller.initialize();
            controller.setClickListener(clickListener);
            videoHolder.addView(controller);

            // send video prepared evt
            eventListener.saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent.Video_Prepared, SAVideoPlayer.this.current, duration);

        } else {
            container.removeView(videoHolder);
        }

        return videoHolder;
    }

    /**
     * Main "play" method of the video player
     *
     * @param path          the path of the video
     * @throws Throwable    the method throws an exception with whatever error it encounters
     */
    public void play (String path) throws Throwable {

        final Activity current = getActivity();
        if (current == null) {
            eventListener.saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent.Video_Error, SAVideoPlayer.this.current, duration);
            throw new Exception("Fragment not prepared yet! Await the 'Video_Prepared' event in order to play.");
        } else {
            File file = new File(current.getFilesDir(), path);
            if (file.exists()) {

                String videoURL = file.toString();

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(current, Uri.parse(videoURL));

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mp) {

                        Log.d("SuperAwesome", "Media Player - Prepared");

                        mp.start();

                        if (mCurrentSeekPos != 0) {
                            mp.seekTo(mCurrentSeekPos);
                            mp.start();
                        }

                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {

                        Log.d("SuperAwesome", "Media Player - Error");

                        // send events
                        if (!isErrorHandled) {
                            isErrorHandled = true;
                            eventListener.saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent.Video_Error, SAVideoPlayer.this.current, duration);
                        }
                        return false;
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        Log.d("SuperAwesome", "Media Player - Completed");

                        videoHolder.removeView(videoView);
                        videoHolder.removeView(controller);

                        mp.stop();
                        mp.setDisplay(null);
                        mp.release();
                        mediaPlayer = null;

                        // send events
                        if (!isCompleteHandled) {
                            isCompleteHandled = true;
                            if (!isErrorHandled) {
                                eventListener.saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent.Video_End, SAVideoPlayer.this.current, duration);
                            }
                        }
                    }
                });

            } else {
                eventListener.saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent.Video_Error, SAVideoPlayer.this.current, duration);
                throw new Exception("File " + path + " does not exist on disk. Will not play!");
            }
        }
    }

    /**
     * Sets if the video should have a small click surface area or a full click surface area
     *
     * @param value new value
     */
    public void setShouldShowSmallClickButton (boolean value) {
        shouldShowSmallClickButton = value;
    }

    /**
     * Setter for a new event listener
     *
     * @param listener library user listener implementation
     */
    public void setEventListener (SAVideoPlayerEventInterface listener) {
        eventListener = listener != null ? listener : eventListener;
    }

    /**
     * Setter for a new click listener
     *
     * @param listener library user listener implementation
     */
    public void setClickListener (SAVideoPlayerClickInterface listener) {
        clickListener = listener != null ? listener : clickListener;
    }

    /**
     *
     * Method that pauses the player
     */
    @Deprecated
    public void pausePlayer() {
        if (!isCompleteHandled && !isErrorHandled) {
            mediaPlayer.pause();
        }
    }

    /**
     * Method that resumes the player
     */
    @Deprecated
    public void resumePlayer () {
        if (!isCompleteHandled && !isErrorHandled) {
            mediaPlayer.start();
        }
    }

    /**
     * Method that closes the player
     */
    public void close () {
        Log.d("SuperAwesome", "SAVideoPlayer - Close!");
        isCompleteHandled = true;
        videoHolder.removeView(videoView);
        videoHolder.removeView(controller);

        // only close media player if not already null-ed or closed.
        // this allows for both explicitly closing the media player using this method or
        // letting the media player close itself when it reached the end
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.setDisplay(null);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Returns the current video view
     *
     * @return instance of video view
     */
    public VideoView getVideoPlayer() { return videoView; }

    /**
     * Returns the current media player
     *
     * @return instance of media player
     */
    public MediaPlayer getMediaPlayer () { return mediaPlayer; }

    /**
     * Returns the current container
     *
     * @return instance of the container view
     */
    public FrameLayout getVideoHolder () {
        return videoHolder;
    }

    /**
     * Method that gets the video view proper layout params so it maintains aspect ratio
     *
     * @param sourceW   the video width
     * @param sourceH   the video height
     * @param boundingW the container width
     * @param boundingH the container height
     * @return          the FrameLayout.LayoutParams needed by the video
     */
    private FrameLayout.LayoutParams getVideoViewLayoutParams(float sourceW, float sourceH, float boundingW, float boundingH) {
        float sourceRatio = sourceW / sourceH;
        float boundingRatio = boundingW / boundingH;
        float X, Y, W, H;
        if(sourceRatio > boundingRatio) {
            W = boundingW;
            H = W / sourceRatio;
            X = 0.0F;
            Y = (boundingH - H) / 2.0F;
        } else {
            H = boundingH;
            W = sourceRatio * H;
            Y = 0.0F;
            X = (boundingW - W) / 2.0F;
        }

        FrameLayout.LayoutParams returnParams = new FrameLayout.LayoutParams((int)W, (int)H);
        returnParams.setMargins((int)X, (int)Y, 0, 0);

        return returnParams;
    }
}