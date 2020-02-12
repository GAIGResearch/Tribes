package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Archer extends Unit
{
    public Archer(Vector2d pos, int kills, boolean isVeteran, int ownerID) {
        super(ARCHER_ATTACK, ARCHER_DEFENCE, ARCHER_MOVEMENT, ARCHER_MAX_HP, ARCHER_RANGE, ARCHER_COST, pos, kills, isVeteran, ownerID);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.ARCHER;
    }

    @Override
    public Archer copy() {
        Archer c = new Archer(getCurrentPosition(), getKills(), isVeteran(), getOwnerID());
        c.setCurrentHP(getCurrentHP());
        return c;
    }
}
