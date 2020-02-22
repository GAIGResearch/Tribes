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

    private int x;
    private int y;

    public ClearForest(City c) {
        super.city = c;
    }
    public void setLocation(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        Board currentBoard = gs.getBoard();
        LinkedList<Vector2d> tiles = currentBoard.getCityTiles(city.getActorID());
        boolean techReq = gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.FORESTRY);
        if (techReq){
            for(Vector2d tile: tiles){
                if (currentBoard.getTerrainAt(tile.x, tile.y) == Types.TERRAIN.FOREST){
                    ClearForest action = new ClearForest(city);
                    action.setLocation(tile.x, tile.y);
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        boolean isForest = gs.getBoard().getTerrainAt(x, y) == Types.TERRAIN.FOREST;
        boolean isBelonging = gs.getBoard().getTileCityId(x, y) == city.getActorID();
        return isForest && isBelonging;
    }

    @Override
    public boolean execute(GameState gs) {
        if (isFeasible(gs)){
            gs.getBoard().setTerrainAt(x, y, Types.TERRAIN.PLAIN);
            gs.getTribe(city.getTribeId()).addStars(TribesConfig.CLEAR_FOREST_STAR);
            return true;
        }
        return false;
    }
}
