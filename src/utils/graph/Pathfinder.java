package utils.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Created by dperez on 13/01/16.
 */
public class Pathfinder
{
    public static PriorityQueue<Node> closedList, openList;
    public HashMap<Integer, ArrayList<Node>> pathCache;
    public Graph graph;

    public Pathfinder(Graph graph)
    {
        this.graph = graph;
        pathCache = new HashMap<Integer, ArrayList<Node>>();
    }

    public void emptyCache()
    {
        pathCache.clear();

    }

    private static double heuristicEstimatedCost(Node curNode, Node goalNode)
    {
        //4-way: using Manhattan
        double xDiff = Math.abs(curNode.getX() - goalNode.getX());
        double yDiff = Math.abs(curNode.getY() - goalNode.getY());
        return xDiff + yDiff;
    }


    private ArrayList<Node> calculatePath(Node node)
    {
        ArrayList<Node> path = new ArrayList<Node>();
        while(node != null)
        {
            if(node.getParent() != null) //to avoid adding the start node.
            {
                path.add(0,node);
            }
            node = node.getParent();
        }
        return path;
    }

    public ArrayList<Node> getPath(Node start, Node goal)
    {
        int pathId = start.getId() * 10000 + goal.getId();
        if(pathCache.containsKey(pathId))
            return pathCache.get(pathId);
        return null;
    }

    //Set goal to null to run Dijkstra
    public ArrayList<Node> findPath(Node start, Node goal)
    {
        if(goal != null)
        {
            int pathId = start.getId() * 10000 + goal.getId();
            if(pathCache.containsKey(pathId))
                return pathCache.get(pathId);
            ArrayList<Node> path = _findPath(start, goal);

            if(path!=null)
                pathCache.put(pathId, path);

            return path;
        }

        _dijkstra(start);
        return null;
    }


    private void _dijkstra(Node start)
    {

        ArrayList<Node> destinationsFromStart = new ArrayList<Node>();
        //All unvisited at the beginning.
//        visited = new boolean[pathfinder.navigableGrid.length][pathfinder.navigableGrid[0].length];
        //...except the starting node
//        visited[start.getX()][start.getY()] = true;

        graph.reset();
        start.setVisited(true);


        Node node = null;


        openList = new PriorityQueue<Node>();
        start.setTotalCost(0.0);

        openList.add(start);

        while(openList.size() != 0)
        {
            node = openList.poll();
            //System.out.println("Remaining in list: " + openList.size());

            if(!destinationsFromStart.contains(node) && (node != start))
            {
                destinationsFromStart.add(node);
            }

            ArrayList<Node> neighbours = node.getNeighbours();

            for(int i = 0; i < neighbours.size(); ++i)
            {
                Node neighbour = neighbours.get(i);
                double curDistance = neighbour.getTotalCost();
                if(!neighbour.isVisited())
                {
                    neighbour.setVisited(true);
                    neighbour.setTotalCost(curDistance + node.getTotalCost());
                    neighbour.setParent(node);
                    openList.add(neighbour);

                }else if(curDistance + node.getTotalCost() < neighbour.getTotalCost())
                {
                    neighbour.setTotalCost(curDistance + node.getTotalCost());
                    neighbour.setParent(node);
                }
            }

        }


        for(Node dest : destinationsFromStart)
        {
            int pathid = start.getId() * 10000 + dest.getId();
            pathCache.put(pathid, calculatePath(dest));
        }

    }

    private ArrayList<Node> _findPath(Node start, Node goal)
    {
        Node node = null;
        openList = new PriorityQueue<Node>();
        closedList = new PriorityQueue<Node>();

        start.setTotalCost(0.0);
        start.setEstimatedCost(heuristicEstimatedCost(start, goal));

        openList.add(start);

        while(openList.size() != 0)
        {
            node = openList.poll();
            closedList.add(node);

            if(node.getX() == goal.getX() && node.getY() == goal.getY())
                return calculatePath(node);

            ArrayList<Node> neighbours = node.getNeighbours();

            for(int i = 0; i < neighbours.size(); ++i)
            {
                Node neighbour = neighbours.get(i);
                double curDistance = neighbour.getTotalCost();

                if(!openList.contains(neighbour) && !closedList.contains(neighbour))
                {
                    neighbour.setTotalCost(curDistance + node.getTotalCost());
                    neighbour.setEstimatedCost(heuristicEstimatedCost(neighbour, goal));
                    neighbour.setParent(node);

                    openList.add(neighbour);

                }else if(curDistance + node.getTotalCost() < neighbour.getTotalCost())
                {
                    neighbour.setTotalCost(curDistance + node.getTotalCost());
                    neighbour.setParent(node);

                    if(openList.contains(neighbour))
                        openList.remove(neighbour);

                    if(closedList.contains(neighbour))
                        closedList.remove(neighbour);

                    openList.add(neighbour);
                }
            }

        }

        if(node.getX() != goal.getX() || node.getY() != goal.getY()) //not the goal
            return null;

        return calculatePath(node);

    }

    private int[][] uncompressPathId(int pathId)
    {
        int[][] ends = new int[2][2];

        int org =  pathId / 10000;
        int dest = pathId % 10000;

        ends[0] = new int[]{org/100 , org%100};
        ends[1] = new int[]{dest/100 , dest%100};
        return ends;
    }

    public void printPath(int pathId, ArrayList<Node> nodes)
    {
        if(nodes == null)
        {
            System.out.println("No Path");
            return;
        }

        int[][] endsIds = uncompressPathId(pathId);

        String ends = "(" + endsIds[0][0] + "," + endsIds[0][1] + ") -> ("
                + endsIds[1][0] + "," + endsIds[1][1] + ")";


        System.out.print("Path " + ends + "; ("+ nodes.size() + "): ");
        for(Node n : nodes)
        {
            System.out.print(n.getX() + ":" + n.getY() + ", ");
        }
        System.out.println();
    }
}
