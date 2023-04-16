import java.util.HashMap;

import greenfoot.*;

public class MyWorld extends World {
    public static HashMap<PieceColor, Piece> pieces = new HashMap<PieceColor, Piece>();
    public static HashMap<PieceColor, GreenfootImage> images = new HashMap<PieceColor, GreenfootImage>();

    public static final int worldWidth = 1000;
    public static final int worldHeight = 800;
    public static final int worldHalfWidth = worldWidth / 2;
    public static final int worldHalfHeight = worldHeight / 2;

    public static final int gridCellSize = 30;
    public static final int gridWidth = 10;
    public static final int gridHeight = 20;

    public static Vector2 posGridToWorld(double x, double y) {
        // Find corner
        double worldX = worldHalfWidth - gridCellSize * gridWidth / 2.;
        double worldY = worldHalfHeight + gridCellSize * gridHeight / 2.;

        // Center point into grid
        worldX += gridCellSize / 2.;
        worldY -= gridCellSize / 2.;

        // Position in gridspace
        worldX += gridCellSize * x;
        worldY -= gridCellSize * y;

        return new Vector2(worldX, worldY);
    }

    private void initializePieces() {
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

    private void loadImages() {
        for (var key : PieceColor.values()) {
            String name = key.name().toLowerCase();
            String filename = "block_" + name + ".png";
            GreenfootImage image = new GreenfootImage(filename);
            images.put(key, image);
        }
    }

    public MyWorld() {
        super(worldWidth, worldHeight, 1);

        initializePieces();
        loadImages();

        // Temp initialize background
        for (int x = 0; x < gridWidth; x++) for (int y = 0; y < gridHeight; y++) {
            var pos = posGridToWorld(x, y);
            addObject(new Block(), (int)pos.x, (int)pos.y);
        }
    }
}