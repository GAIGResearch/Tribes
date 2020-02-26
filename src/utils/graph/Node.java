package utils.graph;

import java.util.ArrayList;

public class Node<T>
{
    private T id;
    private ArrayList<Node> neighbours;

    public Node (T id)
    {
        this.id = id;
        this.neighbours = new ArrayList<>();
    }

    public void addNeighbour(Node neigh)
    {
        this.neighbours.add(neigh);
    }

    public void removeNeighbour(Node neigh)
    {
        this.neighbours.remove(neigh);
    }
}
