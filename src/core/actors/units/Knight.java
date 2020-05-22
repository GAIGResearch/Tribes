package core.actors.units;

import core.TribesConfig;
import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Knight extends Unit
{
    public Knight(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId, TribesConfig tc) {
        super(tc.KNIGHT_ATTACK, tc.KNIGHT_DEFENCE, tc.KNIGHT_MOVEMENT, tc.KNIGHT_MAX_HP, tc.KNIGHT_RANGE, tc.KNIGHT_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.KNIGHT;
    }

    @Override
    public Knight copy(boolean hideInfo) {
        Knight c = new Knight(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId(), new TribesConfig());
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        return hideInfo ? (Knight) c.hide() : c;
    }
}
