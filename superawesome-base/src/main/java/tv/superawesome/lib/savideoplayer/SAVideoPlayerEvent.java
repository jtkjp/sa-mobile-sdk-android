/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.savideoplayer;

/**
 * VideoPlayer event enum, containing the following events:
 *  - Video_Prepared:   triggered when the video view, container and controller are all initialized
 *                      this is also the place to call "play" on the video player
 *  - Video_Start:      triggered when a video starts playing
 *  - Video_1_4:        triggered when a video reaches a quarter of its playing duration
 *  - Video_1_2:        triggered when a video reaches half of its playing duration
 *  - Video_3_4:        triggered when a video reaches three quarters of its playing duration
 *  - Video_End:        triggered when a video ends playing
 *  - Video_Error:      triggered anytime there is any kind of error that prevents
 *                      the video from playing
 */
public enum SAVideoPlayerEvent {
    Video_Prepared,
    Video_Start,
    Video_2s,
    Video_1_4,
    Video_1_2,
    Video_3_4,
    Video_End,
    Video_15s,
    Video_Error
}
