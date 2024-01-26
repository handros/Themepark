package themepark;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainWindow extends JFrame implements ItemListener {

    private final static String ATTRACTIONPANEL = "Attractions";
    private final static String RESTAURANTPANEL = "Restaurants";
    private final static String PARKITEMSPANEL = "Park Items";
    private final static String HIREPANEL = "Hiring";
    private final static String OTHERPANEL = "Other tools";

    private final GameArea gameArea = new GameArea();
    private final JPanel sideMenu = new JPanel();


    private final JLabel time;
    private final JLabel money;
    private final JLabel hintLabel;

    private final JMenuItem pauseGame;
    private final JMenuItem openPark;

    private int clock = 0;

    /**
     * Constructor of the class.
     * Setting up the mainwindow and configuring different menus - for building
     * attractions, parkitems, hiring staff etc.
     * Assigning buttons for the proper menus.
     * Configure the design of containers.
     */
    public MainWindow() {
        setTitle("Theme Park Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
// --------------------------------------------------------
        JPanel comboBoxPanel = new JPanel();
        comboBoxPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1, false));
        String[] comboBoxItems = {ATTRACTIONPANEL, RESTAURANTPANEL, PARKITEMSPANEL, HIREPANEL, OTHERPANEL};
        JComboBox<String> cb = new JComboBox<>(comboBoxItems);
        cb.setEditable(false);
        cb.addItemListener(this);
        comboBoxPanel.add(cb);
// --------------------------------------------------------
        JPanel card1 = new JPanel();
        setupButton("FERRIS WHEEL", card1, new AttractionListener("ferris"));
        setupButton("DODGEM", card1, new AttractionListener("dodgem"));
        setupButton("CAROUSEL", card1, new AttractionListener("carousel"));
        setupButton("HAUNTED HOUSE", card1, new AttractionListener("haunted"));
        setupButton("ROLLER COASTER", card1, new AttractionListener("roller"));
// --------------------------------------------------------
        JPanel card2 = new JPanel();
        setupButton("PIZZERIA", card2, new RestaurantListener("pizzeria"));
        setupButton("SMOOTHIE STAND", card2, new RestaurantListener("smoothie"));
        setupButton("ICECREAM STAND", card2, new RestaurantListener("icecream"));
// --------------------------------------------------------
        JPanel card3 = new JPanel();
        setupButton("BUSH", card3, new ParkItemListener("bush"));
        setupButton("TREE", card3, new ParkItemListener("tree"));
        setupButton("TRASH", card3, new ParkItemListener("trash"));
// --------------------------------------------------------
        JPanel card4 = new JPanel();
        GridLayout gridLayout = new GridLayout(2, 1);
        card4.setLayout(gridLayout);

        JPanel cleanerContainer = new JPanel();
        cleanerContainer.add(new JLabel("Cleaner"));
        setupButton("+", cleanerContainer, new WorkersListener("cleaner"));
        setupButton("-", cleanerContainer, new DismissListener("cleaner"));
        card4.add(cleanerContainer);

        JPanel repairContainer = new JPanel();
        repairContainer.add(new JLabel("Repairman"));
        setupButton("+", repairContainer, new WorkersListener("repairer"));
        setupButton("-", repairContainer, new DismissListener("repairer"));
        card4.add(repairContainer);
// --------------------------------------------------------
        JPanel card5 = new JPanel();
        setupButton("DESTROY", card5, new DestroyListener());
        setupButton("ROAD BUILDING", card5, new RoadListener());
// --------------------------------------------------------
        sideMenu.setLayout(new CardLayout());
        sideMenu.setBorder(BorderFactory.createLineBorder(Color.black, 1, false));
        sideMenu.setPreferredSize(new Dimension(125, 600));
        sideMenu.add(card1, ATTRACTIONPANEL);
        sideMenu.add(card2, RESTAURANTPANEL);
        sideMenu.add(card3, PARKITEMSPANEL);
        sideMenu.add(card4, HIREPANEL);
        sideMenu.add(card5, OTHERPANEL);
// --------------------------------------------------------
        JMenuBar menuBar = new JMenuBar();
        JMenu menuGame = new JMenu("Menu");

        openPark = new JMenuItem(new AbstractAction("Open Park") {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameArea.setOpened(true);
                openPark.setVisible(false);
            }
        });

        pauseGame = new JMenuItem(new AbstractAction("Pause Game") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(gameArea.isOpened()) {
                    gameArea.setPaused(!gameArea.isPaused());

                    if(gameArea.isPaused()) {
                        pauseGame.setText("Unpause Game");
                    } else {
                        pauseGame.setText("Pause Game");
                    }
                }
            }
        });

        JMenuItem exit = new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuGame.add(pauseGame);
        menuGame.add(openPark);
        menuGame.add(exit);
        menuBar.add(menuGame);
