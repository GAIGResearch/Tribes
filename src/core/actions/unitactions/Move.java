package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;
import utils.graph.NeighbourHelper;
import utils.graph.PathNode;
import utils.graph.Pathfinder;

import java.util.ArrayList;
import java.util.LinkedList;

public class Move extends UnitAction
{
    private int destX;
    private int destY;

    public Move(Unit u)
    {
        super.unit = u;
    }

    public void setDest(int x, int y) {this.destX = x; this.destY = y;}
    public int getDestX() {
        return destX;
    }
    public int getDestY() {
        return destY;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute all the possible Move actions for super.unit.

        // Code below for demonstration purposes only:
        Pathfinder tp = new Pathfinder(unit.getPosition(), new StepMove(gs, unit));

        //This gets all reachable nodes.
        ArrayList<PathNode> reachableNodes = tp.findPaths();

        //This finds a path to a given destination
        ArrayList<PathNode> path = tp.findPathTo(new Vector2d(destX, destY));

        return new LinkedList<>();
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        //TODO: isFeasible this Move action
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO Execute this Move action
        return false;
    }

    private class StepMove implements NeighbourHelper
    {
        private GameState gs;
        private Unit unit;

        StepMove(GameState curGameState, Unit movingUnit)
        {
            this.gs = curGameState;
            this.unit = movingUnit;
        }

        @Override
        // from: position from which we need neighbours
        // costFrom: is the total move cost computed up to "from"
        // Using this.gs, this.unit, from and costFrom, gets all the adjacent neighbours to tile in position "from"
        public ArrayList<PathNode> getNeighbours(Vector2d from, double costFrom) {

            ArrayList<PathNode> neighbours = new ArrayList<>();

            // Each one of the tree nodes added to "neighbours" must have a position (x,y) and also the cost of moving there from "from":
            //  TreeNode tn = new TreeNode (vector2d pos, double stepCost)

            // We only add nodes to neighbours if costFrom+stepCost <= total move range of this.unit

            return neighbours;
        }

        @Override
        public void addJumpLink(Vector2d from, Vector2d to, boolean reverse) {
            //No jump links
        }
    }

}
