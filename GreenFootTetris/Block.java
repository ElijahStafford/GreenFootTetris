import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
public class Block extends Actor
{
    public Block() {
        setImage(MyWorld.images.get(PieceColor.GREEN));
        getImage().scale(MyWorld.gridCellSize, MyWorld.gridCellSize);
    }
}
