package utils.graph;

import utils.Vector2d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Created by dperez on 13/01/16.
 */
public class Pathfinder
{
    public PathNode root;
    private NeighbourHelper provider;

    public HashSet<PathNode> nodes;

    public Pathfinder(Vector2d rootPos, NeighbourHelper provider)
    {
        root = new PathNode(rootPos);
        this.provider = provider;
    }


    private ArrayList<PathNode> calculatePath(PathNode node)
    {
        ArrayList<PathNode> path = new ArrayList<>();
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

    //Dijkstraa to all possible destinations. Returns nodes of all destinations.
    public ArrayList<PathNode> findPaths()
    {
        return _dijkstra();
    }

    //A* to destination
    public ArrayList<PathNode> findPathTo(Vector2d goalPosition)
    {
        return _findPath(new PathNode(goalPosition));
    }


    private ArrayList<PathNode> _dijkstra()
    {
        nodes = new HashSet<>();

        root.setVisited(true);
        root.setTotalCost(0.0);

        ArrayList<PathNode> destinationsFromStart = new ArrayList<>();
        PathNode node;

        PriorityQueue<PathNode> openList = new PriorityQueue<>();
        HashSet<PathNode> visited = new HashSet<>();
        visited.add(root);
        openList.add(root);
        nodes.add(root);

        while (openList.size() != 0)
        {
            node = openList.poll();
            //nodes.add(node);
//            System.out.println("Remaining in list: " + openList.size());

            if (!destinationsFromStart.contains(node) && (node != root))
            {
                destinationsFromStart.add(node);
            }

            ArrayList<PathNode> neighbours = provider.getNeighbours(node.getPosition(), node.getTotalCost());
            for (PathNode nb : neighbours) {
                double nbCost = nb.getTotalCost();

                boolean inCache = false;

                PathNode neighbour = null;
                if (nodes != null) {
                    for (PathNode n2 : nodes) {
                        if (nb.equals(n2)) {
                            neighbour = n2;
                            inCache = true;
                            break;
                        }
                    }
                }
                if (neighbour == null) {
                    neighbour = nb;  // Node was not found in cache
                }

                if (!visited.contains(neighbour)) {
                    neighbour.setVisited(true);
                    visited.add(neighbour);
                    neighbour.setTotalCost(nbCost + node.getTotalCost());
                    openList.add(neighbour);
                    nodes.add(neighbour);
                } else if (nbCost + node.getTotalCost() < neighbour.getTotalCost()) {
                    neighbour.setTotalCost(nbCost + node.getTotalCost());
                }
            }
        }

        return destinationsFromStart;
    }

    private ArrayList<PathNode> _findPath(PathNode goal)
    {
        // TODO this method repeats calculations that are already done in _dijsktra above, could be made a lot more
        // efficient to avoid re-calculating neighbours

        nodes = new HashSet<>();
        PathNode node = null;
        PriorityQueue<PathNode> openList = new PriorityQueue<>();
        PriorityQueue<PathNode> closedList = new PriorityQueue<>();

        root.setTotalCost(0.0);
        double dist = Vector2d.chebychevDistance(root.getPosition(), goal.getPosition());
        root.setEstimatedCost(dist);
        openList.add(root);
        nodes.add(root);

        while(openList.size() != 0)
        {
            node = openList.poll();
            //nodes.add(node);
            closedList.add(node);

            if(node.getX() == goal.getX() && node.getY() == goal.getY())
                return calculatePath(node);

            ArrayList<PathNode> neighbours = provider.getNeighbours(node.getPosition(), node.getTotalCost());

            for (PathNode nb : neighbours) {
                // This neighbour is a new object, it will not have any of the costs set up
                // use the cached nodes HashSet to find the correct object with the information available,
                // only missing estimated distance
                double nbCost = nb.getTotalCost();
                boolean inCache = false;

                PathNode neighbour = null;
                if (nodes != null) {
                    for (PathNode n2 : nodes) {
                        if (nb.equals(n2)) {
                            neighbour = n2;
                            inCache = true;
                            break;
                        }
                    }
                }
                if (neighbour == null) {
                    neighbour = nb;  // Node was not found in cache
                }

                if (!openList.contains(neighbour) && !closedList.contains(neighbour)) {
                    neighbour.setTotalCost(nbCost + node.getTotalCost());
                    dist = Vector2d.chebychevDistance(neighbour.getPosition(), goal.getPosition());
                    neighbour.setEstimatedCost(dist);
                    neighbour.setParent(node);

                    openList.add(neighbour);
                    nodes.add(neighbour);

                } else if (nbCost + node.getTotalCost() < neighbour.getTotalCost()) {
                    neighbour.setTotalCost(nbCost + node.getTotalCost());
                    neighbour.setParent(node);

                    openList.remove(neighbour);
                    closedList.remove(neighbour);

                    openList.add(neighbour);
                }
            }

        }

        if(node == null || node.getX() != goal.getX() || node.getY() != goal.getY()) //not the goal
            return null;

        return calculatePath(node);

    }




//    public void printPath(int pathId, ArrayList<Node> nodes)
//    {
//        if(nodes == null)
//        {
//            System.out.println("No Path");
//            return;
//        }
//
//        int[][] endsIds =  new int[2][2];
//
//        int org =  pathId / 10000;
//        int dest = pathId % 10000;
//
//        endsIds[0] = new int[]{org/100 , org%100};
//        endsIds[1] = new int[]{dest/100 , dest%100};
//
//        String ends = "(" + endsIds[0][0] + "," + endsIds[0][1] + ") -> ("
//                + endsIds[1][0] + "," + endsIds[1][1] + ")";
//
//
//        System.out.print("Path " + ends + "; ("+ nodes.size() + "): ");
//        for(Node n : nodes)
//        {
//            System.out.print(n.getX() + ":" + n.getY() + ", ");
//        }
//        System.out.println();
//    }
}
