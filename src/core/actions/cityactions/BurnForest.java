package core.actions.cityactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.City;
import utils.Vector2d;

import java.util.LinkedList;

public class BurnForest extends CityAction
{
    private Vector2d position;

    public BurnForest(City c) {
        super.city = c;
    }
    public void setPosition(int x, int y){
        this.position = new Vector2d(x, y);
    }
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        Board currentBoard = gs.getBoard();
        LinkedList<Vector2d> tiles = currentBoard.getCityTiles(city.getActorId());
        boolean techReq = gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.CHIVALRY);
        boolean costReq = gs.getTribe(city.getTribeId()).getStars() >= TribesConfig.FOREST_COST;
        if (techReq && costReq){
            for(Vector2d tile: tiles){
                if (currentBoard.getTerrainAt(tile.x, tile.y) == Types.TERRAIN.FOREST){
                    BurnForest action = new BurnForest(city);
                    action.setPosition(tile.x, tile.y);
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        boolean isForest = gs.getBoard().getTerrainAt(position.x, position.y) == Types.TERRAIN.FOREST;
        boolean isBelonging = gs.getBoard().getCityIdAt(position.x, position.y) == city.getActorId();
        boolean isBuildable = gs.getTribe(city.getTribeId()).getStars() >= TribesConfig.FOREST_COST;
        boolean isResearched = gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.CHIVALRY);
        return isForest && isBelonging && isBuildable && isResearched;
    }

    @Override
    public boolean execute(GameState gs) {
        if (isFeasible(gs)){
            gs.getBoard().setTerrainAt(position.x, position.y, Types.TERRAIN.PLAIN);
            if (gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.ORGANIZATION)) {
                gs.getBoard().setResourceAt(position.x, position.y, Types.RESOURCE.CROPS);
            }
            gs.getTribe(city.getTribeId()).subtractStars(TribesConfig.FOREST_COST);
            return true;
        }
        return false;
    }
}
