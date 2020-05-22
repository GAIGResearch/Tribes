package core.actors.units;

import core.TribesConfig;
import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Boat extends Unit
{
    private Types.UNIT baseLandUnit;

    public Boat(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId, TribesConfig tc) {
        super(tc.BOAT_ATTACK, tc.BOAT_DEFENCE, tc.BOAT_MOVEMENT, -1, tc.BOAT_RANGE, tc.BOAT_COST, pos, kills, isVeteran, cityId, tribeId);
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
    public Boat copy(boolean hideInfo) {
        Boat c = new Boat(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId(), new TribesConfig());
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setBaseLandUnit(getBaseLandUnit());
        return hideInfo ? (Boat) c.hide() : c;
    }
}