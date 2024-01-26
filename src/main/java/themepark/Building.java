package themepark;

import java.awt.Image;
import java.util.ArrayList;

public class Building extends Sprite {

    private final int capacity;
    private final int price;
    private Road neighboringRoad = null;
    private final ArrayList<Guest> peopleInside = new ArrayList<>();

    /**
     * Constructor, setting up the details of an object.
     *
     * @param x        coordinate x.
     * @param y        coordinate y.
     * @param width    width of the building.
     * @param height   height of the building.
     * @param img      image of the building.
     * @param capacity capacity of the building.
     * @param price    price of the service.
     */
    public Building(int x, int y, int width, int height, Image img, int capacity, int price) {
        super(x, y, width, height, img);
        this.capacity = capacity;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public Road getNeighboringRoad() {
        return neighboringRoad;
    }

    public ArrayList<Guest> getPeopleInside() {
        return peopleInside;
    }

    public void setNeighboringRoad(Road neighboringRoad) {
        this.neighboringRoad = neighboringRoad;
    }

    public void addToPeopleInside(Guest g) {
        peopleInside.add(g);
    }

    public void removeFromPeopleInside(Guest g) {
        peopleInside.remove(g);
    }

    public void emptyPeopleInside() {
        peopleInside.clear();
    }
}
