package tv.superawesome.plugins.air;

import android.content.Context;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREInvalidObjectException;
import com.adobe.fre.FREObject;
import com.adobe.fre.FRETypeMismatchException;
import com.adobe.fre.FREWrongThreadException;

import tv.superawesome.lib.sasession.SAConfiguration;
import tv.superawesome.sdk.SuperAwesome;
import tv.superawesome.sdk.views.SAEvent;
import tv.superawesome.sdk.views.SAInterface;
import tv.superawesome.sdk.views.SAInterstitialAd;
import tv.superawesome.sdk.views.SAOrientation;

public class SAAIRInterstitialAd {

    private static final String airName = "SAInterstitialAd";

    public static class SuperAwesomeAIRSAInterstitialAdCreate implements FREFunction {

        @Override
        public FREObject call(final FREContext freContext, FREObject[] freObjects) {

            SAInterstitialAd.setListener(new SAInterface() {
                @Override
                public void onEvent(int placementId, SAEvent event) {
                    switch (event) {
                        case adLoaded: SAAIRCallback.sendToAIR(freContext, airName, placementId, "adLoaded"); break;
                        case adFailedToLoad: SAAIRCallback.sendToAIR(freContext, airName, placementId, "adFailedToLoad"); break;
                        case adShown: SAAIRCallback.sendToAIR(freContext, airName, placementId, "adShown"); break;
                        case adFailedToShow: SAAIRCallback.sendToAIR(freContext, airName, placementId, "adFailedToShow"); break;
                        case adClicked: SAAIRCallback.sendToAIR(freContext, airName, placementId, "adClicked"); break;
                        case adClosed: SAAIRCallback.sendToAIR(freContext, airName, placementId, "adClosed");break;
                    }
                }
            });

            return null;
        }
    }

    public static class SuperAwesomeAIRSAInterstitialAdLoad implements FREFunction {

        @Override
        public FREObject call(FREContext freContext, FREObject[] freObjects) {

            Context context = freContext.getActivity();
            int placementId = SuperAwesome.getInstance().defaultPlacementId();
            int configuration = SuperAwesome.getInstance().defaultConfiguration().ordinal();
            boolean test = SuperAwesome.getInstance().defaultTestMode();

            try {
                placementId = freObjects[0].getAsInt();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }
            try {
                configuration = freObjects[1].getAsInt();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                test = freObjects[2].getAsBool();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            // configure & load
            SAInterstitialAd.setTestMode(test);
            SAInterstitialAd.setConfiguration(SAConfiguration.fromValue(configuration));
            SAInterstitialAd.load(placementId, context);

            return null;
        }
    }

    public static class SuperAwesomeAIRSAInterstitialAdHasAdAvailable implements FREFunction {

        @Override
        public FREObject call(FREContext freContext, FREObject[] freObjects) {

            int placementId = SuperAwesome.getInstance().defaultPlacementId();

            try {
                placementId = freObjects[0].getAsInt();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            boolean hasAdAvailable = SAInterstitialAd.hasAdAvailable(placementId);

            try {
                return FREObject.newObject(hasAdAvailable);
            } catch (FREWrongThreadException e) {
                return null;
            }
        }
    }

    public static class SuperAwesomeAIRSAInterstitialAdPlay implements FREFunction {

        @Override
        public FREObject call(FREContext freContext, FREObject[] freObjects) {

            Context context = freContext.getActivity();
            int placementId = SuperAwesome.getInstance().defaultPlacementId();
            boolean isParentalGateEnabled = SuperAwesome.getInstance().defaultParentalGate();
            int orientation = SuperAwesome.getInstance().defaultOrientation().ordinal();
            boolean isBackButtonEnabled = SuperAwesome.getInstance().defaultBackButton();

            try {
                placementId = freObjects[0].getAsInt();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                isParentalGateEnabled = freObjects[1].getAsBool();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                orientation = freObjects[2].getAsInt();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                isBackButtonEnabled = freObjects[3].getAsBool();
            } catch (FRETypeMismatchException | FREInvalidObjectException | FREWrongThreadException e) {
                e.printStackTrace();
            }

            // config & play
            SAInterstitialAd.setParentalGate(isParentalGateEnabled);
            SAInterstitialAd.setOrientation(SAOrientation.fromValue(orientation));
            SAInterstitialAd.setBackButton(isBackButtonEnabled);
            SAInterstitialAd.play(placementId, context);

            return null;
        }
    }
}