import greenfoot.Greenfoot;

import java.security.Key;

public class KeyWatcher {
    private int spamHoldMs = 200;
    private int spamRateMs = 30;
    private boolean previous = false;
    private final String key;
    public boolean isDown;
    public boolean activated;
    public boolean pressed;
    public SimpleTimer downTimer = new SimpleTimer();
    public SimpleTimer spamTimer = new SimpleTimer();

    public KeyWatcher(String key) {
        this.key = key;
    }
    public KeyWatcher(String key, int spamHoldMs) {
        this(key);
        this.spamHoldMs = spamHoldMs;
    }

    public KeyWatcher(String key, int spamHoldMs, int spamRateMs) {
        this(key, spamHoldMs);
        this.spamRateMs = spamRateMs;
    }

    public void watch() {
        pressed = Greenfoot.isKeyDown(key);
        isDown = !previous && pressed;
        previous = pressed;

        activated = false;

        if (isDown) {
            downTimer.mark();
            activated = true;
        }

        boolean spamElapsed = downTimer.millisElapsed() > spamHoldMs - spamRateMs;
        boolean rateElapsed = spamTimer.millisElapsed() > spamRateMs;

        if (pressed && spamElapsed && rateElapsed) {
            spamTimer.mark();
            activated = true;
        }
    }
}
