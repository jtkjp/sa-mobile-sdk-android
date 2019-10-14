package tv.superawesome.plugins.publisher.admob;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;

import tv.superawesome.lib.sasession.defines.SAConfiguration;
import tv.superawesome.sdk.publisher.SAEvent;
import tv.superawesome.sdk.publisher.SAInterface;
import tv.superawesome.sdk.publisher.SAInterstitialAd;
import tv.superawesome.sdk.publisher.SAOrientation;

public class SAAdMobInterstitialCustomEvent implements CustomEventInterstitial {

    private Context context = null;
    private Integer loadedPlacementId = 0;

    @Override
    public void requestInterstitialAd(final Context context, final CustomEventInterstitialListener listener, String s, MediationAdRequest mediationAdRequest, Bundle bundle) {

        // save the context
        this.context = context;

        // set values
        if (bundle != null) {
            SAInterstitialAd.setConfiguration(SAConfiguration.fromValue(bundle.getInt(SAAdMobExtras.kKEY_CONFIGURATION)));
            SAInterstitialAd.setTestMode(bundle.getBoolean(SAAdMobExtras.kKEY_TEST));
            SAInterstitialAd.setParentalGate(bundle.getBoolean(SAAdMobExtras.kKEY_PARENTAL_GATE));
            SAInterstitialAd.setBumperPage(bundle.getBoolean(SAAdMobExtras.kKEY_BUMPER_PAGE));
            SAInterstitialAd.setBackButton(bundle.getBoolean(SAAdMobExtras.kKEY_BACK_BUTTON));
            SAInterstitialAd.setOrientation(SAOrientation.fromValue(bundle.getInt(SAAdMobExtras.kKEY_ORIENTATION)));
        }

        SAInterstitialAd.setListener(new SAInterface() {
            @Override
            public void onEvent(int placementId, SAEvent event) {
                switch (event) {

                    case adLoaded: {

                        loadedPlacementId = placementId;

                        if (listener != null) {
                            listener.onAdLoaded();
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
            Integer placementId = Integer.parseInt(s);
            SAInterstitialAd.load(placementId, context);
        } catch (NumberFormatException e) {
            if (listener != null) {
                listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            }
        }
    }

    @Override
    public void showInterstitial() {
        SAInterstitialAd.play(loadedPlacementId, context);
    }

    @Override
    public void onDestroy() {
        // do nothing
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
