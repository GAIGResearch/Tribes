package core.actors.units;

import core.TribesConfig;
import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Catapult extends Unit
{
    public Catapult(Vector2d pos, int kills, boolean isVeteran, int ownerID, int tribeId, TribesConfig tc) {
        super(tc.CATAPULT_ATTACK, tc.CATAPULT_DEFENCE, tc.CATAPULT_MOVEMENT, tc.CATAPULT_MAX_HP, tc.CATAPULT_RANGE, tc.CATAPULT_COST, pos, kills, isVeteran, ownerID, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.CATAPULT;
    }

    @Override
    public Catapult copy(boolean hideInfo) {
        Catapult c = new Catapult(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId(), new TribesConfig());
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        return hideInfo ? (Catapult) c.hide() : c;
    }
}
