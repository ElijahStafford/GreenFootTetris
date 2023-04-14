import java.util.HashMap;

import greenfoot.*; 

public class MyWorld extends World
{
    public static HashMap<PieceName, Piece> pieces = new HashMap<PieceName, Piece>();

    public MyWorld()
    {   
        super(600, 400, 1); 

        pieces.put(PieceName.LONG, new Piece(PieceColor.BLUE, new Vector2[] {
            new Vector2(0, 0),
            new Vector2(0, 1),
            new Vector2(0, 2),
            new Vector2(0, -1)
        }));
    }
}