package core.actions.unitactions.command;

import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.unitactions.Attack;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.GameState;
import utils.Pair;
import utils.Vector2d;

public class AttackCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        Attack action = (Attack)a;
        //Check if action is feasible before execution
        if(action.isFeasible(gs)) {

            int unitId = action.getUnitId();
            int targetId = action.getTargetId();

            Unit attacker = (Unit) gs.getActor(unitId);
            Unit target = (Unit) gs.getActor(targetId);

            attacker.transitionToStatus(Types.TURN_STATUS.ATTACKED);
            Tribe attackerTribe = gs.getTribe(attacker.getTribeId());
            attackerTribe.resetPacifistCount();

            Pair<Integer, Integer> results = action.getAttackResults(gs);
            int attackResult = results.getFirst();
            int defenceResult = results.getSecond();

            if (target.getCurrentHP() <= attackResult) {

                attacker.addKill();
                attackerTribe.addKill();

                //Actually kill the unit
                gs.killUnit(target);

                //After killing the unit, move to target position if unit is melee type
                switch (attacker.getType()) {
                    case DEFENDER:
                    case SWORDMAN:
                    case RIDER:
                    case WARRIOR:
                    case KNIGHT:
                    case SUPERUNIT:
                        gs.getBoard().tryPush(attackerTribe, attacker, attacker.getPosition().x, attacker.getPosition().y, target.getPosition().x, target.getPosition().y, gs.getRandomGenerator());
                }


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
                        gs.getTribe(target.getTribeId()).addKill();

                        //Actually kill the unit
                        gs.killUnit(attacker);
                    }
                }

            }
            return true;
        }
        return false;
    }
}
