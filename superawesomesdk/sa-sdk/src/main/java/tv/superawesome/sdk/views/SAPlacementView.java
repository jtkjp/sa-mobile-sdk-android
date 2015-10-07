package tv.superawesome.sdk.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.nexage.sourcekit.mraid.MRAIDNativeFeatureListener;
import org.nexage.sourcekit.mraid.MRAIDView;

import tv.superawesome.sdk.AdLoaderListener;
import tv.superawesome.sdk.AdManager;
import tv.superawesome.sdk.R;
import tv.superawesome.sdk.SuperAwesome;
import tv.superawesome.sdk.events.SAEventManager;
import tv.superawesome.sdk.models.SAAd;
import tv.superawesome.sdk.padlock.SAPadlock;
import tv.superawesome.sdk.parentalgate.SAParentalGate;
import tv.superawesome.sdk.parentalgate.SAParentalGateListener;


public abstract class SAPlacementView extends FrameLayout implements MRAIDNativeFeatureListener {

	protected static final String TAG = "SA SDK - Placement";
    protected String placementID;
    protected boolean testMode = false;
    protected boolean parentalGateEnabled = false;
    protected Context context;
    protected AdManager adManager;
    protected SAPlacementListener listener = null;
    protected AdLoaderListener adLoaderListener;
    protected MRAIDView mraidView;
    protected SAAd loadedAd = null;
    protected String baseUrl = "http://superawesome.tv";
    protected ImageButton padlockImage;
    protected SAParentalGate gate;

    public SAPlacementView(Context context, String placementID, AdManager adManager) {
        super(context);
        this.context = context;
        this.placementID = placementID;
        this.adManager = adManager;
        this.createPadlockImage();
    }

    public SAPlacementView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.adManager = SuperAwesome.createAdManager();
        this.createPadlockImage();
    }

    protected void createPadlockImage() {
        // only display this when ad is not fallback
        padlockImage = new ImageButton(context);
        padlockImage.setBackgroundColor(Color.TRANSPARENT);
        padlockImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPadlockClick(v);
                }
            });
        padlockImage.setImageResource(R.drawable.sa_padlock);
    }

    public void setPlacementID(String placementID) {
        this.placementID = placementID;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public void setParentalGateEnabled(boolean parentalGateEnabled){
        this.parentalGateEnabled = parentalGateEnabled;
    }

    private boolean checkAppPermissions() {
        int res = getContext().checkCallingOrSelfPermission("android.permission.INTERNET");
        if (res != PackageManager.PERMISSION_GRANTED) {
            String message = "Error: Your app does not have a required permission: INTERNET. Not requesting ad.";
            if (this.listener != null) this.listener.onAdError(message);
            Log.d(TAG, message);
            return false;
        }
        res = getContext().checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE");
        if (res != PackageManager.PERMISSION_GRANTED) {
            String message = "Error: Your app does not have a required permission: ACCESS_NETWORK_STATE. Not requesting ad.";
            if (this.listener != null) this.listener.onAdError(message);
            Log.d(TAG, message);
            return false;
        }
        return true;
    }

    protected void showPadlock(View view)
    {
        //If ad isn't loaded for some reason, or ad is a fallback ad, do not show the padlock.
        if (this.loadedAd == null || this.loadedAd.isFallback) {
            return;
        }
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = 20 * metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT;
        int height = 20 * metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT;
        int x = view.getMeasuredWidth() - width;
        int y = view.getMeasuredHeight() - height;

        WebView.LayoutParams padlockParams = new WebView.LayoutParams(width, height, x, y);

        padlockImage.setLayoutParams(padlockParams);
        padlockImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        padlockImage.setPadding(0, 0, 0, 0);

        ViewGroup padlockParent = (ViewGroup)padlockImage.getParent();
        if (padlockParent != null) padlockParent.removeView(padlockImage);
        Log.d(TAG, "Showing padlock");
        ((ViewGroup) view).addView(padlockImage);
        padlockImage.bringToFront();
    }

    protected void onPadlockClick(View view) {
        SAPadlock padlock = new SAPadlock();
        padlock.createPadlock(context);
    }

    public void enableTestMode() {
        this.testMode = true;
    }

    public void disableTestMode() {
        this.testMode = false;
    }

    protected abstract void setView(String content);

    public void setListener(SAPlacementListener listener) {
        this.listener = listener;
    }

    public void loadAd() {
        if (!this.checkAppPermissions()) {
            return;
        }
        try {
            this.adManager.getAd(this.placementID, this.testMode, new AdLoaderListener() {
                @Override
                public void onBeginLoad(String url) {

                }

                @Override
                public void onError(String message) {
                    SAEventManager.getIntance().LogAdFailed(loadedAd);
                    Log.d(TAG, "Error:" + message);
                    if (listener != null) listener.onAdError(message);
                }

                @Override
                public void onLoaded(SAAd superAwesomeAd) {
                    if (listener != null) listener.onAdLoaded(superAwesomeAd);
                    loadedAd = superAwesomeAd;
                    Log.d(TAG, "SAAd loaded");
                    try {
                        if (!superAwesomeAd.error) {
                            if (superAwesomeAd.url != null) baseUrl = superAwesomeAd.url;
                            setView(superAwesomeAd.getContent());
                        } else {
                            if (listener != null) listener.onAdError(superAwesomeAd.error_message);
                            Log.d(TAG, "Error: " + superAwesomeAd.error_message);
                        }
                    } catch (Exception e) {
                        if (listener != null) listener.onAdError(e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            if (listener != null) listener.onAdError(e.getMessage());
        }
    }

    @Override
    public void mraidNativeFeatureCallTel(String url) {

    }

    @Override
    public void mraidNativeFeatureCreateCalendarEvent(String eventJSON) {

    }

    @Override
    public void mraidNativeFeaturePlayVideo(String url) {

    }

    @Override
    public void mraidNativeFeatureOpenBrowser(final String url) {
        Log.d(TAG, "mraidNativeFeatureOpenBrowser " + url);

        // with parental gate
        if (this.parentalGateEnabled) {
            // create new and show
            gate = new SAParentalGate(context);
            gate.setListener(new SAParentalGateListener() {
                @Override
                public void onPressCancel() {
                    SAEventManager.getIntance().LogUserCanceledParentalGate(loadedAd);
                }

                @Override
                public void onPressContinueWithError() {
                    SAEventManager.getIntance().LogUserErrorWithParentalGate(loadedAd);
                }

                @Override
                public void onPressContinueWithSuccess() {
                    // do nothing
                    SAEventManager.getIntance().LogUserSuccessWithParentalGate(loadedAd);
                    SAEventManager.getIntance().LogClick(loadedAd);
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });
            gate.show();
        }
        // case with no parental gate
        else {
            SAEventManager.getIntance().LogClick(loadedAd);
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {

    }

    public abstract void paused();

    public abstract void resumed();

}