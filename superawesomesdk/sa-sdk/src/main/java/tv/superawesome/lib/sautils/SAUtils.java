/**
 * @class: SAUtils.java
 * @package: tv.superawesome.sdk.aux
 * @copyright: (c) 2015 SuperAwesome Ltd. All rights reserved.
 * @author: Gabriel Coman
 * @date: 28/09/2015
 *
 */

/**
 * packaged and imports for this class
 */
package tv.superawesome.lib.sautils;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

import tv.superawesome.lib.sanetwork.SAApplication;

/**
 * Class that contains a lot of static aux functions
 */
public class SAUtils {

    /**
     * Function that returns a random number between two limits
     * @param min - min edge
     * @param max - max edge
     * @return a random integer
     */
    public static int randomNumberBetween(int min, int max){
        Random rand  = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

    /**
     * return true if json is empty, false otherwise
     * @param dict a json dict
     */
    public static boolean isJSONEmpty(JSONObject dict) {
        if (dict == null) return true;
        if (dict.length() == 0) return true;
        if (dict.toString().equals("{}")) return true;
        return false;
    }

    /**
     * Function that checks if the value of an string is actually integer
     * @param s
     * @return boolean
     */
    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    private static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    /**
     * Get an .txt/.html file from the Assets folder and return a String with its content
     * @param assetPath - the path to the asset
     * @return - the string contents
     */

    public static String openAssetAsString(String assetPath) throws IOException {
        /** create the input streams and all that stuff */
        StringBuilder builder = new StringBuilder();
        InputStream text = SAApplication.getSAApplicationContext().getAssets().open(assetPath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(text, "UTF-8"));

        /** go through all the file and append */
        String str;
        while ((str = reader.readLine()) != null) {
            builder.append(str);
        }

        /** close the reader */
        reader.close();

        /** return the new string */
        return builder.toString();
    }


    /**
     * Dynamically returns a resource Id
     * @param name the name of the resource
     * @param type the type of the resource
     * @param context the context
     * @return returns the actual ID or 0
     */
    public static int getResourceIdByName(String name, String type, Activity context){
        if (SAApplication.getSAApplicationContext() != null){
            String packageName = SAApplication.getSAApplicationContext().getPackageName();
            return context.getResources().getIdentifier(name, type, packageName);
        } else {
            return 0;
        }
    }

    /**
     * Returns a string by name
     * @param name the name of the string
     * @param context the current context
     * @return the String
     */
    public static String getStringByName(String name, Activity context){
        int id = getResourceIdByName(name, "string", context);
        return context.getResources().getString(id);
    }
//    public static int getResourceIdByName(String packageName, String className, String name) {
//
//        Class r = null;
//        int id = 0;
//        try {
//            r = Class.forName(packageName + ".R");
//
//            Class[] classes = r.getClasses();
//            Class desireClass = null;
//
//            for (int i = 0; i < classes.length; i++) {
//                if (classes[i].getName().split("\\$")[1].equals(className)) {
//                    desireClass = classes[i];
//
//                    break;
//                }
//            }
//
//            if (desireClass != null) {
//                id = desireClass.getField(name).getInt(desireClass);
//            }
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//
//        return id;
//    }

    public static Rect arrangeAdInNewFrame(float newW, float newH, float oldW, float oldH) {
        if (oldW == 1 || oldW == 0) { oldW = newW; }
        if (oldH == 1 || oldH == 0) { oldH = newH; }

        float oldR = oldW / oldH;
        float newR = newW / newH;

        float X = 0, Y = 0, W = 0, H = 0;

        if (oldR > newR) {
            W = newW;
            H = W / oldR; // or oldH * oldR
            X = 0;
            Y = (newH - H) / 2.0f;
        }
        else {
            H = newH;
            W = H * oldR;
            Y = 0;
            X = (newW - W) / 2.0f;
        }

        return new Rect((int)X, (int)Y, (int)W, (int)H);
    }

    /**
     * Get the current scale factor
     * @param activity - the activity to pass along as context
     * @return a float meaning the scale
     */
    public static float getScaleFactor(Activity activity){
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        return  (float) metrics.densityDpi / (float) DisplayMetrics.DENSITY_DEFAULT;
    }
}
