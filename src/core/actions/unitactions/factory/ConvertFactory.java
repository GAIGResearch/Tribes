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
        Board b = gs.getBoard();
        Vector2d position = unit.getPosition();

        for(int i = position.x- unit.RANGE; i <= position.x+ unit.RANGE; i++) {
            for (int j = position.y - unit.RANGE; j <= position.y + unit.RANGE; j++) {

                //Not converting itself
                if(i != position.x || j != position.y) {

                    Unit target = b.getUnitAt(i,j);
                    if(target != null)
                    {
                        Convert c = new Convert(unit.getActorId());
                        c.setTargetId(target.getActorId());
                        if(c.isFeasible(gs)){
                            converts.add(c);
                        }
                    }
                }
            }
        }
        return converts;
    }

}
