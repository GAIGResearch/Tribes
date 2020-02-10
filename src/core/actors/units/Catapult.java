package core.actors.units;

import utils.Vector2d;

import static core.TribesConfig.*;

public class Catapult extends Unit
{
    public Catapult(Vector2d pos, int kills, boolean isVeteran, int ownerID) {
        super(CATAPULT_ATTACK, CATAPULT_DEFENCE, CATAPULT_MOVEMENT, CATAPULT_MAX_HP, CATAPULT_RANGE, CATAPULT_COST, pos, kills, isVeteran, ownerID);
    }

    @Override
    public Catapult copy() {
        Catapult c = new Catapult(getCurrentPosition(), getKills(), isVeteran(), getOwnerID());
        c.setCurrentHP(getCurrentHP());
        return c;
    }
}
