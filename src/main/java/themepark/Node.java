
package themepark;

import java.util.ArrayList;
import java.util.List;

/**
 * Auxiliary class for pathfinding implementation
 * 
 */
public class Node{
    
    private final Sprite sprite;
    private final List<Node> neighbors;
    private boolean visited;
    private Node parent;

    public Node(Sprite sprite) {
        this.sprite = sprite;
        this.neighbors = new ArrayList<>();
        this.visited = false;
        
    }

    public Sprite getSprite() {
        return sprite;
    }

    public boolean isVisited() {
        return visited;
    }
    
    public void addNeighbor(Node nd){
        neighbors.add(nd);
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    
    public ArrayList<Node> getUnvisitedNeighbors(){
        ArrayList<Node> unvisitedNeighs = new ArrayList<>();
        
        neighbors.stream().filter((n) -> (!n.isVisited())).forEachOrdered(unvisitedNeighs::add);
        return unvisitedNeighs;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); 
    }

}
