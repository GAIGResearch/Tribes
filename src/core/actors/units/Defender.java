package core.actors.units;

import core.TribesConfig;
import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Defender extends Unit
{

    public Defender(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId, TribesConfig tc) {
        super(tc.DEFENDER_ATTACK, tc.DEFENDER_DEFENCE, tc.DEFENDER_MOVEMENT, tc.DEFENDER_MAX_HP, tc.DEFENDER_RANGE, tc.DEFENDER_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.DEFENDER;
    }

    @Override
    public Defender copy(boolean hideInfo) {
        Defender c = new Defender(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId(),new TribesConfig());
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        return hideInfo ? (Defender) c.hide() : c;
    }
}
