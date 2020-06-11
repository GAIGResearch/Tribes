package core.actors.units;

import core.TribesConfig;
import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Swordman extends Unit
{
    public Swordman(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId, TribesConfig tc) {
        super(tc.SWORDMAN_ATTACK, tc.SWORDMAN_DEFENCE, tc.SWORDMAN_MOVEMENT, tc.SWORDMAN_MAX_HP, tc.SWORDMAN_RANGE, tc.SWORDMAN_COST, pos, kills, isVeteran, cityId, tribeId, tc);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.SWORDMAN;
    }

    @Override
    public Swordman copy(boolean hideInfo) {
        Swordman c = new Swordman(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId(),tc);
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        return hideInfo ? (Swordman) c.hide() : c;
    }
}
