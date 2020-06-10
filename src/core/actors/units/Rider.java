package core.actors.units;

import core.TribesConfig;
import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Rider extends Unit
{
    public Rider(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId, TribesConfig tc) {
        super(tc.RIDER_ATTACK, tc.RIDER_DEFENCE, tc.RIDER_MOVEMENT, tc.RIDER_MAX_HP, tc.RIDER_RANGE, tc.RIDER_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.RIDER;
    }

    @Override
    public Rider copy(boolean hideInfo, TribesConfig tc) {
        Rider c = new Rider(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId(),tc);
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        return hideInfo ? (Rider) c.hide() : c;
    }
}
