public class Piece {
    PieceColor color;
    double[] shape;
    Vector2 position;
    Block[] blocks;

    public Piece(PieceColor color) {
        this.color = color;

        shape = MyWorld.shapes.get(color).clone();
        position = MyWorld.startingPositions.get(color).copy();

        int[] blockShape = gridShape();
        blocks = new Block[shape.length / 2];

        for (int i = 0; i < shape.length; i += 2) {
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
        return gridShape(shape);
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

    public void rotate() {
        rotate(false);
    }

    public void rotate(boolean counterClockwise) {
        var newShape = shape.clone();

        for (int i = 0; i < shape.length; i += 2) {
            Vector2 vec = new Vector2(shape[i], shape[i + 1]);
            vec.rotate(counterClockwise ? -90 : 90);

            newShape[i] = vec.x;
            newShape[i + 1] = vec.y;
        }

        int[] blockShape = gridShape(newShape);

        if (!shapeAvailable(blockShape))
            return;

        shape = newShape;

        for (int i = 0; i < shape.length; i += 2) {
            int x = blockShape[i];
            int y = blockShape[i + 1];
            var vec = MyWorld.posGridToWorld(x, y);

            Block block = blocks[i / 2];
            block.setLocation(vec.intx(), vec.inty());
        }
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
            for (int i = 0; i < shape.length; i += 2) {
                int gridX = blockShape[i];
                int gridY = blockShape[i + 1];
                var vec = MyWorld.posGridToWorld(gridX, gridY);

                Block block = blocks[i / 2];
                block.setLocation(vec.intx(), vec.inty());
            }
        }
        else {
            position.x -= x;
            position.y -= y;
        }
    }
}