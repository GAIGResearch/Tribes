package core.actors.units;

import core.Types;
import utils.Vector2d;

import static core.TribesConfig.*;

public class Battleship extends Unit
{
    private int hp;

    public Battleship(Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeId, int hp) {
        super(BATTLESHIP_ATTACK, BATTLESHIP_DEFENCE, BATTLESHIP_MOVEMENT, hp, BATTLESHIP_RANGE, BATTLESHIP_COST, pos, kills, isVeteran, cityId, tribeId);
        this.hp = hp;
    }

    @Override
    public Types.UNIT getType() {
        return Types.UNIT.BATTLESHIP;
    }

    @Override
    public Battleship copy() {
        Battleship c = new Battleship(getPosition(), getKills(), isVeteran(), getCityID(), getTribeId(), hp);
        c.setCurrentHP(getCurrentHP());
        c.setActorId(getActorId());
        return c;
    }
}