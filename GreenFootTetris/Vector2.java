public class Vector2 {
    public double x = 0;
    public double y = 0;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(double x) {
        this.x = x;
    }

    public void rotate(double degrees) {
        rotate(degrees, 0, 0);
    }

    public void rotate(double degrees, double anchorX, double anchorY) {
        Vector2 delta = new Vector2(x - anchorX, y - anchorY);

        double angle = Math.atan2(-delta.y, delta.x);
        angle += Math.toRadians(degrees);

        double newX = Math.cos(angle);
        double newY = Math.sin(angle);
        newX *= delta.magnitude();
        newY *= delta.magnitude();

        x = newX + anchorX;
        y = newY + anchorY;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }
}
