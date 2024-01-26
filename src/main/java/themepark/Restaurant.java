package themepark;

public class Restaurant extends Building {

    private int counter = 0;
    private final ResTypes type;

    /**
     * Constructor, setting up the details of an Restaurant.
     *
     * @param x    coordinate x.
     * @param y    coordinate y.
     * @param type type of the Restaurant.
     */
    public Restaurant(int x, int y, ResTypes type) {
        super(x - (x % 10), y - (y % 10),
                type.getWidth(), type.getHeight(),
                type.getImg(), type.getCapacity(),
                type.getPrice());
        this.type = type;
    }

    /**
     * Responsible for the interaction between the guest and restaurants.
     * 
     * @return with the price we will get.
     */
    public int servePeople() {
        int sum = 0;
        if (getPeopleInside().size() > 0) {
            counter++;
            if (counter == 3) {
                Guest guest = getPeopleInside().get(0);
                removeFromPeopleInside(guest);
                guest.changeHunger(type.getHungerBoost());
                guest.changeThirst(type.getThirstBoost());
                guest.changeMood(type.getMoodBoost());
                sum += type.getPrice();
                guest.setX(getNeighboringRoad().getX());
                guest.setY(getNeighboringRoad().getY());
                guest.setHasLitter(true);
                guest.setInsideBuilding(false);
                counter = 0;
            }
        }
        return sum;
    }

    /**
     * Changing the state of guests inside the restaurant.
     * 
     * @param guest is a guest.
     */
    public void enterBuilding(Guest guest) {
        if (type.getCapacity() == getPeopleInside().size()) {
            guest.changeMood(-5);
            guest.setX(getNeighboringRoad().getX());
            guest.setY(getNeighboringRoad().getY());
        } else {
            guest.setInsideBuilding(true);
            addToPeopleInside(guest);
        }
    }

    public ResTypes getType() {
        return type;
    }

}
