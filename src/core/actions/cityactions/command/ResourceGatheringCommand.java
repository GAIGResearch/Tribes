package core.actions.cityactions.command;

import core.TribesConfig;
import core.Types;
import core.Types.CITY_LEVEL_UP;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.cityactions.LevelUp;
import core.actions.cityactions.ResourceGathering;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

public class ResourceGatheringCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        ResourceGathering action = (ResourceGathering)a;
        if(action.isFeasible(gs)){
            int cityId = action.getCityId();
            City city = (City) gs.getActor(cityId);
            Vector2d position = action.getTargetPos();
            gs.getBoard().setResourceAt(position.x, position.y, null);
            Tribe tribe = gs.getTribe(city.getTribeId());
            Types.RESOURCE resource = action.getResource();
            tribe.subtractStars(resource.getCost());
            switch (resource){
                case FISH:
                case ANIMAL:
                case FRUIT:
                    city.addPopulation(tribe, resource.getBonus());
                    return true;
                case WHALES: //Whaling is the only resource which provides extra stars
                    Board b = gs.getBoard();
                    Tribe t  = b.getTribe(city.getTribeId());
                    t.addStars(resource.getBonus());
                    return true;
            }
        }
        return false;
    }
}
