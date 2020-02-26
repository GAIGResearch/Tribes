package core.actions.unitactions;

import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;

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

        for(int x = this.unit.getCurrentPosition().x- this.unit.RANGE; x <= x+ this.unit.RANGE; x++) {
            for (int y = this.unit.getCurrentPosition().y - this.unit.RANGE; y <= y + this.unit.RANGE; y++) {
                Attack a = new Attack(this.unit);
                a.setTarget(b.getUnitAt(x,y));
                if(!obsGrid[x][y]){
                    continue;
                }
                if(a.isFeasible(gs)){
                    attacks.add(a);
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
            float attackForce =this.unit.ATK*(this.unit.getCurrentHP()/this.unit.getMaxHP());
            float defenceForce =target.DEF*(target.getCurrentHP()/target.getMaxHP());
            float accelerator = 4.5f;
            float totalDamage =attackForce+defenceForce;
            int attackResult = Math.round((attackForce/totalDamage)*this.unit.ATK*accelerator);
            if (target.getCurrentHP() <= attackResult) {
                unit.addKill();
                target = null;
            } else {
                target.setCurrentHP(target.getCurrentHP() - attackResult);
            }
            return true;
        }
        return false;
    }
}
