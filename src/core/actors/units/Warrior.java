package core.actors.units;

import utils.Vector2d;

import static core.TribesConfig.*;

public class Warrior extends Unit
{
    public Warrior(Vector2d pos, int kills, boolean isVeteran, int ownerID) {
        super(WARRIOR_ATTACK, WARRIOR_DEFENCE, WARRIOR_MOVEMENT, WARRIOR_MAX_HP, WARRIOR_RANGE, WARRIOR_COST, pos, kills, isVeteran, ownerID);
    }

    @Override
    public Warrior copy() {
        return new Warrior(getCurrentPosition(), getKills(), isVeteran(), getOwnerID());
    }
}
