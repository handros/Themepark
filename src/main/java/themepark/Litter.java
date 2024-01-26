package themepark;

import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Litter extends Sprite {
    private static final int HEIGHT = 10;
    private static final int WIDTH = 10;
    private static final String path = "./src/main/resources/litter.png";
    private boolean underProcess = false;

    /**
     * Constructor, setting up the details of litter.
     *
     * @param x      coordinate x.
     * @param y      coordinate y.
     */
    public Litter(int x, int y) {
        super(x, y, WIDTH, HEIGHT, new ImageIcon(path).getImage());
    }
    
    /**
     * Responsible for checking whether a trashcan is nearby.
     * 
     * @param parkItems list of all parkItems.
     * @return true when a trashcan is located in a 240px radius.
     */
    public boolean trashcanNearby(ArrayList<ParkItems> parkItems) {
        for (ParkItems p : parkItems) {
            if (p.getType() == ParkTypes.TRASHCAN) {
                if((this.getX() >= p.getX()-80) && 
                        (this.getX() <= p.getX()+p.getWidth()+80) && 
                        (this.getY() >= p.getY()-80) && 
                        (this.getY() <= p.getY()+p.getHeight()+80)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getMoodBoost() {
        return -10;
    }
    
    public boolean isUnderProcess() {
        return underProcess;
    }

    public void setUnderProcess(boolean underProcess) {
        this.underProcess = underProcess;
    }
}
