package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Boat extends Unit
{
    private Types.UNIT baseLandUnit;

    public Boat(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId) {
        super(BOAT_ATTACK, BOAT_DEFENCE, BOAT_MOVEMENT, -1, BOAT_RANGE, BOAT_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    public Types.UNIT getBaseLandUnit() {
        return baseLandUnit;
    }

    public void setBaseLandUnit(Types.UNIT baseLandUnit) {
        this.baseLandUnit = baseLandUnit;
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.BOAT;
    }

    @Override
    public Boat copy() {
        Boat c = new Boat(getPosition(), getKills(), isVeteran(), getCityID(), getTribeId());
        c.setCurrentHP(getCurrentHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setBaseLandUnit(getBaseLandUnit());
        c.setKilled(isKilled());
        return c;
    }
}