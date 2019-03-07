/**
 * @file Rectangle.java
 * @author dylan.hu
 * @assignment Project 4: Shapes Subclass Implementation
 * @date 2/20/2018
 * @description Class that contains the implementations of the constructors
 * and methods of the Rectangle class, a subclass of FilledRect. It has its
 * own constructors and overrides many of the methods in FilledRect to
 * accommodate for its frame.
 */

package stratego;

import objectdraw.*;
import java.awt.Color;

//Rectangle is a subclass of FilledRect (a Rectangle is a FilledRect)
public class Rectangle extends FilledRect {

   //instance variable - frame of the Rectangle
   private FramedRect frame;

   //constructor that takes a Location for its location, a double for its
   //width, a double for its height, and a DrawingCanvas.
   public Rectangle(Location origin, double width, double height,
                    DrawingCanvas canvas) {
      //calls the following constructor with x and y values of the Location
      //other constructor calls superclass constructor and initializes frame
      this(origin.getX(), origin.getY(), width, height, canvas);
   }

   //constructor that takes a double x and double y for its location, a double
   //for its width, a double for its height, and a DrawingCanvas.
   public Rectangle(double x, double y, double width, double height,
                    DrawingCanvas canvas) {
      //calls superclass constructor with the same parameters
      super(x, y, width, height, canvas);
      //initializes frame to same dimensions as the FilledRect
      frame = new FramedRect(x, y, width, height, canvas);
   }

   //constructor that takes 2 Locations for its location and a DrawingCanvas.
   public Rectangle(Location p0, Location p1, DrawingCanvas canvas) {
      //calls superclass constructor with the same parameters
      super(p0, p1, canvas);
      //initializes frame to same dimensions as the FilledRect
      frame = new FramedRect(p0, p1, canvas);
   }

   @Override //to give the user functionality for a more all-purpose Rectangle
   //sets color of frame and fill to the given color
   public void setColor(Color color) {
      super.setColor(color);
      frame.setColor(color);
   }

   //additional method that sets only the frame color
   public void setFrameColor(Color color) {
      frame.setColor(color);
   }

   //additional method that gets only the frame color
   public Color getFrameColor() {
      return frame.getColor();
   }

   //additional method that sets only the fill color
   public void setFillColor(Color color) {
      super.setColor(color);
   }

   //additional method that gets only the fill color
   public Color getFillColor() {
      return super.getColor();
   }

   //helper method that syncs certain changes to the fill with the frame
   private void synchronize() {
      frame.setWidth(this.getWidth());
      frame.setHeight(this.getHeight());
      frame.moveTo(this.getLocation());
      if (isHidden()) {
         frame.hide();
      } else {
         frame.show();
      }
   }

   @Override //because frame needs to be hidden along with fill
   public void hide() {
      super.hide();
      synchronize();
   }

   @Override //because frame needs to be shown along with fill
   public void show() {
      super.show();
      synchronize();
   }

   @Override //because frame needs to move with fill
   public void move(double dx, double dy) {
      super.move(dx, dy);
      synchronize();
   }

   @Override //because frame needs to move with fill
   public void moveTo(double x, double y) {
      super.moveTo(x, y);
      synchronize();
   }

   @Override //because frame needs to move with fill
   public void moveTo(Location point) {
      super.moveTo(point);
      synchronize();
   }

   @Override //because frame needs to be removed when fill is removed
   public void removeFromCanvas() {
      super.removeFromCanvas();
      frame.removeFromCanvas();
   }

   @Override //because frame needs to be sent backwards with the fill
   //this method is only for when moving a Rectangle behind a shape other than
   //Rectangle or Oval
   public void sendBackward() {
      //sends fill back first since it is behind the frame to start
      super.sendBackward();
      frame.sendBackward();
   }

   //additional method that calls sendBackward twice for when moving a
   //Rectangle behind another Rectangle or Oval
   public void sendBothBackward() {
      sendBackward();
      sendBackward();
   }

   @Override //because frame needs to be sent forwards with the frame
   public void sendForward() {
      //sends frame forward first since it is in front of the fill to start
      frame.sendForward();
      super.sendForward();
   }

   //additional method that calls sendForward twice for when moving a
   //Rectangle in front of another Rectangle or Oval
   public void sendBothForward() {
      sendForward();
      sendForward();
   }

   @Override //because frame needs to be sent to back along with fill
   public void sendToBack() {
      //sends frame back first so that fill will be at the back once it is
      //sent to the back
      frame.sendToBack();
      super.sendToBack();
   }

   @Override //because frame needs to be sent to front along with fill
   public void sendToFront() {
      //sends fill to front first so that frame will be at the front once it
      //is sent to the front
      super.sendToFront();
      frame.sendToFront();
   }

   @Override //because frame height needs to change with fill height
   public void setHeight(double height) {
      super.setHeight(height);
      synchronize();
   }

   @Override //because frame width needs to change with fill width
   public void setWidth(double width) {
      super.setWidth(width);
      synchronize();
   }

   @Override //because frame size needs to change with fill size
   public void setSize(double width, double height) {
      super.setSize(width, height);
      synchronize();
   }

   @Override //because it is not a FilledRect
   public String toString() {
      //Gives Object type, x and y values, width and height, and frame and
      //fill colors.
      return getClass().getName() + " at " + getX() + "," + getY() + " width:"
         + getWidth() + " height:" + getHeight() + " fill color:" +
         this.getColor().toString() + " frame color: " + getFrameColor();
   }

   /**
    * Methods not overridden:
    *
    * addToCanvas - not really sure why
    *
    * contains - the FilledRect component of the Rectangle has the same
    * dimensions as the Rectangle. If contains is called on a Rectangle, it
    * will check the same area as the one in which the Rectangle presides.
    *
    * getCanvas - the FilledRect component is always on the same canvas as the
    * Rectangle as a whole since the constructor passes the same canvas to the
    * superclass constructor as it does the FramedRect constructor.
    *
    * getColor - if Rectangle is to be used as an all-purpose Rectangle, then
    * setColor sets the same color to the frame as it does to the fill. So,
    * when treating a Rectangle as a uniform Rectangle, getColor should return
    * either the fill color or frame color. It already returns the fill color.
    *
    * getHeight/getWidth - again, the FilledRect component of the Rectangle has
    * the same dimensions as the Rectangle. Therefore, getting the height or
    * width of the fill returns the same values as the height or width of the
    * Rectangle.
    *
    * getLocation - since the FilledRect component occupies the same space as
    * the Rectangle as a whole, its location is the same as that of the
    * Rectangle.
    *
    * getX/getY - y and x coordinates are the same for the FilledRect component
    * and the Rectangle as a whole.
    *
    * isHidden - since any changes to the Rectangle's visibility are
    * synchronized between the frame and the fill, there is no need to override
    * this method because when the FilledRect component is hidden, the frame is
    * as well and vice versa.
    *
    * overlaps - if another shape were to overlap the FilledRect component of
    * the Rectangle, it would also overlap the Rectangle since they have the
    * same dimensions.
    *
    *
    */

}
