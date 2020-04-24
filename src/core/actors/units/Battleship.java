package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Battleship extends Unit
{
    private Types.UNIT baseLandUnit;

    public Battleship(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId) {
        super(BATTLESHIP_ATTACK, BATTLESHIP_DEFENCE, BATTLESHIP_MOVEMENT, -1, BATTLESHIP_RANGE, BATTLESHIP_COST, pos, kills, isVeteran, cityId, tribeId);
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
        Battleship c = new Battleship(getPosition(), getKills(), isVeteran(), getCityId(), getTribeId());
        c.setCurrentHP(getCurrentHP());
        c.setMaxHP(getMaxHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        c.setBaseLandUnit(getBaseLandUnit());
        return hideInfo ? (Battleship) c.hide() : c;
    }
}