package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.game.Board;

public class Temple extends Building {

    public Temple(int x, int y) {
        super(x, y, TribesConfig.TEMPLE_COST, Types.BUILDING.TEMPLE, TribesConfig.TEMPLE_PRODUCTION, TribesConfig.TEMPLE_POINT);
    }

    @Override
    public boolean is_buildable(Board board) {
        return board.getTerrainAt(position.x, position.y).equals(Types.TERRAIN.PLAIN) || board.getTerrainAt(position.x, position.y).equals(Types.TERRAIN.MOUNTAIN) ||
                board.getTerrainAt(position.x, position.y).equals(Types.TERRAIN.SHALLOW_WATER) || board.getTerrainAt(position.x, position.y).equals(Types.TERRAIN.DEEP_WATER);
    }

    @Override
    public Building copy() {
        return new Temple(position.x, position.y);
    }
}
