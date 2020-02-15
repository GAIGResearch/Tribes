package core.actions.unitactions;

import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.ArrayList;
import java.util.LinkedList;

public class Upgrade extends UnitAction
{
    public Upgrade(Unit target)
    {
        super.unit = target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: Compute all the available Upgrade actions

        LinkedList<Action> upgradeActions = new LinkedList<>();
        Board b = gs.getBoard();
        for(int x = 0; x<b.getSize(); x++ ){
            for(int y = 0; y<b.getSize(); y++){
                Unit u = b.getUnitAt(x,y);
                if (u != null){
                    if(u.getKills()>3){
                        //u.canUpgrade();
                        upgradeActions.add(new Upgrade(u));
                    }
                }
            }
        }


        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: check if this Upgrade action is feasible.


        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: executes this Upgrade action



        return false;
    }
}
