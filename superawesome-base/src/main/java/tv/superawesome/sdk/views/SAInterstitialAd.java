/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.sdk.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;

import tv.superawesome.lib.saadloader.SALoader;
import tv.superawesome.lib.saadloader.SALoaderInterface;
import tv.superawesome.lib.sajsonparser.SAJsonParser;
import tv.superawesome.lib.samodelspace.saad.SAAd;
import tv.superawesome.lib.samodelspace.saad.SACreativeFormat;
import tv.superawesome.lib.samodelspace.saad.SAResponse;
import tv.superawesome.lib.sasession.SAConfiguration;
import tv.superawesome.lib.sasession.SASession;
import tv.superawesome.lib.sasession.SASessionInterface;
import tv.superawesome.lib.sautils.SAImageUtils;
import tv.superawesome.lib.sautils.SAUtils;
import tv.superawesome.sdk.SuperAwesome;

/**
 * Class that abstracts away the process of loading & displaying an
 * interstitial / fullscreen type Ad.
 * A subclass of the Android "Activity" class.
 */
public class SAInterstitialAd extends Activity {

    // subviews
    private RelativeLayout          parent = null;
    private SABannerAd              interstitialBanner = null;
    private ImageButton             closeButton = null;

    // the ad
    private SAAd ad = null;

    // static session
    private static SASession session = null;

    // fully private variables
    private static HashMap<Integer, Object> ads = new HashMap<>();

    // private vars w/ exposed setters & getters (state vars)
    private static SAInterface      listener = new SAInterface() { @Override public void onEvent(int placementId, SAEvent event) {} };

    private static boolean          isParentalGateEnabled = SuperAwesome.getInstance().defaultParentalGate();
    private static boolean          isTestingEnabled = SuperAwesome.getInstance().defaultTestMode();
    private static boolean          isBackButtonEnabled = SuperAwesome.getInstance().defaultBackButton();
    private static SAOrientation    orientation = SuperAwesome.getInstance().defaultOrientation();
    private static SAConfiguration  configuration = SuperAwesome.getInstance().defaultConfiguration();
    private static boolean          isMoatLimitingEnabled = SuperAwesome.getInstance().defaultMoatLimitingState();

    /**********************************************************************************************
     * Activity initialization & instance methods
     **********************************************************************************************/

    /**
     * Overridden "onCreate" method, part of the Activity standard set of methods.
     * Here is the part where the activity / interstitial ad gets configured
     *
     * @param savedInstanceState previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call super
        super.onCreate(savedInstanceState);

        // local vars
        boolean isParentalGateEnabledL = getIsParentalGateEnabled();
        SAOrientation orientationL = getOrientation();
        SAInterface listenerL = getListener();
        boolean isMoatLimitingEnabledL = getMoatLimitingState();
        Bundle bundle = getIntent().getExtras();
        String adStr = bundle.getString("ad");
        ad = new SAAd(SAJsonParser.newObject(adStr));

        // make sure direction is locked
        switch (orientationL) {
            case ANY:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;
            case PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }

        // create the parent relative layout
        parent = new RelativeLayout(this);
        parent.setId(SAUtils.randomNumberBetween(1000000, 1500000));
        parent.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        // create the interstitial banner
        interstitialBanner = new SABannerAd(this);
        interstitialBanner.setId(SAUtils.randomNumberBetween(1000000, 1500000));
        interstitialBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        interstitialBanner.setColor(false);
        interstitialBanner.setAd(ad);
        interstitialBanner.setTestMode(isTestingEnabled);
        interstitialBanner.setConfiguration(configuration);
        interstitialBanner.setListener(listenerL);
        interstitialBanner.setParentalGate(isParentalGateEnabledL);
        if (!isMoatLimitingEnabledL) {
            interstitialBanner.disableMoatLimiting();
        }

        // create the close button
        float fp = SAUtils.getScaleFactor(this);
        closeButton = new ImageButton(this);
        closeButton.setImageBitmap(SAImageUtils.createCloseButtonBitmap());
        closeButton.setBackgroundColor(Color.TRANSPARENT);
        closeButton.setPadding(0, 0, 0, 0);
        closeButton.setScaleType(ImageView.ScaleType.FIT_XY);
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

        // set the view hierarchy
        parent.addView(interstitialBanner);
        parent.addView(closeButton);
        setContentView(parent);

        // finally play!
        interstitialBanner.play(this);
    }

    /**
     * Overridden "onBackPressed" method of the activity
     * Depending on how the ad is customised, this will lock the back button or it will allow it.
     * If it allows it, it's going to also send an "adClosed" event back to the SDK user
     */
    @Override
    public void onBackPressed () {
        boolean isBackButtonEnabledL = getIsBackButtonEnabled();
        if (isBackButtonEnabledL) {
            SAInterface listenerL = getListener();
            listenerL.onEvent(ad.placementId, SAEvent.adClosed);
            super.onBackPressed();
        }
    }

