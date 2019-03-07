/**
 * @file Unit.java
 * @author dylan.hu
 * @assignment Game Project
 * @date 4/6/2018
 * @description Unit class for Stratego. Unit is a VisibleImage with a rank
 * and a row and column to store its location on the board. It also has a
 * colored background to show its team.
 */

package stratego;

import java.awt.*;

import objectdraw.*;

public class Unit extends VisibleImage {

   private int rank;
   private double row;
   private double column;
   private boolean isBlue;
   private Rectangle background;
   public static final int UNIT_X_OFFSET = 17;
   public static final int UNIT_Y_OFFSET = 10;
   public static final Color unitBlue = new Color(64, 112, 255);
   public static final Color unitRed = new Color(186, 16, 16);
   private boolean selected;
   private boolean selectable;
   private boolean isPlaced;

   //constructor
   public Unit(Image img, boolean isBlue, int rank, DrawingCanvas canvas) {
      //calls VisibleImage constructor
      super(img, -100, -100, 54, 68, canvas);
      this.rank = rank;
      this.isBlue = isBlue;
      //makes a Rectangle as a background
      background = new Rectangle(-100, -100, this.getWidth(), this.getHeight(),
         canvas);
      background.sendBackward();
      //set the background for the appropriate color
      if (isBlue) {
         background.setColor(unitBlue);
      }
      else {
         background.setColor(unitRed);
      }
      selectable = true;
   }

   //moves to a normal x and y coordinate
   public void moveTo(double x, double y) {
      super.moveTo(x, y);
      background.moveTo(x, y);
   }

   //moves to a row and column
   public void moveToTile(double row, double column) {
      this.row = row;
      this.column = column;
      super.moveTo(Stratego.BOARD_X_OFFSET + UNIT_X_OFFSET +
         (BoardTile.TILE_DIM * column), Stratego.BOARD_Y_OFFSET +
         UNIT_Y_OFFSET + (BoardTile.TILE_DIM * row));
      background.moveTo(this.getX(), this.getY());
   }

   public void setColor(Color c){
      background.setColor(c);
   }

   public double getRow() {
      return row;
   }

   public double getColumn() {
      return column;
   }

   public boolean isBlue() {
      return isBlue;
   }

   public void conceal() {
      background.sendToFront();
   }

   public void reveal() {
      super.sendToFront();
   }

   public void sendToFront() {
      super.sendToFront();
      background.sendToFront();
      background.sendBothForward();
      super.sendToFront();
   }

   public int getRank() {
      return rank;
   }

   public void select() {
      if(selectable) {
         selected = true;
      }
   }

   public void deselect() {
      selected = false;
   }

   public boolean isSelected() {
      return selected;
   }

   public void markAsPlaced() {
      isPlaced = true;
   }

   public boolean isPlaced() {
      return isPlaced;
   }

   public void removeFromCanvas() {
      super.removeFromCanvas();
      background.removeFromCanvas();
   }
}
