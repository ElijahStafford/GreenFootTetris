import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
public class Block extends Actor
{
    double gridX;
    double gridY;
    int worldX;
    int worldY;

    public Block(PieceColor color, double gridX, double gridY) {
        this(color);
        setGridPosition(gridX, gridY);
    }

    public Block(PieceColor color) {
        GreenfootImage image = MyWorld.images.get(color);
        image.scale(MyWorld.gridCellSize, MyWorld.gridCellSize);
        setImage(image);
    }

    public void setGridPosition(double x, double y) {
        gridX = x;
        gridY = y;
        Vector2 gridLocation = MyWorld.posGridToWorld(x, y);
        worldX = gridLocation.intx();
        worldY = gridLocation.inty();
        setLocation(worldX, worldY);
    }

    public static boolean gridPositionAvailable(int x, int y) {
        if (x < 0 || x >= MyWorld.gridWidth)
            return false;
        if (y < 0 || y >= MyWorld.gridHeight)
            return false;

        var row = MyWorld.rows.get(y);

        for (Block block : row) {
            if (block.gridX == x && block.gridY == y)
                return false;
        }

        return true;
    }

    public static boolean gridPositionAvailable(Vector2 vec) {
        return gridPositionAvailable(vec.intx(), vec.inty());
    }
}
