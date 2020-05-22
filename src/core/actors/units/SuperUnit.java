package core.actors.units;

import core.TribesConfig;
import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class SuperUnit extends Unit
{
    public SuperUnit(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId, TribesConfig tc) {
        super(tc.SUPERUNIT_ATTACK, tc.SUPERUNIT_DEFENCE, tc.SUPERUNIT_MOVEMENT, tc.SUPERUNIT_MAX_HP, tc.SUPERUNIT_RANGE, tc.SUPERUNIT_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.SUPERUNIT;
    }

    @Override
    public SuperUnit copy(boolean hideInfo) {
        SuperUnit c = new SuperUnit(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId(), new TribesConfig());
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        return hideInfo ? (SuperUnit) c.hide() : c;
    }
}
