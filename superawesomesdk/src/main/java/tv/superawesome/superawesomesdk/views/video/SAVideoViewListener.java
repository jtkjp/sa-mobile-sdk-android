package tv.superawesome.superawesomesdk.views.video;

import tv.superawesome.superawesomesdk.views.SAPlacementListener;

/**
 * Created by connor.leigh-smith on 10/07/15.
 */
public interface SAVideoViewListener extends SAPlacementListener {

    void onAdStart();

    void onAdPause();

    void onAdResume();

    void onAdFirstQuartile();

    void onAdMidpoint();

    void onAdThirdQuartile();

    void onAdComplete();

    void onAdClosed();

    void onAdSkipped();
}
