package core.actors.units;

import core.TribesConfig;
import core.Types;
import core.game.GameState;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Ship extends Unit
{
    private Types.UNIT baseLandUnit;

    public Ship(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId, TribesConfig tc) {
        super(tc.SHIP_ATTACK, tc.SHIP_DEFENCE, tc.SHIP_MOVEMENT, -1, tc.SHIP_RANGE, tc.SHIP_COST, pos, kills, isVeteran, cityId, tribeId, tc);
    }

    public Types.UNIT getBaseLandUnit() {
        return baseLandUnit;
    }

    public void setBaseLandUnit(Types.UNIT baseLandUnit) {
        this.baseLandUnit = baseLandUnit;
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.SHIP;
    }

    @Override
    public Ship copy(boolean hideInfo) {
        Ship c = new Ship(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId(), tc);
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setBaseLandUnit(getBaseLandUnit());
        return hideInfo ? (Ship) c.hide() : c;
    }
}