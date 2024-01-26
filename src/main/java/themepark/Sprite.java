package themepark;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public class Sprite {

    private int x;
    private int y;
    private final int width;
    private final int height;
    private Image img;

    /**
     * Constructor, setting up the details of an object.
     *
     * @param x      coordinate x
     * @param y      coordinate y
     * @param width  width of the object
     * @param height height of the object
     * @param img    image of the object
     */
    public Sprite(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }

    /**
     * Responsible for drawing an object.
     *
     * @param g we draw with drawImage from Graphics class
     */
    public void draw(Graphics g) {
        g.drawImage(img, x, y, width, height, null);
    }

    /**
     * Checks whether two objects crash
     *
     * @param other is the second object
     * @return true if the two objects have common area
     */
    public boolean collides(Sprite other) {
        Rectangle rect = new Rectangle(x, y, width, height);
        Rectangle otherRect = new Rectangle(other.x, other.y, other.width, other.height);
        return rect.intersects(otherRect);
    }

    /**
     * Change the sprite's coordinates inside the border if its out of it.
     */
    public void changeIfOutsideBorder() {
        if (this.getX() == 0) this.setX(this.getX() + 10);
        else if (this.getX() + this.getWidth() >= 1790) this.setX(1780 - this.getWidth());

        if (this.getY() == 0) this.setY(this.getY() + 10);
        else if (this.getY() + this.getHeight() >= 930) this.setY(930 - this.getHeight());
    }

    /**
     * Determinates whether the sprites are located next to each other.
     *
     * @param other Sprite to be compare.
     * @return True if the sprites are neighbors.
     */
    public boolean isNeighbor(Sprite other) {
        if (!this.collides(other) &&
                this.collides(new Sprite(other.getX(), other.getY() + 1, other.getWidth(), other.getHeight(), other.getImage()))) {
            return true;
        }
        if (!this.collides(other) &&
                this.collides(new Sprite(other.getX() + 1, other.getY(), other.getWidth(), other.getHeight(), other.getImage()))) {
            return true;
        }
        if (!this.collides(other) &&
                this.collides(new Sprite(other.getX(), other.getY() - 1, other.getWidth(), other.getHeight(), other.getImage()))) {
            return true;
        }
        return !this.collides(other) &&
                this.collides(new Sprite(other.getX() - 1, other.getY(), other.getWidth(), other.getHeight(), other.getImage()));
    }
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImage() {
        return img;
    }

    public void setImage(Image image) {
        this.img = image;
    }
}
