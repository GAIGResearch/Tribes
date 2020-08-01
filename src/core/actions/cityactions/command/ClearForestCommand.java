package core.actions.cityactions.command;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.cityactions.ClearForest;
import core.actors.City;
import core.game.GameState;
import utils.Vector2d;

public class ClearForestCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        ClearForest action = (ClearForest)a;
        if (action.isFeasible(gs)){
            int cityId = action.getCityId();
            City city = (City) gs.getActor(cityId);
            Vector2d targetPos = action.getTargetPos();
            gs.getBoard().setTerrainAt(targetPos.x, targetPos.y, Types.TERRAIN.PLAIN);
            gs.getTribe(city.getTribeId()).addStars(TribesConfig.CLEAR_FOREST_STAR);
            return true;
        }
        return false;
    }
}
