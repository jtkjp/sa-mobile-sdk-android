package tv.superawesome.demoapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import tv.superawesome.lib.samodelspace.SAVASTAdType;
import tv.superawesome.sdk.SuperAwesome;
import tv.superawesome.sdk.views.SABannerAd;
import tv.superawesome.sdk.views.SAEvent;
import tv.superawesome.sdk.views.SAInterface;
import tv.superawesome.sdk.views.SAInterstitialAd;
import tv.superawesome.sdk.views.SAVideoAd;

public class MainActivity extends Activity {

//    private SALoader loader = null;
//    private SAAd bannerData = null;
//    private SAAd interstitial1Data = null;
//    private SAAd interstitial2Data = null;
//    private SAAd interstitial3Data = null;
//    private SAAd video1Data = null;
//    private SAAd video2Data = null;
    private SABannerAd bannerAd = null;

    /** the options list */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SuperAwesome.getInstance().setApplicationContext(getApplicationContext());

        bannerAd = (SABannerAd) findViewById(R.id.mybanner);
        bannerAd.setConfigurationStaging();
        bannerAd.disableTestMode();
        bannerAd.disableParentalGate();
        bannerAd.load(414);

        SAInterstitialAd.setConfigurationStaging();
        SAInterstitialAd.setOrientationPortrait();
        SAInterstitialAd.load(415);
        SAInterstitialAd.load(418);

        SAVideoAd.setConfigurationStaging();
        SAVideoAd.disableParentalGate();
        SAVideoAd.setOrientationLandscape();
        SAVideoAd.disableCloseButton();
        SAVideoAd.enableSmallClickButton();
        SAVideoAd.load(416);
        SAVideoAd.load(417);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        if (SAVideoAd.hasAdAvailable(416)) {
            SAVideoAd.play(416, MainActivity.this);
        }
    }

    public void playVideo2(View v){
        if (SAVideoAd.hasAdAvailable(417)) {
            SAVideoAd.play(417, MainActivity.this);
        }
    }
}
