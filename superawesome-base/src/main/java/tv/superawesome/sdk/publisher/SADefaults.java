/**
 * @Copyright:   SADefaults Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.sdk.publisher;


import tv.superawesome.lib.sasession.defines.SAConfiguration;
import tv.superawesome.lib.sasession.defines.SARTBStartDelay;

/**
 * This is a Singleton class through which SDK users setup their AwesomeAds instance
 */
public class SADefaults {

    /**
     * Methods that return default values for each of the variables that can be used to
     * customize Video, Banner, Interstitial and AppWall ads
     *
     * @return whatever return value
     */
    public static int defaultPlacementId () {
        return 0;
    }
    public static boolean defaultTestMode () {
        return false;
    }
    public static boolean defaultParentalGate () {
        return false;
    }
    public static boolean defaultBumperPage () {
        return false;
    }
    public static SAConfiguration defaultConfiguration () {
        return SAConfiguration.PRODUCTION;
    }
    public static SAOrientation defaultOrientation () {
        return SAOrientation.ANY;
    }
    public static boolean defaultCloseButton () {
        return false;
    }
    public static boolean defaultSmallClick () {
        return false;
    }
    public static boolean defaultCloseAtEnd () {
        return true;
    }
    public static boolean defaultBgColor () {
        return false;
    }
    public static boolean defaultBackButton () {
        return false;
    }
    public static boolean defaultMoatLimitingState () { return true; }
    public static SARTBStartDelay defaultPlaybackMode () {
        return SARTBStartDelay.PRE_ROLL;
    }
    public static Long defaultClickThreshold () {
        return 5L;
    }
}
