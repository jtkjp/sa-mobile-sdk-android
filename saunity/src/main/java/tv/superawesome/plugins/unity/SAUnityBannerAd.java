package tv.superawesome.plugins.unity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.HashMap;

import tv.superawesome.lib.sasession.SAConfiguration;
import tv.superawesome.lib.sautils.SAUtils;
import tv.superawesome.sdk.views.SABannerAd;
import tv.superawesome.sdk.views.SAEvent;
import tv.superawesome.sdk.views.SAInterface;

/**
 * Created by gabriel.coman on 05/01/2017.
 */

public class SAUnityBannerAd {

    private static HashMap<String, SABannerAd> bannerAdHashMap = new HashMap<>();

    public static void SuperAwesomeUnitySABannerAdCreate (Context context, final String unityName) {

        SABannerAd bannerAd = new SABannerAd(context);

        bannerAd.setListener(new SAInterface() {
            @Override
            public void onEvent(int placementId, SAEvent event) {
                switch (event) {
                    case adLoaded: SAUnityCallback.sendToUnity(unityName, placementId, "adLoaded"); break;
                    case adFailedToLoad: SAUnityCallback.sendToUnity(unityName, placementId, "adFailedToLoad"); break;
                    case adShown: SAUnityCallback.sendToUnity(unityName, placementId, "adShown"); break;
                    case adFailedToShow: SAUnityCallback.sendToUnity(unityName, placementId, "adFailedToShow"); break;
                    case adClicked: SAUnityCallback.sendToUnity(unityName, placementId, "adClicked"); break;
                    case adClosed: SAUnityCallback.sendToUnity(unityName, placementId, "adClosed"); break;
                }
            }
        });

        bannerAdHashMap.put(unityName, bannerAd);
    }

    public static void SuperAwesomeUnitySABannerAdLoad(Context context, String unityName, int placementId, int configuration, boolean test) {
        if (bannerAdHashMap.containsKey(unityName)) {
            SABannerAd bannerAd = bannerAdHashMap.get(unityName);
            bannerAd.setConfiguration(SAConfiguration.fromValue(configuration));
            bannerAd.setTestMode(test);
            bannerAd.load(placementId);
        }
    }

    public static boolean SuperAwesomeUnitySABannerAdHasAdAvailable (Context context, String unityName) {
        if (bannerAdHashMap.containsKey(unityName)) {
            SABannerAd bannerAd = bannerAdHashMap.get(unityName);
            return bannerAd.hasAdAvailable();
        }
        return false;
    }

    public static void SuperAwesomeUnitySABannerAdPlay (Context context, String unityName, boolean isParentalGateEnabled, int position, int width, int height, boolean color) {

        if (bannerAdHashMap.containsKey(unityName)) {

            // get activity
            Activity activity = (Activity) context;

            // get banner ad
            final SABannerAd bannerAd = bannerAdHashMap.get(unityName);
            bannerAd.setParentalGate(isParentalGateEnabled);
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

            bannerAd.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, scaledHeight));

            // create a relative layout as big as the screen
            RelativeLayout screenLayout = new RelativeLayout(context);
            screenLayout.setBackgroundColor(Color.TRANSPARENT);
            screenLayout.setGravity(position == 0 ? Gravity.TOP : Gravity.BOTTOM);
            screenLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            // form hierarchy
            ViewGroup current = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            screenLayout.addView(bannerAd);
            current.addView(screenLayout);

            // finally play banner ad
            bannerAd.play(context);
        }
    }

    public static void SuperAwesomeUnitySABannerAdClose (Context context, String unityName) {
        if (bannerAdHashMap.containsKey(unityName)) {
            SABannerAd bannerAd = bannerAdHashMap.get(unityName);
            bannerAd.close();
            ViewGroup parent = (ViewGroup) bannerAd.getParent();
            if (parent != null) parent.removeView(bannerAd);
            bannerAdHashMap.remove(unityName);
        }
    }
}