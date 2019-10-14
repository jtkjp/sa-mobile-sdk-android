package tv.superawesome.plugins.publisher.admob;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

import tv.superawesome.lib.sasession.defines.SAConfiguration;
import tv.superawesome.lib.sautils.SAUtils;
import tv.superawesome.sdk.publisher.SABannerAd;
import tv.superawesome.sdk.publisher.SAEvent;
import tv.superawesome.sdk.publisher.SAInterface;

public class SAAdMobBannerCustomEvent implements CustomEventBanner {

    private final int ID = SAUtils.randomNumberBetween(1000000, 1500000);
    private boolean layouted = false;
    private boolean loaded = false;
    private boolean setup = false;
    private SABannerAd bannerAd;

    @Override
    public void requestBannerAd(final Context context, final CustomEventBannerListener listener, String s, AdSize adSize, MediationAdRequest mediationAdRequest, Bundle bundle) {

        bannerAd = new SABannerAd(context);
        bannerAd.setId(ID);

        // Internally, smart banners use constants to represent their ad size, which means a call to
        // AdSize.getHeight could return a negative value. You can accommodate this by using
        // AdSize.getHeightInPixels and AdSize.getWidthInPixels instead, and then adjusting to match
        // the device's display metrics.
        int widthInPixels = adSize.getWidthInPixels(context);
        int heightInPixels = adSize.getHeightInPixels(context);
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int widthInDp = Math.round(widthInPixels / displayMetrics.density);
        int heightInDp = Math.round(heightInPixels / displayMetrics.density);

        bannerAd.setLayoutParams(new ViewGroup.LayoutParams(widthInDp, heightInDp));

        if (bundle != null) {
            bannerAd.setTestMode(bundle.getBoolean(SAAdMobExtras.kKEY_TEST));
            bannerAd.setConfiguration(SAConfiguration.fromValue(bundle.getInt(SAAdMobExtras.kKEY_CONFIGURATION)));
            bannerAd.setColor(bundle.getBoolean(SAAdMobExtras.kKEY_TRANSPARENT));
            bannerAd.setParentalGate(bundle.getBoolean(SAAdMobExtras.kKEY_PARENTAL_GATE));
            bannerAd.setBumperPage(bundle.getBoolean(SAAdMobExtras.kKEY_BUMPER_PAGE));
        }

        bannerAd.setListener(new SAInterface() {
            @Override
            public void onEvent(int i, SAEvent saEvent) {
                switch (saEvent) {
                    case adLoaded: {
                        // send load event
                        if (listener != null) {
                            listener.onAdLoaded(bannerAd);
                        }

                        loaded = true;

                        if (layouted && bannerAd != null && !setup) {
                            bannerAd.play(context);
                            setup = true;
                        }
                        break;
                    }
                    case adEmpty:
                    case adFailedToLoad: {
                        if (listener != null) {
                            listener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
                        }
                        break;
                    }
                    case adAlreadyLoaded:
                        break;
                    case adShown: {
                        if (listener != null) {
                            listener.onAdOpened();
                        }
                        break;
                    }
                    case adFailedToShow: {
                        if (listener != null) {
                            listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
                        }
                        break;
                    }
                    case adClicked: {
                        if (listener != null) {
                            listener.onAdClicked();
                            listener.onAdLeftApplication();
                        }
                        break;
                    }
                    case adEnded:
                        break;
                    case adClosed: {
                        if (listener != null) {
                            listener.onAdClosed();
                        }
                        break;
                    }
                }
            }
        });

        try {
            bannerAd.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    layouted = true;
                    if (loaded && !setup) {
                        bannerAd.play(context);
                    }
                }
            });
            int placementId = Integer.valueOf(s);
            bannerAd.load(placementId);
        } catch (NumberFormatException e) {
            if (listener != null) {
                listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (bannerAd != null) {
            bannerAd.close();
            ViewGroup parent = (ViewGroup) bannerAd.getParent();
            if (parent != null) {
                parent.removeView(bannerAd);
            }
            bannerAd = null;
        }
    }

    @Override
    public void onPause() {
        // do nothing
    }

    @Override
    public void onResume() {
        // do nothing
    }
}
