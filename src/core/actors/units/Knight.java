package core.actors.units;

import utils.Vector2d;

import static core.TribesConfig.*;

public class Knight extends Unit
{
    public Knight(Vector2d pos, int kills, boolean isVeteran, int ownerID) {
        super(KNIGHT_ATTACK, KNIGHT_DEFENCE, KNIGHT_MOVEMENT, KNIGHT_MAX_HP, KNIGHT_RANGE, KNIGHT_COST, pos, kills, isVeteran, ownerID);
    }

    @Override
    public Knight copy() {
        return new Knight(getCurrentPosition(), getKills(), isVeteran(), getOwnerID());
    }
}
