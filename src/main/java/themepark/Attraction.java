package themepark;
//package main.java.themepark;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Attraction extends Building {

    private int counter = 0;
    private final FunTypes type;
    private int condition;
    private final ArrayList<Guest> queue;
    private final int amortization;
    private final int expenses;
    private boolean down = false;
    private boolean underRepairment = false;

    /**
     * Constructor, setting up the details of an Attraction.
     *
     * @param x    coordinate x
     * @param y    coordinate y
     * @param type type of the Attraction
     */
    public Attraction(int x, int y, FunTypes type) {
        super(x - (x % 10), y - (y % 10),
                type.getWidth(), type.getHeight(),
                type.getImg(), type.getCapacity(),
                type.getPrice());
        this.type = type;
        this.amortization = type.getAmortization();
        this.expenses = type.getExpenses();
        this.condition = 100;
        queue = new ArrayList<>();
    }

    /**
     * Responsible for entertaining guests.
     * 
     * @param x coordinate x.
     * @param y coordinate y.
     * @return with the price we will get.
     */
    public int entertain(int x, int y) {
        int sum = 0;
        if (!isDown() && !queue.isEmpty()) {
            counter++;
            if (counter == 10 || (queue.size() == type.getCapacity())) {
                for (Guest g : queue) {
                    g.setInLine(false);
                    g.setInsideBuilding(true);
                    sum += type.getPrice();
                    addToPeopleInside(g);
                }
                queue.clear();

                try {
                    setImage(ImageIO.read(new File("./src/main/resources/" + type + "_ON.png")));
                } catch (IOException e) {
                    e.getStackTrace();
                }
                Timer timer = new Timer(10000, actionEvent -> {
                    try {
                        setImage(ImageIO.read(new File("./src/main/resources/" + type + ".png")));
                    } catch (IOException e) {
                        e.getStackTrace();
                    }
                    int toPutX, toPutY;
                    if (getNeighboringRoad() == null) {
                        toPutX = x;
                        toPutY = y;
                    } else {
                        toPutX = getNeighboringRoad().getX();
                        toPutY = getNeighboringRoad().getY();
                    }
                    for (Person p : getPeopleInside()) {
                        Guest g = (Guest) p;
                        g.setX(toPutX);
                        g.setY(toPutY);
                        g.changeMood(type.getMoodBoost());
                        g.setInsideBuilding(false);
                    }
                    emptyPeopleInside();
                    condition -= type.getAmortization();
                    counter = 0;
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
        return sum;
    }

    /**
     * Checks whether this Attraction must be repaired.
     *
     * @return true if the condition reached 0.
     */
    public boolean needRepair() {
        return this.condition <= 0;
    }

    /**
     * Attractions will be repaired if needed and a repairer is close enough.
     * Set condition to 100,
     * remove this attraction from repairable and
     * swapping the Attraction's status (picture).
     */
    public void repair() {
        condition = 100;
        down = false;

        try {
            setImage(ImageIO.read(new File("./src/main/resources/" + type + ".png")));
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public FunTypes getType() {
        return type;
    }

    public int getAmortization() {
        return amortization;
    }

    public int getExpenses() {
        return expenses;
    }

    public void modifyCondition(int value) {
        this.condition += value;
    }

    public int getCondition() {
        return condition;
    }

    public ArrayList<Guest> getQueue() {
        return queue;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isUnderRepairment() {
        return underRepairment;
    }

    public void setUnderRepairment(boolean underRepairment) {
        this.underRepairment = underRepairment;
    }

    public void addToQueue(Guest guest) {
        if (type.getCapacity() <= queue.size() || isDown()) {
            guest.changeMood(-5);
            guest.setX(getNeighboringRoad().getX());
            guest.setY(getNeighboringRoad().getY());
        } else {
            guest.setInLine(true);
            queue.add(guest);
        }
    }
}
