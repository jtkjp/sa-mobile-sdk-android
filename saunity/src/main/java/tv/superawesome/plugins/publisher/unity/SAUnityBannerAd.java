/**
 * @Copyright:   SADefaults Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.plugins.publisher.unity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.HashMap;

import tv.superawesome.lib.sasession.defines.SAConfiguration;
import tv.superawesome.lib.sautils.SAUtils;
import tv.superawesome.sdk.publisher.SABannerAd;
import tv.superawesome.sdk.publisher.SAEvent;
import tv.superawesome.sdk.publisher.SAInterface;

/**
 * Class that holds a number of static methods used to communicate with Unity
 */
public class SAUnityBannerAd {

    // hash map containing banner ads
//    private static HashMap<String, SABannerSupport> supportHashMap = new HashMap<>();
    private static HashMap<String, SABannerAd> bannerAdHashMap = new HashMap<>();

    /**
     * Method that creates a new banner ad (from Unity)
     */
    public static void SuperAwesomeUnitySABannerAdCreate (Context context, final String unityName) {

        // create the banner
        SABannerAd bannerAd = new SABannerAd(context);
        bannerAd.setId(SAUtils.randomNumberBetween(1000000, 1500000));

        bannerAd.setListener(new SAInterface() {
            @Override
            public void onEvent(int placementId, SAEvent event) {
                switch (event) {
                    case adLoaded: SAUnityCallback.sendAdCallback(unityName, placementId, SAEvent.adLoaded.toString()); break;
                    case adEmpty: SAUnityCallback.sendAdCallback(unityName, placementId, SAEvent.adEmpty.toString()); break;
                    case adFailedToLoad: SAUnityCallback.sendAdCallback(unityName, placementId, SAEvent.adFailedToLoad.toString()); break;
                    case adAlreadyLoaded: SAUnityCallback.sendAdCallback(unityName, placementId, SAEvent.adAlreadyLoaded.toString()); break;
                    case adShown: SAUnityCallback.sendAdCallback(unityName, placementId, SAEvent.adShown.toString()); break;
                    case adFailedToShow: SAUnityCallback.sendAdCallback(unityName, placementId, SAEvent.adFailedToShow.toString()); break;
                    case adClicked: SAUnityCallback.sendAdCallback(unityName, placementId, SAEvent.adClicked.toString()); break;
                    case adEnded: SAUnityCallback.sendAdCallback(unityName, placementId, SAEvent.adEnded.toString()); break;
                    case adClosed: SAUnityCallback.sendAdCallback(unityName, placementId, SAEvent.adClosed.toString()); break;
                }
            }
        });

        bannerAdHashMap.put(unityName, bannerAd);

        // create the dialog
//        SABannerSupport dialog = new SABannerSupport(context);
//        supportHashMap.put(unityName, dialog);
    }

    /**
     * Method that loads a new Banner Ad (from Unity)
     */
    public static void SuperAwesomeUnitySABannerAdLoad(Context context, String unityName, int placementId, int configuration, boolean test) {
        if (bannerAdHashMap.containsKey(unityName)) {
            SABannerAd bannerAd = bannerAdHashMap.get(unityName);
            bannerAd.setConfiguration(SAConfiguration.fromValue(configuration));
            bannerAd.setTestMode(test);
            bannerAd.load(placementId);
        }
    }

    /**
     * Method that checks to see if an ad is available for a banner ad (from Unity)
     */
    public static boolean SuperAwesomeUnitySABannerAdHasAdAvailable (Context context, String unityName) {
        if (bannerAdHashMap.containsKey(unityName)) {
            SABannerAd bannerAd = bannerAdHashMap.get(unityName);
            return bannerAd.hasAdAvailable();
        }
        return false;
    }

