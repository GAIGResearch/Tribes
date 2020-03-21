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

public class ResourceGathering extends CityAction
{
    private Types.RESOURCE resource;

    public ResourceGathering(int cityId)
    {
        super.cityId = cityId;
    }

    public void setResource(Types.RESOURCE resource) {this.resource = resource;}
    public Types.RESOURCE getResource() {
        return resource;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        City city = (City) gs.getActor(this.cityId);
        Board b = gs.getBoard();
        Tribe t = b.getTribe(city.getTribeId());

        // Check if resource can be gathered
        if(b.getResourceAt(targetPos.x, targetPos.y) == this.resource && t.getStars() >= this.resource.getCost()){
            switch (this.resource){
                case ANIMAL:
                    return t.getTechTree().isResearched(Types.TECHNOLOGY.HUNTING);
                case FISH:
                    return t.getTechTree().isResearched(Types.TECHNOLOGY.FISHING);
                case WHALES:
                    return t.getTechTree().isResearched(Types.TECHNOLOGY.WHALING);
                case FRUIT:
                    return t.getTechTree().isResearched(Types.TECHNOLOGY.ORGANIZATION);
            }
        }
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //Check if action feasible before execution
        if(isFeasible(gs)){
            City city = (City) gs.getActor(this.cityId);
            Vector2d position = super.getTargetPos();
            gs.getBoard().setResourceAt(position.x, position.y, null);
            switch (this.resource){
                case FISH:
                case ANIMAL:
                case FRUIT:
                    city.addPopulation(this.resource.getBonus());
                    return true;
                case WHALES: //Whaling is the only resource which provides extra stars
                    Board b = gs.getBoard();
                    Tribe t  = b.getTribe(city.getTribeId());
                    t.addStars(this.resource.getBonus());
                    return true;
            }
        }
        return false;
    }

    @Override
    public Action copy() {
        ResourceGathering res = new ResourceGathering(this.cityId);
        res.setResource(this.resource);
        res.setTargetPos(targetPos.copy());
        return res;
    }
}
