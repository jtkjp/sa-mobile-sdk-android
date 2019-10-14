/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.savideoplayer;

/**
 * Interface that is used by SAVideoPlayer to send back video events to the library users
 */
public interface SAVideoPlayerEventInterface {

    /**
     * Main method of the interface
     *
     * @param event the event that just happened
     */
    void saVideoPlayerDidReceiveEvent(SAVideoPlayerEvent event, int time, int duration);
}
