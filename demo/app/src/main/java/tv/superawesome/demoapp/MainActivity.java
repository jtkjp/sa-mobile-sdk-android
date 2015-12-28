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
import tv.superawesome.sdk.views.SAParentalGate;
import tv.superawesome.sdk.views.SAVideoActivity;

public class MainActivity extends Activity implements SAAdListener, SAParentalGateListener, SAVideoAdListener {

    private SAAdListener adListener = this;
    private SAParentalGateListener parentalGateListener = this;
    private SAVideoAdListener videoAdListener = this;

    private SAParentalGate parentalGate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SuperAwesome.getInstance().setConfigurationProduction();
        SuperAwesome.getInstance().enableTestMode();
        SALog.Log(SuperAwesome.getInstance().getSDKVersion());
        SALog.Log(SASystem.getVerboseSystemDetails());
    }

    /** Open buttons */
    public void showVideo1(View v) {
        SALog.Log("CDE");
        SALoader.loadAd(21022, new SALoaderListener() {
            @Override
            public void didLoadAd(SAAd ad) {
                SAVideoActivity.start(MainActivity.this, ad, false, adListener, parentalGateListener, videoAdListener);
            }

            @Override
            public void didFailToLoadAdForPlacementId(int placementId) {
                SALog.Log("Failed to load " + placementId);
            }
        });
    }

    public void showVideo2(View v){
        SALog.Log("ABC");
        SALoader.loadAd(30245, new SALoaderListener() {
            @Override
            public void didLoadAd(SAAd ad) {
                SAVideoActivity.start(MainActivity.this, ad, true, adListener, parentalGateListener, videoAdListener);
            }

            @Override
            public void didFailToLoadAdForPlacementId(int placementId) {
                SALog.Log("Failed to load " + placementId);
            }
        });
    }

    public void showPG(View v) {
        parentalGate = new SAParentalGate(MainActivity.this, null);
        parentalGate.show();
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
