public class Piece {
    PieceColor color;
    Vector2[] hitbox;

    public Piece(PieceColor color, Vector2[] hitbox) {
        this.color = color;
        this.hitbox = hitbox;
    }
}