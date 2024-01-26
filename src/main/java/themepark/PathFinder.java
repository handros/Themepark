package themepark;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class provinding pathfinding method based on breadth-first search algorithm.
 */
public class PathFinder {

    private final ArrayList<Road> roads;
    private final ArrayList<Building> buildings;

    private ArrayList<Node> adjList;

    public PathFinder(ArrayList<Road> roads, ArrayList<Building> buildings) {
        this.roads = roads;
        this.buildings = buildings;

    }

    /**
     * Creates an adjacency list from the data of gamearea.
     */
    public void buildAdjList() {
        this.adjList = new ArrayList<>();
        for (Road r : roads) {
            adjList.add(new Node(r));
        }

        for (Building b : buildings) {
            adjList.add(new Node(b));
        }

        for (Node nd1 : adjList) {
            if (!nd1.isVisited()) {
                nd1.setVisited(true);
                Sprite sp1 = nd1.getSprite();

                for (Node nd2 : adjList) {
                    if (!nd2.isVisited()) {
                        Sprite sp2 = nd2.getSprite();
                        if (sp1.isNeighbor(sp2)) {
                            nd1.addNeighbor(nd2);
                            nd2.addNeighbor(nd1);

                        }
                    }
                }

            }

        }

        for (Node nd : adjList) {
            nd.setVisited(false);
        }

    }

    public Deque<Node> bfs(Person p, Sprite b) {
        if (b instanceof Building) {
            if (((Building) b).getNeighboringRoad() == null) {
                return null;
            }
            boolean roadNearby = false;
            for (Road r : roads) {
                if (((Building) b).getNeighboringRoad().isNeighbor(r)) roadNearby = true;
            }
            if (!roadNearby) return null;
        }
        buildAdjList();

        Node start = findNode(p);
        Node end = findNode(b);

        Queue<Node> q = new LinkedList<>();
        Deque<Node> dq = new ArrayDeque<>();

        if (start != null && end != null) {
            start.setVisited(true);
            q.add(start);

            while (!q.isEmpty()) {
                Node currNode = q.poll();
                if (currNode.equals(end)) {
                    break;
                }
                ArrayList<Node> unvisitedNeighbors = currNode.getUnvisitedNeighbors();
                for (Node n : unvisitedNeighbors) {
                    n.setVisited(true);
                    n.setParent(currNode);
                    q.add(n);
                }

            }

            Node path = end;
            while (path != null && path != start) {
                dq.push(path);
                path = path.getParent();
            }
        }
        return dq;
    }

    public Node findNode(Sprite sp) {
        Node res = null;
        for (Node nd : adjList) {
            if (nd.getSprite().getX() == sp.getX() && nd.getSprite().getY() == sp.getY())
                res = nd;
        }

        return res;
    }
}

