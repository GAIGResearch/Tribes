package utils.graph;

import utils.Vector2d;

import java.util.Objects;

public class PathNode implements Comparable<PathNode>
{
    private int id;
    private Vector2d position;
    private PathNode parent;

    private double totalCost = 0.0;
    private double estimatedCost = 0.0;
    private boolean visited = false;

    public int MAX_CAPACITY = 10000;


    public PathNode(Vector2d position)
    {
        this.parent = null;
        this.position = new Vector2d(position);
        this.id = calcNodeId(position);
    }

    public PathNode(Vector2d position, double totalCost)
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

    public PathNode getParent() {
        return parent;
    }

    public void setParent(PathNode parent) {
        this.parent = parent;
    }

    public double getTotalCost() {return totalCost;}
    public void setTotalCost(double totalCost) {this.totalCost = totalCost;}
    public double getEstimatedCost() {return estimatedCost;}
    public void setEstimatedCost(double estimatedCost) { this.estimatedCost = estimatedCost; }
    public boolean isVisited() {return visited;}
    public void setVisited(boolean visited) {this.visited = visited;}

    public boolean equals(PathNode n)
    {
        return this.id == n.id;
    }

    //Repeated from Graph, not nice.
    private int calcNodeId(Vector2d pos)
    {
        return pos.x * MAX_CAPACITY + pos.y;
    }

    @Override
    public int compareTo(PathNode o) {
        return Double.compare(totalCost, o.totalCost);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathNode pathNode = (PathNode) o;
        return position.equals(pathNode.position);
    }

    @Override
    public int hashCode() {
        return id;
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
