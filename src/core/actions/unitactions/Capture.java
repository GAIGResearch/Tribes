package core.actions.unitactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actors.Building;
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

        if(!unit.isFresh()) return false;

        // If unit not in city, city belongs to the units tribe or if city is null then action is not feasible
        Board b = gs.getBoard();
        if(targetCity == null) return false;

        Vector2d targetPos = targetCity.getPosition();
        if(b.getUnitAt(targetPos.x,targetPos.y) == null) return false;

        Vector2d unitPos = unit.getPosition();
        if(!targetPos.equals(unitPos)) return false;

        return targetCity.getTribeId() != unit.getTribeId();
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs)) {
            // Change city tribe id to execute action
            Unit unit = (Unit) gs.getActor(this.unitId);
            City targetCity = (City) gs.getActor(this.targetCityId);
            Board b = gs.getBoard();
            Tribe thisTribe = b.getTribe(unit.getTribeId());
            Tribe targetTribe = b.getTribe(targetCity.getTribeId());
            //Subtract score  from target tribe based on the number of tiles and add score to this tribe
            LinkedList<Vector2d> tiles = gs.getBoard().getCityTiles(targetCityId);
            //LinkedList<Building> buildings = targetCity.getBuildings();
            targetTribe.subtractScore(tiles.size() * TribesConfig.CITY_BORDER_POINTS);
            thisTribe.addScore(tiles.size() * TribesConfig.CITY_BORDER_POINTS);

            return b.capture(gs, thisTribe, targetCity.getPosition().x, targetCity.getPosition().y);
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
