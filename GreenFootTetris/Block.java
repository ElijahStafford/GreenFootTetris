import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
public class Block extends Actor
{
    int gridX;
    int gridY;
    int worldX;
    int worldY;

    public Block(PieceColor color, Vector2 gridPos, boolean addToWorld) {
        this(color, gridPos.x, gridPos.y, addToWorld);
    }

    public Block(PieceColor color, Vector2 gridPos) {
        this(color, gridPos.x, gridPos.y);
    }

    public Block(PieceColor color, double gridX, double gridY, boolean addToWorld) {
        this(color, addToWorld);
        setGridPosition(gridX, gridY);
    }

    public Block(PieceColor color, double gridX, double gridY) {
        this(color);
        setGridPosition(gridX, gridY);
    }

    public Block(PieceColor color) {
        this(color, true);
    }

    public Block(PieceColor color, boolean addToWorld) {
        GreenfootImage image = MyWorld.images.get(color);
        image.scale(MyWorld.gridCellSize, MyWorld.gridCellSize);
        setImage(image);

        if (addToWorld)
            MyWorld.world.addObject(this, 0, 0);
    }

    public void setGridPosition(Vector2 vector) {
        gridX = vector.intx();
        gridY = vector.inty();
        Vector2 gridLocation = MyWorld.posGridToWorld(vector);
        worldX = gridLocation.intx();
        worldY = gridLocation.inty();
        setLocation(worldX, worldY);
    }

    public void setGridPosition(double x, double y) {
        setGridPosition(new Vector2(x, y));
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
