package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Boat extends Unit
{
    private int hp;

    public Boat(Vector2d pos, int kills, boolean isVeteran, int ownerID, int hp) {
        super(BOAT_ATTACK, BOAT_DEFENCE, BOAT_MOVEMENT, hp, BOAT_RANGE, BOAT_COST, pos, kills, isVeteran, ownerID);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.BOAT;
    }

    @Override
    public Boat copy() {
        Boat c = new Boat(getCurrentPosition(), getKills(), isVeteran(), getOwnerID(), hp);
        c.setCurrentHP(getCurrentHP());
        return c;
    }
}