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

        //Only if unit can attack.
        if(unit.canAttack()) {
            Board b = gs.getBoard();
            Vector2d position = unit.getPosition();

            // Loop through unit range, check if tile observable and action feasible, if so add action
            LinkedList<Vector2d> potentialTiles = position.neighborhood(unit.RANGE, 0, b.getSize()); //use neighbourhood for board limits
            for (Vector2d tile : potentialTiles) {
                Unit other = b.getUnitAt(tile.x, tile.y);
                if (other != null && other.getActorId() != unit.getActorId()) {
                    // Check if there is actually a unit there (and it's not me)
                    Attack a = new Attack(unit.getActorId());
                    a.setTargetId(other.getActorId());
                    if (a.isFeasible(gs)) {
                        attacks.add(a);
                    }
                }
            }
        }

        return attacks;
    }

}
