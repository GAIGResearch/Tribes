package core.units;

import core.Types;

public class Port extends Building {

    private Types.BUILDING type = Types.BUILDING.PORT;
    private int cost = 10;
    private int production = 2;
    private Types.TERRAIN constraint = Types.TERRAIN.SHALLOW_WATER;
    private City belonging;

    public Port(int x, int y, City belonging) {
        super(x, y);
        this.belonging = belonging;
    }

    public Types.BUILDING getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public int getProduction() {
        return production;
    }

    public Types.TERRAIN getConstraint() {
        return constraint;
    }

    public City getBelonging() {
        return belonging;
    }
}
