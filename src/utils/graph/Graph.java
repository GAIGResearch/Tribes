package utils.graph;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

    private HashMap<Integer, Node> nodes;
    private Node mainNode;
    public Pathfinder pathfinder;
    public boolean VERBOSE = false;

    public Graph()
    {
        nodes = new HashMap<>();
    }

    public void find(Graph graph, int startX, int startY)
    {
        //    this.navigableGrid = navigableGrid;
        this.pathfinder = new Pathfinder(this);

        Node start = new Node(-1, startX, startY);
        Node goal = null; //To get all routes.

        pathfinder.findPath(start, goal);


        if(VERBOSE)
        {
            for(Integer pathId : pathfinder.pathCache.keySet())
            {
                ArrayList<Node> nodes = pathfinder.pathCache.get(pathId);
                pathfinder.printPath(pathId, nodes);
            }
        }
    }

//    private boolean navigable(int x, int y)
//    {
//        if(x < 0 || x >= navigableGrid.length || y < 0 || y >= navigableGrid[0].length)
//            return false;
//        return navigableGrid[x][y];
//    }


    public void addNode(int id, int x, int y)
    {
        Node n = new Node(id, x, y);
        nodes.put(id, n);

        if(nodes.size() == 1)
            mainNode = n;
    }

    public void addNode(Node n)
    {
        nodes.put(n.getId(), n);
    }

    public void addEdge(int id0, int id1)
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

    public void removeEdge(int id0, int id1)
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


//    public Graph copy() {
//        Graph gr = new Graph();
//        mainNode = (gr.mainNode == null)? null : gr.mainNode.copy();
//        for(Node n : nodes.values())
//        {
//            gr.addNode(n.copy());
//        }
//        return gr;
//    }

    public void reset() {
        for(Node n : nodes.values())
        {
           n.setVisited(false);
        }
    }
}
