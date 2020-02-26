package core.actions.cityactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.City;
import utils.Vector2d;

import java.util.LinkedList;

public class GrowForest extends CityAction
{

    private Vector2d position;

    public GrowForest(City c)
    {
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
        boolean techReq = gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.SPIRITUALISM);
        boolean costReq = gs.getTribe(city.getTribeId()).getStars() >= TribesConfig.FOREST_COST;
        if (techReq && costReq){
            for(Vector2d tile: tiles){
                if (currentBoard.getTerrainAt(tile.x, tile.y) == Types.TERRAIN.PLAIN){
                    GrowForest action = new GrowForest(city);
                    action.setPosition(tile.x, tile.y);
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        boolean isPlain = gs.getBoard().getTerrainAt(position.x, position.y) == Types.TERRAIN.PLAIN;
        boolean isBelonging = gs.getBoard().getCityIdAt(position.x, position.y) == city.getActorId();
        boolean isBuildable = gs.getTribe(city.getTribeId()).getStars() >= TribesConfig.FOREST_COST;
        boolean isResearched = gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.SPIRITUALISM);
        return isPlain && isBelonging && isBuildable && isResearched;
    }

    @Override
    public boolean execute(GameState gs) {
        if (isFeasible(gs)){
            gs.getBoard().setTerrainAt(position.x, position.y, Types.TERRAIN.FOREST);
            gs.getTribe(city.getTribeId()).subtractStars(TribesConfig.FOREST_COST);
            return true;
        }
        return false;
    }
}
