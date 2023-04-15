import java.util.HashMap;

import greenfoot.*;

public class MyWorld extends World {
    public static HashMap<PieceColor, Piece> pieces = new HashMap<PieceColor, Piece>();
    public static final int gridCellSize = 20;
    public static final int gridWidth = 10;
    public static final int gridHeight = 20;

    public MyWorld() {
        super(600, 400, 1);

        Piece[] newPieces = {
                new Piece(PieceColor.AQUA, new double[]{
                        -1.5, 0.5,
                        -0.5, 0.5,
                        0.5, 0.5,
                        1.5, 0.5
                }),
                new Piece(PieceColor.BLUE, new double[]{
                        -1, 1,
                        -1, 0,
                        0, 0,
                        1, 0
                }),
                new Piece(PieceColor.ORANGE, new double[]{
                        -1, 0,
                        0, 0,
                        1, 0,
                        1, 1
                }),
                new Piece(PieceColor.YELLOW, new double[]{
                        0.5, 0.5,
                        0.5, -0.5,
                        -0.5, -0.5,
                        -0.5, 0.5
                }),
                new Piece(PieceColor.GREEN, new double[]{
                        -1, 0,
                        0, 0,
                        0, 1,
                        1, 1
                }),
                new Piece(PieceColor.PURPLE, new double[]{
                        -1, 0,
                        0, 0,
                        0, 1,
                        1, 0
                }),
                new Piece(PieceColor.RED, new double[]{
                        -1, 1,
                        0, 1,
                        0, 0,
                        1, 0
                }),
        };

        for (Piece piece : newPieces) {
            pieces.put(piece.color, piece);
        }
    }
}