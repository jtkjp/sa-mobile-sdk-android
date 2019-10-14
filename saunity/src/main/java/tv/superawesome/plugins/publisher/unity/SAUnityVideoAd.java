/**
 * @Copyright:   SADefaults Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.plugins.publisher.unity;

import android.content.Context;

import tv.superawesome.lib.sasession.defines.SAConfiguration;
import tv.superawesome.lib.sasession.defines.SARTBStartDelay;
import tv.superawesome.sdk.publisher.SAEvent;
import tv.superawesome.sdk.publisher.SAInterface;
import tv.superawesome.sdk.publisher.SAOrientation;
import tv.superawesome.sdk.publisher.SAVideoAd;

/**
 * Class that holds a number of static methods used to communicate with Unity
 */
public class SAUnityVideoAd {

    private static final String unityName = "SAVideoAd";

    /**
     * Method that creates a new Video Ad (from Unity)
     */
    public static void SuperAwesomeUnitySAVideoAdCreate (Context context) {
        SAVideoAd.setListener(new SAInterface() {
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
    }

    /**
     * Method that loads a new Video Ad (from Unity)
     */
    public static void SuperAwesomeUnitySAVideoAdLoad(Context context,
                                                      int placementId,
                                                      int configuration,
                                                      boolean test,
                                                      int playback) {
        SAVideoAd.setTestMode(test);
        SAVideoAd.setConfiguration(SAConfiguration.fromValue(configuration));
        SAVideoAd.setPlaybackMode(SARTBStartDelay.fromValue(playback));
        SAVideoAd.load(placementId, context);
    }

    /**
     * Method that checks to see if an ad is available for a video ad (from Unity)
     */
    public static boolean SuperAwesomeUnitySAVideoAdHasAdAvailable (Context context,
                                                                    int placementId) {
        return SAVideoAd.hasAdAvailable(placementId);
    }

    /**
     * Method that plays a new video ad (from Unity)
     */
    public static void SuperAwesomeUnitySAVideoAdPlay (Context context,
                                                       int placementId,
                                                       boolean isParentalGateEnabled,
                                                       boolean isBumperPageEnabled,
                                                       boolean shouldShowCloseButton,
                                                       boolean shouldShowSmallClickButton,
                                                       boolean shouldAutomaticallyCloseAtEnd,
                                                       int orientation,
                                                       boolean isBackButtonEnabled) {
        SAVideoAd.setParentalGate(isParentalGateEnabled);
        SAVideoAd.setBumperPage(isBumperPageEnabled);
        SAVideoAd.setCloseAtEnd(shouldAutomaticallyCloseAtEnd);
        SAVideoAd.setCloseButton(shouldShowCloseButton);
        SAVideoAd.setSmallClick(shouldShowSmallClickButton);
        SAVideoAd.setBackButton(isBackButtonEnabled);
        SAVideoAd.setOrientation(SAOrientation.fromValue(orientation));
        SAVideoAd.play(placementId, context);
    }

}
