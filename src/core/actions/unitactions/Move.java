package core.actions.unitactions;

import core.Types;
import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;
import utils.graph.PathNode;
import utils.graph.Pathfinder;

import java.util.ArrayList;

public class Move extends UnitAction
{
    private Vector2d destination;

    public Move(int unitId)
    {
        super(Types.ACTION.MOVE);
        super.unitId = unitId;
    }

    public void setDestination(Vector2d destination) {this.destination = destination; }
    public Vector2d getDestination() { return destination; }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        Unit unit = (Unit) gs.getActor(this.unitId);
        if(unit == null)
            return false;
        Pathfinder tp = new Pathfinder(unit.getPosition(), new StepMove(gs, unit));

        //If the unit can move and the destination is vacant, try to reach it.
        if(unit.canMove() && gs.getBoard().getUnitAt(destination.x, destination.y) == null) {
            ArrayList<PathNode> path = tp.findPathTo(destination);
//            if(path == null)
//            {
//                System.out.println("ERROR calculating a path (if actions were created by MoveFactory)");
//            }
            return path != null;
        }
        return false;
    }


    @Override
    public Action copy() {
        Move move = new Move(this.unitId);
        move.setDestination(this.destination);
        return move;
    }

    public String toString()
    {
        return "MOVE by unit " + this.unitId + " to " + destination;
    }

}
