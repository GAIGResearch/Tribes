package core.actors.units;

import utils.Vector2d;

import static core.TribesConfig.*;

public class MindBender extends Unit
{
    public MindBender(Vector2d pos, int kills, boolean isVeteran, int ownerID) {
        super(MINDBENDER_ATTACK, MINDBENDER_DEFENCE, MINDBENDER_MOVEMENT, MINDBENDER_MAX_HP, MINDBENDER_RANGE, MINDBENDER_COST, pos, kills, isVeteran, ownerID);
    }

    @Override
    public MindBender copy() {
        return new MindBender(getCurrentPosition(), getKills(), isVeteran(), getOwnerID());
    }
}
