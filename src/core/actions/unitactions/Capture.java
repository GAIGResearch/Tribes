package core.actions.unitactions;

import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.City;
import core.actors.units.Unit;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Capture extends UnitAction
{
    private int targetCityId; // This can be a city or a village.

    public Capture(int unitId)
    {
        super.unitId = unitId;
    }

    public void setTargetCity(int targetCityId) {this.targetCityId = targetCityId;}
    public int getTargetCity() {
        return targetCityId;
    }


    @Override
    public boolean isFeasible(final GameState gs)
    {
        City targetCity = (City) gs.getActor(this.targetCityId);
        Unit unit = (Unit) gs.getActor(this.unitId);

        // If unit not in city, city belongs to the units tribe or if city is null then action is not feasible
        Board b = gs.getBoard();
        if(targetCity == null) return false;

        Vector2d targetPos = targetCity.getPosition();
        if(b.getUnitAt(targetPos.x,targetPos.y) == null) return false;

        Vector2d unitPos = unit.getPosition();
        if(targetPos.x != unitPos.x || targetPos.y != unitPos.y) return false;

        return targetCity.getTribeId() != unit.getTribeId();
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs)) {
            // Change city tribe id to execute action
            Unit unit = (Unit) gs.getActor(this.unitId);
            City targetCity = (City) gs.getActor(this.targetCityId);
            Board b = gs.getBoard();
            Tribe t = b.getTribe(unit.getTribeId());
            return b.capture(gs, t, targetCity.getPosition().x, targetCity.getPosition().y);
        }
        return false;
    }

    @Override
    public Action copy() {
        Capture capture = new Capture(this.unitId);
        capture.setTargetCity(this.targetCityId);
        return capture;
    }
}
