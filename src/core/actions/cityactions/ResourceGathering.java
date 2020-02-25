package core.actions.cityactions;

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

    public ResourceGathering(City c)
    {
        super.city = c;
    }

    public void setResource(Types.RESOURCE resource) {this.resource = resource;}
    public Types.RESOURCE getResource() {
        return resource;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        Board b = gs.getBoard();
        LinkedList<Action> resources = new LinkedList<>();
        LinkedList<Vector2d> cityTiles = b.getCityTiles(b.getCityIdAt(this.city.getX(),this.city.getY()));
        // lopp through bounds of city and add resource actions if they are feasible
        // TODO: Find more effecient method other than asking board for city tiles
        for(int i = 0; i<cityTiles.size(); i++) {
            Vector2d pos = cityTiles.get(i);
            Types.RESOURCE r = b.getResourceAt(pos.x, pos.y);
            if (r == null)
                continue;
            ResourceGathering resource = new ResourceGathering(this.city);
            resource.setResource(r);
            if (resource.isFeasible(gs)) {
                resource.targetX = pos.x;
                resource.targetY = pos.y;
                resources.add(resource);
            }

        }
        return resources;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        Board b = gs.getBoard();
        Tribe t = b.getTribe(this.city.getTribeId());
        // Check if resource in range
                if(b.getResourceAt(targetX,targetY) == this.resource){
                    switch (this.resource){
                        case ANIMAL:
                            if(t.getTechTree().isResearched(Types.TECHNOLOGY.HUNTING) && t.getStars() >=2)
                                return true;
                            else
                                return false;
                        case FISH:
                            if(t.getTechTree().isResearched(Types.TECHNOLOGY.FISHING) && t.getStars() >=2)
                                return true;
                            else
                                return false;
                        case ORE:
                            if(t.getTechTree().isResearched(Types.TECHNOLOGY.MINING) && t.getStars() >=5)
                                return true;
                            else
                                return false;
                            case WHALES:
                            if(t.getTechTree().isResearched(Types.TECHNOLOGY.WHALING))
                                return true;
                            else
                                return false;
                        case FRUIT:
                            if(t.getTechTree().isResearched(Types.TECHNOLOGY.ORGANIZATION) && t.getStars() >=2)
                                return true;
                            else
                                return false;
                        case CROPS:
                            if(t.getTechTree().isResearched(Types.TECHNOLOGY.FARMING) && t.getStars() >=2 ||t.getTechTree().isResearched(Types.TECHNOLOGY.ORGANIZATION) && t.getStars() >=2 )
                                return true;
                            else
                                return false;
                    }
                }
        return false;
    }


    @Override
    public boolean execute(GameState gs) {
        //Check if action feasible before execution
        if(isFeasible(gs)){
            switch (this.resource){
                case CROPS:
                case ORE:
                    this.city.addPopulation(2);
                    return true;
                case FISH:
                case ANIMAL:
                case FRUIT:
                    this.city.addPopulation(1);
                    return true;
                case WHALES: //Whaling is the only resource which provides extra stars
                    Board b = gs.getBoard();
                    Tribe t  = b.getTribe(this.city.getTribeId());
                    t.addStars(10);
                    return true;
            }
        }
        return false;
    }
}
