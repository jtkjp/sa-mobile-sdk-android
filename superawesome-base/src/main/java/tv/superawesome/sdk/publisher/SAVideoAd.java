/**
 * @Copyright:   SADefaults Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.sdk.publisher;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;

import tv.superawesome.lib.saadloader.SALoader;
import tv.superawesome.lib.saadloader.SALoaderInterface;
import tv.superawesome.lib.sabumperpage.SABumperPage;
import tv.superawesome.lib.saevents.SAEvents;
import tv.superawesome.lib.sajsonparser.SAJsonParser;
import tv.superawesome.lib.samodelspace.saad.SAAd;
import tv.superawesome.lib.samodelspace.saad.SACampaignType;
import tv.superawesome.lib.samodelspace.saad.SACreativeFormat;
import tv.superawesome.lib.samodelspace.saad.SAResponse;
import tv.superawesome.lib.saparentalgate.SAParentalGate;
import tv.superawesome.lib.sasession.defines.SAConfiguration;
import tv.superawesome.lib.sasession.defines.SARTBInstl;
import tv.superawesome.lib.sasession.defines.SARTBPlaybackMethod;
import tv.superawesome.lib.sasession.defines.SARTBPosition;
import tv.superawesome.lib.sasession.defines.SARTBSkip;
import tv.superawesome.lib.sasession.defines.SARTBStartDelay;
import tv.superawesome.lib.sasession.session.SASession;
import tv.superawesome.lib.sasession.session.SASessionInterface;
import tv.superawesome.lib.sautils.SAImageUtils;
import tv.superawesome.lib.sautils.SAUtils;
import tv.superawesome.lib.savideoplayer.SAVideoPlayer;
import tv.superawesome.lib.savideoplayer.SAVideoPlayerClickInterface;
import tv.superawesome.lib.savideoplayer.SAVideoPlayerEvent;
import tv.superawesome.lib.savideoplayer.SAVideoPlayerEventInterface;

import static java.security.AccessController.getContext;

/**
 * Class that abstracts away the process of loading & displaying a video type Ad.
 * A subclass of the Android "Activity" class.
 */
public class SAVideoAd extends Activity {

    // the ad
    private SAAd ad = null;

    // the internal loader
    private static SASession session = null;
    private SAEvents events = null;

    private RelativeLayout parent = null;
    private ImageButton padlock = null;
    private ImageButton closeButton = null;
    private SAVideoPlayer videoPlayer = null;

    private Long currentClickThreshold = 0L;

    private static final String videoTag = "SAVideoTag";

    // private vars w/ a public interface
    private static HashMap<Integer, Object> ads = new HashMap<>();
    private static SAInterface listener = new SAInterface() { @Override public void onEvent(int placementId, SAEvent event) {} };

    private static boolean isParentalGateEnabled            = SADefaults.defaultParentalGate();
    private static boolean isBumperPageEnabled              = SADefaults.defaultBumperPage();
    private static boolean shouldShowCloseButton            = SADefaults.defaultCloseButton();
    private static boolean shouldAutomaticallyCloseAtEnd    = SADefaults.defaultCloseAtEnd();
    private static boolean shouldShowSmallClickButton       = SADefaults.defaultSmallClick();
    private static boolean isTestingEnabled                 = SADefaults.defaultTestMode();
    private static boolean isBackButtonEnabled              = SADefaults.defaultBackButton();
    private static SAOrientation orientation                = SADefaults.defaultOrientation();
    private static SAConfiguration configuration            = SADefaults.defaultConfiguration();
    private static SARTBStartDelay playback                 = SADefaults.defaultPlaybackMode();
    private static boolean isMoatLimitingEnabled            = SADefaults.defaultMoatLimitingState();

    /**********************************************************************************************
     * Activity initialization & instance methods
     **********************************************************************************************/

