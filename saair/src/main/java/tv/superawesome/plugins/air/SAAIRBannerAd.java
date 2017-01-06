package tv.superawesome.plugins.air;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREInvalidObjectException;
import com.adobe.fre.FREObject;
import com.adobe.fre.FRETypeMismatchException;
import com.adobe.fre.FREWrongThreadException;

import java.util.HashMap;

import tv.superawesome.lib.sasession.SAConfiguration;
import tv.superawesome.lib.sautils.SAUtils;
import tv.superawesome.sdk.SuperAwesome;
import tv.superawesome.sdk.views.SABannerAd;
import tv.superawesome.sdk.views.SAEvent;
import tv.superawesome.sdk.views.SAInterface;

public class SAAIRBannerAd {

    private static HashMap<String, SABannerAd> bannerAdHashMap = new HashMap<>();

    public static class SuperAwesomeAIRSABannerAdCreate implements FREFunction {

        @Override
        public FREObject call(final FREContext freContext, FREObject[] freObjects) {

            Context context = freContext.getActivity();
            String airName = null;

            try {
                airName = freObjects[0].getAsString();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            final String airName2 = airName;

            if (airName2 != null) {
                SABannerAd bannerAd = new SABannerAd(context);
                bannerAd.setListener(new SAInterface() {
                    @Override
                    public void onEvent(int placementId, SAEvent event) {
                        switch (event) {
                            case adLoaded: SAAIRCallback.sendToAIR(freContext, airName2, placementId, "adLoaded"); break;
                            case adFailedToLoad: SAAIRCallback.sendToAIR(freContext, airName2, placementId, "adFailedToLoad"); break;
                            case adShown: SAAIRCallback.sendToAIR(freContext, airName2, placementId, "adShown"); break;
                            case adFailedToShow: SAAIRCallback.sendToAIR(freContext, airName2, placementId, "adFailedToShow"); break;
                            case adClicked: SAAIRCallback.sendToAIR(freContext, airName2, placementId, "adClicked"); break;
                            case adClosed: SAAIRCallback.sendToAIR(freContext, airName2, placementId, "adClosed"); break;
                        }
                    }
                });
                bannerAdHashMap.put(airName2, bannerAd);
            }

            return null;
        }
    }

    public static class SuperAwesomeAIRSABannerAdLoad implements FREFunction {
        @Override
        public FREObject call(FREContext freContext, FREObject[] freObjects) {

            String airName = null;
            int placementId = SuperAwesome.getInstance().defaultPlacementId();
            int configuration = SuperAwesome.getInstance().defaultConfiguration().ordinal();
            boolean test = SuperAwesome.getInstance().defaultTestMode();

            try {
                airName = freObjects[0].getAsString();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                placementId = freObjects[1].getAsInt();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                configuration = freObjects[2].getAsInt();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                test = freObjects[3].getAsBool();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            if (airName != null && bannerAdHashMap.containsKey(airName)) {
                SABannerAd bannerAd = bannerAdHashMap.get(airName);
                bannerAd.setConfiguration(SAConfiguration.fromValue(configuration));
                bannerAd.setTestMode(test);
                bannerAd.load(placementId);
            }

            return null;
        }
    }

    public static class SuperAwesomeAIRSABannerAdHasAdAvailable implements FREFunction {

        @Override
        public FREObject call(FREContext freContext, FREObject[] freObjects) {

            String airName = null;
            boolean hasAdAvailable = false;

            try {
                airName = freObjects[0].getAsString();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            if (airName != null && bannerAdHashMap.containsKey(airName)) {
                SABannerAd bannerAd = bannerAdHashMap.get(airName);
                hasAdAvailable = bannerAd.hasAdAvailable();
            }

            try {
                return FREObject.newObject(hasAdAvailable);
            } catch (FREWrongThreadException e) {
                return null;
            }
        }
    }

    public static class SuperAwesomeAIRSABannerAdPlay implements FREFunction {

        @Override
        public FREObject call(FREContext freContext, FREObject[] freObjects) {

            // default values
            Activity activity = freContext.getActivity();
            String airName = null;
            int position = 0;
            int width = 320;
            int height = 50;
            boolean isParentalGateEnabled = SuperAwesome.getInstance().defaultParentalGate();
            boolean color = SuperAwesome.getInstance().defaultBgColor();

            try {
                airName = freObjects[0].getAsString();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                isParentalGateEnabled = freObjects[1].getAsBool();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                position = freObjects[2].getAsInt();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                width = freObjects[3].getAsInt();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                height = freObjects[4].getAsInt();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                color = freObjects[5].getAsBool();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            if (airName != null && bannerAdHashMap.containsKey(airName)) {

                // get banner ad
                final SABannerAd bannerAd = bannerAdHashMap.get(airName);

                // customize
                bannerAd.setParentalGate(isParentalGateEnabled);
                bannerAd.setColor(color);

                // get real screen size
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
                RelativeLayout screenLayout = new RelativeLayout(activity);
                screenLayout.setBackgroundColor(Color.TRANSPARENT);
                screenLayout.setGravity(position == 0 ? Gravity.TOP : Gravity.BOTTOM);
                screenLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                // form hierarchy
                ViewGroup current = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
                screenLayout.addView(bannerAd);
                current.addView(screenLayout);

                // finally play banner ad
                bannerAd.play(activity);
            }

            return null;
        }
    }

    public static class SuperAwesomeAIRSABannerAdClose implements FREFunction {

        @Override
        public FREObject call(FREContext freContext, FREObject[] freObjects) {

            String airName = null;

            try {
                airName = freObjects[0].getAsString();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            if (airName != null && bannerAdHashMap.containsKey(airName)) {
                SABannerAd bannerAd = bannerAdHashMap.get(airName);
                bannerAd.close();
                ((ViewGroup)bannerAd.getParent()).removeView(bannerAd);
                bannerAdHashMap.remove(airName);
            }

            return null;
        }
    }
}