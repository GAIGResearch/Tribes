package core.actions.unitactions;

import core.actions.Action;
import core.actors.units.Unit;
import core.game.Board;
import utils.Vector2d;

public abstract class UnitAction implements Action
{
    /**
     * Unit that PERFORMS this action
     */
    protected int unitId;

    public void setUnitId(int unitId) {this.unitId = unitId;}

    public int getUnitId()
    {
        return unitId;
    }

    public boolean unitInRange(Unit attacker, Unit defender, Board b)
    {
        //Check if target is visible.
        Vector2d targetPos = defender.getPosition();
        boolean[][] obsGrid = b.getTribe(attacker.getTribeId()).getObsGrid();
        if(!obsGrid[targetPos.x][targetPos.y]) return false;

        //We need to check if the target is in range (Actions may _not_ be created in AttackFactory.computeActionVariants)
        Vector2d attackerPos = attacker.getPosition();
        double distance = Vector2d.chebychevDistance(attackerPos, targetPos);
        return distance <= attacker.RANGE;
    }

}
