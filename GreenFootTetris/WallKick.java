import java.util.HashMap;

public class WallKick {
    public static HashMap<RotationAction, int[]> aquaKicks = new HashMap<>();
    public static HashMap<RotationAction, int[]> standardKicks = new HashMap<>();

    public enum RotationAction {
        _0TO90,
        _90TO0,
        _90TO180,
        _180TO90,
        _180TO270,
        _270TO180,
        _270TO0,
        _0TO270
    }

    public static RotationAction getRotationAction(int before, int after) {
        if (before == 0) {
            if (after == 90) return RotationAction._0TO90;
            if (after == 270) return RotationAction._0TO270;
        }

        if (before == 90) {
            if (after == 0) return RotationAction._90TO0;
            if (after == 180) return RotationAction._90TO180;
        }

        if (before == 180) {
            if (after == 90) return RotationAction._180TO90;
            if (after == 270) return RotationAction._180TO270;
        }

        if (before == 270) {
            if (after == 180) return RotationAction._270TO180;
        }

        return RotationAction._270TO0;
    }

    public static void registerKicks() {
        aquaKicks.put(RotationAction._0TO90, new int[] {
                -2, 0,
                1, 0,
                -2, -1,
                1, 2
        });
        aquaKicks.put(RotationAction._90TO0, new int[] {
                2, 0,
                -1, 0,
                2, 1,
                -1, -2
        });
        aquaKicks.put(RotationAction._90TO180, new int[] {
                -1, 0,
                2, 0,
                -1, 2,
                2, -1
        });
        aquaKicks.put(RotationAction._180TO90, new int[] {
                1, 0,
                -2, 0,
                1, -2,
                -2, 1
        });
        aquaKicks.put(RotationAction._180TO270, new int[] {
                2, 0,
                -1, 0,
                2, 1,
                -1, -2
        });
        aquaKicks.put(RotationAction._270TO180, new int[] {
                -2, 0,
                1, 0,
                -2, -1,
                1, 2
        });
        aquaKicks.put(RotationAction._270TO0, new int[] {
                1, 0,
                -2, 0,
                1, -2,
                -2, 1
        });
        aquaKicks.put(RotationAction._0TO270, new int[] {
                -1, 0,
                2, 0,
                -1, 2,
                2, -1
        });

        standardKicks.put(RotationAction._0TO90, new int[] {
                -1, 0,
                -1, 1,
                0, -2,
                -1, -2
        });
        standardKicks.put(RotationAction._90TO0, new int[] {
                1, 0,
                1, -1,
                0, 2,
                1, 2
        });
        standardKicks.put(RotationAction._90TO180, new int[] {
                1, 0,
                1, -1,
                0, 2,
                1, 2
        });
        standardKicks.put(RotationAction._180TO90, new int[] {
                -1, 0,
                -1, 1,
                0, -2,
                1, -2
        });
        standardKicks.put(RotationAction._180TO270, new int[] {
                1, 0,
                1, 1,
                0, -2,
                1, -2
        });
        standardKicks.put(RotationAction._270TO180, new int[] {
                -1, 0,
                -1, -1,
                0, 2,
                -1, 2
        });
        standardKicks.put(RotationAction._270TO0, new int[] {
                -1, 0,
                -1, -1,
                0, 2,
                -1, 2
        });
        standardKicks.put(RotationAction._0TO270, new int[] {
                1, 0,
                1, 1,
                0, -2,
                1, -2
        });
    }

    public static int[] attemptKick(PieceColor color, int beforeRotation, int afterRotation, int[] shape) {
        RotationAction rotationAction = getRotationAction(beforeRotation, afterRotation);

        int[] offsets = color == PieceColor.AQUA ?
                aquaKicks.get(rotationAction) :
                standardKicks.get(rotationAction);

        int[] result = new int[0];

        for (int i = 0; i < offsets.length; i += 2) {
            int x = offsets[i];
            int y = offsets[i + 1];
            int[] newShape = shape.clone();

            for (int j = 0; j < newShape.length; j += 2) {
                newShape[j] += x;
                newShape[j + 1] += y;
            }

            if (PieceShape.shapeAvailable(newShape)) {
                result = new int[] { x, y };
                break;
            }
        }

        return result;
    }
}
