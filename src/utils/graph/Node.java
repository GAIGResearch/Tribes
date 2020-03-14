package utils.graph;

import utils.Vector2d;

import java.util.ArrayList;

public class Node
{
    private int id;
    private Vector2d position;
    private ArrayList<Node> neighbours;

    private double totalCost;

    private double estimatedCost;
    private Node parent;
    private boolean visited;

    public Node (int id, int x, int y)
    {
        this.parent = null;
        this.id = id;
        this.position = new Vector2d(x, y);
        this.neighbours = new ArrayList<>();
    }

    public void addNeighbour(Node neighbour)
    {
        this.neighbours.add(neighbour);
    }
    public void removeNeighbour(Node neighbour)
    {
        this.neighbours.remove(neighbour);
    }

    public ArrayList<Node> getNeighbours() {
        return neighbours;
    }

    public int getId() {return id;}
    public int getX() {return position.x; }
    public int getY() {return position.y;}

    public Node getParent() {return parent;}
    public void setParent(Node parent) {this.parent = parent;}
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

//    public Node copy()
//    {
//        Node other = new Node(this.id, this.position.x, this.position.y);
//        other.setParent(this.parent);
//        other.setTotalCost(this.totalCost);
//        other.setEstimatedCost(this.estimatedCost);
//        for(Node id : neighbours)
//        {
//            other.addNeighbour(id.copy());
//        }
//        return other;
//    }
}
