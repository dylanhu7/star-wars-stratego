/**
 * @file UnitButton.java
 * @author dylan.hu
 * @assignment Game Project
 * @date 4/6/2018
 * @description Extends Unit and adds an outline and quantity so that it may
 * act as a button for choosing pieces to place during placement.
 */

package stratego;

import java.awt.*;

import objectdraw.*;

public class UnitButton extends Unit {

   private int rank;
   //each UnitButton has an outline and a quantity
   private Rectangle outline;
   private int quantity;
   private Text quantityText;
   private boolean selected;
   private boolean selectable;

   //constructor
   public UnitButton(Image img, boolean isBlue, int rank,
                     DrawingCanvas canvas) {
      //calls Unit constructor
      super(img, isBlue, rank, canvas);
      this.rank = rank;
      //creates outline off screen since it will be moved immediately
      outline = new Rectangle(-100, -100, this.getWidth() + 4,
              this.getHeight() + 23, canvas);
      outline.sendToBack();
      outline.setFillColor(Color.white);
      //creates quantity text at 0, 0 since it will be moved immediately
      quantityText = new Text("x" + getInitialQuantity(), -100, -100, canvas);
      quantity = getInitialQuantity();
      selectable = true;
   }

   public void moveToTile(double row, double column) {
      super.moveToTile(row, column);
      outline.moveTo(this.getX() - 2, this.getY() - 2);
      quantityText.moveTo(this.getX() + 17, this.getY() + 68);
   }

   public boolean isSelectable() {
      return (quantity > 0);
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

   public void decrementQuantity() {
      quantity--;
      quantityText.setText("x" + quantity);
   }

   public void removeFromCanvas() {
      super.removeFromCanvas();
      outline.removeFromCanvas();
      quantityText.removeFromCanvas();
   }

   //helper method that is used in constructor to determine quantity for each
   //type of Unit
   private int getInitialQuantity() {
      if(rank == 4 || rank == 5 || rank == 6) {
         return 4;
      }
      else if(rank == 2) {
         return 8;
      }
      else if(rank == 11) {
         return 6;
      }
      else if(rank == 3) {
         return 5;
      }
      else if(rank == 8) {
         return 2;
      }
      else if(rank == 7) {
         return 3;
      }
      else if(rank == 0 || rank == 1 || rank == 9 || rank == 10) {
         return 1;
      }
      else {
         return 0;
      }
   }

}
