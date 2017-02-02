/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.sdk.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.superawesome.lib.saadloader.SALoader;
import tv.superawesome.lib.saadloader.SALoaderInterface;
import tv.superawesome.lib.saevents.SAEvents;
import tv.superawesome.lib.sajsonparser.SAJsonParser;
import tv.superawesome.lib.samodelspace.SAAd;
import tv.superawesome.lib.samodelspace.SACampaignType;
import tv.superawesome.lib.samodelspace.SACreativeFormat;
import tv.superawesome.lib.samodelspace.SAResponse;
import tv.superawesome.lib.sasession.SAConfiguration;
import tv.superawesome.lib.sasession.SASession;
import tv.superawesome.lib.sasession.SASessionInterface;
import tv.superawesome.lib.sautils.SAImageUtils;
import tv.superawesome.lib.sautils.SAUtils;
import tv.superawesome.sdk.SuperAwesome;

/**
 * Class that abstracts away the process of loading & displaying an App Wall type Ad.
 * A subclass of the Android "Activity" class.
 */
public class SAAppWall extends Activity {

    // private instance vars
    private SAResponse response = null;

    // parental gate
    private SAParentalGate gate;

    // a list of events to fire
    private List<SAEvents> events = new ArrayList<>();

    // static private vars used to communicate with the ad
    private static HashMap<Integer, Object> responses = new HashMap<>();
    private static SAInterface listener = new SAInterface() { @Override public void onEvent(int placementId, SAEvent event) {} };

    private static boolean isParentalGateEnabled    = SuperAwesome.getInstance().defaultParentalGate();
    private static boolean isTestingEnabled         = SuperAwesome.getInstance().defaultTestMode();
    private static boolean isBackButtonEnabled      = SuperAwesome.getInstance().defaultBackButton();
    private static SAConfiguration configuration    = SuperAwesome.getInstance().defaultConfiguration();

    /**
     * Overridden "onCreate" method, part of the Activity standard set of methods.
     * Here is the part where the activity / appwall gets configured
     *
     * @param savedInstanceState previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get local vars
        final SAInterface listenerL = getListener();
        final boolean isParentalGateEnabledL = getIsParentalGateEnabled();
        Bundle bundle = getIntent().getExtras();
        String responseStr = bundle.getString("response");
        response = new SAResponse(SAJsonParser.newObject(responseStr));

        // create the events array
        for (SAAd ad : response.ads) {
            SAEvents event = new SAEvents(this);
            event.setAd(ad);
            events.add(event);
        }

        // get resources (dynamically)
        float fp = SAUtils.getScaleFactor(this);

        // create the background - which will also serve as the content view for the app wall
        RelativeLayout background = new RelativeLayout(this);
        background.setBackgroundColor(Color.BLUE);
        background.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(background);

        // create the bg image
        ImageView bgImage = new ImageView(this);
        bgImage.setImageBitmap(SAImageUtils.gameWallBackground());
        bgImage.setScaleType(ImageView.ScaleType.FIT_XY);
        bgImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        background.addView(bgImage);

        // create the header holder
        RelativeLayout header = new RelativeLayout(this);
        RelativeLayout.LayoutParams headerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (fp * 107));
        header.setLayoutParams(headerParams);
        background.addView(header);

        // add the header background
        ImageView headerBg = new ImageView(this);
        headerBg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        headerBg.setImageBitmap(SAImageUtils.gameWallHeader());
        headerBg.setScaleType(ImageView.ScaleType.FIT_XY);
        header.addView(headerBg);

        // and the header title ("app wall")
        ImageView headerTitle = new ImageView(this);
        headerTitle.setImageBitmap(SAImageUtils.gameWallAppData());
        headerTitle.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams headerTitleParams = new RelativeLayout.LayoutParams((int)(200 * fp), (int)(40 * fp));
        headerTitleParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        headerTitle.setLayoutParams(headerTitleParams);
        header.addView(headerTitle);

        // and the padlock button
        ImageButton padlock = new ImageButton(this);
        padlock.setImageBitmap(SAImageUtils.padlockImage());
        padlock.setPadding(0, 0, 0, 0);
        padlock.setBackgroundColor(Color.TRANSPARENT);
        padlock.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams padlockParams = new RelativeLayout.LayoutParams((int) (83 * fp), (int) (31 * fp));
        padlockParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        padlockParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        padlock.setLayoutParams(padlockParams);
        padlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ads.superawesome.tv/v2/safead"));
                SAAppWall.this.startActivity(browserIntent);
            }
        });
        header.addView(padlock);

        // and the close button
        ImageButton close = new ImageButton(this);
        close.setImageBitmap(SAImageUtils.gameWallClose());
        close.setPadding(0, 0, 0, 0);
        close.setBackgroundColor(Color.TRANSPARENT);
        close.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams((int) (18 * fp), (int) (18 * fp));
        closeParams.setMargins(0, (int)(fp * 7), (int)(fp * 7), 0);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        close.setLayoutParams(closeParams);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        header.addView(close);

        // create adapter
        AppWallAdapter adapter = new AppWallAdapter(this, response.ads);

        // create the game grid
        GridView gameGrid = new GridView(this);
        gameGrid.setAdapter(adapter);
        gameGrid.setVerticalSpacing(0);
        gameGrid.setHorizontalSpacing(0);
        gameGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gameGrid.setNumColumns(response.ads.size() <= 3 ? 1 : 3);
        gameGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // check for PG
                SAAd ad = response.ads.get(position);
                if (isParentalGateEnabledL) {
                    gate = new SAParentalGate(SAAppWall.this, SAAppWall.this, ad, position);
                    gate.show();
                } else {
                    click(position);
                }
            }
        });
        RelativeLayout.LayoutParams gameGridParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        gameGridParams.setMargins(0, (int)(fp * 107), 0, 0);
        gameGrid.setLayoutParams(gameGridParams);
        background.addView(gameGrid);

        // send events
        for (SAEvents event : events) {
            event.sendEventsFor("impression");
            // event.sendEventsFor("sa_impr");
            event.sendViewableImpressionForDisplay(gameGrid);
        }

        // send event
        listenerL.onEvent(response.placementId, SAEvent.adShown);
    }

    /**
     * Overridden "onBackPressed" method of the activity
     * Depending on how the ad is customised, this will lock the back button or it will allow it.
     * If it allows it, it's going to also send an "adClosed" event back to the SDK user
     */
    @Override
    public void onBackPressed() {
        boolean isBackButtonEnabledL = getIsBackButtonEnabled();
        if (isBackButtonEnabledL) {
            SAInterface listenerL = getListener();
            listenerL.onEvent(response.placementId, SAEvent.adClosed);
            super.onBackPressed();
        }
    }

