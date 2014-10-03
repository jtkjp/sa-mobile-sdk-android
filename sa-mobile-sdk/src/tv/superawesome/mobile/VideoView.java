package tv.superawesome.mobile;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent.AdErrorListener;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent.AdEventListener;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsLoader.AdsLoadedListener;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.google.ads.interactivemedia.v3.samples.demoapp.player.DemoPlayer;
import com.google.ads.interactivemedia.v3.samples.demoapp.player.TrackingVideoView.CompleteCallback;

public class VideoView extends FrameLayout implements AdErrorListener, AdsLoadedListener, AdEventListener, CompleteCallback, Observer{
	
	private ImaSdkFactory sdkFactory;
	private AdsLoader adsLoader;
	protected ImaSdkSettings sdkSettings;
	protected AdsManager adsManager;
	protected DemoPlayer videoPlayer;
	protected VideoViewListener listener; 
	

	public VideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Log.v("SuperAwesome SDK", "VideoView created");
		
		videoPlayer = new DemoPlayer(context);
		videoPlayer.setCompletionCallback(this);
		this.addView(videoPlayer);

		sdkFactory = ImaSdkFactory.getInstance();
		adsLoader = sdkFactory.createAdsLoader(context, getImaSdkSettings());
		
		adsLoader.addAdErrorListener(this);
		adsLoader.addAdsLoadedListener(this);
		
		SuperAwesome.getInstance().addObserver(this);
		SuperAwesome.getInstance().setContext(context);
		
		if(!SuperAwesome.getInstance().getIsLoadingConfiguration()){
			requestAd();
		}
	}
	
	public void setListener(VideoViewListener listener){
	 this.listener = listener;
	}

	@Override
	public void onAdError(AdErrorEvent event) {
	  // An error occurred.
	  Log.v("SuperAwesome SDK", "Video Ad Error"+event.getError().getMessage());
	}

	@Override
	public void onAdsManagerLoaded(AdsManagerLoadedEvent event) {
	  // Ads were successfully loaded
	  Log.v("SuperAwesome SDK", "Video Ad Loaded "+event.toString());
	  adsManager = event.getAdsManager();
	  
	  adsManager = event.getAdsManager();
	  adsManager.addAdErrorListener(this);
	  adsManager.addAdEventListener(this);
	  adsManager.init();

	}
	
	protected void requestAd() {
	  AdDisplayContainer container = sdkFactory.createAdDisplayContainer();
	  container.setPlayer(videoPlayer);
	  container.setAdContainer(videoPlayer.getUiContainer());
	  
	  AdsRequest request = sdkFactory.createAdsRequest();
	  
	  Preroll preroll = SuperAwesome.getInstance().getPreroll();
	  if(preroll == null){
		  request.setAdTagUrl("http://u-ads.adap.tv/a/h/0A9CJKYei5h0XHk2xFl36YvivtZ9NIb4jSZkKFRs7AQ=?cb=%7Bcachebreaker%7D&pageUrl=http%3A%2F%2Fadap.tv&eov=eov");
	  }else{
		  request.setAdTagUrl(preroll.vast);
	  }
	  request.setAdDisplayContainer(container);
	  adsLoader.requestAds(request);
	  Log.v("SuperAwesome SDK", "Ad requested");
	}
	
	protected ImaSdkSettings getImaSdkSettings() {
	    if (sdkSettings == null) {
	      sdkSettings = sdkFactory.createImaSdkSettings();
	    }
	    return sdkSettings;
	}

	@Override
	public void onAdEvent(AdEvent event) {
		switch (event.getType()) {
	      case LOADED:
	    	Log.v("SuperAwesome SDK", "Calling start");
	    	if(listener != null){
	    		listener.onLoaded();
	    	}
	        break;
	      default:
			break;
		}
	}
	
	public void play(){
		adsManager.start();
	}

	@Override
	public void onComplete() {
		if(listener != null){
    		listener.onPLaybackCompleted();
    	}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		Log.v("SuperAwesome SDK", "observed");
		
		requestAd();
	}

}