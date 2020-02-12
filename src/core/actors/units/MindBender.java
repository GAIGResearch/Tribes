package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class MindBender extends Unit
{
    public MindBender(Vector2d pos, int kills, boolean isVeteran, int ownerID) {
        super(MINDBENDER_ATTACK, MINDBENDER_DEFENCE, MINDBENDER_MOVEMENT, MINDBENDER_MAX_HP, MINDBENDER_RANGE, MINDBENDER_COST, pos, kills, isVeteran, ownerID);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.MIND_BEARER;
    }

    @Override
    public MindBender copy() {
        MindBender c = new MindBender(getCurrentPosition(), getKills(), isVeteran(), getOwnerID());
        c.setCurrentHP(getCurrentHP());
        return c;
    }
}
