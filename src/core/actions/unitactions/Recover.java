package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.ArrayList;
import core.Types;

import static core.TribesConfig.RECOVER_IN_CITY_PLUS_HP;
import static core.TribesConfig.RECOVER_PLUS_HP;

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
        Unit unit = (Unit) gs.getActor(this.unitId);
        int currentHP = unit.getCurrentHP();
        int addHP = RECOVER_PLUS_HP;

        if (isFeasible(gs)) {

           int cityID = gs.getBoard().getCityIdAt(unit.getPosition().x, unit.getPosition().y);
           if (cityID != -1){
               ArrayList<Integer> citesID = gs.getTribe(unit.getTribeId()).getCitiesID();
               if (citesID.contains(cityID)){
                   addHP += RECOVER_IN_CITY_PLUS_HP;
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
