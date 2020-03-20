package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Rider extends Unit
{
    public Rider(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId) {
        super(RIDER_ATTACK, RIDER_DEFENCE, RIDER_MOVEMENT, RIDER_MAX_HP, RIDER_RANGE, RIDER_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.RIDER;
    }

    @Override
    public Rider copy() {
        Rider c = new Rider(getPosition(), getKills(), isVeteran(), getCityID(), getTribeId());
        c.setCurrentHP(getCurrentHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setIsKilled(getIsKilled());
        return c;
    }
}
