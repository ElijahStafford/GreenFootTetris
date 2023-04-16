public class Piece {
    PieceColor color;
    double[] shape;
    Vector2 position;

    public Piece(PieceColor color) {
        this.color = color;

        shape = MyWorld.shapes.get(color).clone();
        position = MyWorld.startingPositions.get(color).copy();

        int[] blockShape = gridShape();

        for (int i = 0; i < shape.length; i += 2) {
            int x = blockShape[i];
            int y = blockShape[i + 1];
            var vec = MyWorld.posGridToWorld(x, y);
            MyWorld.world.addObject(new Block(color), vec.intx(), vec.inty());
        }
    }

    public int[] gridShape() {
        return gridShape(shape);
    }

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
}