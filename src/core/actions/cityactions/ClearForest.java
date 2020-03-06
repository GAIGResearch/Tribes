package core.actions.cityactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actors.City;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

import java.util.LinkedList;

public class ClearForest extends CityAction
{

    public ClearForest(City c) {
        super.city = c;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        Board currentBoard = gs.getBoard();
        LinkedList<Vector2d> tiles = currentBoard.getCityTiles(city.getActorId());
        boolean techReq = gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.FORESTRY);
        if (techReq){
            for(Vector2d tile: tiles){
                if (currentBoard.getTerrainAt(tile.x, tile.y) == Types.TERRAIN.FOREST){
                    ClearForest action = new ClearForest(city);
                    action.setTargetPos(new Vector2d(tile.x, tile.y));
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        boolean isForest = gs.getBoard().getTerrainAt(targetPos.x, targetPos.y) == Types.TERRAIN.FOREST;
        boolean isBelonging = gs.getBoard().getCityIdAt(targetPos.x, targetPos.y) == city.getActorId();
        boolean isResearched = gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.FORESTRY);
        return isForest && isBelonging && isResearched;
    }

    @Override
    public boolean execute(GameState gs) {
        if (isFeasible(gs)){
            gs.getBoard().setTerrainAt(targetPos.x, targetPos.y, Types.TERRAIN.PLAIN);
            gs.getTribe(city.getTribeId()).addStars(TribesConfig.CLEAR_FOREST_STAR);
            return true;
        }
        return false;
    }
}
