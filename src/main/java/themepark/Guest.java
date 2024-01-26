package themepark;

import java.util.*;
import javax.swing.ImageIcon;

public class Guest extends Person {
    private static final String path = "./src/main/resources/guest_g.png";
    private int hunger;
    private int thirst;
    private int mood;
    private boolean isLeaving = false;
    private boolean hasLitter = false;
    private boolean inLine = false;
    private boolean insideBuilding = false;
    private boolean hasDestination = false;
    private Building targetBuilding = null;
    private String currColor;

    /**
     * Constructor of a Guests.
     * 
     * @param x coordinate x.
     * @param y coordinate y.
     */
    public Guest(int x, int y) {
        super(x, y, new ImageIcon(path).getImage());
        hunger = 100;
        thirst = 100;
        mood = 100;
        currColor = "GREEN";
    }

    /**
     * Decides whether the guest wants to visit a restaurant or an attraction
     * @return "CONSUME" if hunger/thrist is below 30
     *          "PLAY" if mood is below 30
     *          random if none of them are true
     */
    public String decideOnAction() {        
        if(hunger <= 30 || thirst <= 30) return "CONSUME";
        if(mood <= 30) return "PLAY";
        
        Random rand = new Random();
        int x = rand.nextInt(2);
        
        if(x == 1) {
            return "CONSUME";
        }
        return "PLAY";
    }

    /**
     * Checks the actual Guest object's surroundings to summarize the mood boost.
     * @param parkItems all parkItems
     * @param litters all litters
     * @return the total number that the mood should be boosted by
     */
    public int moodModifiersNearby(ArrayList<ParkItems> parkItems, ArrayList<Litter> litters) {
        int moodBoost = 0;
        for (ParkItems p : parkItems) {
            if ((this.getX() >= p.getX() - 50) &&
                    (this.getX() <= p.getX() + p.getWidth() + 50) &&
                    (this.getY() >= p.getY() - 50) &&
                    (this.getY() <= p.getY() + p.getHeight() + 50)) {
                moodBoost += p.getType().getMoodBoost();
            }
        }
        for (Litter p : litters) {
            if ((this.getX() >= p.getX() - 40) &&
                    (this.getX() <= p.getX() + p.getWidth() + 40) &&
                    (this.getY() >= p.getY() - 40) &&
                    (this.getY() <= p.getY() + p.getHeight() + 40)) {
                moodBoost += p.getMoodBoost();
            }
        }
        return moodBoost;
    }

    public int getHunger() {
        return hunger;
    }

    public void changeHunger(int hungerBoost) {
        hunger += hungerBoost;
        if (hunger < 0) {
            hunger = 0;
        } else if (hunger > 100) {
            hunger = 100;
        }
    }

    public int getThirst() {
        return thirst;
    }

    public void changeThirst(int thirstBoost) {
        thirst += thirstBoost;
        if (thirst < 0) {
            thirst = 0;
        } else if (thirst > 100) {
            thirst = 100;
        }
    }

    public int getMood() {
        return mood;
    }

    public void changeMood(int moodBoost) {
        mood += moodBoost;
        if (mood < 0) {
            mood = 0;
        } else if (mood > 100) {
            mood = 100;
        }
    }

    public boolean isHasLitter() {
        return hasLitter;
    }

    public void setHasLitter(boolean hasLitter) {
        this.hasLitter = hasLitter;
    }

    public boolean isInLine() {
        return inLine;
    }

    public void setInLine(boolean inLine) {
        this.inLine = inLine;
    }

    public boolean isInsideBuilding() {
        return insideBuilding;
    }

    public void setInsideBuilding(boolean insideBuilding) {
        this.insideBuilding = insideBuilding;
    }

    public boolean isHasDestination() {
        return hasDestination;
    }

    public void setHasDestination(boolean hasDestination) {
        this.hasDestination = hasDestination;
    }

    public boolean isLeaving() {
        return isLeaving;
    }

    public void setLeaving(boolean leaving) {
        isLeaving = leaving;
    }

    public String getCurrColor() {
        return currColor;
    }

    public void setCurrColor(String currColor) {
        this.currColor = currColor;
    }

    public Building getTargetBuilding() {
        return targetBuilding;
    }

    public void setTargetBuilding(Building targetBuilding) {
        this.targetBuilding = targetBuilding;
    }
}
