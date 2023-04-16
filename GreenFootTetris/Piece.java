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
        for (int i = 0; i < shape.length; i += 2) {
            Vector2 vec = new Vector2(shape[i], shape[i + 1]);
            vec.rotate(counterClockwise ? -90 : 90);

            // here's where I'd check if this is a valid rotation

            shape[i] = vec.x;
            shape[i + 1] = vec.y;
        }

        int[] blockShape = gridShape();

        for (int i = 0; i < shape.length; i += 2) {
            int x = blockShape[i];
            int y = blockShape[i + 1];
            var vec = MyWorld.posGridToWorld(x, y);

            Block block = blocks[i / 2];
            block.setLocation(vec.intx(), vec.inty());
        }
    }
}