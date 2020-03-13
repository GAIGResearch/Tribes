package core.actions.unitactions;

import core.Types;
import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;
import utils.graph.NeighbourProvider;
import utils.graph.TreeNode;
import utils.graph.TreePathfinder;

import java.util.ArrayList;
import java.util.LinkedList;

public class Move extends UnitAction
{
    private Vector2d destination;

    public Move(Unit u)
    {
        super.unit = u;
    }

    public void setDestination(Vector2d destination) {this.destination = destination; }
    public Vector2d getDestination() { return destination; }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute all the possible Move actions for super.unit.
        LinkedList<Action> moves = new LinkedList<>();
        TreePathfinder tp = new TreePathfinder(unit.getPosition(), new StepMove(gs, unit));

        //If a units turn is FINISHED don't do unnecessary calculations.
        if(unit.getStatus() != Types.TURN_STATUS.FINISHED) {
            for(TreeNode tile : tp.findPaths()) {
                Move action = new Move(unit);
                action.setDestination(tile.getPosition());

                if(action.isFeasible(gs)) {
                    moves.add(action);
                }
            }
        }
        //This gets all reachable nodes.
        ArrayList<TreeNode> reachableNodes = tp.findPaths();

        //This finds a path to a given destination
        ArrayList<TreeNode> path = tp.findPathTo(destination);

        return moves;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        TreePathfinder tp = new TreePathfinder(unit.getPosition(), new StepMove(gs, unit));

        if(unit.checkStatus(Types.TURN_STATUS.MOVED)) {
            return !tp.findPathTo(destination).isEmpty();
        }
        return false;
    }

    @Override
    public boolean execute(GameState gs)
    {
        if(isFeasible(gs)) {
            unit.setStatus(Types.TURN_STATUS.MOVED);
            unit.setPosition(destination.x, destination.y);
            return true;
        }
        return false;
    }

    private class StepMove implements NeighbourProvider
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
        public ArrayList<TreeNode> getNeighbours(Vector2d from, double costFrom) {

            ArrayList<TreeNode> neighbours = new ArrayList<>();

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
