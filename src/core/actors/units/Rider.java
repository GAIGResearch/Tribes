package core.actors.units;

import utils.Vector2d;

import static core.TribesConfig.*;

public class Rider extends Unit
{
    public Rider(Vector2d pos, int kills, boolean isVeteran, int ownerID) {
        super(RIDER_ATTACK, RIDER_DEFENCE, RIDER_MOVEMENT, RIDER_MAX_HP, RIDER_RANGE, RIDER_COST, pos, kills, isVeteran, ownerID);
    }

    @Override
    public Rider copy() {
        return new Rider(getCurrentPosition(), getKills(), isVeteran(), getOwnerID());
    }
}