    /**
     * Method that closes the interstitial ad
     */
    private void close () {
        // close the banner as well
        interstitialBanner.close();
        interstitialBanner.setAd(null);

        // remove the ad from the "ads" hash map once it's been played
        ads.remove(ad.placementId);

        // close & resume previous activity
        super.onBackPressed();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /**********************************************************************************************
     * Class public interface - static methods to interact with an Interstitial Ad
     **********************************************************************************************/

    /**
     * Static method that loads an ad into the interstitial queue.
     * Ads can only be loaded once and then can be reloaded after they've been played.
     *
     * @param placementId   the Ad placement id to load data for
     * @param context       the current context
     */
    public static void load(final int placementId, Context context) {

        // if the ad data for the placement id doesn't existing in the "ads" hash map, then
        // proceed with loading it
        if (!ads.containsKey(placementId)) {

            // set a placeholder
            ads.put(placementId, new Object());

            // create the loader
            final SALoader loader = new SALoader(context);

            // create a current session
            session = new SASession (context);
            session.setTestMode(isTestingEnabled);
            session.setConfiguration(configuration);
            session.setVersion(SuperAwesome.getInstance().getSDKVersion());
            session.prepareSession(new SASessionInterface() {
                @Override
                public void didFindSessionReady() {

                    // after session is prepared, start loading
                    loader.loadAd(placementId, session, new SALoaderInterface() {
                        @Override
                        public void saDidLoadAd(SAResponse response) {

                            // put the correct value
                            if (response.isValid()) {
                                ads.put(placementId, response.ads.get(0));
                            }
                            // remove existing
                            else {
                                ads.remove(placementId);
                            }

                            // call listener
                            listener.onEvent(placementId, response.isValid () ? SAEvent.adLoaded : SAEvent.adFailedToLoad);
                        }
                    });
                }
            });

        }
        // else if the ad data for the placement exists in the "ads" hash map, then notify the
        // user that it already exists and he should just play it
        else {
            listener.onEvent(placementId, SAEvent.adAlreadyLoaded);
        }
    }

    /**
     * Static method that returns whether ad data for a certain placement has already been loaded
     *
     * @param placementId   the Ad placement id to check for
     * @return              true or false
     */
    public static boolean hasAdAvailable(int placementId) {
        Object object = ads.get(placementId);
        return object != null && object instanceof SAAd;
    }

    /**
     * Static method that, if an ad data is loaded, will play the content for the user
     *
     * @param placementId   the Ad placement id to play an ad for
     * @param context       the current context (activity or fragment)
     */
    public static void play(int placementId, Context context) {

        // get the generic Object
        Object generic = ads.get(placementId);

        // if notnull & instance of SAAd
        if (generic != null && generic instanceof SAAd) {

            // try to get the ad that fits the placement id
            SAAd adL = (SAAd) generic;

            // try to start the activity
            if (adL.creative.format != SACreativeFormat.video && context != null) {
                Intent intent = new Intent(context, SAInterstitialAd.class);
                intent.putExtra("ad", adL.writeToJson().toString());
                context.startActivity(intent);
            } else {
                listener.onEvent(placementId, SAEvent.adFailedToShow);
            }
        }
        else {
            listener.onEvent(placementId, SAEvent.adFailedToShow);
        }
    }

    /**
     * Method used for testing purposes (and the AwesomeApp) to manually put an ad in the
     * interstitial ads map
     *
     * @param ad an instance of SAAd
     */
    public static void setAd (SAAd ad) {
        if (ad != null && ad.isValid()) {
            ads.put(ad.placementId, ad);
        }
    }

    /**********************************************************************************************
     * Setters and getters
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

    public static void enableTestMode () {
        setTestMode(true);
    }

    public static void disableTestMode () {
        setTestMode(false);
    }

    public static void enableBackButton () {
        setBackButton(true);
    }

    public static void disableBackButton () {
        setBackButton(false);
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

    private static SAInterface getListener () {
        return listener;
    }

    private static boolean getIsParentalGateEnabled () {
        return isParentalGateEnabled;
    }

    private static SAOrientation getOrientation () {
        return orientation;
    }

    private static boolean getIsBackButtonEnabled () {
        return isBackButtonEnabled;
    }

    public static void setParentalGate (boolean value) {
        isParentalGateEnabled = value;
    }

    public static void setTestMode (boolean value) {
        isTestingEnabled = value;
    }

    public static void setBackButton (boolean value) {
        isBackButtonEnabled = value;
    }

    public static void setConfiguration (SAConfiguration value) {
        configuration = value;
    }

    public static void setOrientation (SAOrientation value) {
        orientation = value;
    }

    public static void disableMoatLimiting () {
        isMoatLimitingEnabled = false;
    }

    private static boolean getMoatLimitingState () { return isMoatLimitingEnabled; }
}