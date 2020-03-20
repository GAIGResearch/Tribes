package core.actions.cityactions;

import core.Types;
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
            Building removedBuilding = city.removeBuilding(targetPos.x, targetPos.y);
            if (removedBuilding != null) {

                Board b = gs.getBoard();
                b.setBuildingAt(targetPos.x, targetPos.y, null);

                if (removedBuilding.type != Types.BUILDING.CUSTOM_HOUSE) {
                    city.addPopulation(-removedBuilding.getBonus());
                }

                // TODO: Should be check if the building enum is changed
                if (removedBuilding.type.getKey() >= Types.BUILDING.TEMPLE.getKey()) {
                    gs.getTribe(city.getTribeId()).subtractScore(removedBuilding.getPoints());
                }

                int removedType = removedBuilding.type.getKey();
                if(removedType == Types.BUILDING.TEMPLE.getKey()
                        || removedType == Types.BUILDING.WATER_TEMPLE.getKey()
                        || removedType == Types.BUILDING.FOREST_TEMPLE.getKey()
                        || removedType == Types.BUILDING.MOUNTAIN_TEMPLE.getKey()){
                    city.subtractLongTermPoints(removedBuilding.getPoints());
                }

                if(removedBuilding.type == Types.BUILDING.PORT)
                {
                    //If a port is removed, then the tile stops belonging to the trade network
                    b.setTradeNetwork(targetPos.x, targetPos.y, false);
                }

                return true;
            }
        }
        return false;
    }
}
