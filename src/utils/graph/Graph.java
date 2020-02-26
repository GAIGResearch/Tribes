package utils.graph;

import java.util.HashMap;

public class Graph<T> {

    private HashMap<T, Node> nodes;
    private Node mainNode;

    public Graph()
    {
        nodes = new HashMap<>();
    }

    public void addNode(T id)
    {
        Node n = new Node<T>(id);
        nodes.put(id, n);

        if(nodes.size() == 1)
            mainNode = n;
    }

    public void addEdge(T id0, T id1)
    {
        Node n0 = nodes.get(id0);
        Node n1 = nodes.get(id1);
        if(n0==null || n1==null)
        {
            System.out.println("WARNING: nodes not found for adding edge from "
                    + id0 + " (" + n0 + ") to " + id1 + " (" + n1 + ")");
            return;
        }
        n0.addNeighbour(n1);
    }

    public void removeEdge(T id0, T id1)
    {
        Node n0 = nodes.get(id0);
        Node n1 = nodes.get(id1);
        if(n0==null || n1==null)
        {
            System.out.println("WARNING: nodes not found for removing edge from "
                    + id0 + " (" + n0 + ") to " + id1 + " (" + n1 + ")");
            return;
        }
        n0.removeNeighbour(n1);
    }




}
