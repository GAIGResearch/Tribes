package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Ship extends Unit
{

    public Ship(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId) {
        super(SHIP_ATTACK, SHIP_DEFENCE, SHIP_MOVEMENT, -1, SHIP_RANGE, SHIP_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.SHIP;
    }

    @Override
    public Ship copy() {
        Ship c = new Ship(getPosition(), getKills(), isVeteran(), getCityID(), getTribeId());
        c.setCurrentHP(getCurrentHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        return c;
    }
}