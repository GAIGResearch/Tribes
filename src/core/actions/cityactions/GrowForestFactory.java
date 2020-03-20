package core.actions.cityactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actions.ActionFactory;
import core.actors.Actor;
import core.actors.City;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

import java.util.LinkedList;

public class GrowForestFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        City city = (City) actor;
        LinkedList<Action> actions = new LinkedList<>();
        LinkedList<Vector2d> tiles = gs.getBoard().getCityTiles(city.getActorId());

        for(Vector2d tile: tiles){
                GrowForest action = new GrowForest(city.getActorId());
                action.setTargetPos(new Vector2d(tile.x, tile.y));
                if(action.isFeasible(gs)) {
                    actions.add(action);
                }
        }

        return actions;
    }

}