    /**
     * Overridden "onCreate" method, part of the Activity standard set of methods.
     * Here is the part where the activity / video ad gets configured
     *
     * @param savedInstanceState previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // local versions of the static vars
        final SAInterface listenerL = getListener();
        final boolean isParentalGateEnabledL = getIsParentalGateEnabled();
        final boolean shouldShowCloseButtonL = getShouldShowCloseButton();

        final boolean shouldAutomaticallyCloseAtEndL = getShouldAutomaticallyCloseAtEnd();
        final boolean shouldShowSmallClickButtonL = getShouldShowSmallClickButton();
        final SAOrientation orientationL = getOrientation();
        final boolean isMoatLimitingEnabledL = getMoatLimitingState();
        Bundle bundle = getIntent().getExtras();
        String adString = bundle.getString("ad");
        ad = new SAAd(SAJsonParser.newObject(adString));

        // make sure direction is locked
        switch (orientationL) {
            case ANY: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); break;
            case PORTRAIT: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); break;
            case LANDSCAPE: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); break;
        }

        // start events
        events = new SAEvents();
        events.setAd(this, session, ad);
        if (!isMoatLimitingEnabledL) {
            events.disableMoatLimiting();
        }

        // create main content for activity
        parent = new RelativeLayout(this);
        parent.setId(SAUtils.randomNumberBetween(1000000, 1500000));
        parent.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(parent);

        // create the padlock
        padlock = new ImageButton(this);
        padlock.setImageBitmap(SAImageUtils.createPadlockBitmap());
        padlock.setPadding(0, 0, 0, 0);
        padlock.setBackgroundColor(Color.TRANSPARENT);
        padlock.setScaleType(ImageView.ScaleType.FIT_XY);
        float sf = SAUtils.getScaleFactor(this);
        padlock.setLayoutParams(new ViewGroup.LayoutParams((int) (83 * sf), (int) (31 * sf)));
        padlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SAParentalGate.setListener(new SAParentalGate.Interface() {
                    @Override
                    public void parentalGateOpen() {}

                    @Override
                    public void parentalGateSuccess() {
                        SABumperPage.setListener(new SABumperPage.Interface() {
                            @Override
                            public void didEndBumper() {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ads.superawesome.tv/v2/safead"));
                                SAVideoAd.this.startActivity(browserIntent);
                            }
                        });
                        SABumperPage.play(SAVideoAd.this);
                    }

                    @Override
                    public void parentalGateFailure() {}

                    @Override
                    public void parentalGateCancel() {}
                });
                SAParentalGate.show(SAVideoAd.this);
            }
        });

        // create the close button
        closeButton = new ImageButton(this);
        closeButton.setImageBitmap(SAImageUtils.createCloseButtonBitmap());
        closeButton.setPadding(0, 0, 0, 0);
        closeButton.setBackgroundColor(Color.TRANSPARENT);
        closeButton.setScaleType(ImageView.ScaleType.FIT_XY);
        float fp = SAUtils.getScaleFactor(this);
        RelativeLayout.LayoutParams buttonLayout = new RelativeLayout.LayoutParams((int) (30 * fp), (int) (30* fp));
        buttonLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        buttonLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closeButton.setLayoutParams(buttonLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        // create the main video player
        FragmentManager manager = getFragmentManager();
        if (manager.findFragmentByTag(videoTag) == null) {

            videoPlayer = new SAVideoPlayer();
            videoPlayer.setShouldShowSmallClickButton(shouldShowSmallClickButtonL);

            videoPlayer.setEventListener(new SAVideoPlayerEventInterface() {
                @Override
                public void saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent saVideoPlayerEvent, int time, int duration) {
                    switch (saVideoPlayerEvent) {

                        case Video_Prepared: {

                            try {
                                videoPlayer.play(ad.creative.details.media.path);
                                Log.d("AwesomeAds", "PLAYING " + ad.creative.details.media.path);
                            } catch (Throwable throwable) {
                                // do nothing
                            }

                            break;
                        }
                        case Video_Start: {

                            boolean result = events.startMoatTrackingForVideoPlayer(videoPlayer.getVideoPlayer(), duration);

                            Log.d("SADefaults", "Moat Video start: " + result);

                            // add padlock
                            padlock.setVisibility(ad.isPadlockVisible ? View.VISIBLE : View.GONE);
                            parent.addView(padlock);

                            // add close button
                            closeButton.setVisibility(shouldShowCloseButtonL ? View.VISIBLE : View.GONE);
                            parent.addView(closeButton);

                            // send callback
                            if (listenerL != null) {
                                listenerL.onEvent(ad.placementId, SAEvent.adShown);
                            } else {
                                Log.w("AwesomeAds", "Video Ad listener not implemented. Should have been adShown");
                            }

                            // send vast events - including impression
                            events.triggerVASTImpressionEvent();
                            events.triggerVASTStartEvent();
                            events.triggerVASTCreativeViewEvent();

                            // moat
                            events.sendMoatPlayingEvent(time);
                            events.sendMoatStartEvent(time);

                            break;
                        }
                        case Video_2s: {
                            boolean isViewable = events.isChildInRect(videoPlayer.getVideoHolder());
                            Log.d("AwesomeAds", "Is viewable is " + isViewable);
                            if (isViewable) {
                                events.triggerViewableImpressionEvent();
                            }
                            break;
                        }
                        case Video_1_4: {
                            events.sendMoatFirstQuartileEvent(time);
                            events.triggerVASTFirstQuartileEvent();
                            break;
                        }
                        case Video_1_2: {
                            events.sendMoatMidpointEvent(time);
                            events.triggerVASTMidpointEvent();
                            break;
                        }
                        case Video_3_4: {
                            events.sendMoatThirdQuartileEvent(time);
                            events.triggerVASTThirdQuartileEvent();
                            break;
                        }
                        case Video_End: {

                            // send events
                            events.sendMoatCompleteEvent(duration);
                            events.triggerVASTCompleteEvent();

                            // send an ad ended event
                            if (listenerL != null) {
                                listenerL.onEvent(ad.placementId, SAEvent.adEnded);
                            } else {
                                Log.w("AwesomeAds", "Video Ad listener not implemented. Should have been adEnded");
                            }

                            // make btn visible
                            closeButton.setVisibility(View.VISIBLE);

                            // auto close
                            if (shouldAutomaticallyCloseAtEndL) {
                                close();
                            }

                            break;
                        }
                        case Video_15s:{
                            // do nothing
                            break;
                        }
                        case Video_Error: {

                            // send events
                            events.stopMoatTrackingForVideoPlayer();
                            events.triggerVASTErrorEvent();

                            // ad failed to show
                            if (listenerL != null) {
                                listenerL.onEvent(ad.placementId, SAEvent.adFailedToShow);
                            } else {
                                Log.w("AwesomeAds", "Video Ad listener not implemented. Should have been adFailedToShow");
                            }

                            // close this whole
                            close();

                            break;
                        }
                    }
                }
            });

            videoPlayer.setClickListener(new SAVideoPlayerClickInterface() {
                @Override
                public void onClick(View v) {

                    // check to see if there is a click through url
                    final String destinationUrl;

                    // if the campaign is a CPI one, get the normal CPI url so that
                    // we can append the "referrer data" to it (since most likely
                    // "click_through" will have a redirect)
                    if (ad.campaignType == SACampaignType.CPI) {
                        destinationUrl = ad.creative.clickUrl;
                    } else {
                        destinationUrl = events.getVASTClickThroughEvent();
                    }

                    if (destinationUrl != null) {
                        // check for parental gate on click
                        if (isParentalGateEnabledL) {
                            SAParentalGate.setListener(new SAParentalGate.Interface() {
                                @Override
                                public void parentalGateOpen() {
                                    SAVideoAd.this.pause();
                                    events.triggerPgOpenEvent();
                                }

                                @Override
                                public void parentalGateSuccess() {
                                    events.triggerPgSuccessEvent();
                                    click(destinationUrl);
                                    SAVideoAd.this.pause();
                                }

                                @Override
                                public void parentalGateFailure() {
                                    events.triggerPgFailEvent();
                                    SAVideoAd.this.resume();
                                }

                                @Override
                                public void parentalGateCancel() {
                                    events.triggerPgCloseEvent();
                                    SAVideoAd.this.resume();
                                }
                            });
                            SAParentalGate.show(SAVideoAd.this);
                        } else {
                            click(destinationUrl);
                        }
                    }
                }
            });

            // finally add the video player
            try {
                manager.beginTransaction()
                        .add(parent.getId(), videoPlayer, videoTag)
                        .commit();
            } catch (Exception e) {
                // do nothing
            }

        }
        else {
            videoPlayer = (SAVideoPlayer) manager.findFragmentByTag(videoTag);
        }
    }

    /**
     * Overridden "onBackPressed" method of the activity
     * Depending on how the ad is customised, this will lock the back button or it will allow it.
     * If it allows it, it's going to also send an "adClosed" event back to the SDK user
     */
    @Override
    public void onBackPressed() {
        boolean isBackButtonEnabledL = getIsBackButtonEnabled();
        if (isBackButtonEnabledL) {
            close();
            super.onBackPressed();
        }
    }

