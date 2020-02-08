package core.actors.units;

import utils.Vector2d;

import static core.TribesConfig.*;

public class Battleship extends Unit
{
    private int hp;

    public Battleship(Vector2d pos, int kills, boolean isVeteran, int ownerID, int hp) {
        super(BATTLESHIP_ATTACK, BATTLESHIP_DEFENCE, BATTLESHIP_MOVEMENT, hp, BATTLESHIP_RANGE, BATTLESHIP_COST, pos, kills, isVeteran, ownerID);
        this.hp = hp;
    }

    @Override
    public Battleship copy() {
        Battleship c = new Battleship(getCurrentPosition(), getKills(), isVeteran(), getOwnerID(), hp);
        c.setCurrentHP(getCurrentHP());
        return c;
    }
}