import java.util.HashMap;

import greenfoot.*;

public class MyWorld extends World {
    public static World world;

    public static HashMap<PieceColor, double[]> shapes = new HashMap<PieceColor, double[]>();
    public static HashMap<PieceColor, GreenfootImage> images = new HashMap<PieceColor, GreenfootImage>();
    public static HashMap<PieceColor, Vector2> startingPositions = new HashMap<PieceColor, Vector2>();

    public static final int worldWidth = 1000;
    public static final int worldHeight = 800;
    public static final int worldHalfWidth = worldWidth / 2;
    public static final int worldHalfHeight = worldHeight / 2;

    public static final int gridCellSize = 30;
    public static final int gridWidth = 10;
    public static final int gridHeight = 20;

    public static Piece activePiece;

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

    private void initializeShapes() {
        shapes.put(PieceColor.AQUA, new double[]{
                -1.5, 0.5,
                -0.5, 0.5,
                0.5, 0.5,
                1.5, 0.5
        });
        shapes.put(PieceColor.BLUE, new double[]{
                -1, 1,
                -1, 0,
                0, 0,
                1, 0
        });
        shapes.put(PieceColor.ORANGE, new double[]{
                -1, 0,
                0, 0,
                1, 0,
                1, 1
        });
        shapes.put(PieceColor.YELLOW, new double[]{
                0.5, 0.5,
                0.5, -0.5,
                -0.5, -0.5,
                -0.5, 0.5
        });
        shapes.put(PieceColor.GREEN, new double[]{
                -1, 0,
                0, 0,
                0, 1,
                1, 1
        });
        shapes.put(PieceColor.PURPLE, new double[]{
                -1, 0,
                0, 0,
                0, 1,
                1, 0
        });
        shapes.put(PieceColor.RED, new double[]{
                -1, 1,
                0, 1,
                0, 0,
                1, 0
        });
    }

    private void initializeStartingPositions() {
        startingPositions.put(PieceColor.AQUA, new Vector2(4.5, 17.5));
        startingPositions.put(PieceColor.BLUE, new Vector2(4, 18));
        startingPositions.put(PieceColor.ORANGE, new Vector2(4, 18));
        startingPositions.put(PieceColor.YELLOW, new Vector2(4.5, 18.5));
        startingPositions.put(PieceColor.GREEN, new Vector2(4, 18));
        startingPositions.put(PieceColor.PURPLE, new Vector2(4, 18));
        startingPositions.put(PieceColor.RED, new Vector2(4, 18));
    }

    private void initializeBackground() {
        GreenfootImage image = new GreenfootImage("block_neutral.png");
        image.scale(MyWorld.gridCellSize, MyWorld.gridCellSize);
        image.setColor(new Color(0,0,5));

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                var pos = posGridToWorld(x, y);
                // pos.rotate(y, worldHalfWidth, worldHalfHeight);

                var block = new Block(PieceColor.BLUE);
                block.setImage(image);

                addObject(block, pos.intx(), pos.inty());
            }
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

        world = this;

        initializeShapes();
        initializeStartingPositions();
        loadImages();
        initializeBackground();

        activePiece = new Piece(PieceColor.RED);
    }

    public void act() {

    }
}