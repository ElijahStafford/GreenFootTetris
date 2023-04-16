import greenfoot.GreenfootImage;

import java.util.Arrays;

public class Piece {
    PieceColor color;
    double[] blockOffsets;
    Vector2 position;
    Block[] blocks;
    public int[] lowestShape;
    public Block[] lowestShapeVisuals;

    public Piece(PieceColor color) {
        // Initializing variables
        this.color = color;
        blockOffsets = MyWorld.pieceOffsets.get(color).clone();
        position = MyWorld.startingPositions.get(color).copy();

        // Initialing shape visuals
        lowestShapeVisuals = new Block[blockOffsets.length / 2];

        for (int i = 0; i < lowestShapeVisuals.length; i++) {
            Block block = new Block(color);
            lowestShapeVisuals[i] = block;
        }

        updateLowestShape();

        // Initializing block position
        int[] blockShape = gridShape();
        blocks = new Block[blockOffsets.length / 2];

        for (int i = 0; i < blockOffsets.length; i += 2) {
            int x = blockShape[i];
            int y = blockShape[i + 1];

            Block block = new Block(color, x, y);
            blocks[i / 2] = block;
            MyWorld.world.addObject(block, block.worldX, block.worldY);
        }
    }

    public static boolean shapeAvailable(int[] shape) {
        for (int i = 0; i < shape.length; i += 2) {
            var x = shape[i];
            var y = shape[i + 1];

            if (!Block.gridPositionAvailable(x, y))
                return false;
        }

        return true;
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
            var vec = MyWorld.posGridToWorld(x, y);

            Block block = blocks[i / 2];
            block.setLocation(vec.intx(), vec.inty());
        }
    }

    public void rotate() {
        rotate(false);
    }

    public void rotate(boolean counterClockwise) {
        var newOffsets = blockOffsets.clone();

        for (int i = 0; i < blockOffsets.length; i += 2) {
            Vector2 vec = new Vector2(blockOffsets[i], blockOffsets[i + 1]);
            vec.rotate(counterClockwise ? -90 : 90);

            newOffsets[i] = vec.x;
            newOffsets[i + 1] = vec.y;
        }

        int[] blockShape = gridShape(newOffsets);

        if (!shapeAvailable(blockShape))
            return;

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

        if (shapeAvailable(blockShape)) {
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
        var lowestShape = gridShape(blockOffsets);

        boolean canDescend = shapeAvailable(lowestShape);

        while (canDescend) {
            var tempLowestShape = lowestShape.clone();

            for (int i = 0; i < blockOffsets.length; i += 2) {
                tempLowestShape[i + 1]--;
            }

            canDescend = shapeAvailable(tempLowestShape);

            if (canDescend)
                lowestShape = tempLowestShape;
        }

        // Update visuals
        boolean isAtLowestPoint = isAtLowestPoint();

        for (int i = 0; i < lowestShape.length; i += 2) {
            int x = lowestShape[i];
            int y = lowestShape[i + 1];

            Block block = lowestShapeVisuals[i / 2];
            block.setGridPosition(x, y);
            MyWorld.world.addObject(block, block.worldX, block.worldY);

            GreenfootImage image = new GreenfootImage(block.getImage());
            image.setTransparency(isAtLowestPoint ? 0 : 100);
            block.setImage(image);
        }
    }

    public boolean isAtLowestPoint() {
        return Arrays.equals(lowestShape, gridShape());
    }
}