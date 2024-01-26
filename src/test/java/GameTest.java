import org.junit.Before;
import themepark.*;
import org.junit.Test;
import java.util.Deque;
import java.util.Random;

import static org.junit.Assert.*;


public class GameTest {

    private GameArea g;

    @Before
    public void setupGame() {
        g = new GameArea();
    }

    @Test
    public void testAttractionBuilding() {
        Random rand = new Random();
        int n = rand.nextInt(10);
        for (int i = 0; i < n; ++i) {
            g.putNewItem(new Attraction(10 + (FunTypes.FERRIS_WHEEL.getWidth() * i), 10, FunTypes.FERRIS_WHEEL));
        }
        assertEquals(n, g.getBuildings().size());
    }

    @Test
    public void testAttractionBuildingMoneyReduction() {
        int startingMoney = g.getFinances();
        g.putNewItem(new Attraction(10, 10, FunTypes.CAROUSEL));
        assertEquals(startingMoney - FunTypes.CAROUSEL.getPrice(), g.getFinances());
    }

    @Test
    public void testAttractionAmortization() {
        g.setOpened(true); //?
        Attraction a = new Attraction(10, 10, FunTypes.HAUNTED_HOUSE);
        g.putNewItem(a);
        int initialCondition = a.getCondition();
        g.amortizationHandler();
        assertEquals(initialCondition - a.getAmortization(), a.getCondition());
    }

    @Test
    public void testRestaurantBuilding() {
        Random rand = new Random();
        int n = rand.nextInt(10);
        for (int i = 0; i < n; ++i) {
            g.putNewItem(new Restaurant(10 + (ResTypes.PIZZERIA.getWidth() * i), 10, ResTypes.PIZZERIA));
        }
        assertEquals(n, g.getBuildings().size());
    }

    @Test
    public void testRestaurantBuildingMoneyReduction() {
        int startingMoney = g.getFinances();
        g.putNewItem(new Restaurant(10, 10, ResTypes.SMOOTHIE_STAND));
        assertEquals(startingMoney - ResTypes.SMOOTHIE_STAND.getPrice(), g.getFinances());
    }

    @Test
    public void testParkItemBuilding() {
        Random rand = new Random();
        int n = rand.nextInt(10);
        for (int i = 0; i < n; ++i) {
            g.putNewParkItem(new ParkItems(10 + (ParkTypes.BUSH.getWidth() * i), 10, ParkTypes.BUSH));
        }
        assertEquals(n, g.getParkItems().size());
    }

    @Test
    public void testParkItemBuildingMoneyReduction() {
        int startingMoney = g.getFinances();
        g.putNewParkItem(new ParkItems(10, 10, ParkTypes.TRASHCAN));
        assertEquals(startingMoney - ParkTypes.TRASHCAN.getPrice(), g.getFinances());
    }

    @Test
    public void testRoadBuilding() {
        g.putNewRoad(new Road(g.getxCoord(), g.getyCoord() - (g.getRoads().size() * 40)));
        assertEquals(3, g.getRoads().size());
    }

    @Test
    public void testRoadBuildingMoneyReduction() {
        int startingMoney = g.getFinances();
        Road r = new Road(g.getxCoord(), g.getyCoord() - (g.getRoads().size() * 40));
        g.putNewRoad(r);
        assertEquals(startingMoney - Road.getPrice(), g.getFinances());
    }

    @Test
    public void testBuildingWithoutEnoughMoney() {
        g.setBudget(0);
        Road r = new Road(g.getxCoord(), g.getyCoord() - (g.getRoads().size() * 40));
        g.putNewRoad(r);
        assertEquals(2, g.getRoads().size());
    }

    @Test
    public void testIfNotFitsToRoad() {
        Road r = new Road(10, 320);
        g.putNewRoad(r);
        assertEquals(2, g.getRoads().size());
    }

