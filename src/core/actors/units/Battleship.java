package core.actors.units;

import core.TribesConfig;
import core.Types;
import core.actors.Tribe;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Battleship extends Unit
{
    private Types.UNIT baseLandUnit;

    public Battleship(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId, TribesConfig tc) {
        super(tc.BATTLESHIP_ATTACK, tc.BATTLESHIP_DEFENCE, tc.BATTLESHIP_MOVEMENT, -1, tc.BATTLESHIP_RANGE, tc.BATTLESHIP_COST, pos, kills, isVeteran, cityId, tribeId, tc);
    }

    public Types.UNIT getBaseLandUnit() {
        return baseLandUnit;
    }

    public void setBaseLandUnit(Types.UNIT baseLandUnit) {
        this.baseLandUnit = baseLandUnit;
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.BATTLESHIP;
    }

    @Override
    public Battleship copy(boolean hideInfo) {
        Battleship c = new Battleship(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId(), tc);
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setBaseLandUnit(getBaseLandUnit());
        return hideInfo ? (Battleship) c.hide() : c;
    }
}