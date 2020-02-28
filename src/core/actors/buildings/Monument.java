package core.actors.buildings;

import core.TribesConfig;
import core.Types;

public class Monument extends Building{

    public Monument(int x, int y, Types.BUILDING type) {
        super(x, y, 0, type, TribesConfig.MONUMENT_PRODUCTION, TribesConfig.MONUMENT_POINT);
    }

    @Override
    public Building copy() {
        return new Monument(getX(), getY(), getTYPE());
    }
}
