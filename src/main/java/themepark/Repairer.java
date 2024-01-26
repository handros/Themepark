package themepark;

import javax.swing.ImageIcon;

public class Repairer extends Person {
    private static final String path = "./src/main/resources/repairer.png";
    private final int salary = 100;
    private boolean inDuty = false;
    private Attraction target = null;

    /**
     * Constructor of a Repairer.
     * 
     * @param x coordinate x.
     * @param y coordinate y.
     */
    public Repairer(int x, int y) {
        super(x, y, new ImageIcon(path).getImage());
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

    public Attraction getTarget() {
        return target;
    }

    public void setTarget(Attraction target) {
        this.target = target;
    }


}
