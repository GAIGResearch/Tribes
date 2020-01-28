package core.units;

import utils.Vector2d;

public class Rider extends Unit
{
    public Rider(Vector2d pos, int kills, boolean isVeteran) {
        super(2, 1, 2, 10, 1, 3, pos, kills, isVeteran);
    }
}
