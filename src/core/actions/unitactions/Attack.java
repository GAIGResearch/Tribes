package core.actions.unitactions;

import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;

import java.util.LinkedList;

public class Attack extends UnitAction
{
    private Unit target;

    public Attack (Unit attacker)
    {
        super.unit = attacker;
    }

    public void setTarget(Unit target) {this.target = target;}
    public Unit getTarget() {
        return target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {

        LinkedList<Action> attacks = new LinkedList<>();
        Board b = gs.getBoard();
        boolean[][] obsGrid = b.getTribe(this.unit.getTribeId()).getObsGrid();
        Vector2d position = unit.getPosition();

        // Loop through unit range, check if tile observable and action feasible, if so add action
        for(int i = position.x - this.unit.RANGE; i <= position.x + this.unit.RANGE; i++) {
            for (int j = position.y - this.unit.RANGE; j <= position.y + this.unit.RANGE; j++) {

                //Not attacking itself
                if(i != position.x || j != position.y) {
                    Attack a = new Attack(this.unit);
                    a.setTarget(b.getUnitAt(i, j));
                    if (!obsGrid[i][j]) {
                        continue;
                    }
                    if (a.isFeasible(gs)) {
                        attacks.add(a);
                    }
                }
            }
        }
        return attacks;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        // Check if target is not null
        if(target == null)
            return false;

        return true;
    }

    @Override
    public boolean execute(GameState gs) {
        //Check if action is feasible before execution
        if(isFeasible(gs)) {
            float attackForce =this.unit.ATK*((float)this.unit.getCurrentHP()/this.unit.getMaxHP());
            float defenceForce =target.DEF*((float)target.getCurrentHP()/target.getMaxHP());
            float accelerator = 4.5f;
            float totalDamage =attackForce+defenceForce;
            int attackResult = Math.round((attackForce/totalDamage)*this.unit.ATK*accelerator);
            if (target.getCurrentHP() <= attackResult) {
                unit.addKill();
                target = null;
            } else {
                target.setCurrentHP(target.getCurrentHP() - attackResult);
                Board b = gs.getBoard();
                //Check if this unit is in target attacking range
                for (int x = target.getPosition().x - target.RANGE; x < target.getPosition().x + this.unit.RANGE; x++){
                    for (int y = target.getPosition().y - target.RANGE; y < target.getPosition().y + this.unit.RANGE; y++){
                        if(b.getUnitAt(x,y).equals(this.unit)){
                            //Deal damage based on targets defence stat, regardless of this units defence stat
                            this.unit.setCurrentHP(this.unit.getCurrentHP()-target.DEF);
                            //Check if attack kills this unit, if it does add a kill to the target
                            if(this.unit.getCurrentHP() <=0)
                                target.addKill();
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
