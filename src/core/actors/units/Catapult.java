package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Catapult extends Unit
{
    public Catapult(Vector2d pos, int kills, boolean isVeteran, int ownerID, int tribeId) {
        super(CATAPULT_ATTACK, CATAPULT_DEFENCE, CATAPULT_MOVEMENT, CATAPULT_MAX_HP, CATAPULT_RANGE, CATAPULT_COST, pos, kills, isVeteran, ownerID, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.CATAPULT;
    }

    @Override
    public Catapult copy() {
        Catapult c = new Catapult(getPosition(), getKills(), isVeteran(), getCityID(), getTribeId());
        c.setCurrentHP(getCurrentHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setKilled(isKilled());
        return c;
    }
}
