package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Defender extends Unit
{
    public Defender(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId) {
        super(DEFENDER_ATTACK, DEFENDER_DEFENCE, DEFENDER_MOVEMENT, DEFENDER_MAX_HP, DEFENDER_RANGE, DEFENDER_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.DEFENDER;
    }

    @Override
    public Defender copy() {
        Defender c = new Defender(getCurrentPosition(), getKills(), isVeteran(), getCityID(), getTribeID());
        c.setCurrentHP(getCurrentHP());
        return c;
    }
}
