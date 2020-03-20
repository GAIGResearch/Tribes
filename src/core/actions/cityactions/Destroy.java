package core.actions.cityactions;

import core.Types;
import core.actors.Tribe;
import core.actors.buildings.Building;
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
        if(gs.getBoard().getCityIdAt(targetPos.x, targetPos.y) != this.cityId) return false;
        return gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.CONSTRUCTION);
    }

    @Override
    public boolean execute(GameState gs) {
        if (isFeasible(gs)){
            City city = (City) gs.getActor(this.cityId);
            Tribe tribe = (Tribe) gs.getActor(city.getTribeId());
            Building buildingToRemove = city.getBuilding(targetPos.x, targetPos.y);

            Board b = gs.getBoard();
            b.setBuildingAt(targetPos.x, targetPos.y, null);

            tribe.addScore(buildingToRemove.getPoints());

            if(buildingToRemove.type == Types.BUILDING.PORT)
            {
                //If a port is removed, then the tile stops belonging to the trade network
                b.setTradeNetwork(targetPos.x, targetPos.y, false);
            }

            city.removeBuilding(buildingToRemove);
            return true;
        }
        return false;
    }
}
