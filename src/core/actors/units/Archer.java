package core.actors.units;

import core.TribesConfig;
import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Archer extends Unit
{
    public Archer(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId, TribesConfig tc) {
        super(tc.ARCHER_ATTACK, tc.ARCHER_DEFENCE, tc.ARCHER_MOVEMENT, tc.ARCHER_MAX_HP, tc.ARCHER_RANGE, tc.ARCHER_COST, pos, kills, isVeteran, cityId, tribeId,tc);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.ARCHER;
    }

    @Override
    public Archer copy(boolean hideInfo) {
        Archer c = new Archer(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId(),tc.copy());
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        return hideInfo ? (Archer) c.hide() : c;
    }
}
