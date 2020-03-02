package core.actors.buildings;

import core.TribesConfig;
import core.Types;

public class Temple extends Building {

    public Temple(int x, int y, int cost, Types.BUILDING type) {
        super(x, y, cost, type, TribesConfig.TEMPLE_PRODUCTION, TribesConfig.TEMPLE_POINT);
    }

    @Override
    public Building copy() {
        return new Temple(position.x, position.y, getCOST(), getTYPE());
    }
}
