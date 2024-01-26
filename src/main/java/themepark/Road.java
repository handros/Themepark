package themepark;

import javax.swing.ImageIcon;

public class Road extends Sprite {

    private static final String path = "./src/main/resources/cobblestone.png";

    /**
     * Constructor, setting up the details of a road-piece.
     *
     * @param x coordinate x.
     * @param y coordinate y.
     */
    public Road(int x, int y) {
        super(x - (x % 10), y - (y % 10), 40, 40, new ImageIcon(path).getImage());
    }

    public Road() {
        super(10, 10, 40, 40, new ImageIcon(path).getImage());
    }

    public static int getPrice() {
        return 5;
    }
}
