package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.game.Board;

public class Temple extends Building {

    public Temple(int x, int y, int cost, Types.BUILDING type) {
        super(x, y, cost, type, TribesConfig.TEMPLE_PRODUCTION, TribesConfig.TEMPLE_POINT);
    }

    @Override
    public boolean is_buildable(Board board) {
        return board.getTerrainAt(getX(), getY()).equals(Types.TERRAIN.PLAIN) || board.getTerrainAt(getX(), getY()).equals(Types.TERRAIN.MOUNTAIN) ||
                board.getTerrainAt(getX(), getY()).equals(Types.TERRAIN.SHALLOW_WATER) || board.getTerrainAt(getX(), getY()).equals(Types.TERRAIN.DEEP_WATER);
    }

    @Override
    public Building copy() {
        return new Temple(getX(), getY(), getCOST(), getTYPE());
    }
}
