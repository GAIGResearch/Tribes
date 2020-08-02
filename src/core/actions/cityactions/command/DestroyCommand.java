package core.actions.cityactions.command;

import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.cityactions.Destroy;
import core.actors.Building;
import core.actors.City;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

public class DestroyCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        Destroy action = (Destroy)a;
        if (action.isFeasible(gs)){
            int cityId = action.getCityId();
            Vector2d targetPos = action.getTargetPos();
            City city = (City) gs.getActor(cityId);
            Building buildingToRemove = city.getBuilding(targetPos.x, targetPos.y);

            Board b = gs.getBoard();
            b.setBuildingAt(targetPos.x, targetPos.y, null);

            if(buildingToRemove.type == Types.BUILDING.PORT)
            {
                //If a port is removed, then the tile stops belonging to the trade network
                b.destroyPort(targetPos.x, targetPos.y);
            }

            city.removeBuilding(gs, buildingToRemove);
            return true;
        }
        return false;
    }
}
