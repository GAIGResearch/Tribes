package core.actions.cityactions;

import core.Types;
import core.actions.Action;
import core.actors.buildings.Building;
import core.game.Board;
import core.game.GameState;
import core.actors.City;
import utils.Vector2d;

import java.util.LinkedList;

public class Destroy extends CityAction
{

    public Destroy(City c)
    {
        super.city = c;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        Board currentBoard = gs.getBoard();
        LinkedList<Vector2d> tiles = currentBoard.getCityTiles(city.getActorId());
        boolean techReq = gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.CONSTRUCTION);
        if (techReq){
            for(Vector2d tile: tiles){
                if (currentBoard.getBuildingAt(tile.x, tile.y) != null){
                    Destroy action = new Destroy(city);
                    action.setTargetPos(new Vector2d(tile.x, tile.y));
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        boolean isBuilding = gs.getBoard().getBuildingAt(targetPos.x, targetPos.y) != null;
        boolean isBelonging = gs.getBoard().getCityIdAt(targetPos.x, targetPos.y) == city.getActorId();
        boolean isResearched = gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.CONSTRUCTION);
        return isBuilding && isBelonging && isResearched;
    }

    @Override
    public boolean execute(GameState gs) {
        if (isFeasible(gs)){
            Building removedBuilding = city.removeBuilding(targetPos.x, targetPos.y);
            if (removedBuilding != null) {
                gs.getBoard().setBuildingAt(targetPos.x, targetPos.y, null);
                if (removedBuilding.getTYPE() != Types.BUILDING.CUSTOM_HOUSE) {
                    city.subtractPopulation(removedBuilding.getPRODUCTION());
                }else{
                    city.subtractProduction(removedBuilding.getPRODUCTION());
                }
                // TODO: Should be check if the building enum is changed
                if (removedBuilding.getTYPE().getKey() >= Types.BUILDING.TEMPLE.getKey()) {
                    gs.getTribe(city.getTribeId()).subtractScore(removedBuilding.getPoints());
                }

                boolean isTemple = removedBuilding.getTYPE().getKey() >= Types.BUILDING.TEMPLE.getKey() && removedBuilding.getTYPE().getKey() <= Types.BUILDING.MOUNTAIN_TEMPLE.getKey();
                if(isTemple){
                    city.subtractLongTermPoints(removedBuilding.getPoints());
                }


                return true;
            }
        }
        return false;
    }
}
