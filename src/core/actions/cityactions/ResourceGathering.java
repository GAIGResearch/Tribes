package core.actions.cityactions;

import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.City;

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

        for(int x = this.city.getX()- this.city.getBound(); x < x+ this.city.getBound(); x++){
            for(int y = this.city.getX()- this.city.getBound(); y < y+ this.city.getBound(); y++) {
                Types.RESOURCE r = b.getResourceAt(x,y);
                ResourceGathering resource = new ResourceGathering(this.city);
                resource.setResource(r);
                if(resource.isFeasible(gs)){
                    resources.add(resource);
                }
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
        for(int x = this.city.getX()- this.city.getBound(); x < x+ this.city.getBound(); x++){
            for(int y = this.city.getX()- this.city.getBound(); y < y+ this.city.getBound(); y++){
                if(b.getResourceAt(x,y) == this.resource){
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
                    return true;
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
                    t.addStars(5);
                    return true;
            }
        }
        return false;
    }
}
