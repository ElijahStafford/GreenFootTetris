import greenfoot.Color;
import greenfoot.GreenfootImage;

import java.util.*;

public class Piece {
    PieceColor color;
    double[] blockOffsets;
    Vector2 position;
    Block[] blocks;
    public int[] lowestShape;
    public Block[] lowestShapeBlocks;
    public SimpleTimer sinceLowestPoint = new SimpleTimer();
    public SimpleTimer sinceLastMove = new SimpleTimer();

    public Piece(PieceColor color) {
        // Initializing variables
        this.color = color;
        blockOffsets = PieceShape.pieceOffsets.get(color).clone();
        position = PieceShape.startingPositions.get(color).copy();

        // Initializing block position
        int[] blockShape = gridShape();
        boolean gameEnded = false;

        if (!PieceShape.shapeAvailable(blockShape)) {
            position.y++;
            blockShape = gridShape();

            if (!PieceShape.shapeAvailable(blockShape)) {
                gameEnded = true;
                MyWorld.endGame();
            }
        }

        blocks = new Block[blockOffsets.length / 2];

        for (int i = 0; i < blockOffsets.length; i += 2) {
            int x = blockShape[i];
            int y = blockShape[i + 1];

            Block block = new Block(color, x, y);
            blocks[i / 2] = block;

            if (gameEnded) {
                Block errorBlock = new Block(color, x, y);

                var image = new GreenfootImage(errorBlock.getImage());
                var size = MyWorld.gridCellSize + 10;
                image.scale(size, size);
                image.setColor(Color.WHITE);
                image.fill();
                errorBlock.setImage(image);
            }
        }

        if (gameEnded)
            return;

        // Initialing shape visuals
        lowestShapeBlocks = new Block[blockOffsets.length / 2];

        GreenfootImage image = new GreenfootImage(MyWorld.images.get(color));
        image.setTransparency(100);
        image.scale(MyWorld.gridCellSize, MyWorld.gridCellSize);

        for (int i = 0; i < lowestShapeBlocks.length; i++) {
            Block block = new Block(color);
            block.setImage(image);

            lowestShapeBlocks[i] = block;
        }

        updateLowestShape();
    }

    public int[] gridShape() {
        return gridShape(blockOffsets);
    }

    /**
     * Converts shape offsets to grid positions
     * @param shape The shape offsets to use
     * @return New grid positions
     */
    public int[] gridShape(double[] shape) {
        int[] result = new int[shape.length];

        for (int i = 0; i < shape.length; i += 2) {
            double x = shape[i] + position.x;
            double y = shape[i + 1] + position.y;

            result[i] = (int)Math.round(x);
            result[i + 1] = (int)Math.round(y);
        }

        return result;
    }

    public void moveToShape(int[] shape) {
        for (int i = 0; i < shape.length; i += 2) {
            int x = shape[i];
            int y = shape[i + 1];

            Block block = blocks[i / 2];
            block.setGridPosition(x, y);
        }

        sinceLastMove.mark();
    }

    public int rotation = 0;

    public void rotate() {
        rotate(false);
    }

    public void rotate(boolean counterClockwise) {
        if (color == PieceColor.YELLOW) {
            sinceLastMove.mark();
            return;
        }

        var newOffsets = blockOffsets.clone();
        var rotateAmount = counterClockwise ? -90 : 90;
        var newRotation = (rotation + rotateAmount) % 360;

        for (int i = 0; i < blockOffsets.length; i += 2) {
            Vector2 vec = new Vector2(blockOffsets[i], blockOffsets[i + 1]);
            vec.rotate(counterClockwise ? -90 : 90);

            newOffsets[i] = vec.x;
            newOffsets[i + 1] = vec.y;
        }

        int[] blockShape = gridShape(newOffsets);

        if (!PieceShape.shapeAvailable(blockShape)) {
            var kick = WallKick.attemptKick(color, rotation, newRotation, blockShape);

            if (kick.length == 0)
                return;

            position.x += kick[0];
            position.y += kick[1];

            blockShape = gridShape(newOffsets);
        }

        rotation = newRotation;
        blockOffsets = newOffsets;
        updateLowestShape();
        moveToShape(blockShape);
    }

    public void lower() {
        translate(0, -1);
    }

    public void moveLeft() {
        translate(-1, 0);
    }

