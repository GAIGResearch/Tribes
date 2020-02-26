package utils.graph;

import java.util.ArrayList;

public class Node<T>
{
    private T id;
    private ArrayList<T> neighbours;

    public Node (T id)
    {
        this.id = id;
        this.neighbours = new ArrayList<>();
    }

    public void addNeighbour(T neighbourId)
    {
        this.neighbours.add(neighbourId);
    }

    public void removeNeighbour(T neighbourId)
    {
        this.neighbours.remove(neighbourId);
    }

    public T getId() {return id;}

    public Node copy()
    {
        Node other = new Node(this.id);
        for(T id : neighbours)
        {
            other.addNeighbour(id);
        }
        return other;
    }
}
