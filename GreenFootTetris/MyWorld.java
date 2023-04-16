import java.util.*;

import greenfoot.*;

public class MyWorld extends World {
    public static Random random = new Random();
    public static World world;

    public static SimpleTimer placeTimer = new SimpleTimer();
    public static HashMap<PieceColor, GreenfootImage> images = new HashMap<>();
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

    public static final Vector2 holdBoxPos = new Vector2(-5, gridHeight - 2.5);
    public static PieceColor heldPieceColor;
    public static Block[] heldBlocks = new Block[0];
    public static boolean heldThisTurn = false;
    public static int holdGridWidth = 4;
    public static double heldSize = 0.7;

    public static SimpleTimer deleteTimer = new SimpleTimer();
    public static int deleteRowAnimationMs = 150;
    public static int deleteFallAnimationMs = 100;
    public static int deleteAnimationCrossoverMs = 40;
    public static HashMap<Integer, ArrayList<Block>> blockOffsetMap = new HashMap<>();
    public static HashMap<Integer, ArrayList<Block>> deletedRows = new HashMap<>();

    public static PieceColor nextPieceColor() {
        if (pieceBag.isEmpty())
            Collections.addAll(pieceBag, PieceColor.values());

        int index = random.nextInt(pieceBag.size());
        return pieceBag.remove(index);
    }

    public static void nextPiece() {
        PieceColor color = nextPieceColor();
        activePiece = new Piece(color);
        placeTimer.mark();
        heldThisTurn = false;
    }

    public static Vector2 posGridToWorld(Vector2 vector) {
        return posGridToWorld(vector.x, vector.y);
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

    private void initializeBackground() {
        GreenfootImage image = new GreenfootImage("block_neutral.png");
        image.scale(MyWorld.gridCellSize, MyWorld.gridCellSize);

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                var block = new Block(PieceColor.BLUE, x, y);
                block.setImage(image);
            }
        }

        double gridOffset = (holdGridWidth - 1) / 2.;

        for (int x = 0; x < holdGridWidth; x++) {
            for (int y = 0; y < holdGridWidth; y++) {
                var vec = new Vector2(x - gridOffset, y - gridOffset);
                vec.add(holdBoxPos);

                var block = new Block(PieceColor.BLUE, vec);
                block.setImage(image);
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
        registerKey("shift");
    }

    private void watchKeys() {
        for (KeyWatcher watcher : keys.values()) {
            watcher.watch();
        }
    }

    private void resetState() {
        placeTimer.mark();
        rows.clear();
        keys.clear();
        activePiece = null;
        heldPieceColor = null;
        pieceBag.clear();
        deleteTimer.mark();
        blockOffsetMap.clear();
        deletedRows.clear();
        gameEnded = false;
    }

    public static void hold() {
        if (heldThisTurn)
            return;

        PieceColor currentColor = activePiece.color;

        world.removeObjects(List.of(activePiece.lowestShapeBlocks));
        world.removeObjects(List.of(activePiece.blocks));
        world.removeObjects(List.of(heldBlocks));
        placeTimer.mark();

        var shape = PieceShape.pieceOffsets.get(currentColor);
        var center = PieceShape.getVisualCenter(currentColor);

        heldBlocks = new Block[shape.length / 2];

        int size = (int)Math.round(gridCellSize * heldSize);
        GreenfootImage image = new GreenfootImage(MyWorld.images.get(currentColor));
        image.scale(size, size);

        for (int i = 0; i < shape.length; i += 2) {
            var vec = new Vector2(shape[i], shape[i + 1]);
            vec.subtract(center);
            vec.multiply(heldSize);
            vec.add(holdBoxPos);

            var block = new Block(currentColor, vec);
            heldBlocks[i / 2] = block;
            block.setImage(image);
        }

        var newColor = heldPieceColor == null ? nextPieceColor() : heldPieceColor;
        activePiece = new Piece(newColor);
        heldPieceColor = currentColor;
        heldThisTurn = true;
    }

    public MyWorld() {
        super(worldWidth, worldHeight, 1);

        // System.out.print('\u000C');

        resetState();
        world = this;

        PieceShape.registerAll();
        WallKick.registerKicks();
        loadImages();
        initializeBackground();
        fillRows();
        registerKeys();

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
        if (gameEnded)
            return;

        watchKeys();

        if (!deletedRows.isEmpty()) {
            animateRows();
            return;
        }

        if (keys.get("shift").isDown) {
            hold();
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

    public static boolean gameEnded = false;

    public static void endGame() {
        gameEnded = true;
    }
}