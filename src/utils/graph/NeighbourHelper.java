package utils.graph;

import utils.Vector2d;

import java.util.ArrayList;

public interface NeighbourHelper
{
    /**
     * Returns a list of nodes adjacent to "from" (in a graph sense)
     * @param from position to find adjacent nodes of.
     * @param costFrom Accummulated cost up to 'from'
     * @return The lists of nodes adjacent to that in position 'from'
     */
    ArrayList<PathNode> getNeighbours(Vector2d from, double costFrom);

    /**
     * Adds a jump link from 'from' to 'to'
     * @param from start of the jump link
     * @param to end of the jump link
     * @param reverse true if another jump link to -> from must also be created.
     */
    void addJumpLink(Vector2d from, Vector2d to, boolean reverse);
}
