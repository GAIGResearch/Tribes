package core.actors.buildings;

import core.actors.units.Unit;
import utils.Vector2d;

public class Swordsman extends Unit
{
    public Swordsman(Vector2d pos, int kills, boolean isVeteran, int ownerID) {
        super(3, 3, 1, 15, 1, 5, pos, kills, isVeteran, ownerID);
    }

    @Override
    public Swordsman copy() {
        return new Swordsman(getCurrentPosition(), getKills(), isVeteran(), getOwnerID());
    }
}
