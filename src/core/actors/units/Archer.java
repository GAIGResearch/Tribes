package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Archer extends Unit
{
    public Archer(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId) {
        super(ARCHER_ATTACK, ARCHER_DEFENCE, ARCHER_MOVEMENT, ARCHER_MAX_HP, ARCHER_RANGE, ARCHER_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.ARCHER;
    }

    @Override
    public Archer copy() {
        Archer c = new Archer(getPosition(), getKills(), isVeteran(), getCityID(), getTribeId());
        c.setCurrentHP(getCurrentHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setIsKilled(getIsKilled());
        return c;
    }
}