    @Test
    public void testBuildingIntersection() {
        Random rand = new Random();
        int n = rand.nextInt(10 - 1) + 1;
        for (int i = 0; i < n; ++i) {
            g.putNewParkItem(new ParkItems(10, 10, ParkTypes.BUSH));
        }
        assertEquals(1, g.getParkItems().size());
    }

    @Test
    public void testBuildingOutsideBorder() {
        Attraction temp = new Attraction(0, 0, FunTypes.FERRIS_WHEEL);
        temp.changeIfOutsideBorder();
        assertTrue(temp.getX() == 10 && temp.getY() == 10);
    }

    @Test
    public void testDestroying() {
        Sprite temp = new Sprite(300, 300, 1, 1, null);
        Attraction atr = new Attraction(300, 300, FunTypes.DODGEM);
        g.putNewItem(atr);
        g.removeIntersecting(temp);
        assertEquals(0, g.getBuildings().size());
    }

    @Test
    public void testHiring() {
        Random rand = new Random();
        int n = rand.nextInt(10);
        for (int i = 0; i < n; ++i) {
            if (i % 2 == 0) {
                g.putNewPerson(new Cleaner(i * 10, 20));
            } else {
                g.putNewPerson(new Repairer(i * 10, 20));
            }
        }
        assertEquals(n, g.getStaffList().size());
    }

    @Test
    public void testHiringMoneyReduction() {
        int startingMoney = g.getFinances();
        Repairer p = new Repairer(g.getxCoord(), g.getyCoord());
        g.putNewPerson(p);
        assertEquals(startingMoney - p.getSalary(), g.getFinances());
    }

    @Test
    public void testDismissal() {
        g.putNewPerson(new Repairer(g.getxCoord(), g.getyCoord()));
        g.deletePerson("repairer");
        g.putNewPerson(new Cleaner(g.getxCoord(), g.getyCoord()));
        assertEquals(1, g.getStaffList().size());
    }

    @Test
    public void testMoodModifiersNearby() {
        g.setOpened(true);
        Guest g1 = new Guest(g.getxCoord(), g.getyCoord());
        g.putNewPerson(g1);
        g.putNewParkItem(new ParkItems(g.getxCoord() - ParkTypes.BUSH.getWidth(), g.getyCoord(), ParkTypes.BUSH));
        int x = g1.moodModifiersNearby(g.getParkItems(), g.getLitters());
        assertEquals(10, x);
    }

    @Test
    public void testMoodChange() {
        g.setOpened(true);
        Guest g1 = new Guest(g.getxCoord(), g.getyCoord());
        g.putNewPerson(g1);
        int initialMood = g1.getMood();
        g.moodHandler();
        int moodChange = g1.getHunger() - 100 / 10 + g1.getThirst() - 100 / 10 + g1.moodModifiersNearby(g.getParkItems(), g.getLitters());
        int actualMood = Math.min(initialMood + moodChange, 100);
        assertEquals(actualMood, g1.getMood());
    }

    @Test
    public void testHungerAndThirstChange() {
        g.setOpened(true);
        Guest g1 = new Guest(g.getxCoord(), g.getyCoord());
        g.putNewPerson(g1);
        int initialHunger = g1.getHunger();
        int initialThirst = g1.getThirst();
        g.moodHandler();
        assertTrue(initialHunger - 2 == g1.getHunger() && initialThirst - 5 == g1.getThirst());
    }

    @Test
    public void testDecideOnActionHunger() {
        g.setOpened(true);
        Guest g1 = new Guest(g.getxCoord(), g.getyCoord());
        g.putNewPerson(g1);
        g1.changeHunger(-70);
        String decision = g1.decideOnAction();
        assertEquals("CONSUME", decision);
    }

    @Test
    public void testDecideOnActionThirst() {
        g.setOpened(true);
        Guest g1 = new Guest(g.getxCoord(), g.getyCoord());
        g.putNewPerson(g1);
        g1.changeThirst(-70);
        String decision = g1.decideOnAction();
        assertEquals("CONSUME", decision);
    }

