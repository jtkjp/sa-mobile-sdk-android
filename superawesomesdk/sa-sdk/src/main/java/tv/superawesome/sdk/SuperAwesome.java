package tv.superawesome.sdk;

import android.util.Log;

public class SuperAwesome {

	private static final String TAG = "SuperAwesome SDK";
	private static final String VERSION = "2.2.1";
	private static final String PLATFORM = "android";

	protected static final String baseUrl = "https://ads.superawesome.tv/v2";

	public static String getVersion(){
		return VERSION;
	}

	public static String getPlatform() {
		return PLATFORM;
	}

	public static String getSdkVersion() {
		return SuperAwesome.getPlatform() + "_" + SuperAwesome.getVersion();
	}

	public SuperAwesome(){
		Log.v(TAG, "SuperAwesome SDK version " + VERSION);
	}

	public static AdManager createAdManager () {
		return new AdManager(baseUrl);
	}

	public static UrlLoader createUrlLoader() {
		return new UrlLoader();
	}

	public static String getBaseUrl() { return baseUrl; }
}
