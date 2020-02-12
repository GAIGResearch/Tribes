package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Ship extends Unit
{
    private int hp;

    public Ship(Vector2d pos, int kills, boolean isVeteran, int ownerID, int hp) {
        super(SHIP_ATTACK, SHIP_DEFENCE, SHIP_MOVEMENT, hp, SHIP_RANGE, SHIP_COST, pos, kills, isVeteran, ownerID);
        this.hp = hp;
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.SHIP;
    }

    @Override
    public Ship copy() {
        Ship c = new Ship(getCurrentPosition(), getKills(), isVeteran(), getOwnerID(), hp);
        c.setCurrentHP(getCurrentHP());
        return c;
    }
}