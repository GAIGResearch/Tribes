package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Swordman extends Unit
{
    public Swordman(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId) {
        super(SWORDMAN_ATTACK, SWORDMAN_DEFENCE, SWORDMAN_MOVEMENT, SWORDMAN_MAX_HP, SWORDMAN_RANGE, SWORDMAN_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.SWORDMAN;
    }

    @Override
    public Swordman copy(boolean hideInfo) {
        Swordman c = new Swordman(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId());
        c.setCurrentHP(getCurrentHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setKilled(isKilled());
        return hideInfo ? (Swordman) c.hide() : c;
    }
}
