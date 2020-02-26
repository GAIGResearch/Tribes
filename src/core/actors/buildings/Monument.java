package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.game.Board;

public class Monument extends Building{

    public Monument(int x, int y, Types.BUILDING type) {
        super(x, y, 0, type, TribesConfig.MONUMENT_PRODUCTION, TribesConfig.MONUMENT_POINT);
    }

    @Override
    public boolean is_buildable(Board board) {
        return board.getTerrainAt(position.x, position.y).equals(Types.TERRAIN.PLAIN) || board.getTerrainAt(position.x, position.y).equals(Types.TERRAIN.SHALLOW_WATER);
    }

    @Override
    public Building copy() {
        return new Monument(position.x, position.y, getTYPE());
    }
}