// --------------------------------------------------------
        time = new JLabel("\tTime elapsed since game start: " + clock + " s");
        time.setPreferredSize(new Dimension(200, 30));
        time.setHorizontalAlignment(JLabel.LEFT);
        hintLabel = new JLabel("Left click - build / destroy                Right click - exit from view");
        hintLabel.setHorizontalAlignment(JLabel.CENTER);
        hintLabel.setVisible(false);
        money = new JLabel(gameArea.getFinances() + " $");
        money.setPreferredSize(new Dimension(200, 30));
        money.setHorizontalAlignment(JLabel.RIGHT);
        JPanel underBar = new JPanel();
        underBar.setLayout(new GridLayout(1, 3));
        underBar.setBorder(BorderFactory.createLineBorder(Color.black, 1, false));
        underBar.add(time);
        underBar.add(hintLabel);
        underBar.add(money);
// --------------------------------------------------------
        ImageIcon icon = new ImageIcon("./src/main/resources/wall.png");

        gameArea.setBorder(BorderFactory.createMatteBorder(10, 10, 0, 14, icon));

        getContentPane().add(gameArea, BorderLayout.CENTER);
        getContentPane().add(comboBoxPanel, BorderLayout.PAGE_START);
        getContentPane().add(sideMenu, BorderLayout.LINE_START);
        getContentPane().add(underBar, BorderLayout.PAGE_END);
        setJMenuBar(menuBar);

        pack();
        setExtendedState(this.getExtendedState() | MAXIMIZED_BOTH);
        setVisible(true);
