package tv.superawesome.sdk.publisher;

public class SAVersion {

    // version & sdk private vars
    private static String version = "7.0.0";
    private static String sdk = "android";

    /**
     * Getter for the current version
     *
     * @return string representing the current version
     */
    private static String getVersion() {
        return version;
    }

    /**
     * Getter for the current SDK
     *
     * @return string representing the current SDK
     */
    private static String getSdk() {
        return sdk;
    }

    /**
     * Getter for a string comprising of SDK & version bundled
     *
     * @return  a string
     */
    public static String getSDKVersion() {
        return getSdk() + "_" + getVersion();
    }

    /**
     * Method that overrides the current version string. It's used by the AIR & Unity SDKs
     *
     * @param version the new version
     */
    public static void overrideVersion (String version) {
        SAVersion.version = version;
    }

    /**
     * Method that overrides the current sdk string. It's used by the AIR & Unity SDKs
     *
     * @param sdk the new sdk
     */
    public static void overrideSdk (String sdk) {
        SAVersion.sdk = sdk;
    }
}
