package core.actions.unitactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.ArrayList;


public class Recover extends UnitAction
{
    public Recover(int unitId)
    {
        super.unitId = unitId;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        Unit unit = (Unit) gs.getActor(this.unitId);
        float currentHP = unit.getCurrentHP();
        return unit.isFresh() && currentHP < unit.getMaxHP() && currentHP > 0;
    }

    @Override
    public boolean execute(GameState gs) {
        TribesConfig tc = gs.getTribesConfig();
        Unit unit = (Unit) gs.getActor(this.unitId);
        if(unit == null)
            return false;

        int currentHP = unit.getCurrentHP();
        int addHP = tc.RECOVER_PLUS_HP;

        //Check if action is feasible before execution
        if (isFeasible(gs)) {

            int cityID = gs.getBoard().getCityIdAt(unit.getPosition().x, unit.getPosition().y);
            if (cityID != -1){
                ArrayList<Integer> citesID = gs.getTribe(unit.getTribeId()).getCitiesID();
                if (citesID.contains(cityID)){
                    addHP += tc.RECOVER_IN_BORDERS_PLUS_HP;
                }
            }
            unit.setCurrentHP(Math.min(currentHP + addHP, unit.getMaxHP()));
            unit.transitionToStatus(Types.TURN_STATUS.FINISHED);
            return true;
        }
        return false;
    }

    @Override
    public Action copy() {
        return new Recover(this.unitId);
    }

    public String toString() {
        return "RECOVER by unit " + this.unitId;
    }
}
