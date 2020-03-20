package core.actions.unitactions;

import core.actions.Action;
import core.actions.ActionFactory;
import core.actors.Actor;
import core.actors.City;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;

import java.util.LinkedList;

public class CaptureFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        Unit unit = (Unit) actor;

        // get city from board, check if action is feasible and add to list
        Board b = gs.getBoard();
        LinkedList<Action> captures = new LinkedList<>();
        City c = b.getCityInBorders(unit.getPosition().x, unit.getPosition().y);
        if(c != null) {
            Capture capture = new Capture(unit.getActorId());
            capture.setTargetCity(c.getActorId());
            if (capture.isFeasible(gs)) {
                captures.add(capture);
            }
        }

        return captures;
    }

}