    /**
     * Method that plays a new Banner ad (from Unity)
     */
    public static void SuperAwesomeUnitySABannerAdPlay (Context context, String unityName, boolean isParentalGateEnabled, boolean isBumperPageEnabled, int position, int width, int height, boolean color) {

        if (bannerAdHashMap.containsKey(unityName) && !bannerAdHashMap.get(unityName).isClosed()) {

            // get activity
            Activity activity = (Activity) context;

            // get banner ad
            final SABannerAd bannerAd = bannerAdHashMap.get(unityName);
            bannerAd.setParentalGate(isParentalGateEnabled);
            bannerAd.setBumperPage(isBumperPageEnabled);
            bannerAd.setColor(color);

            // get screen size
            SAUtils.SASize screenSize = SAUtils.getRealScreenSize(activity, false);

            // get scale factor
            float factor = SAUtils.getScaleFactor(activity);

            // scale it according to the factor
            int scaledWidth = (int)(factor * width);
            int scaledHeight = (int)(factor * height);

            // make sure it's not bigger than the screen
            if (scaledWidth > screenSize.width) {
                scaledHeight = (screenSize.width * scaledHeight) / scaledWidth;
            }

            // but not bigger than 15% of the screen's height
            if (scaledHeight > 0.15 * screenSize.height) {
                scaledHeight = (int)(0.15 * screenSize.height);
            }

            FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(screenSize.width, scaledHeight);
            layout.gravity = position == 0 ? Gravity.TOP : Gravity.BOTTOM;

            try {
                activity.addContentView(bannerAd, layout);
                bannerAd.play(context);
            } catch (Exception e) {
                Log.e("SuperAwesome", "Failed to add banner to Unity activity! " + e.getMessage());
            }

            // !!OLD CODE!!
//            SABannerSupport support = supportHashMap.get(unityName);
//            support.setGravity(position == 0 ? Gravity.TOP : Gravity.BOTTOM);
//            support.setFeatures();
//
//            support.setContentView(bannerAd, layout);
//            support.setCanceledOnTouchOutside(false);
//            support.show();
//
//            // finally play banner ad
//            bannerAd.play(context);
        }
    }

    /**
     * Method that closes a banner ad (from Unity)
     */
    public static void SuperAwesomeUnitySABannerAdClose (Context context, String unityName) {
        if (bannerAdHashMap.containsKey(unityName)) {
            // close the banner
            SABannerAd bannerAd = bannerAdHashMap.get(unityName);
            bannerAd.close();
            bannerAd.setVisibility(View.GONE);
            // !!OLD CODE!!
            // close the dialog
//            Dialog dialog = supportHashMap.get(unityName);
//            dialog.hide();
        }
    }
}

//class SABannerSupport extends Dialog {
//
//    SABannerSupport(Context context) {
//        super(context);
//    }
//
//    void setGravity(int gravity) {
//        Window window = getWindow();
//        if (window != null) {
//            window.setGravity(gravity);
//        }
//    }
//
//    void  setFeatures () {
//        Window window = getWindow();
//
//        if (window != null) {
//            // setup window & dialog support
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
////            window.requestFeature(Window.FEATURE_NO_TITLE);
////            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
////
////            // Set the dialog to not focusable.
////            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
////                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////
////                window.getDecorView().setSystemUiVisibility(
////                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
////                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
////                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
////                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
////                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
////            } else {
////                window.getDecorView().setSystemUiVisibility(
////                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
////                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
////                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
////                                | View.SYSTEM_UI_FLAG_FULLSCREEN);
////            }
//        }
//    }
//
//    /**
//     * An hack used to show the dialogs in Immersive Mode (that is with the NavBar hidden). To
//     * obtain this, the method makes the dialog not focusable before showing it, change the UI
//     * visibility of the window like the owner activity of the dialog and then (after showing it)
//     * makes the dialog focusable again.
//     */
//    @Override
//    public void show() {
//        // Show the dialog with NavBar hidden.
//        super.show();
//
//        // Set the dialog to focusable again.
////        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//    }
//}
