package core.actions.unitactions;

import core.Types;
import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;

import java.util.LinkedList;

public class Move extends UnitAction
{
    private Vector2d destination;

    public Move(Unit u)
    {
        super.unit = u;
    }

    public void setDest(Vector2d destination) { this.destination = destination; }
    public Vector2d getDest() {
        return destination;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> moves = new LinkedList<>();
        LinkedList<Vector2d> possibleTiles = unit.getPosition().neighborhood(unit.RANGE, gs.getBoard().getSize());

        //If a units turn is FINISHED don't do unnecessary calculations.
        if(unit.getStatus() != Types.TURN_STATUS.FINISHED) {
            for (Vector2d tile : possibleTiles) {
                Move action = new Move(unit);
                action.setDest(tile);

                if (action.isFeasible(gs)) {
                    moves.add(action);
                }
            }
        }
        return moves;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        //TODO: isFeasible this Move action
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        Types.TURN_STATUS status = unit.getStatus();

        if(isFeasible(gs)) {
            switch(unit.getType()) {
                case WARRIOR:
                case BOAT:
                case SHIP:
                case BATTLESHIP:
                case ARCHER:
                case SWORDMAN:
                    unit.setStatus(Types.TURN_STATUS.MOVED);
                    unit.setPosition(destination.x, destination.y);
                    return true;
                case RIDER:
                    if(status == Types.TURN_STATUS.MOVED_AND_ATTACKED) {
                        unit.setStatus(Types.TURN_STATUS.FINISHED);
                        unit.setPosition(destination.x, destination.y);
                        return true;
                    }else if(status == Types.TURN_STATUS.ATTACKED) {
                        unit.setStatus(Types.TURN_STATUS.MOVED_AND_ATTACKED);
                        unit.setPosition(destination.x, destination.y);
                        return true;
                    }else {
                        unit.setStatus(Types.TURN_STATUS.MOVED);
                        unit.setPosition(destination.x, destination.y);
                        return true;
                    }
                case KNIGHT:
                    
            }
        }
        return false;
    }
}
