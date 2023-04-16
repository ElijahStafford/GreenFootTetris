import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
public class Block extends Actor
{
    public Block(PieceColor color) {
        GreenfootImage image = MyWorld.images.get(color);
        image.scale(MyWorld.gridCellSize, MyWorld.gridCellSize);
        setImage(image);
    }
}
