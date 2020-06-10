package core.actors.units;

import core.TribesConfig;
import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Warrior extends Unit
{
    public Warrior(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId, TribesConfig tc) {
        super(tc.WARRIOR_ATTACK, tc.WARRIOR_DEFENCE, tc.WARRIOR_MOVEMENT, tc.WARRIOR_MAX_HP, tc.WARRIOR_RANGE, tc.WARRIOR_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.WARRIOR;
    }

    @Override
    public Warrior copy(boolean hideInfo, TribesConfig tc) {
        Warrior c = new Warrior(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId(),tc);
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        return hideInfo ? (Warrior) c.hide() : c;
    }
}
