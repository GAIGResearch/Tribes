package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class SuperUnit extends Unit
{
    public SuperUnit(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId) {
        super(SUPERUNIT_ATTACK, SUPERUNIT_DEFENCE, SUPERUNIT_MOVEMENT, SUPERUNIT_MAX_HP, SUPERUNIT_RANGE, SUPERUNIT_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.SUPERUNIT;
    }

    @Override
    public SuperUnit copy() {
        SuperUnit c = new SuperUnit(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId());
        c.setCurrentHP(getCurrentHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setKilled(isKilled());
        return c;
    }
}
