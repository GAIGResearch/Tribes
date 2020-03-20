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

public class GrowForest extends CityAction
{

    public GrowForest(int cityId)
    {
        super.cityId = cityId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        City city = (City) gs.getActor(this.cityId);
        Board b = gs.getBoard();
        if(b.getTerrainAt(targetPos.x, targetPos.y) != Types.TERRAIN.PLAIN) return false;
        if(b.getCityIdAt(targetPos.x, targetPos.y) != city.getActorId()) return false;

        Tribe t = gs.getTribe(city.getTribeId());
        if(t.getStars() < TribesConfig.FOREST_COST) return false;
        return t.getTechTree().isResearched(Types.TECHNOLOGY.SPIRITUALISM);
    }

    @Override
    public boolean execute(GameState gs) {
        if (isFeasible(gs)){
            gs.getBoard().setTerrainAt(targetPos.x, targetPos.y, Types.TERRAIN.FOREST);
            gs.getTribe(this.cityId).subtractStars(TribesConfig.FOREST_COST);
            return true;
        }
        return false;
    }
}
