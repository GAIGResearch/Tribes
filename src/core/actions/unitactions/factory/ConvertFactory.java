package core.actions.unitactions.factory;

import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.unitactions.Convert;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

import java.util.LinkedList;

public class ConvertFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        Unit unit = (Unit) actor;
        LinkedList<Action> converts = new LinkedList<>();

        //Only if unit can attack.
        if(unit.canAttack()) {
            Board b = gs.getBoard();
            Vector2d position = unit.getPosition();

            LinkedList<Vector2d> potentialTiles = position.neighborhood(unit.RANGE, 0, b.getSize()); //use neighbourhood for board limits
            for (Vector2d tile : potentialTiles) {
                Unit target = b.getUnitAt(tile.x, tile.y);
                if(target != null && target.getActorId() != unit.getActorId())
                {
                    // Check if there is actually a unit there (and it's not me)
                    Convert c = new Convert(unit.getActorId());
                    c.setTargetId(target.getActorId());
                    if(c.isFeasible(gs)){
                        converts.add(c);
                    }
                }
            }
        }

        return converts;
    }

}
