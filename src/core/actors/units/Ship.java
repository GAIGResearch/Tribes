package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Ship extends Unit
{
    private Types.UNIT baseLandUnit;

    public Ship(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId) {
        super(SHIP_ATTACK, SHIP_DEFENCE, SHIP_MOVEMENT, -1, SHIP_RANGE, SHIP_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    public Types.UNIT getBaseLandUnit() {
        return baseLandUnit;
    }

    public void setBaseLandUnit(Types.UNIT baseLandUnit) {
        if(baseLandUnit == Types.UNIT.BOAT)
        {
            int a = 0;
        }
        this.baseLandUnit = baseLandUnit;
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.SHIP;
    }

    @Override
    public Ship copy(boolean hideInfo) {
        Ship c = new Ship(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId());
        c.setCurrentHP(getCurrentHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setBaseLandUnit(getBaseLandUnit());
        c.setKilled(isKilled());
        return hideInfo ? (Ship) c.hide() : c;
    }
}