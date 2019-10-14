package tv.superawesome.sdk.publisher;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import tv.superawesome.lib.saevents.SAEvents;
import tv.superawesome.lib.sanetwork.file.SAFileDownloader;
import tv.superawesome.sagdprisminorsdk.minor.SAAgeCheck;
import tv.superawesome.sagdprisminorsdk.minor.process.GetIsMinorInterface;

/**
 * Created by gabriel.coman on 30/04/2018.
 */

public class AwesomeAds {

    private static boolean isInitialised = false;

    public static void init (Application application, boolean loggingEnabled) {
        if (!isInitialised) {
            Log.d("SuperAwesome", "Initialising AwesomeAds!");
            SAEvents.initMoat(application, loggingEnabled);
            SAFileDownloader.cleanup(application);
            isInitialised = true;
        } else {
            Log.d("SuperAwesome", "Already initialised AwesomeAds!");
        }
    }

    public static void triggerAgeCheck (Context context, String dateOfBirth, GetIsMinorInterface listener) {
        SAAgeCheck.sdk.getIsMinor(context, dateOfBirth, listener);
    }
}