    @Test
    public void testDecideOnActionPlay() {
        g.setOpened(true);
        Guest g1 = new Guest(g.getxCoord(), g.getyCoord());
        g.putNewPerson(g1);
        g1.changeMood(-70);
        String decision = g1.decideOnAction();
        assertEquals("PLAY", decision);
    }

    @Test
    public void testDecideOnAttraction() {
        g.setOpened(true);
        for (int i = 0; i < 10; ++i) {
            Attraction attr = new Attraction(10 + i * FunTypes.CAROUSEL.getWidth(), 10, FunTypes.CAROUSEL);
            g.putNewItem(attr);
        }
        assertNotNull(g.findAttraction());
    }

    @Test
    public void testDecideOnRestaurant() {
        g.setOpened(true);
        for (int i = 0; i < 10; ++i) {
            Restaurant rest = new Restaurant(10 + i * ResTypes.PIZZERIA.getWidth(), 10, ResTypes.PIZZERIA);
            g.putNewItem(rest);
        }
        assertNotNull(g.findRestaurant());
    }

    @Test
    public void testDecideOnAttractionNotWorking() {
        g.setOpened(true);
        Attraction attr = new Attraction(10, 10, FunTypes.HAUNTED_HOUSE);
        g.putNewItem(attr);
        attr.setDown(true);
        assertNull(g.findAttraction());
    }

    @Test
    public void testExpenses() {
        g.setOpened(true);
        int startingMoney = g.getFinances();
        Cleaner c = new Cleaner(g.getxCoord(), g.getyCoord());
        g.putNewPerson(c);
        Attraction a = new Attraction(100, 100, FunTypes.DODGEM);
        g.putNewItem(a);
        g.salaryHandler();
        int totalCost = c.getSalary() * 2 + a.getExpenses() + a.getPrice();
        assertEquals(startingMoney - totalCost, g.getFinances());
    }

    @Test
    public void testReachablePathFinding() {
        Attraction a = new Attraction(g.getxCoord() + 40, g.getyCoord() - FunTypes.DODGEM.getHeight(), FunTypes.DODGEM);
        Guest guest = new Guest(g.getxCoord(), g.getyCoord());
        g.putNewItem(a);
        g.putNewPerson(guest);
        PathFinder p = new PathFinder(g.getRoads(), g.getBuildings());
        Deque<Node> destination = p.bfs(guest, a);
        Node last = destination.getLast();
        Sprite lastSprite = last.getSprite();
        assertTrue(lastSprite.getX() == a.getX() && lastSprite.getY() == a.getY());
    }

    @Test
    public void testNonReachablePathFinding() {
        Attraction a = new Attraction(g.getxCoord() + 80, g.getyCoord() - FunTypes.DODGEM.getHeight(), FunTypes.DODGEM);
        Guest guest = new Guest(g.getxCoord(), g.getyCoord());
        g.putNewItem(a);
        g.putNewPerson(guest);
        PathFinder p = new PathFinder(g.getRoads(), g.getBuildings());
        Deque<Node> destination = p.bfs(guest, a);
        assertNull(destination);
    }

    @Test
    public void testPause() {
        g.setOpened(true);
        g.setPaused(true);
        Guest g1 = new Guest(g.getxCoord(), g.getyCoord());
        g.putNewPerson(g1);
        int initialMood = g1.getMood();
        g.moodHandler();
        assertEquals(initialMood, g1.getMood());
    }
    
    @Test
    public void testOpenPark() {
        int startingMoney = g.getFinances();
        Cleaner c = new Cleaner(g.getxCoord(), g.getyCoord());
        g.putNewPerson(c);
        g.salaryHandler();
        g.setOpened(true);
        g.salaryHandler();
        int totalCost = c.getSalary() * 2;
        assertEquals(startingMoney - totalCost, g.getFinances());
    }
}