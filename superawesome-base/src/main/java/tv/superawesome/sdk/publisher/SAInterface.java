/**
 * @Copyright:   SADefaults Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.sdk.publisher;

import java.io.Serializable;

/**
 * General interface through which ads send back events to the SDK user
 */
public interface SAInterface extends Serializable {

    /**
     * Only method that needs to be implemented to be able to receive events back from the SDK
     *
     * @param placementId   the Ad Placement Id the event happened for
     * @param event         the type of the event, as an SAEvent enum variable
     */
    void onEvent(int placementId, SAEvent event);
}
