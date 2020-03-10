package utils.graph;

import utils.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;

public class Node
{
    private int id;
    private Vector2d position;
    private ArrayList<Integer> neighbours;
    private HashMap<Integer, Double> edgeCosts;
    private int parentId;

    private double totalCost;
    private double estimatedCost;
    private boolean visited;

    public Node (int id, int x, int y)
    {
        this.parentId = -1;
        this.id = id;
        this.position = new Vector2d(x, y);
        this.neighbours = new ArrayList<>();
        this.edgeCosts = new HashMap<>();
    }

    public void addNeighbour(Node neighbour)
    {
        this.addNeighbour(neighbour.id, 1.0);
    }

    public void addNeighbour(int neighbourNode, double edgeCost)
    {
        this.neighbours.add(neighbourNode);
        this.edgeCosts.put(neighbourNode, edgeCost);
    }


    public void removeNeighbour(Node neighbour)
    {
        this.neighbours.remove(neighbour);
        this.edgeCosts.remove(neighbour.id);
    }

    public ArrayList<Integer> getNeighbours() {
        return neighbours;
    }

    public double getNeighbourCost(int nodeId)
    {
        return edgeCosts.get(nodeId);
    }


    public int getId() {return id;}
    public int getX() {return position.x; }
    public int getY() {return position.y;}

    public int getParentId() {return parentId;}
    public void setParentId(Node parent) {this.parentId = parent.getId();}
    public double getTotalCost() {return totalCost;}
    public void setTotalCost(double totalCost) {this.totalCost = totalCost;}
    public double getEstimatedCost() {return estimatedCost;}
    public void setEstimatedCost(double estimatedCost) { this.estimatedCost = estimatedCost; }
    public boolean isVisited() {return visited;}
    public void setVisited(boolean visited) {this.visited = visited;}

    public boolean equals(Node n)
    {
        return this.id == n.id;
    }

    public Node copy()
    {
        Node other = new Node(this.id, this.position.x, this.position.y);
        other.parentId = -1;
        other.totalCost = totalCost;
        other.estimatedCost = estimatedCost;
        other.visited = visited;

        for(int id : neighbours)
        {
            other.addNeighbour(id, edgeCosts.get(id));
        }
        return other;
    }
}
