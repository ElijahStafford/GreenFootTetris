import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import greenfoot.*;

public class MyWorld extends World {
    public static Random random = new Random();
    public static World world;

    public static SimpleTimer placeTimer = new SimpleTimer();

    public static HashMap<PieceColor, double[]> pieceOffsets = new HashMap<>();
    public static HashMap<PieceColor, GreenfootImage> images = new HashMap<>();
    public static HashMap<PieceColor, Vector2> startingPositions = new HashMap<>();
    public static HashMap<Integer, ArrayList<Block>> rows = new HashMap<>();
    public static HashMap<String, KeyWatcher> keys = new HashMap<>();

    public static final int worldWidth = 1000;
    public static final int worldHeight = 800;
    public static final int worldHalfWidth = worldWidth / 2;
    public static final int worldHalfHeight = worldHeight / 2;

    public static final int gridCellSize = 30;
    public static final int gridWidth = 10;
    public static final int gridHeight = 20;

    public static Piece activePiece;
    public static ArrayList<PieceColor> pieceBag = new ArrayList<>();

    public static SimpleTimer deleteTimer = new SimpleTimer();
    public static int deleteRowAnimationMs = 150;
    public static int deleteFallAnimationMs = 100;
    public static int deleteAnimationCrossoverMs = 40;
    public static HashMap<Integer, ArrayList<Block>> blockOffsetMap = new HashMap<>();
    public static HashMap<Integer, ArrayList<Block>> deletedRows = new HashMap<>();

    public static void nextPiece() {
        if (pieceBag.size() == 0) {
            Collections.addAll(pieceBag, PieceColor.values());
        }

        int index = random.nextInt(pieceBag.size());
        PieceColor color = pieceBag.remove(index);
        activePiece = new Piece(color);
    }

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
        pieceOffsets.put(PieceColor.AQUA, new double[]{
                -1.5, 0.5,
                -0.5, 0.5,
                0.5, 0.5,
                1.5, 0.5
        });
        pieceOffsets.put(PieceColor.BLUE, new double[]{
                -1, 1,
                -1, 0,
                0, 0,
                1, 0
        });
        pieceOffsets.put(PieceColor.ORANGE, new double[]{
                -1, 0,
                0, 0,
                1, 0,
                1, 1
        });
        pieceOffsets.put(PieceColor.YELLOW, new double[]{
                0.5, 0.5,
                0.5, -0.5,
                -0.5, -0.5,
                -0.5, 0.5
        });
        pieceOffsets.put(PieceColor.GREEN, new double[]{
                -1, 0,
                0, 0,
                0, 1,
                1, 1
        });
        pieceOffsets.put(PieceColor.PURPLE, new double[]{
                -1, 0,
                0, 0,
                0, 1,
                1, 0
        });
        pieceOffsets.put(PieceColor.RED, new double[]{
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

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                var block = new Block(PieceColor.BLUE, x, y);
                block.setImage(image);

                addObject(block, block.worldX, block.worldY);
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

    private void fillRows() {
        for (int i = 0; i < gridHeight; i++) {
            rows.put(i, new ArrayList<>());
        }
    }

    private void registerKey(String key) {
        keys.put(key, new KeyWatcher(key));
    }

    public void registerKey(String key, int spamHoldMs) {
        keys.put(key, new KeyWatcher(key, spamHoldMs));
    }

    public void registerKey(String key, int spamHoldMs, int spamRateMs) {
        keys.put(key, new KeyWatcher(key, spamHoldMs, spamRateMs));
    }

    private void registerKeys() {
        registerKey("up", 400, 70);
        registerKey("down");
        registerKey("left");
        registerKey("right");
        registerKey("space");
    }

    private void watchKeys() {
        for (KeyWatcher watcher : keys.values()) {
            watcher.watch();
        }
    }

    public MyWorld() {
        super(worldWidth, worldHeight, 1);

        // System.out.print('\u000C');

        initializeShapes();
        initializeStartingPositions();
        loadImages();
        initializeBackground();
        fillRows();
        registerKeys();

        world = this;
        nextPiece();
    }

    private void animateRows() {
        int totalMs = deleteRowAnimationMs + deleteFallAnimationMs - deleteAnimationCrossoverMs;
        int msElapsed = deleteTimer.millisElapsed();

        double gridWeight = 0.5;

        // Animate out deleted rows
        if (msElapsed < deleteRowAnimationMs) {
            for (var row : deletedRows.values()) {
                for (var block : row) {
                    double gridFraction = gridWeight * block.gridX / gridWidth;
                    double fraction = (double)msElapsed / deleteRowAnimationMs;
                    fraction /= 1 + gridWeight;
                    fraction += gridFraction;
                    fraction *= 1 + gridWeight;
                    fraction = Math.min(Math.max(fraction, 0), 1);

                    int size = (int)Math.round(gridCellSize * (1 - fraction));
                    if (size == 0)
                        size = 1;

                    var image = block.getImage();
                    image.setTransparency((int)((1 - fraction) * 255));
                    image.scale(size, size);

                    block.setRotation((int)Math.round(fraction * 20));
                }
            }
        }

        // Make rows above fall
        var fallingStartMs = deleteRowAnimationMs - deleteAnimationCrossoverMs;

        if (fallingStartMs < msElapsed && msElapsed <= totalMs) {
            var localElapsed = msElapsed - deleteRowAnimationMs + deleteAnimationCrossoverMs;

            for (var entry : blockOffsetMap.entrySet()) {
                var row = entry.getValue();
                var offset = entry.getKey();

                for (var block : row) {
                    double gridFraction = gridWeight * block.gridX / gridWidth;

                    double fraction = (double)localElapsed / deleteFallAnimationMs;
                    fraction = 0.8 - fraction;
                    fraction -= gridFraction;
                    fraction /= 1 + gridWeight;
                    fraction = Math.min(Math.max(fraction, 0), 1);

                    var yPos = block.gridY + offset * fraction;
                    var vec = posGridToWorld(block.gridX, yPos);
                    block.setLocation(vec.intx(), vec.inty());
                }
            }
        }

        // Finish
        if (msElapsed > totalMs) {
            for (var row : deletedRows.values())
                removeObjects(row);

            deletedRows.clear();
            blockOffsetMap.clear();
            nextPiece();
        }
    }

    public int lowerMs = 1000;

    public void act() {
        watchKeys();

        if (!deletedRows.isEmpty()) {
            animateRows();
            return;
        }

        if (keys.get("up").activated) {
            activePiece.rotate();
        }

        if (keys.get("left").activated) {
            activePiece.moveLeft();
        }

        if (keys.get("right").activated) {
            activePiece.moveRight();
        }

        boolean enoughTimeToLower = placeTimer.millisElapsed() > lowerMs;
        boolean lowestPoint = activePiece.isAtLowestPoint();

        if ((enoughTimeToLower || keys.get("down").activated) && !lowestPoint) {
            activePiece.lower();
            placeTimer.mark();
        }

        if (keys.get("space").isDown) {
            activePiece.place();
            return;
        }

        boolean recentlyAtLowest = activePiece.sinceLowestPoint.millisElapsed() < lowerMs * 2;
        boolean movedRecently = activePiece.sinceLastMove.millisElapsed() < lowerMs;
        boolean movedTimeUp = placeTimer.millisElapsed() > lowerMs * 4;
        boolean canLower = enoughTimeToLower && lowestPoint && recentlyAtLowest;
        boolean moveExtension = movedRecently && !movedTimeUp;

        if (canLower && !moveExtension) {
            activePiece.place();
        }
    }
}