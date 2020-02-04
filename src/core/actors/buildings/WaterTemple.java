package core.actors.buildings;

import core.Types;
import core.game.Board;

public class WaterTemple extends Building {

    private final Types.BUILDING TYPE = Types.BUILDING.TEMPLE;
    private final int COST = 20;
    private final int PRODUCTION = 1;
    private final int points = 100;
    private final Types.TERRAIN TERRAIN_CONSTRAINT_1 = Types.TERRAIN.SHALLOW_WATER;
    private final Types.TERRAIN TERRAIN_CONSTRAINT_2 = Types.TERRAIN.DEEP_WATER;

    public WaterTemple(int x, int y) {
        super(x, y);
    }

    @Override
    public Building copy() {
        return new WaterTemple(getX(), getY());
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
    public boolean is_buildable(Board board) {
        return board.getTerrainAt(getX(), getY()).equals(TERRAIN_CONSTRAINT_1) || board.getTerrainAt(getX(), getY()).equals(TERRAIN_CONSTRAINT_2);
    }
}
