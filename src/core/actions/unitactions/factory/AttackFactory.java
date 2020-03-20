package core.actions.unitactions.factory;

import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.unitactions.Attack;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

import java.util.LinkedList;

public class AttackFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        Unit unit = (Unit) actor;
        LinkedList<Action> attacks = new LinkedList<>();
        Board b = gs.getBoard();
        Vector2d position = unit.getPosition();

        // Loop through unit range, check if tile observable and action feasible, if so add action
        for(int i = position.x - unit.RANGE; i <= position.x + unit.RANGE; i++) {
            for (int j = position.y - unit.RANGE; j <= position.y + unit.RANGE; j++) {

                //Not attacking itself
                if(i != position.x || j != position.y) {

                    Attack a = new Attack(unit.getActorId());
                    a.setTargetId(b.getUnitAt(i, j).getActorId());
                    if (a.isFeasible(gs)) {
                        attacks.add(a);
                    }
                }
            }
        }
        return attacks;
    }

}