// --------------------------------------------------------
        Timer synchronizer = new Timer(1000, new SyncListener());
        synchronizer.start();
    }

    /**
     * Setting up all the buttons for the game to build, destroy or hire.
     *
     * @param text           is the name of the button.
     * @param container      is the container of the menu.
     * @param actionListener defines the action of the button.
     */
    private void setupButton(String text, JPanel container, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Monospace", Font.BOLD, 10));
        button.setPreferredSize(new Dimension(125, 40));
        button.setMargin(new Insets(1, 1, 1, 1));
        button.setBackground(Color.WHITE);
        container.add(button);
        button.addActionListener(actionListener);
    }

    /**
     * Creating actionslisteners for the attraction builder buttons.
     */
    class AttractionListener implements ActionListener {

        private final String type;

        public AttractionListener(String type) {
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!gameArea.isInAction()) {
                switch (type) {
                    case "ferris":
                        buildFunHandler(FunTypes.FERRIS_WHEEL);
                        break;
                    case "dodgem":
                        buildFunHandler(FunTypes.DODGEM);
                        break;
                    case "carousel":
                        buildFunHandler(FunTypes.CAROUSEL);
                        break;
                    case "haunted":
                        buildFunHandler(FunTypes.HAUNTED_HOUSE);
                        break;
                    case "roller":
                        buildFunHandler(FunTypes.ROLLER_COASTER);
                        break;
                }
            }
        }
    }

    /**
     * Defining eventhandlers for restaurant builder buttons,
     * based on the type of restaurant.
     */
    class RestaurantListener implements ActionListener {

        private final String type;

        public RestaurantListener(String type) {
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!gameArea.isInAction()) {
                switch (type) {
                    case "pizzeria":
                        buildResHandler(ResTypes.PIZZERIA);
                        break;
                    case "smoothie":
                        buildResHandler(ResTypes.SMOOTHIE_STAND);
                        break;
                    case "icecream":
                        buildResHandler(ResTypes.ICECREAM_STAND);
                        break;
                }
            }
        }
    }

    /**
     * Defining eventhandlers for ParkItem buttons.
     */
    class ParkItemListener implements ActionListener {

        private final String type;

        public ParkItemListener(String type) {
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!gameArea.isInAction()) {
                switch (type) {
                    case "bush":
                        buildParkItemHandler(ParkTypes.BUSH);
                        break;
                    case "tree":
                        buildParkItemHandler(ParkTypes.TREE);
                        break;
                    case "trash":
                        buildParkItemHandler(ParkTypes.TRASHCAN);
                        break;
                }
            }
        }
    }

    /**
     * Creating eventhandler for staff hiring buttons.
     */
    class WorkersListener implements ActionListener {

        private final String type;

        public WorkersListener(String type) {
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!gameArea.isInAction()) hireHandler(type);
        }
    }

    /**
     * Eventhandler for the destroyer button.
     */
    class DestroyListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!gameArea.isInAction()) destroyHandler();
        }
    }

    /**
     * Eventhandler for the dismiss process of staff.
     */

    class DismissListener implements ActionListener {

        private final String type;

        public DismissListener(String type) {
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!gameArea.isInAction()) dismissHandler(type);
        }
    }

    /**
     * Eventhandler for the road builder button.
     */
    class RoadListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!gameArea.isInAction()) buildRoadHandler(new Road());
        }
    }

    /**
     * Defining eventhandlers for AttractionListeners.
     * Loads the proper attraction to the gui.
     *
     * @param item is the type of attraction.
     */
    void buildFunHandler(FunTypes item) {
        gameArea.setInAction(true);
        hintLabel.setVisible(true);

        MouseMotionListener mouseMotionListener = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                gameArea.setCurrData(e.getX(), e.getY(), item);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                gameArea.setCurrData(e.getX(), e.getY(), item);
            }
        };
        gameArea.addMouseMotionListener(mouseMotionListener);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Attraction temp = new Attraction(e.getX(), e.getY(), item);
                    temp.changeIfOutsideBorder();
                    gameArea.putNewItem(temp);
                    money.setText(gameArea.getFinances() + " $");
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    hintLabel.setVisible(false);
                    gameArea.removeMouseListener(this);
                    gameArea.removeListener(mouseMotionListener);
                }
            }
        };
        gameArea.addMouseListener(mouseAdapter);
    }

    /**
     * Defining action for restaurant builder buttons.
     *
     * @param item is the type of the restaurant.
     */
    void buildResHandler(ResTypes item) {
        gameArea.setInAction(true);
        hintLabel.setVisible(true);

        MouseMotionListener mouseMotionListener = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                gameArea.setCurrData(e.getX(), e.getY(), item);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                gameArea.setCurrData(e.getX(), e.getY(), item);
            }
        };
        gameArea.addMouseMotionListener(mouseMotionListener);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Restaurant temp = new Restaurant(e.getX(), e.getY(), item);
                    temp.changeIfOutsideBorder();
                    gameArea.putNewItem(temp);
                    money.setText(gameArea.getFinances() + " $");
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    hintLabel.setVisible(false);
                    gameArea.removeMouseListener(this);
                    gameArea.removeListener(mouseMotionListener);
                }
            }
        };
        gameArea.addMouseListener(mouseAdapter);
    }

    /**
     * Creating eventhandler for ParkItem listener.
     *
     * @param item is an object of a ParkItem.
     */

    void buildParkItemHandler(ParkTypes item) {
        gameArea.setInAction(true);
        hintLabel.setVisible(true);

        MouseMotionListener mouseMotionListener = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                gameArea.setCurrData(e.getX(), e.getY(), item);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                gameArea.setCurrData(e.getX(), e.getY(), item);
            }
        };
        gameArea.addMouseMotionListener(mouseMotionListener);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    ParkItems temp = new ParkItems(e.getX(), e.getY(), item);
                    temp.changeIfOutsideBorder();
                    gameArea.putNewParkItem(temp);
                    money.setText(gameArea.getFinances() + " $");
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    hintLabel.setVisible(false);
                    gameArea.removeMouseListener(this);
                    gameArea.removeListener(mouseMotionListener);
                }
            }
        };
        gameArea.addMouseListener(mouseAdapter);
    }

    /**
     * Eventhandler for roadlistener.
     */
    void buildRoadHandler(Road r) {
        gameArea.setInAction(true);
        hintLabel.setVisible(true);

        MouseMotionListener mouseMotionListener = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                gameArea.setCurrData(e.getX(), e.getY(), r);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                gameArea.setCurrData(e.getX(), e.getY(), r);
            }
        };
        gameArea.addMouseMotionListener(mouseMotionListener);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Road temp = new Road(e.getX(), e.getY());
                    temp.changeIfOutsideBorder();
                    gameArea.putNewRoad(temp);
                    money.setText(gameArea.getFinances() + " $");
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    hintLabel.setVisible(false);
                    gameArea.removeMouseListener(this);
                    gameArea.removeListener(mouseMotionListener);
                }
            }
        };
        gameArea.addMouseListener(mouseAdapter);

    }

    /**
     * Eventhandler for DestroyListener.
     * Implementing destroying.
     */
    void destroyHandler() {
        gameArea.setInAction(true);
        gameArea.setCurrWidth(0);
        gameArea.setCurrHeight(0);
        hintLabel.setVisible(true);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Sprite temp = new Sprite(e.getX() - (e.getX() % 10), e.getY() - (e.getY() % 10), 1, 1, null);
                    gameArea.removeIntersecting(temp);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    hintLabel.setVisible(false);
                    gameArea.setInAction(false);
                    gameArea.removeMouseListener(this);
                }
            }
        };
        gameArea.addMouseListener(mouseAdapter);
    }

    /**
     * Eventhandler for WorkerListener.
     */
    void hireHandler(String type) {
        Person temp;
        switch (type) {
            case "cleaner":
                temp = new Cleaner(gameArea.getxCoord(), gameArea.getyCoord());
                break;
            case "repairer":
                temp = new Repairer(gameArea.getxCoord(), gameArea.getyCoord());
                break;
            default:
                temp = null;
        }
        gameArea.putNewPerson(temp);
        money.setText(gameArea.getFinances() + " $");
    }

    /**
     * Managing dismiss process.
     */

    void dismissHandler(String type) {
        gameArea.deletePerson(type);
    }

    /**
     * Method for handling cardlayout.
     *
     * @param e selected/deselected menu item.
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        CardLayout cl = (CardLayout) (sideMenu.getLayout());
        cl.show(sideMenu, (String) e.getItem());
    }

    /**
     * Synchronize the labels with the gameArea inner data.
     */
    class SyncListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!gameArea.isPaused() && gameArea.isOpened()) {
                ++clock;
                money.setText(gameArea.getFinances() + " $");
                time.setText("\tTime elapsed since game start: " + clock + " s");
            }
        }
    }
}
