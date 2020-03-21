package core.actions.cityactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actors.City;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

import java.util.LinkedList;

public class ClearForest extends CityAction
{

    public ClearForest(int cityId) {
        super.cityId = cityId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        Board b = gs.getBoard();

        if(b.getTerrainAt(targetPos.x, targetPos.y) != Types.TERRAIN.FOREST) return false;
        if(b.getCityIdAt(targetPos.x, targetPos.y) != cityId) return false;
        return gs.getTribe(cityId).getTechTree().isResearched(Types.TECHNOLOGY.FORESTRY);
    }

    @Override
    public boolean execute(GameState gs) {
        if (isFeasible(gs)){
            City city = (City) gs.getActor(this.cityId);
            gs.getBoard().setTerrainAt(targetPos.x, targetPos.y, Types.TERRAIN.PLAIN);
            gs.getTribe(city.getTribeId()).addStars(TribesConfig.CLEAR_FOREST_STAR);
            return true;
        }
        return false;
    }

    @Override
    public Action copy() {
        ClearForest clear = new ClearForest(this.cityId);
        clear.setTargetPos(this.targetPos.copy());
        return clear;
    }
}
