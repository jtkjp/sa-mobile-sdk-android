package tv.superawesome.plugins.unity;

import android.content.Context;
import android.util.Log;

import tv.superawesome.sdk.SuperAwesome;
import tv.superawesome.lib.saadloader.SALoader;
import tv.superawesome.lib.saadloader.SALoaderInterface;
import tv.superawesome.lib.samodelspace.SAAd;

/**
 * Created by gabriel.coman on 13/05/16.
 */
public class SAUnityLoadAd {

    /**
     * Loads an ad using the native SDK and returns the Ad's JSON (to act as "ad data" in Unity)
     * @param context the current context
     * @param unityName the unity unique object that sent this
     * @param placementId the placement id to load the ad for
     * @param isTestingEnabled whether testing is enabled or not
     */
    public static void SuperAwesomeUnityLoadAd(final Context context, final String unityName, int placementId, boolean isTestingEnabled, int configuration) {
        /** setup testing */

        SuperAwesome.getInstance().setTestMode(isTestingEnabled);
        Log.d("SuperAwesome", "On Android side Testing is " + SuperAwesome.getInstance().isTestingEnabled());
        SuperAwesome.getInstance().setApplicationContext(context);
        SuperAwesome.getInstance().setConfiguration(configuration);

        /** create the new SALoader */
        SALoader loader = new SALoader();
        loader.loadAd(placementId, new SALoaderInterface() {
            @Override
            public void didLoadAd(SAAd ad) {
                SAUnityExtension.SendUnityMsgPayload(unityName, "callback_didLoadAd", ad.placementId, "adJson", ad.writeToJson().toString());
            }

            @Override
            public void didFailToLoadAdForPlacementId(int placementId) {
                SAUnityExtension.SendUnityMsgPayload(unityName, "callback_didFailToLoadAd", placementId, "", "");
            }
        });
    }
}