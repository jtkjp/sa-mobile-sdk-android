package tv.superawesome.lib.saevents;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.widget.VideoView;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import tv.superawesome.lib.samodelspace.saad.SAAd;
import tv.superawesome.lib.sautils.SAUtils;

public class SAMoatModule {

    private static final String kMoatClass = "tv.superawesome.lib.samoatevents.SAMoatEvents";

    // boolean mostly used for tests, in order to not limit moat at all
    private boolean   moatLimiting = true;

    // a moat object
    private Class<?>  moatClass = null;
    private Object    moatInstance = null;

    // the ad object
    private SAAd      ad;

    public SAMoatModule (SAAd ad) {

        // save the ad
        this.ad = ad;

        // create the moat class
        if (SAUtils.isClassAvailable(kMoatClass)) try {

            moatClass = Class.forName(kMoatClass);
            Constructor<?> moatConstructor = moatClass.getConstructor();
            moatInstance = moatConstructor.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
            Log.w("SuperAwesome", "Could not create Moat instance because " + e.getMessage());
        }
    }

    public static void initMoat (Application application, boolean loggingEnabled) {
        if (SAUtils.isClassAvailable(kMoatClass)) try {
            Class<?> moatCls = Class.forName(kMoatClass);
            java.lang.reflect.Method method = moatCls.getMethod("initMoat", Application.class, boolean.class);
            method.invoke(moatCls, application, loggingEnabled);
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("SuperAwesome", "Could not init Moat instance because " + e.getMessage());
        }
    }

    /**
     * Method that determines if Moat is allowed.
     * Conditions are:
     *  - that the ad should be not null
     *  - that moatLimiting should be enabled
     *  - if it's enabled, the random moat number should be smaller than
     *    the moat threshold of the ad
     *
     * @return true or false
     */
    public boolean isMoatAllowed () {
        // here calc if moat should be displayed
        int moatIntRand = SAUtils.randomNumberBetween(0, 100);
        double moatRand = moatIntRand / 100.0;
        return ad != null && ((moatRand < ad.moat && moatLimiting) || !moatLimiting);
    }

    /**
     * Method that registers a Moat event object, according to the moat specifications
     *
     * @param view      the web view used by Moat to register events on (and that will contain
     *                  an ad at runtime)
     * @return          returns a MOAT specific string that will need to be inserted in the
     *                  web view so that the JS moat stuff works
     */
    public String startMoatTrackingForDisplay(WebView view) {

        if (moatInstance != null && isMoatAllowed()) try {

            HashMap<String, String> adData = new HashMap<>();
            adData.put("advertiserId", "" + ad.advertiserId);
            adData.put("campaignId", "" + ad.campaignId);
            adData.put("lineItemId", "" + ad.lineItemId);
            adData.put("creativeId", "" + ad.creative.id);
            adData.put("app", "" + ad.appId);
            adData.put("placementId", "" + ad.placementId);
            adData.put("publisherId", "" + ad.publisherId);

            java.lang.reflect.Method method = moatClass.getMethod("startMoatTrackingForDisplay", WebView.class, HashMap.class);
            Object returnValue = method.invoke(moatInstance, view, adData);
            return (String) returnValue;

        } catch (Exception e) {
            Log.w("SuperAwesome", "Start Moat Tracking For Display: " + e.getMessage());
            return "";
        } else {
            Log.w("SuperAwesome", "Start Moat Tracking For Display: Moat instance is null or isMoatAllowed() returned false");
            return "";
        }
    }

    /**
     * Unregister moat events for Display
     *
     * @return whether the removal was successful or not
     */
    public boolean stopMoatTrackingForDisplay() {

        if (moatInstance != null) try {

            java.lang.reflect.Method method = moatClass.getMethod("stopMoatTrackingForDisplay");
            Object returnValue = method.invoke(moatInstance);
            return (Boolean) returnValue;

        } catch (Exception e) {
            return false;
        } else {
            return false;
        }
    }

