package core.actions.unitactions;

import core.TribesConfig;
import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;

import java.util.LinkedList;

public class Attack extends UnitAction
{
    private int targetId;

    public Attack (int unitId)
    {
        super.unitId = unitId;
    }

    public void setTargetId(int targetId) {this.targetId = targetId;}
    public int getTargetId() {
        return targetId;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        Unit target = (Unit) gs.getActor(this.targetId);
        Unit attacker = (Unit) gs.getActor(this.unitId);

        // Check if target is not null
        if(target == null)
            return false;

        return unitInRange(attacker, target, gs.getBoard());
    }


    @Override
    public boolean execute(GameState gs) {

        //Check if action is feasible before execution
        if(isFeasible(gs)) {
            Unit attacker = (Unit) gs.getActor(this.unitId);
            Unit target = (Unit) gs.getActor(this.targetId);

            double attackForce = attacker.ATK*((double) attacker.getCurrentHP()/ attacker.getMaxHP());
            double defenceForce =target.DEF*((double)target.getCurrentHP()/target.getMaxHP());
            double accelerator = TribesConfig.ATTACK_MODIFIER;
            double totalDamage =attackForce+defenceForce;

            int attackResult = (int) Math.round((attackForce/totalDamage)* attacker.ATK*accelerator);
            int defenceResult = (int) Math.round((defenceForce / totalDamage) * target.DEF *accelerator);

            if (target.getCurrentHP() <= attackResult) {
                attacker.addKill();
                //TODO: We need to do something with this target that has been killed.
                target = null;

                //TODO: The attacking unit _may_ move to the target's previous position

            } else {

                target.setCurrentHP(target.getCurrentHP() - attackResult);

                //Retaliation attack

                //Check if this unit is in target's attacking range (we can use chebichev distance)
                double distance = Vector2d.chebychevDistance(attacker.getPosition(), target.getPosition());
                if(distance <= target.RANGE)
                {
                    //Deal damage based on targets defence stat, regardless of this units defence stat
                    attacker.setCurrentHP(attacker.getCurrentHP()-defenceResult);
                    //Check if attack kills this unit, if it does add a kill to the target
                    if(attacker.getCurrentHP() <=0 ) {
                        target.addKill();
                        //TODO: attacker is killed here. We need to manage this.
                    }
                }

            }
            return true;
        }
        return false;
    }
}
