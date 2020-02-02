package core.units;

import core.Types;
import core.game.Board;

public class ForestTemple extends Building {

    private final Types.BUILDING TYPE = Types.BUILDING.TEMPLE;
    private final int COST = 15;
    private final int PRODUCTION = 1;
    private final int points = 100;
    private final Types.TERRAIN TERRAIN_CONSTRAINT = Types.TERRAIN.FOREST;


    public ForestTemple(int x, int y) {
        super(x, y);
    }

    @Override
    public Building copy() {
        return new ForestTemple(getX(), getY());
    }

    @Override
    public Types.BUILDING getTYPE() {
        return TYPE;
    }

    @Override
    public int getCOST() {
        return COST;
    }

    @Override
    public int getPRODUCTION() {
        return PRODUCTION;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public Types.TERRAIN getTERRAIN_CONSTRAINT() {
        return TERRAIN_CONSTRAINT;
    }

}
