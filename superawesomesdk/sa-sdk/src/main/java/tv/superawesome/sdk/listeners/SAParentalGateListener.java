package tv.superawesome.sdk.listeners;

/**
 * Created by gabriel.coman on 11/01/16.
 */
public interface SAParentalGateListener {

    /**
     * Callback when user cancels PG
     * @param placementId
     */
    void parentalGateWasCanceled(int placementId);

    /**
     * Callback when user fails PG
     * @param placementId
     */
    void parentalGateWasFailed(int placementId);

    /**
     * Callback when users goes through PG
     * @param placementId
     */
    void parentalGateWasSucceded(int placementId);
}
