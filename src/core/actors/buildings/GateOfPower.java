package core.actors.buildings;

import core.Types;
import core.game.Board;

public class GateOfPower extends Building {

    private final Types.BUILDING TYPE = Types.BUILDING.GATE_OF_POWER;
    private final int PRODUCTION = 3;
    private final int points = 400;
    private final Types.TERRAIN TERRAIN_CONSTRAINT_1 = Types.TERRAIN.PLAIN;
    private final Types.TERRAIN TERRAIN_CONSTRAINT_2 = Types.TERRAIN.SHALLOW_WATER;

    public GateOfPower(int x, int y) {
        super(x, y);
    }

    @Override
    public Building copy() {
        return new GateOfPower(getX(), getY());
    }

    @Override
    public Types.BUILDING getTYPE() {
        return TYPE;
    }

    @Override
    public int getCOST() {
        return 0;
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
