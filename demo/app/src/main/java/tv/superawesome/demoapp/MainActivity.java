package tv.superawesome.demoapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import tv.superawesome.lib.samodelspace.SAVASTAdType;
import tv.superawesome.sdk.SuperAwesome;
import tv.superawesome.sdk.views.SABannerAd;
import tv.superawesome.sdk.views.SAEvent;
import tv.superawesome.sdk.views.SAGameWall;
import tv.superawesome.sdk.views.SAInterface;
import tv.superawesome.sdk.views.SAInterstitialAd;
import tv.superawesome.sdk.views.SAVideoAd;

public class MainActivity extends Activity {

    private SABannerAd bannerAd = null;

    /** the options list */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bannerAd = (SABannerAd) findViewById(R.id.mybanner);
        bannerAd.setConfigurationStaging();
        bannerAd.disableTestMode();
        bannerAd.disableParentalGate();
        bannerAd.setListener(new SAInterface() {
            @Override
            public void onEvent(int placementId, SAEvent event) {
                if (event == SAEvent.adLoaded) {
                    Log.d("SuperAwesome", "Ad " + placementId + " loaded");
                } else if (event == SAEvent.adFailedToLoad) {
                    Log.d("SuperAwesome", "Ad " + placementId + " failed to load");
                }
            }
        });

        SAInterstitialAd.setConfigurationStaging();
        SAInterstitialAd.setOrientationPortrait();
        SAInterstitialAd.setListener(new SAInterface() {
            @Override
            public void onEvent(int placementId, SAEvent event) {
                if (event == SAEvent.adLoaded) {
                    Log.d("SuperAwesome", "Ad " + placementId + " loaded");
                } else if (event == SAEvent.adFailedToLoad) {
                    Log.d("SuperAwesome", "Ad " + placementId + " failed to load");
                }
            }
        });

        SAVideoAd.setConfigurationStaging();
        SAVideoAd.disableParentalGate();
        SAVideoAd.disableTestMode();
        SAVideoAd.setOrientationLandscape();
        SAVideoAd.setListener(new SAInterface() {
            @Override
            public void onEvent(int placementId, SAEvent event) {
                if (event == SAEvent.adLoaded) {
                    Log.d("SuperAwesome", "Ad " + placementId + " loaded");
                } else if (event == SAEvent.adFailedToLoad) {
                    Log.d("SuperAwesome", "Ad " + placementId + " failed to load");
                }
            }
        });

        SAGameWall.setConfigurationStaging();
        SAGameWall.disableTestMode();
        SAGameWall.setListener(new SAInterface() {
            @Override
            public void onEvent(int placementId, SAEvent event) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void loadAds (View view) {
        bannerAd.load(444);
        SAVideoAd.load(445, MainActivity.this);
        SAInterstitialAd.load(415, MainActivity.this);
        SAInterstitialAd.load(418, MainActivity.this);
        SAGameWall.load(437, MainActivity.this);
    }

    public void playBanner(View v){
        if (bannerAd.hasAdAvailable()) {
            bannerAd.play(MainActivity.this);
        }
    }

    public void playInterstitial1(View v){
        if (SAInterstitialAd.hasAdAvailable(415)) {
            SAInterstitialAd.play(415, MainActivity.this);
        }

    }

    public void playInterstitial2(View v){
        if (SAInterstitialAd.hasAdAvailable(418)) {
            SAInterstitialAd.play(418, MainActivity.this);
        }
    }

    public void playVideo1(View v){
        if (SAVideoAd.hasAdAvailable(445)) {
            SAVideoAd.play(445, MainActivity.this);
        }
    }

    public void playVideo2(View v){
        if (SAGameWall.hasAdAvailable(437)) {
            SAGameWall.play(437, MainActivity.this);
        }
//        if (SAVideoAd.hasAdAvailable(417)) {
//            SAVideoAd.play(417, MainActivity.this);
//        }
    }
}
