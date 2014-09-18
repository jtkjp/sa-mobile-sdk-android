package tv.superawesome.mobile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;


public class SettingsAsyncTask extends AsyncTask<String, String, List<Placement>> {
	
	public ISettingsResponse delegate=null;
	private String response;
	private List<Placement> placements;
	
	 private String readStream(InputStream is) {
	    try {
	      ByteArrayOutputStream bo = new ByteArrayOutputStream();
	      int i = is.read();
	      while(i != -1) {
	        bo.write(i);
	        i = is.read();
	      }
	      return bo.toString();
	    } catch (IOException e) {
	      return "";
	    }
	}
	 
	 private void writeStream(OutputStream outputstream, String s) throws IOException
    {
        outputstream.write(s.getBytes());
        outputstream.close();
    }
	 
	private boolean redirected(HttpURLConnection conn) throws IOException{
		int status = conn.getResponseCode();
    	if (status != HttpURLConnection.HTTP_OK) {
    		if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == 307)
    		return true;
    	}
    	return false;
	}
	
	private void postData(String appId) throws JSONException {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://dashboard.superawesome.tv/api/sdk/ads/");

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("app_id", appId));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        this.response = readStream(response.getEntity().getContent());
	        
	        Log.d("SuperAwesome SDK", "response: "+this.response);
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	}
	
	private void processResponse() throws JSONException{
		JSONObject json = new JSONObject(this.response);
        boolean success = (Boolean) json.get("success");
        JSONArray ads = json.getJSONArray("ads");
        placements = new ArrayList<Placement>();
        for (int i = 0; i < ads.length(); i++) {
    	  JSONObject ad = ads.getJSONObject(i);
    	  Placement placement = new Placement();
    	  placement.name = ad.getString("name");
    	  placement.networkId = ad.getInt("networkId");
    	  placement.subNetworkId = ad.getInt("subNetworkId");
    	  placement.alias = ad.getString("alias");
    	  placement.width = ad.getInt("width");
    	  placement.height = ad.getInt("height");
    	  placements.add(placement);
    	  Log.d("SuperAwesome SDK", "ad: "+placement.alias+ " w:"+placement.width+ " h:"+placement.height);
    	}
	}
	
	@Override
	protected List<Placement> doInBackground(String... params) {
		try {
			postData(params[0]);
			processResponse();
			delegate.receivedPlacements(placements);
			return placements;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}