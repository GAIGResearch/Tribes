package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Battleship extends Unit
{
    public Battleship(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId) {
        super(BATTLESHIP_ATTACK, BATTLESHIP_DEFENCE, BATTLESHIP_MOVEMENT, -1, BATTLESHIP_RANGE, BATTLESHIP_COST, pos, kills, isVeteran, cityId, tribeId);
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.BATTLESHIP;
    }

    @Override
    public Battleship copy() {
        Battleship c = new Battleship(getPosition(), getKills(), isVeteran(), getCityID(), getTribeId());
        c.setCurrentHP(getCurrentHP());
        c.setActorId(getActorId());
        c.setStatus(getStatus());
        return c;
    }
}