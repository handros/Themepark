package themepark;

import javax.swing.ImageIcon;

public class Cleaner extends Person {
    private static final String path = "./src/main/resources/cleaner.png";
    private final int salary;
    private boolean inDuty = false;
    private Litter target = null;

    /**
     * Constructor of a Cleaner.
     * 
     * @param x coordinate x.
     * @param y coordinate y.
     */
    public Cleaner(int x, int y) {
        super(x, y, new ImageIcon(path).getImage());
        salary = 100;
    }

    public int getSalary() {
        return salary;
    }
    
    public boolean isInDuty() {
        return inDuty;
    }

    public void setInDuty(boolean inDuty) {
        this.inDuty = inDuty;
    }

    public Litter getTarget() {
        return target;
    }

    public void setTarget(Litter target) {
        this.target = target;
    }


}
