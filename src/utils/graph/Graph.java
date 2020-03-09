package utils.graph;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {

    private HashMap<Integer, Node> nodes;
    private Node mainNode;
    public Pathfinder pathfinder;
    public boolean VERBOSE = false;
    public int MAX_CAPACITY = 10000;

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

    public Node getNode(int x, int y)
    {
        int id = calcNodeId(x, y);
        return nodes.get(id);
    }

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

    public void addEdge(int x0, int y0, int x1, int y1, boolean reverse)
    {
        int id0 = calcNodeId(x0, y0);
        int id1 = calcNodeId(x1, y1);
        this.addEdge(id0, id1);
        if(reverse)
            this.addEdge(id1, id0);
    }

    private void addEdge(int id0, int id1)
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

    /**
     * Sets the data for this graph. For each true value in 'navigableData', a new node is
     * created. It also create links between contiguous nodes (horizontal, vertical and diagonally)
     * @param navigableData data required
     */
    public void setData (boolean[][] navigableData)
    {
        if(navigableData.length >= MAX_CAPACITY)
            System.out.println("(Graph.java, setData()) ERROR: too long of a map.");

        for(int i = 0; i < navigableData.length; ++i)
            for(int j = 0; j < navigableData[0].length; ++j)
            {
                if(navigableData[i][j])
                {
                    //This assumes a theoretical maximim grid size of 10000x10000. Should be fine!
                    int nodeId = calcNodeId(i, j);
                    Node n = new Node(nodeId, i,j);
                    nodes.put(nodeId, n);

                    //Add neighbours:
                    addLinksTo(n, calcNodeId(i-1,j-1));     //up left
                    addLinksTo(n, calcNodeId(i,j-1));     //up center
                    addLinksTo(n, calcNodeId(i+1,j-1));     //up right
                    addLinksTo(n, calcNodeId(i-1,j));     //center left
                }
            }
    }


    private void addLinksTo(Node origin, int nodeId)
    {
        Node to = nodes.get(nodeId);
        if(to != null)
        {
            to.addNeighbour(origin);
            origin.addNeighbour(to);
        }
    }

    private int calcNodeId(int x, int y)
    {
        return x * MAX_CAPACITY + y;
    }


    public void reset() {
        for(Node n : nodes.values())
        {
           n.setVisited(false);
        }
    }
}
