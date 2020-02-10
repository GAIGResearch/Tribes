package core.actors.units;

import utils.Vector2d;

import static core.TribesConfig.*;

public class Swordman extends Unit
{
    public Swordman(Vector2d pos, int kills, boolean isVeteran, int ownerID) {
        super(SWORDMAN_ATTACK, SWORDMAN_DEFENCE, SWORDMAN_MOVEMENT, SWORDMAN_MAX_HP, SWORDMAN_RANGE, SWORDMAN_COST, pos, kills, isVeteran, ownerID);
    }

    @Override
    public Swordman copy() {
        Swordman c = new Swordman(getCurrentPosition(), getKills(), isVeteran(), getOwnerID());
        c.setCurrentHP(getCurrentHP());
        return c;
    }
}
