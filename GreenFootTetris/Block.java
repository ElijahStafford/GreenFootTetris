import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
public class Block extends Actor
{
    double gridX;
    double gridY;
    int worldX;
    int worldY;

    public Block(PieceColor color, double gridX, double gridY) {
        blockConstructor(color);
        setGridPosition(gridX, gridY);
    }

    public Block(PieceColor color) {
        blockConstructor(color);
    }

    private void blockConstructor(PieceColor color) {
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
}
