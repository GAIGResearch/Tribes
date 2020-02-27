package core.actors.buildings;

import core.TribesConfig;
import core.Types;
import core.game.Board;

public class ForestTemple extends Building {

    public ForestTemple(int x, int y) {
        super(x, y, TribesConfig.TEMPLE_FOREST_COST, Types.BUILDING.FOREST_TEMPLE, TribesConfig.TEMPLE_PRODUCTION, TribesConfig.TEMPLE_POINT);
    }

    @Override
    public boolean is_buildable(Board board) {
        return board.getTerrainAt(position.x, position.y) == Types.TERRAIN.FOREST;
    }

    @Override
    public Building copy() {
        return new ForestTemple(position.x, position.y);
    }
}
