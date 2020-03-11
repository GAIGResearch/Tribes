package utils.graph;

import utils.Vector2d;

import java.util.ArrayList;

public interface NeighbourProvider
{
    ArrayList<TreeNode> getNeighbours(Vector2d from, double costFrom);
}
