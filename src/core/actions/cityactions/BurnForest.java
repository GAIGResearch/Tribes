package core.actions.cityactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.City;
import utils.Vector2d;

import java.util.LinkedList;

public class BurnForest extends CityAction
{
    TribesConfig tc = new TribesConfig();

    public BurnForest(int cityId) {
        super.cityId = cityId;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        City city = (City) gs.getActor(this.cityId);

        Board b = gs.getBoard();
        if(b.getTerrainAt(targetPos.x, targetPos.y) != Types.TERRAIN.FOREST) return false;
        if(b.getCityIdAt(targetPos.x, targetPos.y) != this.cityId) return false;

        Tribe t = gs.getTribe(city.getTribeId());
        if(t.getStars() < tc.BURN_FOREST_COST) return false;
        return t.getTechTree().isResearched(Types.TECHNOLOGY.CHIVALRY);
    }

    @Override
    public boolean execute(GameState gs) {
        City city = (City) gs.getActor(this.cityId);
        if (isFeasible(gs)){
            Board b = gs.getBoard();
            Tribe t = gs.getTribe(city.getTribeId());
            b.setTerrainAt(targetPos.x, targetPos.y, Types.TERRAIN.PLAIN);
            b.setResourceAt(targetPos.x, targetPos.y, Types.RESOURCE.CROPS);
            t.subtractStars(tc.BURN_FOREST_COST);
            return true;
        }
        return false;
    }

    @Override
    public Action copy() {
        BurnForest burn = new BurnForest(this.cityId);
        burn.setTargetPos(this.targetPos.copy());
        return burn;
    }

    public String toString()
    {
        return "BURN_FOREST by city " + this.cityId+ " at " + targetPos;
    }
}
