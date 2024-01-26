package themepark;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public enum ParkTypes {
    BUSH(10, 35, 20, 20, "./src/main/resources/bush.png"),
    TREE(20, 30, 40, 20, "./src/main/resources/tree.png"),
    TRASHCAN(-10, 50, 20, 20, "./src/main/resources/trashcan.png");

    private final int moodBoost;
    private final int price;
    private final int height;
    private final int width;
    private BufferedImage img;

    /**
     * Constructor, setting up the details of each Park type.
     *
     * @param moodBoost   value of the mood modifier.
     * @param price       value of the price.
     * @param height      value of the height.
     * @param width       value of the width.
     * @param imgPath     path of the image.
     */
    ParkTypes(int moodBoost, int price, int height, int width, String imgPath) {
        this.moodBoost = moodBoost;
        this.price = price;
        this.height = height;
        this.width = width;
        try {
            this.img = ImageIO.read(new File(imgPath));
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public int getMoodBoost() {
        return moodBoost;
    }

    public int getPrice() {
        return price;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public BufferedImage getImg() {
        return img;
    }


}