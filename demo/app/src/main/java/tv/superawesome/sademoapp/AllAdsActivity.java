package tv.superawesome.sademoapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import tv.superawesome.superawesomesdk.fragments.SABannerFragment;
import tv.superawesome.superawesomesdk.fragments.SAInterstitialFragment;
import tv.superawesome.superawesomesdk.fragments.SAVideoFragment;
import tv.superawesome.superawesomesdk.models.SAAd;
import tv.superawesome.superawesomesdk.views.video.SAVideoViewListener;


public class AllAdsActivity extends ActionBarActivity {

    private static final String TAG = "All Ads Activity";
    private SABannerFragment bannerAd;
    private SAInterstitialFragment interstitialAd;
    private SAVideoFragment videoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_ads);

        this.bannerAd = (SABannerFragment)getSupportFragmentManager().findFragmentById(R.id.sa_banner);
        this.interstitialAd = (SAInterstitialFragment)getSupportFragmentManager().findFragmentById(R.id.sa_interstitial);
        this.videoAd = (SAVideoFragment)getSupportFragmentManager().findFragmentById(R.id.sa_video_ad);

    }

    public void showInterstitial(View view) {
        interstitialAd.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_ads, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}