    public boolean startMoatTrackingForVideoPlayer(VideoView videoView, int duration){

        if (moatInstance != null && isMoatAllowed()) try {

            HashMap<String, String> adData = new HashMap<>();
            adData.put("advertiserId", "" + ad.advertiserId);
            adData.put("campaignId", "" + ad.campaignId);
            adData.put("lineItemId", "" + ad.lineItemId);
            adData.put("creativeId", "" + ad.creative.id);
            adData.put("app", "" + ad.appId);
            adData.put("placementId", "" + ad.placementId);
            adData.put("publisherId", "" + ad.publisherId);

            java.lang.reflect.Method method = moatClass.getMethod("startMoatTrackingForVideoPlayer", VideoView.class, HashMap.class, int.class);
            Object returnValue = method.invoke(moatInstance, videoView, adData, duration);
            return (Boolean) returnValue;

        } catch (Exception e) {
            e.printStackTrace();
            Log.w("SuperAwesome", "Start Moat Tracking For Video: " + e.getMessage());
            return false;
        } else {
            Log.w("SuperAwesome", "Start Moat Tracking For Video: Moat instance is null or isMoatAllowed() returned false");
            return false;
        }
    }

    public boolean sendPlayingEvent (int position) {
        if (moatInstance != null) try {
            java.lang.reflect.Method method = moatClass.getMethod("sendPlayingEvent", int.class);
            Object returnValue = method.invoke(moatInstance, position);
            return (Boolean) returnValue;
        } catch (Exception e) {
            return false;
        } else {
            return false;
        }
    }

    public boolean sendStartEvent (int position) {
        if (moatInstance != null) try {
            java.lang.reflect.Method method = moatClass.getMethod("sendStartEvent", int.class);
            Object returnValue = method.invoke(moatInstance, position);
            return (Boolean) returnValue;
        } catch (Exception e) {
            return false;
        } else {
            return false;
        }
    }

    public boolean sendFirstQuartileEvent (int position) {
        if (moatInstance != null) try {
            java.lang.reflect.Method method = moatClass.getMethod("sendFirstQuartileEvent", int.class);
            Object returnValue = method.invoke(moatInstance, position);
            return (Boolean) returnValue;
        } catch (Exception e) {
            return false;
        } else {
            return false;
        }
    }

    public boolean sendMidpointEvent (int position) {
        if (moatInstance != null) try {
            java.lang.reflect.Method method = moatClass.getMethod("sendMidpointEvent", int.class);
            Object returnValue = method.invoke(moatInstance, position);
            return (Boolean) returnValue;
        } catch (Exception e) {
            return false;
        } else {
            return false;
        }
    }

    public boolean sendThirdQuartileEvent (int position) {
        if (moatInstance != null) try {
            java.lang.reflect.Method method = moatClass.getMethod("sendThirdQuartileEvent", int.class);
            Object returnValue = method.invoke(moatInstance, position);
            return (Boolean) returnValue;
        } catch (Exception e) {
            return false;
        } else {
            return false;
        }
    }

    public boolean sendCompleteEvent (int position) {
        if (moatInstance != null) try {
            java.lang.reflect.Method method = moatClass.getMethod("sendCompleteEvent", int.class);
            Object returnValue = method.invoke(moatInstance, position);
            return (Boolean) returnValue;
        } catch (Exception e) {
            return false;
        } else {
            return false;
        }
    }

    /**
     * Method to unregister a Moat event for video
     *
     * @return whether the video moat event was killed off OK
     */
    public boolean stopMoatTrackingForVideoPlayer() {

        if (moatInstance != null) try {

            java.lang.reflect.Method method = moatClass.getMethod("stopMoatTrackingForVideoPlayer");
            Object returnValue = method.invoke(moatInstance);
            return (Boolean) returnValue;

        } catch (Exception e) {
            return false;
        } else {
            return false;
        }
    }

    public void disableMoatLimiting () {
        moatLimiting = false;
    }
}
