package tv.superawesome.lib.saevents;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import tv.superawesome.lib.saevents.events.SAURLEvent;
import tv.superawesome.lib.samodelspace.saad.SAAd;
import tv.superawesome.lib.samodelspace.vastad.SAVASTEvent;

public class SAVASTModule {

    private SAURLEvent                  vastClickThrough = null;
    private List<SAURLEvent>            vastError = new ArrayList<>();
    private List<SAURLEvent>            vastImpression = new ArrayList<>();
    private List<SAURLEvent>            vastCreativeView = new ArrayList<>();
    private List<SAURLEvent>            vastStart = new ArrayList<>();
    private List<SAURLEvent>            vastFirstQuartile = new ArrayList<>();
    private List<SAURLEvent>            vastMidpoint = new ArrayList<>();
    private List<SAURLEvent>            vastThirdQuartile = new ArrayList<>();
    private List<SAURLEvent>            vastComplete = new ArrayList<>();
    private List<SAURLEvent>            vastClickTracking = new ArrayList<>();

    public SAVASTModule (Context context, SAAd ad) {
        this(context, ad, Executors.newSingleThreadExecutor(), 15000, false);
    }

    public SAVASTModule (Context context, SAAd ad, Executor executor, int timeout, boolean isDebug) {
        try {

            for (SAVASTEvent event : ad.creative.details.media.vastAd.events) {
                if (event.event.contains("vast_click_through")) {
                    vastClickThrough = new SAURLEvent(context, event.URL, executor, timeout, isDebug);
                }
                if (event.event.contains("vast_error")) {
                    vastError.add(new SAURLEvent(context, event.URL, executor, timeout, isDebug));
                }
                if (event.event.contains("vast_impression")) {
                    vastImpression.add(new SAURLEvent(context, event.URL, executor, timeout, isDebug));
                }
                if (event.event.contains("vast_creativeView")) {
                    vastCreativeView.add(new SAURLEvent(context, event.URL, executor, timeout, isDebug));
                }
                if (event.event.contains("vast_start")) {
                    vastStart.add(new SAURLEvent(context, event.URL, executor, timeout, isDebug));
                }
                if (event.event.contains("vast_firstQuartile")) {
                    vastFirstQuartile.add(new SAURLEvent(context, event.URL, executor, timeout, isDebug));
                }
                if (event.event.contains("vast_midpoint")) {
                    vastMidpoint.add(new SAURLEvent(context, event.URL, executor, timeout, isDebug));
                }
                if (event.event.contains("vast_thirdQuartile")) {
                    vastThirdQuartile.add(new SAURLEvent(context, event.URL, executor, timeout, isDebug));
                }
                if (event.event.contains("vast_complete")) {
                    vastComplete.add(new SAURLEvent(context, event.URL, executor, timeout, isDebug));
                }
                if (event.event.contains("vast_click_tracking")) {
                    vastClickTracking.add(new SAURLEvent(context, event.URL, executor, timeout, isDebug));
                }
            }
        } catch (Exception e) {
            // do nothing
        }
    }

    public String getVASTClickThroughEvent () {
        if (vastClickThrough != null) {
            return vastClickThrough.getUrl();
        } else {
            return "";
        }
    }

    public void triggerVastClickThroughEvent (SAURLEvent.Listener listener) {
        if (vastClickThrough != null) {
            vastClickThrough.triggerEvent(listener);
        }
    }

    public void triggerVASTErrorEvent (SAURLEvent.Listener listener) {
        for (SAURLEvent event : vastError) {
            event.triggerEvent(listener);
        }
    }

    public void triggerVASTImpressionEvent (SAURLEvent.Listener listener) {
        for (SAURLEvent event : vastImpression) {
            event.triggerEvent(listener);
        }
    }

    public void triggerVASTCreativeViewEvent (SAURLEvent.Listener listener) {
        for (SAURLEvent event : vastCreativeView) {
            event.triggerEvent(listener);
        }
    }

    public void triggerVASTStartEvent (SAURLEvent.Listener listener) {
        for (SAURLEvent event : vastStart) {
            event.triggerEvent(listener);
        }
    }

    public void triggerVASTFirstQuartileEvent (SAURLEvent.Listener listener) {
        for (SAURLEvent event : vastFirstQuartile) {
            event.triggerEvent(listener);
        }
    }

    public void triggerVASTMidpointEvent (SAURLEvent.Listener listener) {
        for (SAURLEvent event : vastMidpoint) {
            event.triggerEvent(listener);
        }
    }

    public void triggerVASTThirdQuartileEvent (SAURLEvent.Listener listener) {
        for (SAURLEvent event : vastThirdQuartile) {
            event.triggerEvent(listener);
        }
    }

    public void triggerVASTCompleteEvent (SAURLEvent.Listener listener) {
        for (SAURLEvent event : vastComplete) {
            event.triggerEvent(listener);
        }
    }

    public void triggerVASTClickTrackingEvent (SAURLEvent.Listener listener) {
        for (SAURLEvent event : vastClickTracking) {
            event.triggerEvent(listener);
        }
    }

    public SAURLEvent getVastClickThrough() {
        return vastClickThrough;
    }

    public List<SAURLEvent> getVastError() {
        return vastError;
    }

    public List<SAURLEvent> getVastImpression() {
        return vastImpression;
    }

    public List<SAURLEvent> getVastCreativeView() {
        return vastCreativeView;
    }

    public List<SAURLEvent> getVastStart() {
        return vastStart;
    }

    public List<SAURLEvent> getVastFirstQuartile() {
        return vastFirstQuartile;
    }

    public List<SAURLEvent> getVastMidpoint() {
        return vastMidpoint;
    }

    public List<SAURLEvent> getVastThirdQuartile() {
        return vastThirdQuartile;
    }

    public List<SAURLEvent> getVastComplete() {
        return vastComplete;
    }

    public List<SAURLEvent> getVastClickTracking() {
        return vastClickTracking;
    }
}
