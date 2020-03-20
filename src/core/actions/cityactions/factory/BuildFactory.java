package core.actions.cityactions.factory;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.cityactions.Build;
import core.actions.tribeactions.ResearchTech;
import core.actors.Actor;
import core.actors.City;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

import java.util.LinkedList;

public class BuildFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {

        City city = (City) actor;
        LinkedList<Action> actions = new LinkedList<>();
        Board board = gs.getBoard();
        LinkedList<Vector2d> tiles = board.getCityTiles(city.getActorId());

        for(Vector2d tile : tiles){
            for(Types.BUILDING building: Types.BUILDING.values()){
                //check if tile is empty
                if(board.getBuildingAt(tile.x, tile.y) == null) {
                    Build action = new Build(city.getActorId());
                    action.setBuildingType(building);
                    action.targetPos = tile.copy();
                    if (action.isFeasible(gs)) {
                        actions.add(action);
                    }
                }
            }
        }
        return actions;
    }

}
