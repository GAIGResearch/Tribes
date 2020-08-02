package core.actions.tribeactions.command;

import core.TribesConfig;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.tribeactions.BuildRoad;
import core.actors.Tribe;
import core.game.GameState;
import utils.Vector2d;

import java.util.Vector;

public class BuildRoadCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        BuildRoad action = (BuildRoad)a;
        if(action.isFeasible(gs))
        {
            int tribeId = action.getTribeId();
            Vector2d position = action.getPosition();
            Tribe tribe = gs.getTribe(tribeId);
            tribe.subtractStars(TribesConfig.ROAD_COST);
            gs.getBoard().addRoad(position.x, position.y);
            return true;
        }
        return false;
    }
}
