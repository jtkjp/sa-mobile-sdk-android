package tv.superawesome.lib.saevents.events;

import android.content.Context;

import org.json.JSONObject;

import java.util.concurrent.Executor;

import tv.superawesome.lib.sajsonparser.SAJsonParser;
import tv.superawesome.lib.samodelspace.saad.SAAd;
import tv.superawesome.lib.sasession.session.ISASession;

public class SAImpressionEvent extends SAServerEvent {

    public SAImpressionEvent(Context context, SAAd ad, ISASession session) {
        super(context, ad, session);
    }

    public SAImpressionEvent(Context context, SAAd ad, ISASession session, Executor executor, int timeout, boolean isDebug) {
        super(context, ad, session, executor, timeout, isDebug);
    }

    @Override
    public String getEndpoint() {
        return "/impression";
    }

    @Override
    public JSONObject getQuery() {
        try {
            return SAJsonParser.newObject(
                    "placement", ad.placementId,
                    "creative", ad.creative.id,
                    "line_item", ad.lineItemId,
                    "sdkVersion", session.getVersion(),
                    "bundle", session.getPackageName(),
                    "ct", session.getConnectionType().ordinal(),
                    "no_image", true,
                    "rnd", session.getCachebuster(),
                    "type", "impressionDownloaded"
            );
        } catch (Exception e) {
            return new JSONObject();
        }
    }
}
