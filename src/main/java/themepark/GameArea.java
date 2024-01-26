package themepark;

import org.w3c.dom.Attr;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;
import javax.imageio.ImageIO;


/**
 * Class for storing and handling game component datas, and display them.
 * Storing all the buildings, guests, staff, parkitems.
 * Annoting the properties of events.
 */
public class GameArea extends JPanel {

    private final Image background;
    private final PathFinder pathFinder;
    private final ArrayList<Person> guestList = new ArrayList<>();
    private final ArrayList<Person> staffList = new ArrayList<>();
    private final ArrayList<ParkItems> parkItems = new ArrayList<>();
    private final ArrayList<Road> roads = new ArrayList<>();
    private final ArrayList<Litter> litters = new ArrayList<>();
    private final ArrayList<Building> buildings = new ArrayList<>();
    private final ArrayList<Attraction> theRepairables = new ArrayList<>();

    private int currPosX;
    private int currPosY;
    private int currWidth;
    private int currHeight;
    private boolean inAction;
    private int restCount = 0;
    private int attrCount = 0;

    private int budget;
    private int counter = 0;
    private boolean paused = false;
    private boolean opened = false;

//    private final int xCoord = 900;
//    private final int yCoord = 890;
    private final int xCoord = 700;
    private final int yCoord = 670;

    /**
     * Constructor, setting up the appearance and budget.
     */
    public GameArea() {
        background = new ImageIcon("./src/main/resources/land_base.png").getImage();
        for (int i = 0; i < 2; ++i) {
            roads.add(new Road(xCoord, yCoord - i * 40));
        }
        this.budget = 100000;
        pathFinder = new PathFinder(roads, buildings);
        Timer newFrameTimer = new Timer(50, new NewFrameListener());
        newFrameTimer.start();
        Timer t2 = new Timer(10, e -> chooseAction());
        t2.start();
        Timer t1 = new Timer(5000, e -> moodHandler());
        t1.start();
        Timer t3 = new Timer(15000, e -> salaryHandler());
        t3.start();
        Timer t8 = new Timer(1000, e -> buildingHandler());
        t8.start();
        Timer t4 = new Timer(20000, e -> amortizationHandler());
        t4.start();
        Timer t5 = new Timer(5000, e -> newGuestHandler());
        t5.start();
        Timer t6 = new Timer(300, e -> walkToDestination());
        t6.start();
        Timer t7 = new Timer(5000, e -> throwLitter());
        t7.start();
    }

