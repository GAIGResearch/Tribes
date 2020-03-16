package utils.graph;

import utils.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;

public class TreeNode
{
    private int id;
    private Vector2d position;
    private TreeNode parent;

    private double totalCost = 0.0;
    private double estimatedCost = 0.0;
    private boolean visited = false;

    public int MAX_CAPACITY = 10000;


    public TreeNode(Vector2d position)
    {
        this.parent = null;
        this.position = new Vector2d(position);
        this.id = calcNodeId(position);
    }

    public TreeNode(Vector2d position, double totalCost)
    {
        this.parent = null;
        this.position = new Vector2d(position);
        this.id = calcNodeId(position);
        this.totalCost = totalCost;
    }

    public int getId() {return id;}
    public int getX() {return position.x; }
    public int getY() {return position.y;}
    public Vector2d getPosition() {return position; }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public double getTotalCost() {return totalCost;}
    public void setTotalCost(double totalCost) {this.totalCost = totalCost;}
    public double getEstimatedCost() {return estimatedCost;}
    public void setEstimatedCost(double estimatedCost) { this.estimatedCost = estimatedCost; }
    public boolean isVisited() {return visited;}
    public void setVisited(boolean visited) {this.visited = visited;}

    public boolean equals(TreeNode n)
    {
        return this.id == n.id;
    }

    //Repeated from Graph, not nice.
    private int calcNodeId(Vector2d pos)
    {
        return pos.x * MAX_CAPACITY + pos.y;
    }

//    public TreeNode copy()
//    {
//        TreeNode other = new TreeNode(this.id, this.position.x, this.position.y);
//        other.totalCost = totalCost;
//        other.estimatedCost = estimatedCost;
//        other.visited = visited;
//        other.children = new ArrayList<>();
//
//        for(TreeNode tn : children)
//        {
//            other.children.add(tn);
//        }
//        return other;
//    }
}