    public void moveRight() {
        translate(1, 0);
    }

    public void translate(int x, int y) {
        position.x += x;
        position.y += y;

        var blockShape = gridShape();

        if (PieceShape.shapeAvailable(blockShape)) {
            moveToShape(blockShape);
            updateLowestShape();
        }
        else {
            position.x -= x;
            position.y -= y;
        }
    }

    public void updateLowestShape() {
        // Find the lowest shape
        lowestShape = gridShape(blockOffsets);

        boolean canDescend = PieceShape.shapeAvailable(lowestShape);

        while (canDescend) {
            var tempLowestShape = lowestShape.clone();

            for (int i = 0; i < blockOffsets.length; i += 2) {
                tempLowestShape[i + 1]--;
            }

            canDescend = PieceShape.shapeAvailable(tempLowestShape);

            if (canDescend)
                lowestShape = tempLowestShape;
        }

        // Update visuals
        boolean isAtLowestPoint = isAtLowestPoint();

        for (int i = 0; i < lowestShape.length; i += 2) {
            int x = lowestShape[i];
            int y = lowestShape[i + 1];

            Block block = lowestShapeBlocks[i / 2];
            block.setGridPosition(x, y);
        }

        if (isAtLowestPoint)
            sinceLowestPoint.mark();
    }

    public boolean isAtLowestPoint() {
        return Arrays.equals(lowestShape, gridShape());
    }

    public void place() {
        MyWorld.placeTimer.mark();
        moveToShape(lowestShape);

        var rows = MyWorld.rows;

        MyWorld.world.removeObjects(List.of(lowestShapeBlocks));

        for (Block block : blocks) {
            var arr = rows.get(block.gridY);
            arr.add(block);
        }

        // Check for finished rows
        HashSet<Integer> deletedLayers = new HashSet<>();
        HashMap<Integer, ArrayList<Block>> deletedRows = new HashMap<>();
        int offset = 0;

        // Register deleted rows
        for (int layer : rows.keySet()) {
            ArrayList<Block> row = new ArrayList<>(rows.get(layer));

            if (row.size() == MyWorld.gridWidth) {
                offset++;
                deletedRows.put(offset, row);
                deletedLayers.add(layer);

                for (Block block : row) {
                    var image = new GreenfootImage(MyWorld.images.get(PieceColor.BLUE));
                    image.scale(MyWorld.gridCellSize, MyWorld.gridCellSize);
                    image.setColor(new Color(255, 255, 255));
                    image.fill();
                    block.setImage(image);
                }
            }
        }

        if (deletedRows.isEmpty())
            MyWorld.nextPiece();
        else {
            MyWorld.deleteTimer.mark();
            MyWorld.deletedRows = deletedRows;

            var offsetMap = MyWorld.blockOffsetMap;

            // Register offset values
            for (var entry : deletedRows.entrySet()) {
                var layer = entry.getValue().get(0).gridY + 1;
                var entryOffset = entry.getKey();

                if (layer >= MyWorld.gridHeight)
                    return;

                var row = new ArrayList<>(rows.get(layer));
                var nothingInRow = row.size() == 0;
                var rowIsDeleted = deletedLayers.contains(layer);

                while (!rowIsDeleted && !nothingInRow) {
                    if (!offsetMap.containsKey(entryOffset))
                        offsetMap.put(entryOffset, row);
                    else
                        offsetMap.get(entryOffset).addAll(row);

                    layer++;

                    if (layer >= MyWorld.gridHeight)
                        return;

                    row = new ArrayList<>(rows.get(layer));
                    nothingInRow = row.size() == 0;
                    rowIsDeleted = deletedLayers.contains(layer);
                }
            }

            // Clear rows
            int bottomRow = deletedRows.get(1).get(0).gridY;

            for (var entry : rows.entrySet()) {
                var layer = entry.getKey();
                var row = entry.getValue();

                if (row.size() == 0)
                    break;

                if (layer < bottomRow)
                    continue;

                row.clear();
            }

            // Repopulate rows
            for (var entry : offsetMap.entrySet()) {
                var entryOffset = entry.getKey();
                var group = entry.getValue();

                for (var block : group) {
                    block.gridY -= entryOffset;
                    rows.get(block.gridY).add(block);
                }
            }
        }
    }
}