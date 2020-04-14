package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Knight extends Unit
{
    public Knight(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId) {
        super(KNIGHT_ATTACK, KNIGHT_DEFENCE, KNIGHT_MOVEMENT, KNIGHT_MAX_HP, KNIGHT_RANGE, KNIGHT_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.KNIGHT;
    }

    @Override
    public Knight copy() {
        Knight c = new Knight(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId());
        c.setCurrentHP(getCurrentHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setKilled(isKilled());
        return c;
    }
}
