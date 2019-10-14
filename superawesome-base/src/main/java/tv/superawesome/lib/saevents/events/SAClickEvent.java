package tv.superawesome.lib.saevents.events;

import android.content.Context;

import org.json.JSONObject;

import java.util.concurrent.Executor;

import tv.superawesome.lib.sajsonparser.SAJsonParser;
import tv.superawesome.lib.samodelspace.saad.SAAd;
import tv.superawesome.lib.samodelspace.saad.SACreativeFormat;
import tv.superawesome.lib.sasession.session.ISASession;

public class SAClickEvent extends SAServerEvent {

    public SAClickEvent(Context context, SAAd ad, ISASession session) {
        super(context, ad, session);
    }

    public SAClickEvent(Context context, SAAd ad, ISASession session, Executor executor, int timeout, boolean isDebug) {
        super(context, ad, session, executor, timeout, isDebug);
    }

    @Override
    public String getEndpoint() {
        return ad != null && ad.creative != null ? ad.creative.format == SACreativeFormat.video ? "/video/click" : "/click" : "";
    }

    @Override
    public JSONObject getQuery () {
        try {
            return SAJsonParser.newObject(
                    "placement", ad.placementId,
                    "bundle", session.getPackageName(),
                    "creative", ad.creative.id,
                    "line_item", ad.lineItemId,
                    "ct", session.getConnectionType().ordinal(),
                    "sdkVersion", session.getVersion(),
                    "rnd", session.getCachebuster());
        } catch (Exception e) {
            return new JSONObject();
        }
    }
}
