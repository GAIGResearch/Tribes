package core.actions.cityactions.command;

import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.cityactions.Build;
import core.actors.Building;
import core.actors.City;
import core.actors.Temple;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

public class BuildCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        Build action = (Build)a;
        int cityId = action.getCityId();
        City city = (City) gs.getActor(cityId);
        Tribe tribe = gs.getTribe(city.getTribeId());
        Board board = gs.getBoard();

        if(action.isFeasible(gs)) {
            Vector2d targetPos = action.getTargetPos();
            Types.BUILDING buildingType = action.getBuildingType();

            tribe.subtractStars(buildingType.getCost());
            board.setBuildingAt(targetPos.x, targetPos.y, buildingType);
            board.setResourceAt(targetPos.x, targetPos.y, null);

            if(buildingType.isTemple())
                city.addBuilding(gs, new Temple(targetPos.x, targetPos.y, buildingType, cityId));
            else
                city.addBuilding(gs, new Building(targetPos.x, targetPos.y, buildingType, cityId));

            if(buildingType == Types.BUILDING.PORT)
                board.buildPort(targetPos.x, targetPos.y);
            if(buildingType.isMonument())
                tribe.monumentIsBuilt(buildingType);
            if(buildingType == Types.BUILDING.LUMBER_HUT)
                board.setTerrainAt(targetPos.x, targetPos.y, Types.TERRAIN.PLAIN);

            return true;
        }
        return false;
    }
}
