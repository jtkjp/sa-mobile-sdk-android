/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.savideoplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tv.superawesome.lib.sautils.SAImageUtils;

/**
 * Class that represents a very simple media controller, based on a relative layout skeleton.
 * Main purpose is to show a chronograph, a shor more button and a bottom semi-transparent mask.
 */
public class SAMediaController extends RelativeLayout {

    // constants
    private final static String CHRONO_INIT_TXT = "Ad: 0";
    private final static String FIND_OUT_MORE_TXT = "Find out more »";

    // private vars w/ public interface
    private boolean shouldShowSmallClickButton = false;

    // mask, chrono & shor more button views
    private ImageView mask;
    public ImageView chronoBg;
    public TextView chronograph;
    public Button showMore;

    // current context and scale
    private Context context = null;
    private float scale = 0.0f;

    /**
     * Main constructor
     *
     * @param context current context (activity or fragment)
     */
    public SAMediaController(Context context) {
        this(context, null, 0);
    }

    /**
     * Constructor with context and attribute sets
     *
     * @param context current context (activity or fragment)
     * @param attrs   attributes for the web player
     */
    public SAMediaController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Main constructor with context, attribute sets and default style
     *
     * @param context       current context (activity or fragment)
     * @param attrs         attributes for the web player
     * @param defStyleAttr  default style (usually 0)
     */
    public SAMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // get context
        this.context = context;

        // calc current scale
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        scale = (float)metrics.densityDpi / 160.0F;
    }

    /**
     * Main initialization method
     */
    public void initialize () {

        // create the background image
        mask = new ImageView(context);
        mask.setImageBitmap(SAImageUtils.createVideoGradientBitmap());
        mask.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams maskLay = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(31 * scale));
        maskLay.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(mask, maskLay);

        // create the chrono Bg
        chronoBg = new ImageView(context);
        chronoBg.setImageBitmap(SAImageUtils.createBitmap(100, 52, 0xFFFFFFFF, 10.0f));
        chronoBg.setScaleType(ImageView.ScaleType.FIT_XY);
        chronoBg.setAlpha(0.3f);
        RelativeLayout.LayoutParams chronoBgParams = new LayoutParams((int)(50*scale), (int)(26*scale));
        chronoBgParams.addRule(ALIGN_PARENT_BOTTOM);
        chronoBgParams.setMargins((int)(5*scale), 0, 0, (int)(5*scale));
        addView(chronoBg, chronoBgParams);

        // create the timer label
        chronograph = new TextView(context);
        chronograph.setText(CHRONO_INIT_TXT);
        chronograph.setTextColor(Color.WHITE);
        chronograph.setTextSize(11);
        chronograph.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams chronoLay = new RelativeLayout.LayoutParams((int)(50*scale), (int)(26*scale));
        chronoLay.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        chronoLay.setMargins((int)(5*scale), 0, 0, (int)(5*scale));
        addView(chronograph, chronoLay);

        // create the show more button
        if (shouldShowSmallClickButton) {
            showMore = new Button(context);
            showMore.setTransformationMethod(null);
            showMore.setText(FIND_OUT_MORE_TXT);
            showMore.setTextColor(Color.WHITE);
            showMore.setTextSize(12);
            showMore.setBackgroundColor(Color.TRANSPARENT);
            showMore.setGravity(Gravity.CENTER_VERTICAL);
            showMore.setPadding((int) (65 * scale), 0, 0, 0);
            RelativeLayout.LayoutParams showLay = new RelativeLayout.LayoutParams((int)(200*scale), (int)(26*scale));
            showLay.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            showLay.setMargins(0, 0, 0, (int)(5*scale));
            addView(showMore, showLay);
        } else {
            showMore = new Button(context);
            showMore.setTransformationMethod(null);
            showMore.setBackgroundColor(Color.TRANSPARENT);
            RelativeLayout.LayoutParams showLay = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(showMore, showLay);
        }
    }

    /**
     * Method that tells the media controller to show or hide the small click button
     *
     * @param value true or false
     */
    public void setShouldShowSmallClickButton (boolean value) {
        shouldShowSmallClickButton = value;
    }

    /**
     * Method that sets the new chronograph text
     *
     * @param text the text for the cronographer
     */
    public void setChronographText(String text) {
        if (chronograph != null) {
            chronograph.setText(text);
        }
    }

    /**
     * Setter for the listener
     *
     * @param listener - the listener for video callbacks
     */
    public void setClickListener (SAVideoPlayerClickInterface listener) {
        showMore.setOnClickListener(listener);
    }
}
