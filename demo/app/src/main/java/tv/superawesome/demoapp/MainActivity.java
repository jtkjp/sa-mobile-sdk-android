package tv.superawesome.demoapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tv.superawesome.lib.sanetwork.SASystem;
import tv.superawesome.lib.sautils.SALog;
import tv.superawesome.lib.savast.savastmanager.SAVASTManager;
import tv.superawesome.lib.savast.savastparser.SAVASTParser;
import tv.superawesome.lib.savast.savastparser.SAVASTParserListener;
import tv.superawesome.lib.savast.savastparser.models.SAVASTAd;
import tv.superawesome.lib.savast.savastplayer.SAVASTPlayer;
import tv.superawesome.lib.savast.savastplayer.SAVASTPlayerListener;
import tv.superawesome.lib.savast.saxml.SAXML;
import tv.superawesome.sdk.SuperAwesome;
import tv.superawesome.sdk.data.Loader.SALoader;
import tv.superawesome.sdk.data.Loader.SALoaderListener;
import tv.superawesome.sdk.data.Models.SAAd;
import tv.superawesome.sdk.listeners.SAAdListener;
import tv.superawesome.sdk.listeners.SAParentalGateListener;
import tv.superawesome.sdk.listeners.SAVideoAdListener;
import tv.superawesome.sdk.views.SAVideoActivity;

public class MainActivity extends Activity implements SAAdListener, SAParentalGateListener, SAVideoAdListener {

    private static final int CONTENT_VIEW_ID = 10101010;
    private SAVASTPlayer newFragment;
    private SAAdListener adListener = this;
    private SAParentalGateListener parentalGateListener = this;
    private SAVideoAdListener videoAdListener = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SuperAwesome.getInstance().setConfigurationProduction();
        SuperAwesome.getInstance().enableTestMode();
        SALog.Log(SuperAwesome.getInstance().getSDKVersion());
        SALog.Log(SASystem.getVerboseSystemDetails());

        int ad1 = 30245;
        int ad2 = 21022;

        SALoader.loadAd(ad1, new SALoaderListener() {
            @Override
            public void didLoadAd(SAAd ad) {
                SAVideoActivity.start(MainActivity.this, ad, false, adListener, parentalGateListener, videoAdListener);
            }

            @Override
            public void didFailToLoadAdForPlacementId(int placementId) {
                SALog.Log("Failed to load " + placementId);
            }
        });

//        String[] ads = {
//                "Banner ad - code",
//                "Banner ad - XML",
//                "Interstitial - code",
//                "Interstitial - XML",
//                "Video ad - code",
//                "Video ad - XML",
//                "Video ad - fullscreen activity",
//                "Gamewall",
//                "All ads in one activity"
//        };
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, ads);
//
//        ListView list = (ListView) findViewById(R.id.list);
//        list.setAdapter(adapter);
//
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                switch (position) {
//                    case 0:
//                        startActivity(new Intent(MainActivity.this, BannerAdCodeActivity.class));
//                        break;
//                    case 1:
//                        startActivity(new Intent(MainActivity.this, BannerAdXmlActivity.class));
//                        break;
//                    case 2:
//                        startActivity(new Intent(MainActivity.this, InterstitialAdCodeActivity.class));
//                        break;
//                    case 3:
//                        startActivity(new Intent(MainActivity.this, InterstitialAdXmlActivity.class));
//                        break;
//                    case 4:
//                        startActivity(new Intent(MainActivity.this, VideoAdCodeActivity.class));
//                        break;
//                    case 5:
//                        startActivity(new Intent(MainActivity.this, VideoAdXmlActivity.class));
//                        break;
//                    case 6:
////                        SAVideoActivity.start(MainActivity.this, "21022", "false", "true");
//                        break;
//                    case 7:
//                        startActivity(new Intent(MainActivity.this, GamewallActivity.class));
//                        break;
//                    case 8:
//                        startActivity(new Intent(MainActivity.this, AllAdsActivity.class));
//                        break;
//                }
//
//            }
//
//            @SuppressWarnings("unused")
//            public void onClick(View v) {
//            };
//        });
    }

    @Override
    public void adWasShown(int placementId) {
        SALog.Log("adWasShown");
    }

    @Override
    public void adFailedToShow(int placementId) {
        SALog.Log("adFailedToShow");
    }

    @Override
    public void adWasClosed(int placementId) {
        SALog.Log("adWasClosed");
    }

    @Override
    public void adWasClicked(int placementId) {
        SALog.Log("adWasClicked");
    }

    @Override
    public void adHasIncorrectPlacement(int placementId) {
        SALog.Log("adHasIncorrectPlacement");
    }

    @Override
    public void parentalGateWasCanceled(int placementId) {
        SALog.Log("parentalGateWasCanceled");
    }

    @Override
    public void parentalGateWasFailed(int placementId) {
        SALog.Log("parentalGateWasFailed");
    }

    @Override
    public void parentalGateWasSucceded(int placementId) {
        SALog.Log("parentalGateWasSucceded");
    }

    @Override
    public void adStarted(int placementId) {
        SALog.Log("adStarted");
    }

    @Override
    public void videoStarted(int placementId) {
        SALog.Log("videoStarted");
    }

    @Override
    public void videoReachedFirstQuartile(int placementId) {
        SALog.Log("videoReachedFirstQuartile");
    }

    @Override
    public void videoReachedMidpoint(int placementId) {
        SALog.Log("videoReachedMidpoint");
    }

    @Override
    public void videoReachedThirdQuartile(int placementId) {
        SALog.Log("videoReachedThirdQuartile");
    }

    @Override
    public void videoEnded(int placementId) {
        SALog.Log("videoEnded");
    }

    @Override
    public void adEnded(int placementId) {
        SALog.Log("adEnded");
    }

    @Override
    public void allAdsEnded(int placementId) {
        SALog.Log("allAdsEnded");
    }
}
