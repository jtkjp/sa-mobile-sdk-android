package tv.superawesome.lib.sasession.defines;

/**
 * Created by gabriel.coman on 03/05/2018.
 */

public enum  SARTBInstl {
    NOT_FULLSCREEN (0),
    FULLSCREEN (1);

    private final int value;

    SARTBInstl(int value) {
        this.value = value;
    }

    public static SARTBInstl fromValue (int pos) {
        switch (pos) {
            case 1: return FULLSCREEN;
            default: return NOT_FULLSCREEN;
        }
    }

    public int getValue() {
        return value;
    }
}