    /**********************************************************************************************
     * Custom instance methods
     **********************************************************************************************/

    /**
     * Method that handles a click on the ad surface
     *
     * @param position what cell of the AppWall was clicked
     */
    public void click (int position) {
        // get ad
        SAAd ad = response.ads.get(position);
        // get events
        SAEvents event = events.get(position);

        // get local
        SAInterface listenerL = getListener();
        SAConfiguration configurationL = getConfiguration();

        // callback
        listenerL.onEvent(response.placementId, SAEvent.adClicked);

        if (ad.campaignType == SACampaignType.CPI) {

            // send click counter event
            event.sendEventsFor("clk_counter");
            // send install event
            event.sendEventsFor("install");
            // send sa click counter event
            event.sendEventsFor("sa_tracking");

            // get click URL
            String clickUrl = ad.creative.clickUrl;
            clickUrl += "&referrer=";
            JSONObject referrerData = SAJsonParser.newObject(new Object[]{
                    "utm_source", configurationL.ordinal(),
                    "utm_campaign", ad.campaignId,
                    "utm_term", ad.lineItemId,
                    "utm_content", ad.creative.id,
                    "utm_medium", ad.placementId
            });
            String referrerQuery = SAUtils.formGetQueryFromDict(referrerData);
            referrerQuery = referrerQuery.replace("&", "%26");
            referrerQuery = referrerQuery.replace("=", "%3D");
            clickUrl += referrerQuery;

            Log.d("SuperAwesome", "Going to " + clickUrl);

            // open URL
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl));
            startActivity(browserIntent);
        }
    }

    /**
     * Method that handles the app wall close button being pressed
     */
    private void close() {

        // get local
        SAInterface listenerL = getListener();

        // call listener
        listenerL.onEvent(response.placementId, SAEvent.adClosed);

        // delete the response
        responses.remove(response.placementId);

        // close
        this.finish();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

    }

    /**********************************************************************************************
     * Public class interface
     **********************************************************************************************/

    /**
     * Static method that loads an ad into the App wall queue. Ads can only be loaded once and then
     * can be reloaded after they've been played.
     *
     * @param placementId   the Ad placement id to load data for
     * @param context       the current context
     */
    public static void load (final int placementId, Context context) {

        // if the ad data for the placement id doesn't existing in the "ads" hash map, then
        // proceed with loading it
        if (!responses.containsKey(placementId)) {

            // set a placeholder
            responses.put(placementId, new Object());

            // create a loader
            final SALoader loader = new SALoader(context);

            // create a current session
            final SASession session = new SASession (context);
            session.setTestMode(isTestingEnabled);
            session.setConfiguration(configuration);
            session.setVersion(SuperAwesome.getInstance().getSDKVersion());
            session.prepareSession(new SASessionInterface() {
                @Override
                public void didFindSessionReady() {
                    // after session is OK - start loding
                    loader.loadAd(placementId, session, new SALoaderInterface() {
                        @Override
                        public void saDidLoadAd(SAResponse response) {

                            // put the correct value
                            if (response.isValid()) {
                                responses.put(placementId, response);
                            }
                            // remove existing
                            else {
                                responses.remove(placementId);
                            }

                            // call listener
                            listener.onEvent(placementId, response.isValid () ? SAEvent.adLoaded : SAEvent.adFailedToLoad);
                        }
                    });
                }
            });

        }
        // else if the ad data for the placement exists in the "ads" hash map, then notify the
        // user that it already exists and he should just play it
        else {
            listener.onEvent(placementId, SAEvent.adAlreadyLoaded);
        }

    }

    /**
     * Static method that returns whether ad data for a certain placement has already been loaded
     *
     * @param placementId   the Ad placement id to check for
     * @return              true or false
     */
    public static boolean hasAdAvailable (int placementId) {
        Object object = responses.get(placementId);
        return object != null && object instanceof SAResponse;
    }

    /**
     * Static method that, if an ad data is loaded, will play the content for the user
     *
     * @param placementId   the Ad placement id to play an ad for
     * @param context       the current context (activity or fragment)
     */
    public static void play(int placementId, Context context) {

        // try to get the ad that fits the placement id
        SAResponse responseL = (SAResponse) responses.get(placementId);

        // try to start the activity
        if (responseL != null && responseL.format == SACreativeFormat.appwall && context != null) {
            Intent intent = new Intent(context, SAAppWall.class);
            intent.putExtra("response", responseL.writeToJson().toString());
            context.startActivity(intent);
        } else {
            listener.onEvent(placementId, SAEvent.adFailedToShow);
        }
    }

    /**********************************************************************************************
     * Setters & Getters
     **********************************************************************************************/

    public static void setListener(SAInterface value) {
        listener = value != null ? value : listener;
    }

    public static void enableParentalGate () {
        setParentalGate(true);
    }

    public static void disableParentalGate () {
        setParentalGate(false);
    }

    public static void enableTestMode () {
        setTestMode(true);
    }

    public static void disableTestMode () {
        setTestMode(false);
    }

    public static void setConfigurationProduction () {
        setConfiguration(SAConfiguration.PRODUCTION);
    }

    public static void setConfigurationStaging () {
        setConfiguration(SAConfiguration.STAGING);
    }

    public static void enableBackButton () {
        setBackButton(true);
    }

    public static void disableBackButton () {
        setBackButton(false);
    }

    // private static methods to handle static vars

    private static SAInterface getListener () {
        return listener;
    }

    private static boolean getIsTestEnabled () {
        return isTestingEnabled;
    }

    private static boolean getIsParentalGateEnabled () {
        return isParentalGateEnabled;
    }

    private static SAConfiguration getConfiguration () {
        return configuration;
    }

    private static boolean getIsBackButtonEnabled () {
        return isBackButtonEnabled;
    }

    public static void setParentalGate (boolean value) {
        isParentalGateEnabled = value;
    }

    public static void setTestMode (boolean value) {
        isTestingEnabled = value;
    }

    public static void setConfiguration (SAConfiguration value) {
        configuration = value;
    }

    public static void setBackButton (boolean value) {
        isBackButtonEnabled = value;
    }

    /**********************************************************************************************
     * The AppWall adapter
     **********************************************************************************************/

    /**
     * Class that us used as an adapter for the AppWall to display cells in a matrix
     */
    public class AppWallAdapter extends BaseAdapter {

        // private variables
        private Context context = null;
        private List<SAAd> ads = new ArrayList<>();

        /**
         * Constructor with a context and a list of ads
         *
         * @param context   current context (activity or fragment)
         * @param ads       a list of ads to populate the data from
         */
        AppWallAdapter(Context context, List<SAAd> ads){
            this.context = context;
            this.ads = ads;
        }

        /**
         * Overridden adapter method to return the number of cells to display
         *
         * @return the length of the ads array
         */
        @Override
        public int getCount () {
            return ads.size();
        }

        /**
         * Overridden adapter method to return the position
         *
         * @param position  item position
         * @return          null
         */
        @Override
        public Object getItem (int position) {
            return null;
        }

        /**
         * Overridden adapter method that returns the item id
         *
         * @param position  item position
         * @return          item id
         */
        @Override
        public long getItemId (int position) {
            return 0;
        }

        /**
         * Overridden adapter method that returns the current view to display
         *
         * @param position      view position
         * @param convertView   convert view
         * @param parent        parent of the view
         * @return              a customised view for the app wall
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;

            // get elements that won't change
            SAAd ad = ads.get(position);
            int size = ads.size();

            // do big layout
            if (size <= 3) {

                // get actual image w & h
                final float density = context.getResources().getDisplayMetrics().density;
                float radius = 15 * density;
                float fp = SAUtils.getScaleFactor((Activity)context);

                // create the main view of the adapter cell view
                v = new LinearLayout(context);
                AbsListView.LayoutParams pvParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                v.setLayoutParams(pvParams);
                ((LinearLayout) v).setOrientation(LinearLayout.HORIZONTAL);

                // add to it a relative layout half the width of the screen, to hold the icon
                SAUtils.SASize screenW = SAUtils.getRealScreenSize((Activity)context, true);
                RelativeLayout iconHolder = new RelativeLayout(context);
                RelativeLayout.LayoutParams iconHolderParams = new RelativeLayout.LayoutParams(screenW.height / 2, (int)(fp * 160));
                iconHolder.setLayoutParams(iconHolderParams);
                ((LinearLayout) v).addView(iconHolder);

                // and add to the icon holder a background with white rounded background
                Bitmap newBitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(newBitmap);
                canvas.drawColor(0xffffffff);
                StreamDrawable newBitmapDrawable = new StreamDrawable(newBitmap, radius, 0);

                RelativeLayout iconBg= new RelativeLayout(context);
                RelativeLayout.LayoutParams iconBgLayoutParams = new RelativeLayout.LayoutParams((int)(fp * 120), (int) (fp * 120));
                iconBg.setBackgroundDrawable(newBitmapDrawable);
                iconBgLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                iconBg.setLayoutParams(iconBgLayoutParams);
                iconHolder.addView(iconBg);

                // and to that an app icon
                ImageView appIcon = new ImageView(context);
                RelativeLayout.LayoutParams appIconParams = new RelativeLayout.LayoutParams((int)(fp * 114), (int)(fp * 114));
                appIconParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                appIcon.setLayoutParams(appIconParams);
                File file = new File(context.getFilesDir(), ad.creative.details.media.playableDiskUrl);
                if (file.exists()) {
                    String fileUrl = file.toString();
                    Bitmap bitmap = BitmapFactory.decodeFile(fileUrl);
                    int appIconW = (int) (114 * density);
                    int appIconH = (int) (114 * density);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, appIconW, appIconH, true);
                    StreamDrawable drawable = new StreamDrawable(scaledBitmap, radius, 0);
                    appIcon.setImageDrawable(drawable);
                }
                iconBg.addView(appIcon);

                // in the other half of the adapter cell view put a text view
                TextView appName = new TextView(context);
                appName.setTextColor(Color.WHITE);
                appName.setTextSize(22);
                appName.setGravity(Gravity.CENTER_VERTICAL);
                appName.setText(ad.creative.name != null ? ad.creative.name : "Undefined");
                LinearLayout.LayoutParams appNameParams = new LinearLayout.LayoutParams(screenW.height / 2, (int)(fp * 160));
                appName.setLayoutParams(appNameParams );
                ((LinearLayout) v).addView(appName);

            }
            // do small layout
            else {

                float fp = SAUtils.getScaleFactor((Activity)context);
                final float density = context.getResources().getDisplayMetrics().density;
                float radius = 15 * density;

                // create the main view of the adapter cell view
                v = new LinearLayout(context);
                AbsListView.LayoutParams vParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                v.setLayoutParams(vParams);
                ((LinearLayout) v).setOrientation(LinearLayout.VERTICAL);

                // create an icon holder
                RelativeLayout iconHolder = new RelativeLayout(context);
                iconHolder.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(fp * 94)));
                ((LinearLayout) v).addView(iconHolder);

                // and the icon background
                Bitmap newBitmap = Bitmap.createBitmap(90, 90, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(newBitmap);
                canvas.drawColor(0xffffffff);
                StreamDrawable newBitmapDrawable = new StreamDrawable(newBitmap, radius, 0);

                RelativeLayout iconBg = new RelativeLayout(context);
                RelativeLayout.LayoutParams iconBgParams = new RelativeLayout.LayoutParams((int)(fp * 90), (int) (fp * 90));
                iconBg.setBackgroundDrawable(newBitmapDrawable);
                iconBgParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                iconBg.setLayoutParams(iconBgParams);
                iconHolder.addView(iconBg);

                // and the actual icon
                ImageView appIcon = new ImageView(context);
                RelativeLayout.LayoutParams appIconParams = new RelativeLayout.LayoutParams((int)(fp * 84), (int)(fp * 84));
                appIconParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                appIcon.setLayoutParams(appIconParams);
                File file = new File(context.getFilesDir(), ad.creative.details.media.playableDiskUrl);
                if (file.exists()) {
                    String fileUrl = file.toString();
                    Bitmap bitmap = BitmapFactory.decodeFile(fileUrl);
                    int appIconW = (int) (84 * density);
                    int appIconH = (int) (84 * density);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, appIconW, appIconH, true);
                    StreamDrawable drawable = new StreamDrawable(scaledBitmap, radius, 0);
                    appIcon.setImageDrawable(drawable);
                }
                iconBg.addView(appIcon);

                // now add a label underneath
                TextView appName = new TextView(context);
                appName.setTextColor(Color.WHITE);
                appName.setTextSize(15);
                appName.setGravity(Gravity.CENTER);
                appName.setText(ad.creative.name != null ? ad.creative.name : "Undefined");
                RelativeLayout.LayoutParams appNameParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                appNameParams.setMargins(0, (int)(10 * fp), 0, 0);
                appName.setLayoutParams(appNameParams);
                ((LinearLayout) v).addView(appName);
            }

            return v;
        }
    }

    /**********************************************************************************************
     * StreamDrawable class to paint rounded-edges icons on the screen
     **********************************************************************************************/

    /**
     * Class that overrides Drawable to paint an image with rounded corners
     */
    class StreamDrawable extends Drawable {

        // instance members
        private final float mCornerRadius;
        private final RectF mRect = new RectF();
        private final BitmapShader mBitmapShader;
        private final Paint mPaint;
        private final int mMargin;

        /**
         * Constructor that takes a bitmap, radius and margin
         *
         * @param bitmap        current bitmap to paint
         * @param cornerRadius  the corner radius to add
         * @param margin        the margin to add
         */
        StreamDrawable(Bitmap bitmap, float cornerRadius, int margin) {
            mCornerRadius = cornerRadius;
            mMargin = margin;
            mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setShader(mBitmapShader);
        }

        /**
         * Overridden Drawable method that repaints the image when the bounds change
         *
         * @param bounds a Rect of bounds
         */
        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mRect.set(mMargin, mMargin, bounds.width() - mMargin, bounds.height() - mMargin);
        }

        /**
         * Overridden Drawable method that draws on a canvas
         *
         * @param canvas the current canvas
         */
        @Override
        public void draw(Canvas canvas) {
            canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mPaint);
        }

        /**
         * Overridden Drawable method that gets the opacity
         *
         * @return a type of pixel format
         */
        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        /**
         * Overridden Drawable method that sets the transparency
         *
         * @param alpha the current alpha blending factor
         */
        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        /**
         * Overidden Drawable method that sets the color filter
         *
         * @param cf the color filter
         */
        @Override
        public void setColorFilter(ColorFilter cf) {
            mPaint.setColorFilter(cf);
        }

    }
}
