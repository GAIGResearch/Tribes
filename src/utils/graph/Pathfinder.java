package utils.graph;

import utils.Vector2d;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by dperez on 13/01/16.
 */
public class Pathfinder
{
    public PathNode root;
    private NeighbourHelper provider;

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

        ArrayList<PathNode> destinationsFromStart = new ArrayList<>();
        root.setVisited(true);
        PathNode node = null;

        PriorityQueue<PathNode> openList = new PriorityQueue<>();
        root.setTotalCost(0.0);

        openList.add(root);

        while(openList.size() != 0)
        {
            node = openList.poll();
            //System.out.println("Remaining in list: " + openList.size());

            if(!destinationsFromStart.contains(node) && (node != root))
            {
                destinationsFromStart.add(root);
            }

            ArrayList<PathNode> neighbours = provider.getNeighbours(root.getPosition(), root.getTotalCost());

            for(int i = 0; i < neighbours.size(); ++i)
            {
                PathNode neighbour = neighbours.get(i);
                double curDistance = neighbour.getTotalCost();
                if(!neighbour.isVisited())
                {
                    neighbour.setVisited(true);
                    neighbour.setTotalCost(curDistance + node.getTotalCost());
                    openList.add(neighbour);

                }else if(curDistance + node.getTotalCost() < neighbour.getTotalCost())
                {
                    neighbour.setTotalCost(curDistance + node.getTotalCost());
                }
            }

        }

        return destinationsFromStart;
    }

    private ArrayList<PathNode> _findPath(PathNode goal)
    {
        PathNode node = null;
        PriorityQueue<PathNode> openList = new PriorityQueue<>();
        PriorityQueue<PathNode> closedList = new PriorityQueue<>();

        root.setTotalCost(0.0);
        double dist = Vector2d.chebychevDistance(root.getPosition(), goal.getPosition());
        root.setEstimatedCost(dist);
        openList.add(root);

        while(openList.size() != 0)
        {
            node = openList.poll();
            closedList.add(node);

            if(node.getX() == goal.getX() && node.getY() == goal.getY())
                return calculatePath(node);

            ArrayList<PathNode> neighbours = provider.getNeighbours(root.getPosition(), root.getTotalCost());

            for(int i = 0; i < neighbours.size(); ++i)
            {
                PathNode neighbour = neighbours.get(i);
                double curDistance = neighbour.getTotalCost();

                if(!openList.contains(neighbour) && !closedList.contains(neighbour))
                {
                    neighbour.setTotalCost(curDistance + node.getTotalCost());
                    dist = Vector2d.chebychevDistance(neighbour.getPosition(), goal.getPosition());
                    neighbour.setEstimatedCost(dist);
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
