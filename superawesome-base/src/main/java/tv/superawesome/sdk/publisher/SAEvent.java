/**
 * @Copyright:   SADefaults Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.sdk.publisher;

/**
 * This enum holds all the possible callback values that an ad sends during its lifetime
 *  - adLoaded:         ad was loaded successfully and is ready to be displayed
 *  - adEmpty:          ad was empty
 *  - adFailedToLoad:   ad was not loaded successfully and will not be able to play
 *  - adAlreadyLoaded   ad was previously loaded in an interstitial, video or app wall queue
 *  - adShown:          triggered once when the ad first displays
 *  - adFailedToShow:   for some reason the ad failed to show; technically this should
 *                      never happen nowadays
 *  - adClicked:        triggered every time the ad gets clicked
 *  - adClosed:         triggered once when the ad is closed;
 */
public enum SAEvent {
    adLoaded {
        @Override
        public String toString() {
            return "adLoaded";
        }
    },
    adEmpty {
        @Override
        public String toString() {
            return "adEmpty";
        }
    },
    adFailedToLoad {
        @Override
        public String toString() {
            return "adFailedToLoad";
        }
    },
    adAlreadyLoaded {
        @Override
        public String toString() {
            return "adAlreadyLoaded";
        }
    },
    adShown {
        @Override
        public String toString() {
            return "adShown";
        }
    },
    adFailedToShow {
        @Override
        public String toString() {
            return "adFailedToShow";
        }
    },
    adClicked {
        @Override
        public String toString() {
            return "adClicked";
        }
    },
    adEnded {
        @Override
        public String toString() {
            return "adEnded";
        }
    },
    adClosed {
        @Override
        public String toString() {
            return "adClosed";
        }
    }
}
