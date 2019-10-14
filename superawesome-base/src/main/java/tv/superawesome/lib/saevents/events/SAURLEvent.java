package tv.superawesome.lib.saevents.events;

import android.content.Context;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SAURLEvent extends SAServerEvent {

    protected String vastUrl = null;

    public SAURLEvent(Context context, String vastUrl) {
        this(context, vastUrl, Executors.newSingleThreadExecutor(), 15000, false);
    }

    public SAURLEvent (Context context, String vastUrl, Executor executor, int timeout, boolean isDebug) {
        super(context, null, null, executor, timeout, isDebug);
        this.vastUrl = vastUrl;
    }

    @Override
    public String getUrl() {
        return vastUrl;
    }

    @Override
    public String getEndpoint() {
        return "";
    }
}
