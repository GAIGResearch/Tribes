package core.actions.unitactions;

import core.Types;
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
        if(isFeasible(gs)){
            upgradeActions.add(new Upgrade(this.unit));
        }
        return upgradeActions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {

        boolean NavigationResearched = gs.getTribe(unit.getTribeID()).getTechTree().isResearched(Types.TECHNOLOGY.NAVIGATION);
        if(NavigationResearched && this.unit.getType() == Types.UNIT.SHIP){
            return true;
        }else if(this.unit.getType() == Types.UNIT.BOAT){
            return true;
        }

        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: executes this Upgrade action

        this.unit.setMaxHP(this.unit.getMaxHP() + 5);
        //TODO : need units base attack value to upgrade attack
        if(this.unit.getType() == Types.UNIT.BOAT) {
            this.unit.setATK(this.unit.getATK()+1);
            this.unit.setDEF(this.unit.getDEF()+1);
            this.unit.setMOV(this.unit.getMOV()+1);
        }else if(this.unit.getType() == Types.UNIT.SHIP){
            this.unit.setATK(this.unit.getATK()+2);
            this.unit.setDEF(this.unit.getDEF()+1);
            this.unit.setMOV(this.unit.getMOV()+1);
        }
        return false;
    }
}
