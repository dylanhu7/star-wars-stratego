/**
 * @file BoardTile.java
 * @author dylan.hu
 * @assignment Game Project
 * @date 4/6/2018
 * @description BoardTile that is used by Stratego to create the board on
 * which the game is played. It extends VisibleImage and displays either
 * grass or lake.
 */

package stratego;

import java.awt.*;
import objectdraw.*;

public class BoardTile extends VisibleImage {


   public static final int TILE_DIM = 88;
   private boolean isLake;
   private Rectangle outline;
   private boolean isFilled;

   //constructor
   public BoardTile(Image img, boolean isLake, int x, int y,
                    DrawingCanvas canvas) {
      //calls VisibleImage constructor
      super(img, x + 1, y + 1, TILE_DIM - 1, TILE_DIM - 1, canvas);
      outline = new Rectangle(x, y, TILE_DIM, TILE_DIM, canvas);
      outline.sendToBack();
      isFilled = false;
      this.isLake = isLake;
   }

   public void fill() {
      isFilled = true;
   }


   public void empty() {
      isFilled = false;
   }

   public boolean isFilled() {
      return isFilled;
   }

   public boolean isLake() {
      return isLake;
   }

}
