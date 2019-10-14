package tv.superawesome.plugins.publisher.unity;

import android.content.Context;

import tv.superawesome.lib.sabumperpage.SABumperPage;

public class SAUnityBumperPage {

    /**
     * Method that sets the version
     *
     * @param context   current context
     * @param name      new bumper page name
     */
    public static void SuperAwesomeUnityBumperOverrideName (Context context, String name) {
        SABumperPage.overrideName(name);
    }
}