    /**********************************************************************************************
     * Custom instance methods
     **********************************************************************************************/

    /**
     * Method that handles a click on the ad surface
     */
    public void click(final String destination) {

        boolean isBumperPageEnabledL = getIsBumperPageEnabled();

        if (isBumperPageEnabledL || ad.creative.bumper) {
            SABumperPage.setListener(new SABumperPage.Interface() {
                @Override
                public void didEndBumper() {
                    handleUrl(destination);
                }
            });
            SABumperPage.play(this);
        } else {
            handleUrl(destination);
        }
    }

    private void handleUrl (String destination) {

        Long currentTime = System.currentTimeMillis()/1000;
        Long diff = Math.abs(currentTime - currentClickThreshold);

        if (diff < SADefaults.defaultClickThreshold()) {
            Log.d("AwesomeAds-2", "Current diff is " + diff);
            return;
        }

        currentClickThreshold = currentTime;

        Log.d("AwesomeAds-2", "Going to " + destination);

        // get local
        SAInterface listenerL = getListener();
        // call listener
        if (listenerL != null) {
            listenerL.onEvent(ad.placementId, SAEvent.adClicked);
        } else {
            Log.w("AwesomeAds", "Video Ad listener not implemented. Should have been adClicked");
        }

        // send vast click tracking events
        events.triggerVASTClickTrackingEvent();

        // send only in case of CPI where we'll use the direct click url
        if (ad.campaignType == SACampaignType.CPI) {
            events.triggerVASTClickThroughEvent();
        }

        // if it's a CPI campaign
        destination += ad.campaignType == SACampaignType.CPI ? ("&referrer=" + ad.creative.referral.writeToReferralQuery()) : "";

        // start browser
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(destination)));
        } catch (Exception e) {
            // do nothing
        }
    }

    /**
     * Method that handles what should happen when the video pauses
     */
    public void pause () {
        videoPlayer.pausePlayer();
    }

    /**
     * Method that handles what should happen when the video resumes
     */
    public void resume () {
        videoPlayer.resumePlayer();
    }

    /**
     * Method that closes the interstitial ad
     */
    private void close() {

        // get local
        SAInterface listenerL = getListener();

        // close moat
        events.stopMoatTrackingForVideoPlayer();

        // call listener
        if (listenerL != null) {
            listenerL.onEvent(ad.placementId, SAEvent.adClosed);
        } else {
            Log.w("AwesomeAds", "Video Ad listener not implemented. Should have been adClosed");
        }

        // close
        SAParentalGate.close();

        // delete the ad
        ads.remove(ad.placementId);

        // close the video player
        videoPlayer.close();

        // close
        this.finish();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /**********************************************************************************************
     * Public class interface
     **********************************************************************************************/

    /**
     * Static method that loads an ad into the video queue.
     * Ads can only be loaded once and then can be reloaded after they've been played.
     *
     * @param placementId   the Ad placement id to load data for
     * @param context       the current context
     */
    public static void load (final int placementId, Context context) {

        // very late init of the AwesomeAds SDK
        try {
            AwesomeAds.init(((Activity)context).getApplication(), false);
        } catch (Exception e) {
            Log.d("SuperAwesome", "Error initing AwesomeAds in SAVideoAd " + e.getMessage());
        }

        // if the ad data for the placement id doesn't existing in the "ads" hash map, then
        // proceed with loading it
        if (!ads.containsKey(placementId)) {

            // set a placeholder
            ads.put(placementId, new Object());

            // create a loader
            final SALoader loader = new SALoader(context);

            // create a current session
            session = new SASession (context);
            session.setTestMode(isTestingEnabled);
            session.setConfiguration(configuration);
            session.setVersion(SAVersion.getSDKVersion());
            session.setPos(SARTBPosition.FULLSCREEN);
            session.setPlaybackMethod(SARTBPlaybackMethod.WITH_SOUND_ON_SCREEN);
            session.setInstl(SARTBInstl.FULLSCREEN);
            session.setSkip(shouldShowCloseButton ? SARTBSkip.SKIP : SARTBSkip.NO_SKIP);
            session.setStartDelay(getPlaybackMode());

            try {
                SAUtils.SASize size = SAUtils.getRealScreenSize((Activity) context, false);
                session.setWidth(size.width);
                session.setHeight(size.height);
            } catch (Exception e) {
                // do nothing
            }

            session.prepareSession(new SASessionInterface() {
                @Override
                public void didFindSessionReady() {

                    // after session is OK - start loading
                    loader.loadAd(placementId, session, new SALoaderInterface() {
                        @Override
                        public void saDidLoadAd(SAResponse response) {

                            if (response.status != 200) {
                                //
                                // remove from here
                                ads.remove(placementId);

                                //
                                // send callback
                                if (listener != null) {
                                    listener.onEvent(placementId, SAEvent.adFailedToLoad);
                                } else {
                                    Log.w("AwesomeAds", "Video Ad listener not implemented. Event would have been adFailedToLoad");
                                }
                            }
                            else {
                                // find out the real valid
                                boolean isValid = response.isValid();
                                SAAd first = isValid ? response.ads.get(0) : null;
                                isValid = first != null && isValid && first.creative.details.media.isDownloaded;

                                // put the correct value
                                if (isValid) {
                                    ads.put(placementId, first);
                                }
                                // remove existing
                                else {
                                    ads.remove(placementId);
                                }

                                // call listener(s)
                                if (listener != null) {
                                    listener.onEvent(placementId, isValid ? SAEvent.adLoaded : SAEvent.adEmpty);
                                } else {
                                    Log.w("AwesomeAds", "Video Ad listener not implemented. Event would have been either adLoaded or adEmpty");
                                }
                            }
                        }
                    });
                }
            });

        }
        // else if the ad data for the placement exists in the "ads" hash map, then notify the
        // user that it already exists and he should just play it
        else {
            if (listener != null) {
                listener.onEvent(placementId, SAEvent.adAlreadyLoaded);
            } else {
                Log.w("AwesomeAds", "Video Ad listener not implemented. Event would have been adAlreadyLoaded");
            }
        }
    }

    /**
     * Static method that returns whether ad data for a certain placement has already been loaded
     *
     * @param placementId   the Ad placement id to check for
     * @return              true or false
     */
    public static boolean hasAdAvailable (int placementId) {
        Object object = ads.get(placementId);
        return object != null && object instanceof SAAd;
    }

    public static SAAd getAd (int placementId) {
        if (ads.containsKey(placementId)) {
            Object object = ads.get(placementId);
            if (object instanceof SAAd) {
                return (SAAd) object;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Static method that, if an ad data is loaded, will play the content for the user
     *
     * @param placementId   the Ad placement id to play an ad for
     * @param context       the current context (activity or fragment)
     */
    public static void play (int placementId, final Context context) {

        // get the generic Object
        Object generic = ads.get(placementId);

        // if notnull & instance of SAAd
        if (generic != null && generic instanceof SAAd) {

            // try to get the ad that fits the placement id
            SAAd adL = (SAAd) generic;

            // try to start the activity
            if (adL.creative.format == SACreativeFormat.video && context != null) {

                // create intent
                Intent intent = new Intent(context, SAVideoAd.class);
                intent.putExtra("ad", adL.writeToJson().toString());

                // clear ad - meaning that it's been played
                ads.remove(placementId);

                // start new activity
                context.startActivity(intent);

            } else {
                if (listener != null) {
                    listener.onEvent(placementId, SAEvent.adFailedToShow);
                } else {
                    Log.w("AwesomeAds", "Video Ad listener not implemented. Event would have been adFailedToShow");
                }
            }
        }
        else {
            if (listener != null) {
                listener.onEvent(placementId, SAEvent.adFailedToShow);
            } else {
                Log.w("AwesomeAds", "Video Ad listener not implemented. Event would have been adFailedToShow");
            }
        }
    }

    /**
     * Method used for testing purposes (and the AwesomeApp) to manually put an ad in the
     * video ads map
     *
     * @param ad an instance of SAAd
     */
    public static void setAd (SAAd ad) {
        if (ad != null && ad.isValid()) {
            ads.put(ad.placementId, ad);
        }
    }

    /**********************************************************************************************
     * Setters & Getters
     **********************************************************************************************/

    public static void setListener(SAInterface value) {
        listener = value != null ? value : listener;
    }

    public static void enableParentalGate () {
        setParentalGate(true);
    }

    public static void disableParentalGate () {
        setParentalGate(false);
    }

    public static void enableBumperPage () {
        setBumperPage(true);
    }

    public static void disableBumperPage () {
        setBumperPage(false);
    }

    public static void enableTestMode () {
        setTestMode(true);
    }

    public static void disableTestMode () {
        setTestMode(false);
    }

    public static void setConfigurationProduction () {
        setConfiguration(SAConfiguration.PRODUCTION);
    }

    public static void setConfigurationStaging () {
        setConfiguration(SAConfiguration.STAGING);
    }

    public static void setOrientationAny () {
        setOrientation(SAOrientation.ANY);
    }

    public static void setOrientationPortrait () {
        setOrientation(SAOrientation.PORTRAIT);
    }

    public static void setOrientationLandscape () {
        setOrientation(SAOrientation.LANDSCAPE);
    }

    public static void setPlaybackMode (SARTBStartDelay mode) {
        playback = mode;
    }

    public static void enableBackButton () {
        setBackButton(true);
    }

    public static void disableBackButton () {
        setBackButton(false);
    }

    public static void enableCloseButton () {
        setCloseButton(true);
    }

    public static void disableCloseButton () {
        setCloseButton(false);
    }

    public static void enableCloseAtEnd () {
        setCloseAtEnd(true);
    }

    public static void disableCloseAtEnd () {
        setCloseAtEnd(false);
    }

    public static void enableSmallClickButton () {
        setSmallClick(true);
    }

    public static void disableSmallClickButton () {
        setSmallClick(false);
    }

    private static SAInterface getListener () {
        return listener;
    }

    private static boolean getIsTestEnabled () {
        return isTestingEnabled;
    }

    private static boolean getIsParentalGateEnabled () {
        return isParentalGateEnabled;
    }

    private static boolean getIsBumperPageEnabled () {
        return isBumperPageEnabled;
    }

    private static boolean getShouldShowCloseButton () {
        return shouldShowCloseButton;
    }

    private static SARTBStartDelay getPlaybackMode () {
        return playback;
    }

    private static boolean getShouldAutomaticallyCloseAtEnd () {
        return shouldAutomaticallyCloseAtEnd;
    }

    private static boolean getMoatLimitingState () { return isMoatLimitingEnabled; }

    private static boolean getShouldShowSmallClickButton () {
        return shouldShowSmallClickButton;
    }

    private static SAOrientation getOrientation () {
        return orientation;
    }

    private static SAConfiguration getConfiguration () {
        return configuration;
    }

    private static boolean getIsBackButtonEnabled () {
        return isBackButtonEnabled;
    }

    public static void setParentalGate (boolean value) {
        isParentalGateEnabled = value;
    }

    public static void setBumperPage (boolean value) {
        isBumperPageEnabled = value;
    }

    public static void setTestMode (boolean value) {
        isTestingEnabled = value;
    }

    public static void setConfiguration (SAConfiguration value) {
        configuration = value;
    }

    public static void setOrientation (SAOrientation value) {
        orientation = value;
    }

    public static void setBackButton (boolean value) {
        isBackButtonEnabled = value;
    }

    public static void setCloseButton (boolean value) {
        shouldShowCloseButton = value;
    }

    public static void setCloseAtEnd (boolean value) {
        shouldAutomaticallyCloseAtEnd = value;
    }

    public static void setSmallClick (boolean value) {
        shouldShowSmallClickButton = value;
    }

    public static void disableMoatLimiting () {
        isMoatLimitingEnabled = false;
    }
}
