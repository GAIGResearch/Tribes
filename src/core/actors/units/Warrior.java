package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Warrior extends Unit
{
    public Warrior(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId) {
        super(WARRIOR_ATTACK, WARRIOR_DEFENCE, WARRIOR_MOVEMENT, WARRIOR_MAX_HP, WARRIOR_RANGE, WARRIOR_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.WARRIOR;
    }

    @Override
    public Warrior copy(boolean hideInfo) {
        Warrior c = new Warrior(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId());
        c.setCurrentHP(getCurrentHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setKilled(isKilled());
        return hideInfo ? (Warrior) c.hide() : c;
    }
}
