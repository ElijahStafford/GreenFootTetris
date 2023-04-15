public class Piece {
    PieceColor color;
    double[] hitbox;

    public Piece(PieceColor color, double[] hitbox) {
        this.color = color;
        this.hitbox = hitbox;
    }

    public Piece copy() {
        return new Piece(
                this.color,
                this.hitbox
        );
    }
}