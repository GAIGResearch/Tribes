package core.actors.units;

import utils.Vector2d;

public class Knight extends Unit
{
    public Knight(Vector2d pos, int kills, boolean isVeteran, int ownerID) {
        super(4, 1, 3, 15, 1, 8, pos, kills, isVeteran, ownerID);
    }

    @Override
    public Knight copy() {
        return new Knight(getCurrentPosition(), getKills(), isVeteran(), getOwnerID());
    }
}
