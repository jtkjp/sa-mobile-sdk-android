/**
 * @Copyright:   SADefaults Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.sdk.publisher;

/**
 * Enum defining possible orientations a fullscreen ad may be found in:
 * - ANY:       the ad can be viewed in any orientation and will not try to lock it
 * - PORTRAIT:  the ad will lock it's orientation in portrait mode, no matter what the app
 *              orientation is
 * - LANDSCAPE: the ad will lock it's orientation in landscape mode, no matter what the app
 *              orientation is
 */
public enum SAOrientation {
    ANY(0),
    PORTRAIT(1),
    LANDSCAPE(2);

    private final int value;

    /**
     * Constructor needed to be able to assign an int value to each enum value
     *
     * @param value the int value
     */
    SAOrientation(int value) {
        this.value = value;
    }

    /**
     * Static factory method that creates a new enum starting from an integer value
     *
     * @param orientation   the integer value
     * @return              an enum, based on the int value being passed
     */
    public static SAOrientation fromValue (int orientation) {
        return orientation == 2 ? LANDSCAPE : orientation == 1 ? PORTRAIT : ANY;
    }

    /**
     * Method equivalent to myEnum.ordinal()
     *
     * @return int value associated with the enum
     */
    public int getValue() {
        return value;
    }
}
