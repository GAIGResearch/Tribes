package core.units;

import utils.Vector2d;

public class Catapult extends Unit
{
    public Catapult(Vector2d pos, int kills, boolean isVeteran) {
        super(4, 0, 1, 10, 3, 8, pos, kills, isVeteran);
    }
}
