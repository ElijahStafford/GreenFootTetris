import greenfoot.Greenfoot;

public class KeyWatcher {
    private boolean previous = false;
    private String key;

    public KeyWatcher(String key) {
        this.key = key;
    }

    public boolean check() {
        boolean pressed = Greenfoot.isKeyDown(key);
        boolean isDown = !previous && pressed;
        previous = pressed;
        return isDown;
    }
}
