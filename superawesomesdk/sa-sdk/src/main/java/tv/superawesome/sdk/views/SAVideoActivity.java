package tv.superawesome.sdk.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.lang.ref.WeakReference;

import tv.superawesome.lib.sautils.SAApplication;
import tv.superawesome.sdk.models.SAAd;
import tv.superawesome.sdk.listeners.SAAdListener;
import tv.superawesome.sdk.listeners.SAParentalGateListener;
import tv.superawesome.sdk.listeners.SAVideoAdListener;

/**
 * Created by gabriel.coman on 24/12/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SAVideoActivity implements SAViewProtocol {

    /** private activity object values */
    private Context context;
    private Intent intent;
    private static WeakReference<Activity> mActivityRef;

    /**********************************************************************************************/
    /** Normal <Init> functions & other aux functions */
    /**********************************************************************************************/

    /** base constructor */
    public SAVideoActivity(Context context){
        this.context = context;
    }



    public void setAdListener(SAAdListener adListener) {
        AdDataHolder.getInstance()._refAdListener = adListener;
    }

    public void setVideoAdListener(SAVideoAdListener videoAdListener){
        AdDataHolder.getInstance()._refVideoAdListener = videoAdListener;
    }

    public void setParentalGateListener(SAParentalGateListener parentalGateListener){
        AdDataHolder.getInstance()._refParentalGateListener = parentalGateListener;
    }

    public void setIsParentalGateEnabled (boolean isParentalGateEnabled) {
        AdDataHolder.getInstance()._refIsParentalGateEnabled = isParentalGateEnabled;
    }

    public void setShouldShowCloseButton (boolean shouldShowCloseButton){
        AdDataHolder.getInstance()._refShouldShowCloseButton = shouldShowCloseButton;
    }

    public void setShouldAutomaticallyCloseAtEnd (boolean shouldAutomaticallyCloseAtEnd){
        AdDataHolder.getInstance()._refShouldAutomaticallyCloseAtEnd = shouldAutomaticallyCloseAtEnd;
    }

    public void setShouldLockOrientation(boolean shouldLockOrientation) {
        AdDataHolder.getInstance()._refShouldLockOrientation = shouldLockOrientation;
    }

    public void setLockOrientation(int lockOrientation){
        AdDataHolder.getInstance()._refLockOrientation = lockOrientation;
    }

    /** weak ref update function - needed mostly to get the close() function to work */
    protected static void updateActivity(Activity activity){
        mActivityRef = new WeakReference<Activity> (activity);
    }

    /**********************************************************************************************/
    /** <SAViewProtocol> */
    /**********************************************************************************************/

    @Override
    public void setAd(SAAd ad){
        AdDataHolder.getInstance()._refAd = ad;
    }

    @Override
    public SAAd getAd() {
        return AdDataHolder.getInstance()._refAd;
    }

    /** play function */
    @Override
    public void play(){
        intent = new Intent(context, SAVideoActivityInner.class);
        context.startActivity(intent);
    }

    /** close function */
    @Override
    public void close() {
        if (mActivityRef != null) {
            mActivityRef.get().onBackPressed();
        }
    }

    @Override
    public void advanceToClick() {
        /** do nothing */
    }

    @Override
    public void resizeToSize(int width, int height) {
        /** do nothing */
    }

    /** inner activity class */
    public static class SAVideoActivityInner extends Activity {

        /** private variables that control how the ad behaves */
        private SAAd ad;
        private boolean isParentalGateEnabled = true;
        private boolean shouldShowCloseButton = true;
        private boolean shouldAutomaticallyCloseAtEnd = true;
        private boolean shouldLockOrientation = false;
        private int lockOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        /** sdk listeners */
        private SAAdListener adListener;
        private SAParentalGateListener parentalGateListener;
        private SAVideoAdListener videoAdListener;

        /** vast stuff */
        private SAVideoAd videoAd;

        /** is OK to close bool */
        private boolean isOKToClose = false;

        @Override
        public void onSaveInstanceState(Bundle savedInstanceState) {
             super.onSaveInstanceState(savedInstanceState);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            /** update parent class weak ref to point to this activity */
            SAVideoActivity.updateActivity(this);

            /** load resources */
            String packageName = SAApplication.getSAApplicationContext().getPackageName();
            int activity_sa_videoId = getResources().getIdentifier("activity_sa_video", "layout", packageName);
            int video_adId = getResources().getIdentifier("video_ad", "id", packageName);

            setContentView(activity_sa_videoId);

            /** assign data from AdDataHolder */
            ad = AdDataHolder.getInstance()._refAd;
            isParentalGateEnabled = AdDataHolder.getInstance()._refIsParentalGateEnabled;
            shouldShowCloseButton = AdDataHolder.getInstance()._refShouldShowCloseButton;
            shouldAutomaticallyCloseAtEnd = AdDataHolder.getInstance()._refShouldAutomaticallyCloseAtEnd;
            adListener = AdDataHolder.getInstance()._refAdListener;
            videoAdListener = AdDataHolder.getInstance()._refVideoAdListener;
            parentalGateListener = AdDataHolder.getInstance()._refParentalGateListener;
            shouldLockOrientation = AdDataHolder.getInstance()._refShouldLockOrientation;
            lockOrientation = AdDataHolder.getInstance()._refLockOrientation;

            /** make sure direction is locked */
            if (shouldLockOrientation) {
                setRequestedOrientation(lockOrientation);
            }

            /** get player */
            videoAd = (SAVideoAd) findViewById(video_adId);

            if (savedInstanceState == null) {
                videoAd.setAd(ad);
                videoAd.setParentalGateListener(parentalGateListener);
                videoAd.setAdListener(adListener);
                videoAd.setVideoAdListener(videoAdListener);
                videoAd.setIsParentalGateEnabled(isParentalGateEnabled);
                videoAd.setShouldShowCloseButton(shouldShowCloseButton);
                videoAd.setInternalAdListener(new SAAdListener() {
                    @Override
                    public void adWasShown(int placementId) {}
                    @Override
                    public void adWasClosed(int placementId) {
                        close();
                    }
                    @Override
                    public void adWasClicked(int placementId) {}
                    @Override
                    public void adHasIncorrectPlacement(int placementId) {
                        close();
                    }
                    @Override
                    public void adFailedToShow(int placementId) {
                        close();
                    }
                });
                videoAd.setInternalVideoAdListener(new SAVideoAdListener() {
                    @Override
                    public void videoStarted(int placementId) {}
                    @Override
                    public void videoReachedFirstQuartile(int placementId) {}
                    @Override
                    public void videoReachedMidpoint(int placementId) {}
                    @Override
                    public void videoReachedThirdQuartile(int placementId) {}
                    @Override
                    public void videoEnded(int placementId) {}
                    @Override
                    public void adEnded(int placementId) {}
                    @Override
                    public void adStarted(int placementId) {
                        isOKToClose = true;
                    }
                    @Override
                    public void allAdsEnded(int placementId) {
                        if (shouldAutomaticallyCloseAtEnd) {
                            close();
                        }
                    }
                });
                videoAd.play();
            }
        }

        @Override
        public void onBackPressed() {

        }

        /** public close function */
        public void closeVideo(View v){
            if (isOKToClose) {
                close();
            }
        }

        public void close() {
            /** close listener */
            if (adListener != null){
                adListener.adWasClosed(ad.placementId);
            }
            videoAd.close();

            /**
             * call super.onBackPressed() to close the activity because it's own onBackPressed()
             * method is overridden to do nothing e.g. so as not to be closed by the user
             */
            super.finish();

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    /**
     * Private class that hold info to share between activities
     */
    private static class AdDataHolder {
        public SAAd _refAd;
        public boolean _refIsParentalGateEnabled = true;
        public boolean _refShouldShowCloseButton = true;
        public boolean _refShouldAutomaticallyCloseAtEnd = true;
        public boolean _refShouldLockOrientation = false;
        public int _refLockOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        public SAAdListener _refAdListener;
        public SAParentalGateListener _refParentalGateListener;
        public SAVideoAdListener _refVideoAdListener;

        /** set and get methods on the Ad Data Holder class */
        private static final AdDataHolder holder = new AdDataHolder();
        public static AdDataHolder getInstance() {return holder;}
    }
}
