package themepark;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public enum FunTypes {
    FERRIS_WHEEL(50, 25, 500, 100, 100, 20, 5, "./src/main/resources/FERRIS_WHEEL.png"),
    DODGEM(45, 15, 300, 60, 60, 10, 2, "./src/main/resources/DODGEM.png"),
    CAROUSEL(35, 10, 200, 70, 70, 10, 2, "./src/main/resources/CAROUSEL.png"),
    HAUNTED_HOUSE(40, 5, 500, 100, 100, 15, 3, "./src/main/resources/HAUNTED_HOUSE.png"),
    ROLLER_COASTER(55, 20, 300, 120, 150, 25, 5, "./src/main/resources/ROLLER_COASTER.png");

    private final int moodBoost;
    private final int capacity;
    private final int price;
    private final int height;
    private final int width;
    private final int expenses;
    private final int amortization;
    private BufferedImage img;

    /**
     * Constructor, setting up the details of each type of Attraction.
     *
     * @param moodBoost    value of the mood modifier.
     * @param capacity     value of the capacity.
     * @param price        value of the price.
     * @param height       value of the height.
     * @param width        value of the width.
     * @param expenses     expense of the maintenance.
     * @param amortization rate of amortization.
     * @param imgPath      path of the image.
     */
    FunTypes(int moodBoost, int capacity, int price, int height, int width, int expenses, int amortization, String imgPath) {
        this.moodBoost = moodBoost;
        this.capacity = capacity;
        this.price = price;
        this.height = height;
        this.width = width;
        this.expenses = expenses;
        this.amortization = amortization;
        try {
            img = ImageIO.read(new File(imgPath));
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public int getMoodBoost() {
        return moodBoost;
    }

    public int getCapacity() {
        return capacity;
    }

    public BufferedImage getImg() {
        return img;
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

    public int getExpenses() {
        return expenses;
    }

    public int getAmortization() {
        return amortization;
    }

}
