package core.actions.cityactions;

import core.Types;
import core.actions.Action;
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
        LinkedList<Action> resources = new LinkedList<>();
        if(isFeasible(gs)){
            ResourceGathering resourceGathering = new ResourceGathering(this.city);
            resourceGathering.setResource(this.resource);
            resources.add(resourceGathering);
        }
        return resources;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        Board b = gs.getBoard();
        // Check if resource in range
        for(int x = this.city.getX()- this.city.getBound(); x < x+ this.city.getBound(); x++){
            for(int y = this.city.getX()- this.city.getBound(); y < y+ this.city.getBound(); y++){
                if(b.getResourceAt(x,y) == this.resource){
                    switch (this.resource){
                        case FISH:
                            if(b.getTribe(this.city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.FISHING))
                                return true;
                            else
                                return false;
                        case ORE:
                            if(b.getTribe(this.city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.MINING))
                                return true;
                            else
                                return false;
                            case WHALES:
                            if(b.getTribe(this.city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.WHALING))
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
                case FISH:
                case ANIMAL:
                case FRUIT:
                    this.city.addExtraStar(2);
                    return true;
                case ORE:
                case WHALES:
                    this.city.addExtraStar(5);
                    return true;
                case RUINS:
                    this.city.addExtraStar(10);
                    return true;
            }

        }
        return false;
    }
}
