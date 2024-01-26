package themepark;

import java.awt.Image;
import java.util.ArrayDeque;
import java.util.Deque;


public class Person extends Sprite {

    private static final int HEIGHT = 20;
    private static final int WIDTH = 20;
    private boolean hasDestination = false;
    private Deque<Node> destination = new ArrayDeque<>();

    /**
     * Constructor, setting up the details of litter.
     *
     * @param x   coordinate x.
     * @param y   coordinate y.
     * @param img image of the object.
     */
    public Person(int x, int y, Image img) {
        super(x, y, WIDTH, HEIGHT, img);
    }

    public boolean isHasDestination() {
        return hasDestination;
    }

    public void setHasDestination(boolean hasDestination) {
        this.hasDestination = hasDestination;
    }

    public Deque<Node> getDestination() {
        return destination;
    }

    public void setDestination(Deque<Node> destination) {
        this.destination = destination;
    }
}
