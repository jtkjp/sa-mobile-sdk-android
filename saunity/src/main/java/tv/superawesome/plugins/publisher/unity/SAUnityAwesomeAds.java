package tv.superawesome.plugins.publisher.unity;

import android.app.Activity;
import android.content.Context;

import org.json.JSONObject;

import tv.superawesome.sagdprisminorsdk.minor.models.GetIsMinorModel;
import tv.superawesome.sagdprisminorsdk.minor.process.GetIsMinorInterface;
import tv.superawesome.sdk.publisher.AwesomeAds;

/**
 * Created by gabriel.coman on 13/05/2018.
 */

public class SAUnityAwesomeAds {

    public static void SuperAwesomeUnityAwesomeAdsInit(Activity activity, boolean loggingEnabled) {
        AwesomeAds.init(activity.getApplication(), loggingEnabled);
    }

    public static void SuperAwesomeUnityAwesomeAdsTriggerAgeCheck(Context context, String dateOfBirth) {
        AwesomeAds.triggerAgeCheck(context, dateOfBirth, new GetIsMinorInterface() {
            @Override
            public void getIsMinorData(GetIsMinorModel getIsMinorModel) {

                GetIsMinorModel model = getIsMinorModel != null ? getIsMinorModel : new GetIsMinorModel();

                try {
                    JSONObject jsonObject = model.writeToJson();
                    SAUnityCallback.sendToUnity("AwesomeAds", jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                    SAUnityCallback.sendToUnity("AwesomeAds", new JSONObject());
                }
            }
        });
    }
}
