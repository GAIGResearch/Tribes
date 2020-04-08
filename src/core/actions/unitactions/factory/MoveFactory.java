package core.actions.unitactions.factory;

import core.Types;
import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.unitactions.Move;
import core.actions.unitactions.StepMove;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.GameState;
import utils.graph.PathNode;
import utils.graph.Pathfinder;

import java.util.ArrayList;
import java.util.LinkedList;

public class MoveFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        Unit unit = (Unit) actor;
        LinkedList<Action> moves = new LinkedList<>();
        Pathfinder tp = new Pathfinder(unit.getPosition(), new StepMove(gs, unit));

        //If a units turn is FINISHED don't do unnecessary calculations.
        if(unit.canMove()) {
//            ArrayList<PathNode> paths = tp.findPaths();
            for(PathNode tile : tp.findPaths()) {
                Move action = new Move(unit.getActorId());
                action.setDestination(tile.getPosition());
                moves.add(action);
            }
        }
        return moves;
    }

}
