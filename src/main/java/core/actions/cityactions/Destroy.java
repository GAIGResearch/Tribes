package core.actions.cityactions;

import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.actors.Building;
import core.game.Board;
import core.game.GameState;
import core.actors.City;

public class Destroy extends CityAction
{
    public Destroy(int cityId)
    {
        super.cityId = cityId;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        City city = (City) gs.getActor(this.cityId);
        if(gs.getBoard().getBuildingAt(targetPos.x, targetPos.y) == null) return false;
        Building buildingToRemove = city.getBuilding(targetPos.x, targetPos.y);
        if(buildingToRemove == null || buildingToRemove.type == null) return false;
        if(gs.getBoard().getCityIdAt(targetPos.x, targetPos.y) != this.cityId) return false;
        return gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.CONSTRUCTION);
    }

    @Override
    public boolean execute(GameState gs) {
        if (isFeasible(gs)){
            City city = (City) gs.getActor(this.cityId);
            Tribe tribe = gs.getTribe(city.getTribeId());
            Building buildingToRemove = city.getBuilding(targetPos.x, targetPos.y);

            Board b = gs.getBoard();
            b.setBuildingAt(targetPos.x, targetPos.y, null);

            if(buildingToRemove.type == Types.BUILDING.PORT)
            {
                //If a port is removed, then the tile stops belonging to the trade network
                b.destroyPort(targetPos.x, targetPos.y, gs.getTribesConfig());
            }

            city.removeBuilding(gs, buildingToRemove);
            return true;
        }
        return false;
    }

    @Override
    public Action copy() {
        Destroy destroy = new Destroy(this.cityId);
        destroy.setTargetPos(this.targetPos.copy());
        return destroy;
    }

    public String toString()
    {
        return "DESTROY by city " + this.cityId+ " at " + targetPos;
    }
}
