/**
 * @class: SAGet.java
 * @copyright: (c) 2015 SuperAwesome Ltd. All rights reserved.
 * @author: Gabriel Coman
 * @date: 28/09/2015
 *
 */
package tv.superawesome.lib.sanetwork;

/**
 * Needed imports for the implementation
 */
import javax.net.ssl.HttpsURLConnection;
import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.SSLContext;
import tv.superawesome.lib.sautils.*;

/**
 * In Java SAGet is a subclass of AsyncTask
 * to make a GET request to the server
 */
public class SAGet extends AsyncTask<String, Integer, String> {

    /** Private SAGet variables */
    private HttpsURLConnection connection = null;
    private InputStream in = null;
    private BufferedReader reader = null;
    private SSLContext sc = null;

    /** Private net listener */
    private SANetListener listener;
    private String URL = null;

    /**
     *
     * @param url - the URL to get the data from
     * @param listener - a reference to the SANetListner object
     */
    public void execute (String url, SANetListener listener) {
        /** assign listener reference */
        this.listener = listener;

        /** call the super execute */
        super.execute(url);
    }

    /**
     *
     * @param url - a string from where to GET the data
     * @return the contents of the data at the URL given by param
     */
    @Override
    protected String doInBackground(String[] url) {

        // assign URL
        this.URL = url[0];

        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        httpGet.setHeader("User-Agent", SAUserAgent.getUserAgent());

        HttpResponse response = null;
        try {
            response = client.execute(httpGet );
            SALog.Log(response.toString());
        } catch (IOException e ) {
            handleError(e);
        }

        String responseString = null;
        try {
            responseString = new BasicResponseHandler().handleResponse(response);
        } catch (IOException e) {
            handleError(e);
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String response) {
        /** send the success response, with a string as the object */
        if (listener != null) {
            listener.success(response);
        }
    }

    /**
     * this function will be called everytime the SAGet class should return
     * some kind of error
     * @param e - the exception
     */
    private void handleError(Exception e) {
        SALog.Err("Error to " + this.URL);

        /** print stack */
        if (e != null) {
            e.printStackTrace();
        }

        /** call failure, if listner exists (and normally, it should exist) */
        if (listener != null) {
            listener.failure();
        }
    }
}