    /**
     * Responsible for drawing all the buildings, parkitems.
     * In case of building action, draws the possible location of the
     * new object.
     *
     * @param g Graphics object of the JPanel.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, 2000, 1000, null);

        for (Building build : buildings) {
            build.draw(g);
        }

        for (ParkItems parkItem : parkItems) {
            parkItem.draw(g);
        }

        for (Road road : roads) {
            road.draw(g);
        }

        for (Litter litter : litters) {
            litter.draw(g);
        }

        for (Person p : staffList) {
            p.draw(g);
        }

        for (Person p : guestList) {
            if (!((Guest) p).isInsideBuilding()) {
                p.draw(g);
            }
        }

        if (inAction) {
            if (currPosX == 0) currPosX += 10;
            else if (currPosX + currWidth >= 1790) currPosX = 1780 - currWidth;

            if (currPosY == 0) currPosY += 10;
            else if (currPosY + currHeight >= 930) currPosY = 930 - currHeight;

            g.drawRect(currPosX, currPosY, currWidth, currHeight);
        }
        repaint();
    }

    /**
     * Changing state of Attractions (based on condition) and
     * states of Guests (based on mood).
     */
    class NewFrameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!paused && opened) {
                for (Building b : buildings) {
                    if (b instanceof Attraction) {
                        Attraction a = (Attraction) b;
                        if (a.needRepair() && !theRepairables.contains(a)) {
                            theRepairables.add(a);
                            if (!a.isDown()) {
                                try {
                                    a.setImage(ImageIO.read(new File("./src/main/resources/" + ((Attraction) b).getType() + "_DOWN.png")));
                                } catch (IOException ex) {
                                    ex.getStackTrace();
                                }
                                a.setDown(true);
                            }
                        }
                    }
                }
                for (Person p : guestList) {
                    if (!((Guest) p).isLeaving()) {
                        if (((Guest) p).getMood() <= 50 && ((Guest) p).getMood() > 0 && !((Guest) p).getCurrColor().equals("YELLOW")) {
                            p.setImage(new ImageIcon("./src/main/resources/guest_y.png").getImage());
                            ((Guest) p).setCurrColor("YELLOW");
                        } else if (((Guest) p).getMood() <= 0 && !((Guest) p).getCurrColor().equals("RED")) {
                            p.setImage(new ImageIcon("./src/main/resources/guest_r.png").getImage());
                            ((Guest) p).setCurrColor("RED");
                            if (!p.isHasDestination()) {
                                ((Guest) p).setLeaving(true);
                                addDestination(p, new Road(getxCoord(), getyCoord()));
                            }
                        } else if (((Guest) p).getMood() > 50 && !((Guest) p).getCurrColor().equals("GREEN")) {
                            p.setImage(new ImageIcon("/src/main/resources/guest_g.png").getImage());
                            ((Guest) p).setCurrColor("GREEN");
                        }
                    }
                }
                guestList.removeIf(g -> ((Guest) g).getCurrColor().equals("RED") && g.getX() == xCoord && g.getY() == yCoord);
            }
        }
    }

    /**
     * Changing mood of guests.
     */
    public void moodHandler() {
        if (!paused && opened) {
            for (Person p : guestList) {
                ((Guest) p).changeHunger(-2);
                ((Guest) p).changeThirst(-5);

                int moodboost = ((Guest) p).getHunger() - 100 / 40 + ((Guest) p).getThirst() - 100 / 40 + ((Guest) p).moodModifiersNearby(parkItems, litters);
                ((Guest) p).changeMood(((Guest) p).getMood() + moodboost);
            }
        }
    }

    /**
     * Reduces our money with each building's expenses and
     * each staff's salary.
     */
    public void salaryHandler() {
        if (!paused && opened) {
            for (Building b : buildings) {
                if (b instanceof Attraction) {
                    setFinances(-((Attraction) b).getExpenses());
                }
            }

            for (Person p : staffList) {
                if (p instanceof Cleaner) setFinances(-((Cleaner) p).getSalary());
                if (p instanceof Repairer) setFinances(-((Repairer) p).getSalary());
            }
        }
    }

    /**
     * Decreases the condition of Attractions.
     */
    public void amortizationHandler() {
        if (!paused && opened) {
            for (Building b : buildings) {
                if (b instanceof Attraction && b.getPeopleInside().isEmpty() && ((Attraction) b).getQueue().isEmpty()) {
                    ((Attraction) b).modifyCondition(-((Attraction) b).getAmortization());
                }
            }
        }
    }

    /**
     * A new guest spawning at the entrance.
     */
    private void newGuestHandler() {
        if (!paused && opened) {
            if (guestList.size() < 50) {
                putNewPerson(new Guest(xCoord, yCoord));
            }
        }
    }

    /**
     * Responsible for entertaining or serving guests and
     * raising our money.
     */
    private void buildingHandler() {
        if (!paused && opened) {
            for (Building b : buildings) {
                if (b instanceof Attraction) {
                    Attraction attr = (Attraction) b;
                    int summary = attr.entertain(getxCoord(), getyCoord());
                    setFinances(summary);
                } else {
                    Restaurant restr = (Restaurant) b;
                    int summary = restr.servePeople();
                    setFinances(summary);
                }
            }
        }
    }

    /**
     * The guests are deciding what they want to do and
     * which Attracion/Restaurant they want to go and
     * the staffs are also getting a destination.
     */
    private void chooseAction() {
        if (!paused && opened) {
            for (Person p : guestList) {
                if (!p.isHasDestination() && !((Guest) p).isInLine() && !((Guest) p).isInsideBuilding()) {
                    String decision = ((Guest) p).decideOnAction();
                    if (buildings.isEmpty() || buildings.size() == theRepairables.size()) {
                        if (p.getX() == getxCoord() && p.getY() == getyCoord()) {
                            ((Guest) p).changeMood(-100);
                        } else {
                            addDestination(p, roads.get(0));
                        }
                    } else {
                        switch (decision) {
                            case "CONSUME":
                                if (restCount > 0) {
                                    Restaurant destination = findRestaurant();
                                    addGuestDestination((Guest) p, destination);
                                } else {
                                    ((Guest) p).changeMood(-10);
                                }
                                break;
                            case "PLAY":
                                if (attrCount > 0) {
                                    Attraction attrDestination = findAttraction();
                                    addGuestDestination((Guest) p, attrDestination);
                                } else {
                                    ((Guest) p).changeMood(-10);
                                }
                                break;
                            default:
                                throw new RuntimeException("Should not run into this\n");
                        }
                    }
                }
            }
            for (Person p : staffList) {
                if (p instanceof Cleaner) {
                    staffSearchDuty(p);
                    if (!((Cleaner) p).isInDuty() && !p.isHasDestination()) {
                        addDestination(p, randomDestination());
                    }
                } else if (p instanceof Repairer) { // Ez ugyanaz, mint felette
                    staffSearchDuty(p);
                    if (!((Repairer) p).isInDuty() && !p.isHasDestination()) {
                        addDestination(p, randomDestination());
                    }
                }
            }
        }
    }

    /**
     * The guests are throwing away their litters.
     */
    private void throwLitter() {
        if (!paused && opened) {
            for (Person p : guestList) {
                if (((Guest) p).isHasLitter() && !((Guest) p).isInsideBuilding() && !((Guest) p).isInLine()) {
                    ((Guest) p).setHasLitter(false);
                    Litter l = new Litter(p.getX(), p.getY());
                    if (!l.trashcanNearby(parkItems)) {
                        putNewLitter(l);
                        System.out.println(guestList.indexOf(p) + " ELDOBJA a szemetet");
                    }
                }
            }
        }
    }

    /**
     * Finds a random road destination for staff, if they
     * don't have any duty.
     *
     * @return with that destination.
     */
    private Road randomDestination() {
        int range = roads.size();
        int rnd = (int) (Math.random() * range);
        return roads.get(rnd);
    }

    /**
     * The guests are getting their destinations.
     * 
     * @param g is a guest.
     * @param b is a building.
     */
    private void addGuestDestination(Guest g, Building b) {
        Deque<Node> destination = pathFinder.bfs(g, b);
        if (destination == null) {
            g.changeMood(-20);
            g.setTargetBuilding(null);
            g.setHasDestination(false);
            g.setDestination(null);
            return;
        }
        g.setTargetBuilding(b);
        g.setDestination(destination);
        g.setHasDestination(true);
    }

    /**
     * Persons are getting their destinations (mostly a road).
     * 
     * @param p is a person.
     * @param s is a sprite.
     */
    private void addDestination(Person p, Sprite s) {
        if (p instanceof Guest) ((Guest) p).setTargetBuilding(null);
        Deque<Node> destination = pathFinder.bfs(p, s);
        if (destination == null) {
            p.setHasDestination(false);
            return;
        }
        p.setDestination(destination);
        p.setHasDestination(true);
    }

    /**
     * Every person will walk to their destination.
     */
    private void walkToDestination() {
        if (!paused && opened) {
            for (Person p : guestList) {
                Guest g = (Guest) p;
                if (g.getDestination() != null && !g.getDestination().isEmpty()) {
                    Node direction = g.getDestination().poll();
                    System.out.println(direction.getSprite().getX() + " " + direction.getSprite().getY());
                    g.setX(direction.getSprite().getX());
                    g.setY(direction.getSprite().getY());
                } else {
                    if (g.getTargetBuilding() instanceof Restaurant) {
                        ((Restaurant) g.getTargetBuilding()).enterBuilding((Guest) p);
                    } else if (g.getTargetBuilding() instanceof Attraction) {
                        ((Attraction) g.getTargetBuilding()).addToQueue((Guest) p);
                    }
                    g.setTargetBuilding(null);
                    g.setHasDestination(false);
                }
            }

            for (Person p : staffList) {
                if (p instanceof Cleaner) {
                    if (!p.getDestination().isEmpty() && p.getDestination() != null) {
                        Node direction = p.getDestination().poll();
                        System.out.println(direction.getSprite().getX() + " " + direction.getSprite().getY());
                        p.setX(direction.getSprite().getX());
                        p.setY(direction.getSprite().getY());
                    } else {
                        p.setHasDestination(false);
                    }
                } else if (p instanceof Repairer) {
                    if (!p.getDestination().isEmpty() && p.getDestination() != null) {
                        Node direction = p.getDestination().poll();
                        System.out.println(direction.getSprite().getX() + " " + direction.getSprite().getY());
                        p.setX(direction.getSprite().getX());
                        p.setY(direction.getSprite().getY());
                    } else {
                        p.setHasDestination(false);
                    }
                }
            }
        }
    }

    /**
     * Method to find work for both Cleaners and Repairers.
     * Randomly selects the target litter, or the target repairable.
     * If the target litter/building is not underprocess, than reserves for p staff and
     * cleanes it up /repairs it.
     *
     * @param p person from staffList
     */
    private void staffSearchDuty(Person p) {
        if (p instanceof Cleaner) {
            if (!((Cleaner) p).isInDuty()) {
                if (!litters.isEmpty()) {
                    int range = litters.size();
                    int rnd = (int) (Math.random() * range);
                    Litter l = litters.get(rnd);
                    if (!l.isUnderProcess()) {
                        l.setUnderProcess(true);
                        ((Cleaner) p).setTarget(l);
                        addDestination(p, l);
                        ((Cleaner) p).setInDuty(true);
                    }
                }
            }
            if (staffAtTarget(p) && !litters.isEmpty()) {
                litters.remove(((Cleaner) p).getTarget());
                ((Cleaner) p).setTarget(null);
                ((Cleaner) p).setInDuty(false);
            }
        } else if (p instanceof Repairer) {
            if (!((Repairer) p).isInDuty()) {
                if (!theRepairables.isEmpty()) {
                    int range = theRepairables.size();
                    int rnd = (int) (Math.random() * range);
                    Attraction a = theRepairables.get(rnd);
                    if (!a.isUnderRepairment()) {
                        a.setUnderRepairment(true);
                        ((Repairer) p).setTarget(a);
                        addDestination(p, a);
                        ((Repairer) p).setInDuty(true);
                    }
                }
            }
            if (staffAtTarget(p) && !theRepairables.isEmpty()) {
                theRepairables.remove(((Repairer) p).getTarget());
                ((Repairer) p).getTarget().repair();
                ((Repairer) p).getTarget().setUnderRepairment(false);
                ((Repairer) p).setTarget(null);
                ((Repairer) p).setInDuty(false);
            }
        }

    }

    /**
     * Determines whether the staff is at their target.
     *
     * @param p Person from staffList.
     * @return true if staff p is at her destination.
     */
    private boolean staffAtTarget(Person p) {
        if (p instanceof Cleaner) {
            if (((Cleaner) p).getTarget() != null) {
                return (p.getX() == ((Cleaner) p).getTarget().getX() &&
                        p.getY() == ((Cleaner) p).getTarget().getY());
            }
        } else if (p instanceof Repairer) {
            if (((Repairer) p).getTarget() != null) {
                return (p.getX() == ((Repairer) p).getTarget().getX() &&
                        p.getY() == ((Repairer) p).getTarget().getY());
            }
        }
        return false;
    }

    /**
     * Choose a restaurant from buildings.
     * 
     * @return the restaurant if exisit one.
     */
    public Restaurant findRestaurant() {
        ArrayList<Restaurant> temp = new ArrayList<>();
        for (Building b : buildings) {
            if (b instanceof Restaurant) temp.add((Restaurant) b);
        }
        if (temp.size() >= 1) {
            Random rand = new Random();
            int x = rand.nextInt(temp.size());
            return temp.get(x);
        }
        return null;
    }

    /**
     * Choose an attracion from buildings.
     * 
     * @return the attraction if exisit one.
     */
    public Attraction findAttraction() {
        ArrayList<Attraction> temp = new ArrayList<>();
        for (Building b : buildings) {
            if (b instanceof Attraction && !((Attraction) b).isDown()) temp.add((Attraction) b);
        }
        if (temp.size() >= 1) {
            Random rand = new Random();
            int x = rand.nextInt(temp.size());
            return temp.get(x);
        }
        return null;
    }

    /**
     * Sets the current data in the case of FunTypes type objects.
     *
     * @param x    coordinate x.
     * @param y    coordinate y.
     * @param item source of event.
     */
    public void setCurrData(int x, int y, FunTypes item) {
        this.currPosX = x - (x % 10);
        this.currPosY = y - (y % 10);
        this.currWidth = item.getWidth();
        this.currHeight = item.getHeight();
    }


    /**
     * Sets the current data in the case of ResTypes type objects.
     *
     * @param x    coordinate x.
     * @param y    coordinate y.
     * @param item source of event.
     */
    public void setCurrData(int x, int y, ResTypes item) {
        this.currPosX = x - (x % 10);
        this.currPosY = y - (y % 10);
        this.currWidth = item.getWidth();
        this.currHeight = item.getHeight();
    }


    /**
     * Sets the current data in the case of ParkTypes type objects.
     *
     * @param x    coordinate x.
     * @param y    coordinate y.
     * @param item source of event.
     */
    public void setCurrData(int x, int y, ParkTypes item) {
        this.currPosX = x - (x % 10);
        this.currPosY = y - (y % 10);
        this.currWidth = item.getWidth();
        this.currHeight = item.getHeight();
    }

    /**
     * Sets the current data in the case of Road type objects.
     *
     * @param x coordinate x.
     * @param y coordinate y.
     * @param r source of event.
     */
    public void setCurrData(int x, int y, Road r) {
        this.currPosX = x - (x % 10);
        this.currPosY = y - (y % 10);
        this.currWidth = r.getWidth();
        this.currHeight = r.getHeight();
    }

    /**
     * Removes the event handler, and "leaves" a certain action.
     *
     * @param listenOne listener to be removed.
     */
    public void removeListener(MouseMotionListener listenOne) {
        this.removeMouseMotionListener(listenOne);
        this.inAction = false;
        this.currWidth = 0;
        this.currHeight = 0;
    }

    /**
     * Adds an Attraction to the Building List.
     *
     * @param a attraction to be added.
     */
    public void putNewItem(Attraction a) {
        if (enoughMoney(a.getPrice()) && !intersects(a)) {
            setFinances(-a.getPrice());
            buildings.add(a);
            setNeighboringRoad(a);
            rebuildPaths();
            attrCount++;
        }
    }

    /**
     * Adds a Restaurant to the Building List.
     *
     * @param r Restaurant to be added.
     */
    public void putNewItem(Restaurant r) {
        if (enoughMoney(r.getPrice()) && !intersects(r)) {
            setFinances(-r.getPrice());
            buildings.add(r);
            setNeighboringRoad(r);
            rebuildPaths();
            restCount++;
        }
    }

    /**
     * Adds a ParkItem to the ParkItem list.
     *
     * @param p ParkItem to be added.
     */
    public void putNewParkItem(ParkItems p) {
        if (enoughMoney(p.getPrice()) && !intersects(p)) {
            setFinances(-p.getPrice());
            parkItems.add(p);
        }
    }

    /**
     * Adds up a new piece of road.
     *
     * @param r Road to be added.
     */
    public void putNewRoad(Road r) {
        if (enoughMoney(Road.getPrice()) && !intersects(r) && fitToRoad(r)) {
            setFinances(-Road.getPrice());
            roads.add(r);
            setNeighboringRoads();
            rebuildPaths();
        }
    }

    /**
     * Add a litter to litters, so it will appear on board.
     * 
     * @param l is a litter.
     */
    public void putNewLitter(Litter l) {
        litters.add(l);
    }

    /**
     * Add a worker to the stafflist.
     * 
     * @param p is a person.
     */
    public void putNewPerson(Person p) {
        if (p instanceof Cleaner || p instanceof Repairer) {
            if (enoughMoney(100)) {
                setFinances(-100);
                staffList.add(p);
            }
        } else {
            setFinances(100);
            guestList.add(p);
        }
    }

    /**
     * Delete a worker from the stafflist.
     * 
     * @param type is a staff member's type.
     */
    public void deletePerson(String type) {
        switch (type) {
            case "repairer":
                for (Person p : staffList) {
                    if (p instanceof Repairer) {
                        staffList.remove(p);
                        return;
                    }
                }
                break;
            case "cleaner":
                for (Person p : staffList) {
                    if (p instanceof Cleaner) {
                        staffList.remove(p);
                        return;
                    }
                }
                break;
            default:
                guestList.remove(0);
        }
    }

    /**
     * Observing the collision of buildings and of a certain Sprite object.
     *
     * @param other a sprite which is observed.
     * @return True if other collides with any building.
     */
    public boolean intersects(Sprite other) {
        for (Building b : buildings) {
            if (b.collides(other)) return true;
        }
        for (ParkItems p : parkItems) {
            if (p.collides(other)) return true;
        }
        for (Road r : roads) {
            if (r.collides(other)) return true;
        }

        return false;
    }

    /**
     * Checking whether the road tile under construction fits to the sidewalk.
     *
     * @param other piece of Road to build.
     * @return True if accuretaly fits.
     */
    public boolean fitToRoad(Road other) {
        for (Road r : roads) {
            int dX = (int) Math.sqrt(Math.pow((r.getX() - other.getX()), 2));
            int dY = (int) Math.sqrt(Math.pow((r.getY() - other.getY()), 2));
            if (dX == 40 && dY == 0) return true;
            else if (dX == 0 && dY == 40) return true;
        }
        return false;
    }

    /**
     * Removes the intersecting item, which has been clicked on.
     *
     * @param other temporary sprite around mouse.
     */
    public void removeIntersecting(Sprite other) {
        for (Building b : buildings) {
            if (b.collides(other)) {
                if (b instanceof Restaurant) restCount--;
                else if (b instanceof Attraction) {
                    attrCount--;
                    Road road;
                    if ((road = b.getNeighboringRoad()) != null) {
                        for (Guest g : ((Attraction) b).getQueue()) {
                            g.setInLine(false);
                            g.setX(road.getX());
                            g.setY(road.getY());
                        }
                    } else {
                        for (Guest g : ((Attraction) b).getQueue()) {
                            g.setInLine(false);
                            g.setX(getxCoord());
                            g.setY(getyCoord());
                        }
                    }
                }
                for (Person p : guestList) {
                    Guest g = (Guest) p;
                    if (g.getTargetBuilding() == b) {
                        g.setHasDestination(false);
                        g.setTargetBuilding(null);
                        g.setDestination(null);
                        break;
                    }
                }
                Road road;
                if ((road = b.getNeighboringRoad()) != null) {
                    for (Guest g : b.getPeopleInside()) {
                        g.setInsideBuilding(false);
                        g.setX(road.getX());
                        g.setY(road.getY());
                    }
                } else {
                    for (Guest g : b.getPeopleInside()) {
                        g.setInsideBuilding(false);
                        g.setX(getxCoord());
                        g.setY(getyCoord());
                    }
                }
                buildings.remove(b);
                rebuildPaths();
                return;
            }
        }
        for (ParkItems item : parkItems) {
            if (item.collides(other)) {
                parkItems.remove(item);
                return;
            }
        }
        for (Road r : roads) {
            if (r.collides(other)) {
                if (r.getX() != xCoord || r.getY() != yCoord) {
                    roads.remove(r);
                    setNeighboringRoads();
                    rebuildPaths();
                    break;
                }
            }
        }
    }

    /**
     * Decides which route will the persons choose to get their destinations.
     */
    private void rebuildPaths() {
        for (Person p : guestList) {
            Guest g = (Guest) p;
            if (g.isHasDestination()) {
                addGuestDestination(g, g.getTargetBuilding());
            }
        }
        for (Person p : staffList) {
            if (p.isHasDestination()) {
                Node destination = p.getDestination().getLast();
                addDestination(p, destination.getSprite());
            }
        }
    }

    /**
     * Assignes the neighboring roads to all building.
     */
    private void setNeighboringRoads() {
        for (Building b : buildings) {
            setNeighboringRoad(b);
        }
    }

    /**
     * Assignes the neighboring roads to a building.
     * @param b is a building.
     */
    private void setNeighboringRoad(Building b) {
        for (Road r : roads) {
            if (b.isNeighbor(r)) {
                b.setNeighboringRoad(r);
                return;
            }
        }
        b.setNeighboringRoad(null);
    }
    
    /**
     * Checking whether the park have enough money to hire new staff.
     *
     * @return true if we have more than 10 unit of salary.
     */
    public boolean enoughMoney(int price) {
        return budget >= price;
    }

    public boolean isInAction() {
        return inAction;
    }

    public void setInAction(boolean inAction) {
        this.inAction = inAction;
    }

    public void setCurrHeight(int currHeight) {
        this.currHeight = currHeight;
    }

    public void setCurrWidth(int currWidth) {
        this.currWidth = currWidth;
    }

    public int getFinances() {
        return budget;
    }

    public void setFinances(int money) {
        budget += money;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public ArrayList<Person> getStaffList() {
        return staffList;
    }

    public ArrayList<ParkItems> getParkItems() {
        return parkItems;
    }

    public ArrayList<Road> getRoads() {
        return roads;
    }

    public ArrayList<Building> getBuildings() {
        return buildings;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public ArrayList<Litter> getLitters() {
        return litters;
    }
}
