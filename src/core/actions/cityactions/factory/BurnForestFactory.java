package core.actions.cityactions.factory;

import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.cityactions.BurnForest;
import core.actors.Actor;
import core.actors.City;
import core.game.GameState;
import utils.Vector2d;

import java.util.LinkedList;

public class BurnForestFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        City city = (City) actor;
        LinkedList<Vector2d> tiles = gs.getBoard().getCityTiles(city.getActorId());
        for(Vector2d tile: tiles){
            BurnForest action = new BurnForest(city.getActorId());
            action.setTargetPos(new Vector2d(tile.x, tile.y));
            if(action.isFeasible(gs))
            {
                actions.add(action);
            }
        }
        return actions;
    }

}
