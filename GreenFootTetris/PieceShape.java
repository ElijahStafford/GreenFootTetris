import java.util.HashMap;

public class PieceShape {
    public static HashMap<PieceColor, double[]> pieceOffsets = new HashMap<>();
    public static HashMap<PieceColor, Vector2> startingPositions = new HashMap<>();

    public static void registerAll() {
        registerShapes();
        registerStartingPositions();
    }

    public static void registerShapes() {
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

    public static void registerStartingPositions() {
        startingPositions.put(PieceColor.AQUA, new Vector2(4.5, 17.5));
        startingPositions.put(PieceColor.BLUE, new Vector2(4, 18));
        startingPositions.put(PieceColor.ORANGE, new Vector2(4, 18));
        startingPositions.put(PieceColor.YELLOW, new Vector2(4.5, 18.5));
        startingPositions.put(PieceColor.GREEN, new Vector2(4, 18));
        startingPositions.put(PieceColor.PURPLE, new Vector2(4, 18));
        startingPositions.put(PieceColor.RED, new Vector2(4, 18));
    }

    public static Vector2 getVisualCenter(PieceColor color) {
        return new Vector2(0, color == PieceColor.YELLOW ? 0 : 0.5);
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
}
