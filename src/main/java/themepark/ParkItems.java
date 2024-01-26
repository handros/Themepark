package themepark;

public class ParkItems extends Sprite {

    private final ParkTypes type;

    /**
     * Constructor, setting up the details of a park item.
     *
     * @param x    coordinate x.
     * @param y    coordinate y.
     * @param type height of the item.
     */
    public ParkItems(int x, int y, ParkTypes type) {
        super(x - (x % 10), y - (y % 10), type.getWidth(), type.getHeight(), type.getImg());
        this.type = type;
    }

    public ParkTypes getType() {
        return type;
    }

    public int getPrice() {
        return type.getPrice();
    }

}
