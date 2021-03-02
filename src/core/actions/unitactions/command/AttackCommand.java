package core.actions.unitactions.command;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.unitactions.Attack;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.GameState;
import utils.Pair;
import utils.Vector2d;

import static core.Types.TECHNOLOGY.*;
import static core.Types.TERRAIN.*;

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

            Pair<Integer, Integer> results = getAttackResults(action, gs);
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

    /**
     * Calculates the damage dealt by the attacker and by the defender.
     * @param gs - current game state
     * @return Pair, where first element is the attack power (attackResult) and second is defence power (defenceResult)
     */
    private Pair<Integer, Integer> getAttackResults(Attack action, GameState gs) {
        Unit attacker = (Unit) gs.getActor(action.getUnitId());
        Unit target = (Unit) gs.getActor(action.getTargetId());
        Vector2d targetPos = target.getPosition();
        Tribe targetTribe = gs.getTribe(target.getTribeId());

        double attackForce = attacker.ATK*((double) attacker.getCurrentHP()/ attacker.getMaxHP());
        double defenceForce =target.DEF*((double)target.getCurrentHP()/target.getMaxHP());
        double accelerator = TribesConfig.ATTACK_MODIFIER;

        // Defence bonuses:
        //  - DefenceForce x TribesConfig.DEFENCE_IN_WALLS if defender within city walls.
        //  - DefenceForce x TribesConfig.DEFENCE_BONUS if defender:
        //     * in city tile, with no walls, and unit has the Fortify ability.
        //     * in water tile and Aquatism is researched.
        //     * in forest tile if Archery is researched.
        //     * in mountain tile if Meditation is researched.

        Types.TERRAIN targetTerrain = gs.getBoard().getTerrainAt(targetPos.x, targetPos.y);
        if(targetTerrain == CITY)
        {
            int cityID = gs.getBoard().getCityIdAt(targetPos.x, targetPos.y);
            if (targetTribe.controlsCity(cityID)){
                City c = (City) gs.getActor(cityID);
                if (c.hasWalls())
                    defenceForce *= TribesConfig.DEFENCE_IN_WALLS;
                else if (target.getType().canFortify())
                    defenceForce *= TribesConfig.DEFENCE_BONUS;
            }
        }else if(targetTerrain == MOUNTAIN && targetTribe.getTechTree().isResearched(MEDITATION) ||
                (targetTerrain.isWater() && targetTribe.getTechTree().isResearched(AQUATISM)) ||
                (targetTerrain == FOREST && targetTribe.getTechTree().isResearched(ARCHERY))) {
            defenceForce *= TribesConfig.DEFENCE_BONUS;
        }

        double totalDamage =attackForce+defenceForce;

        int attackResult = (int) Math.round((attackForce/totalDamage)* attacker.ATK*accelerator);
        int defenceResult = (int) Math.round((defenceForce / totalDamage) * target.DEF *accelerator);

        return new Pair<>(attackResult, defenceResult);
    }

    public boolean isRetaliation(Attack action, GameState gs) {
        if(action.isFeasible(gs)) {
            Unit attacker = (Unit) gs.getActor(action.getUnitId());
            Unit target = (Unit) gs.getActor(action.getTargetId());
            int attackResult = getAttackResults(action, gs).getFirst();
            if (target.getCurrentHP() > attackResult) {
                double distance = Vector2d.chebychevDistance(attacker.getPosition(), target.getPosition());
                return distance <= target.RANGE;
            }
        }
        return false;
    }
}
