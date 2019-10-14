package tv.superawesome.demoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import tv.superawesome.lib.sabumperpage.SABumperPage;
import tv.superawesome.lib.sasession.defines.SARTBStartDelay;
import tv.superawesome.sagdprisminorsdk.minor.models.GetIsMinorModel;
import tv.superawesome.sagdprisminorsdk.minor.process.GetIsMinorInterface;
import tv.superawesome.sdk.publisher.AwesomeAds;
import tv.superawesome.sdk.publisher.SABannerAd;
import tv.superawesome.sdk.publisher.SAEvent;
import tv.superawesome.sdk.publisher.SAInterface;
import tv.superawesome.sdk.publisher.SAInterstitialAd;
import tv.superawesome.sdk.publisher.SAVideoAd;

public class MainActivity extends Activity {

    /**
     * the options list
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SABumperPage.overrideName("Test app");

        final SABannerAd myBanner = findViewById(R.id.MyBanner);
        myBanner.setConfigurationProduction();
        myBanner.enableBumperPage();
        myBanner.enableParentalGate();
        myBanner.enableTestMode();
        myBanner.disableMoatLimiting();
        myBanner.setListener(new SAInterface() {
            @Override
            public void onEvent(int placementId, SAEvent event) {

                Log.d("SADefaults", "BANNER AD: " + placementId + " -> Event : " + event);

                if (event == SAEvent.adLoaded) {
                    myBanner.play(MainActivity.this);
                }
            }
        });

        SAInterstitialAd.setConfigurationProduction();
        SAInterstitialAd.enableParentalGate();
        SAInterstitialAd.enableBumperPage();
        SAInterstitialAd.enableBackButton();
        SAInterstitialAd.enableTestMode();
        SAInterstitialAd.disableMoatLimiting();
        SAInterstitialAd.setListener(new SAInterface() {
            @Override
            public void onEvent(int placementId, SAEvent event) {

                Log.d("SADefaults", "INTERSTITIAL AD: " + placementId + " -> Event : " + event);

                if (event == SAEvent.adLoaded) {
                    SAInterstitialAd.play(placementId, MainActivity.this);
                }
            }
        });

        SAVideoAd.setConfigurationProduction();
        SAVideoAd.enableParentalGate();
        SAVideoAd.enableBumperPage();
        SAVideoAd.enableTestMode();
        SAVideoAd.disableMoatLimiting();
        SAVideoAd.enableCloseAtEnd();
        SAVideoAd.enableCloseButton();
        SAVideoAd.setPlaybackMode(SARTBStartDelay.POST_ROLL);
        SAVideoAd.enableBackButton();
        SAVideoAd.setListener(new SAInterface() {
            @Override
            public void onEvent(int placementId, SAEvent event) {

                Log.d("SADefaults", "VIDEO AD: " + placementId + " -> Event : " + event);

                if (event == SAEvent.adLoaded) {
                    SAVideoAd.play(placementId, MainActivity.this);
                    SAVideoAd.play(placementId, MainActivity.this);
                }
            }
        });

        ListView myList = findViewById(R.id.MyList);
        final List<AdapterItem> data = Arrays.asList(
                new HeaderItem("Banners"),
                new PlacementItem("Image Banner", 36508, Type.BANNER),
                new PlacementItem("Rich Media Banner", 36508, Type.BANNER),
                new PlacementItem("3rd Party Tag Banner", 36508, Type.BANNER),
                new HeaderItem("Interstitials"),
                new PlacementItem("Image Interstitial", 36510, Type.INTERSTITIAL),
                new PlacementItem("Rich Media Interstitial", 36510, Type.INTERSTITIAL),
                new PlacementItem("3rd Party Tag Interstitial", 36510, Type.INTERSTITIAL),
                new HeaderItem("Videos"),
                new PlacementItem("Direct video", 39521, Type.VIDEO),
                new PlacementItem("Programmatic video", 39521, Type.VIDEO)
        );
        ListAdapter<AdapterItem> adapter = new ListAdapter<>(this);
        myList.setAdapter(adapter);
        adapter.updateData(data);
        adapter.reloadList();

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AdapterItem item = data.get(position);
                if (item instanceof PlacementItem) {
                    PlacementItem placement = (PlacementItem) item;

                    switch (placement.type) {
                        case BANNER:
                            myBanner.load(placement.pid);
                            break;
                        case INTERSTITIAL:
                            SAInterstitialAd.load(placement.pid, MainActivity.this);
                            break;
                        case VIDEO: {
                            if (SAVideoAd.hasAdAvailable(placement.pid)) {
                                Log.e("AwesomeAds", "PLAYING VIDEO");
                                SAVideoAd.play(placement.pid, MainActivity.this);
                            } else {
                                Log.e("AwesomeAds", "LOADING VIDEO");
                                SAVideoAd.load(placement.pid, MainActivity.this);
                            }
                            break;
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void gotoAdMob(View view) {
        Intent intent = new Intent(this, AdMobActivity.class);
        this.startActivity(intent);
    }

    public void gotoMoPub(View view) {
        Intent intent = new Intent(this, MoPubActivity.class);
        this.startActivity(intent);
    }

    public void ageCheck(View view) {

        final String dateOfBirth = "2012-02-02";

        AwesomeAds.triggerAgeCheck(this, dateOfBirth, new GetIsMinorInterface() {
            @Override
            public void getIsMinorData(GetIsMinorModel isMinorModel) {

                String message;
                if (isMinorModel != null) {
                    message = "Min age for '" + isMinorModel.getCountry() + "' is '"
                            + isMinorModel.getConsentAgeForCountry() + "' "
                            + "\nThe age is '" + isMinorModel.getAge() + "' "
                            + "'.\nIs '" + dateOfBirth + "' a minor? -> '" + String.valueOf(isMinorModel.isMinor()) + "'";
                } else {
                    message = "Oops! Something went wrong. No valid model for 'Age Check'...";
                }

                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}

enum Type {
    BANNER,
    INTERSTITIAL,
    VIDEO
}

class AdapterItem {
    // do nothing
}

class HeaderItem extends AdapterItem {
    public String title;

    public HeaderItem(String title) {
        this.title = title;
    }
}

class PlacementItem extends AdapterItem {
    public String name;
    public int pid;
    public Type type;

    public PlacementItem(String name, int pid, Type type) {
        this.name = name;
        this.pid = pid;
        this.type = type;
    }
}

class ListAdapter<T extends AdapterItem> extends ArrayAdapter<T> {

    private List<T> data;

    public ListAdapter(Context context) {
        super(context, 0);
    }

    public void updateData(List<T> newData) {
        data = newData;
    }

    public void reloadList() {
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public T getItem(int i) {
        return data.get(i);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int i) {
        return getItem(i) instanceof HeaderItem ? 0 : 1;
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_placement, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.RowTitle);

        T item = getItem(position);

        if (item instanceof HeaderItem) {
            HeaderItem header = (HeaderItem) item;
            title.setText(header.title);
            convertView.setBackgroundColor(Color.LTGRAY);
        } else {
            PlacementItem placement = (PlacementItem) item;
            title.setText(placement.pid + " | " + placement.name);
            title.setBackgroundColor(Color.WHITE);
        }


        return convertView;
    }
}
