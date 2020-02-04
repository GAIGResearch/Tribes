package core.units;

import utils.Vector2d;

public class Warrior extends Unit
{
    public Warrior(Vector2d pos, int kills, boolean isVeteran, int ownerID) {
        super(2, 2, 1, 10, 1, 2, pos, kills, isVeteran, ownerID);
    }

    @Override
    public Warrior copy() {
        return new Warrior(getCurrentPosition(), getKills(), isVeteran(), getOwnerID());
    }
}
