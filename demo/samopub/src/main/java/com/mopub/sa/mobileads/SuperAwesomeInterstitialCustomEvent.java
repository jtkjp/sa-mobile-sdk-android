package com.mopub.sa.mobileads;

import android.content.Context;

import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Map;

import tv.superawesome.sdk.SuperAwesome;
import tv.superawesome.sdk.data.Loader.SALoader;
import tv.superawesome.sdk.data.Loader.SALoaderListener;
import tv.superawesome.sdk.data.Models.SAAd;
import tv.superawesome.sdk.listeners.SAAdListener;
import tv.superawesome.sdk.listeners.SAParentalGateListener;
import tv.superawesome.sdk.views.SAInterstitialActivity;

/**
 * Created by gabriel.coman on 27/10/15.
 */
public class SuperAwesomeInterstitialCustomEvent extends CustomEventInterstitial {

    private CustomEventInterstitialListener evtListener;

    @Override
    protected void loadInterstitial(final Context context, final CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> map, Map<String, String> map1) {

        // get map variables
        int placementId = 0;
        final boolean isTestEnabled;
        final boolean isParentalGateEnabled;

        if (map1.get("placementId") != null ){
            placementId = Integer.parseInt((String)map1.get("placementId").toString());
        }

        if (map1.get("isTestEnabled") != null) {
            isTestEnabled = Boolean.valueOf(map1.get("isTestEnabled"));
        } else {
            isTestEnabled = true;
        }

        if (map1.get("isParentalGateEnabled") != null){
            isParentalGateEnabled = Boolean.valueOf(map1.get("isParentalGateEnabled"));
        } else {
            isParentalGateEnabled = false;
        }

        /** before loading */
        SuperAwesome.getInstance().setConfigurationProduction();
        SuperAwesome.getInstance().setTestMode(isTestEnabled);
        SuperAwesome.getInstance().setApplicationContext(context);

       // save evt listener
        evtListener = customEventInterstitialListener;

        SALoader loader = new SALoader();
        loader.loadAd(placementId, new SALoaderListener() {
            @Override
            public void didLoadAd(SAAd saAd) {
                /** call listener */
                if (evtListener != null) {
                    evtListener.onInterstitialLoaded();
                }

                SAInterstitialActivity interstitial = new SAInterstitialActivity(context);
                interstitial.setAd(saAd);
                interstitial.setIsParentalGateEnabled(isParentalGateEnabled);
                interstitial.setAdListener(new SAAdListener() {
                    @Override
                    public void adWasShown(int placementId) {
                        if (evtListener != null) {
                            evtListener.onInterstitialShown();
                        }
                    }

                    @Override
                    public void adFailedToShow(int placementId) {
                        if (evtListener != null) {
                            evtListener.onInterstitialFailed(MoPubErrorCode.MRAID_LOAD_ERROR);
                        }
                    }

                    @Override
                    public void adWasClosed(int placementId) {
                        if (evtListener != null) {
                            evtListener.onInterstitialDismissed();
                        }
                    }

                    @Override
                    public void adWasClicked(int placementId) {
                        if (evtListener != null) {
                            evtListener.onInterstitialClicked();
                        }
                    }

                    @Override
                    public void adHasIncorrectPlacement(int placementId) {
                        if (evtListener != null) {
                            evtListener.onInterstitialFailed(MoPubErrorCode.MRAID_LOAD_ERROR);
                        }
                    }
                });

                /** play the ad */
                interstitial.play();
            }

            @Override
            public void didFailToLoadAdForPlacementId(int i) {
                if (evtListener != null) {
                    evtListener.onInterstitialFailed(MoPubErrorCode.MRAID_LOAD_ERROR);
                }
            }
        });
    }

    @Override
    protected void showInterstitial() {

        if (evtListener != null) {
            evtListener.onInterstitialShown();
        }
    }

    @Override
    protected void onInvalidate() {
        // do nothing
    }
}