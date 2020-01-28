package core.units;

import utils.Vector2d;

public class Archer extends Unit
{
    public Archer(Vector2d pos, int kills, boolean isVeteran) {
        super(2, 1, 1, 10, 2, 3, pos, kills, isVeteran);
    }

    @Override
    public Archer copy() {
        return new Archer(getCurrentPosition(), getKills(), isVeteran());
    }
}
