package core.units;

import utils.Vector2d;

public class Swordsman extends Unit
{
    public Swordsman(Vector2d pos, int kills, boolean isVeteran) {
        super(3, 3, 1, 15, 1, 5, pos, kills, isVeteran);
    }
}
