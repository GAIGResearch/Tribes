package core.actors.units;

import core.TribesConfig;
import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class MindBender extends Unit
{
    public MindBender(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId, TribesConfig tc) {
        super(tc.MINDBENDER_ATTACK, tc.MINDBENDER_DEFENCE, tc.MINDBENDER_MOVEMENT, tc.MINDBENDER_MAX_HP, tc.MINDBENDER_RANGE, tc.MINDBENDER_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.MIND_BENDER;
    }

    @Override
    public MindBender copy(boolean hideInfo, TribesConfig tc) {
        MindBender c = new MindBender(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId(), tc);
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        return hideInfo ? (MindBender) c.hide() : c;
    }
}
