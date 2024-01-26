package themepark;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public enum ResTypes {
    PIZZERIA(50, 50, 5, 30, 150,
            80, 100, "./src/main/resources/pizzeria.png"),
    SMOOTHIE_STAND(10, 50, 5, 5, 70,
            40, 20, "./src/main/resources/smoothie_stand.png"),
    ICECREAM_STAND(50, 20, 5, 5, 50,
            50, 50, "./src/main/resources/icecream_stand.png");

    private final int hungerBoost;
    private final int thirstBoost;
    private final int moodBoost;
    private final int capacity;
    private final int price;
    private final int height;
    private final int width;
    private BufferedImage img;

    /**
     * Constructor, setting up the details of each type of Restaurant.
     *
     * @param hungerBoost value of the hunger modifier.
     * @param thirstBoost value of the thirst modifier.
     * @param moodBoost   value of the mood modifier.
     * @param capacity    value of the capacity.
     * @param price       value of the price.
     * @param height      value of the height.
     * @param width       value of the width.
     * @param imgPath     path of image.
     */
    ResTypes(int hungerBoost, int thirstBoost, int moodBoost,
             int capacity, int price, int height, int width, String imgPath) {
        this.hungerBoost = hungerBoost;
        this.thirstBoost = thirstBoost;
        this.moodBoost = moodBoost;
        this.capacity = capacity;
        this.price = price;
        this.height = height;
        this.width = width;
        try {
            this.img = ImageIO.read(new File(imgPath));
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public int getHungerBoost() {
        return hungerBoost;
    }

    public int getThirstBoost() {
        return thirstBoost;
    }

    public int getMoodBoost() {
        return moodBoost;
    }

    public int getPrice() {
        return price;
    }

    public int getCapacity() {
        return capacity;
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
