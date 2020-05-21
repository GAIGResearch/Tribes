package core.actions.unitactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Pair;
import utils.Vector2d;

import java.util.ArrayList;

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

        // Check if target is not null and that it can attack
        if(target == null || !attacker.canAttack() || attacker.getType() == Types.UNIT.MIND_BENDER)
            return false;

        return unitInRange(attacker, target, gs.getBoard());
    }


    @Override
    public boolean execute(GameState gs) {

        //Check if action is feasible before execution
        if(isFeasible(gs)) {
            Unit attacker = (Unit) gs.getActor(this.unitId);
            Unit target = (Unit) gs.getActor(this.targetId);

            attacker.transitionToStatus(Types.TURN_STATUS.ATTACKED);
            Tribe attackerTribe = gs.getTribe(attacker.getTribeId());
            attackerTribe.resetPacifistCount();

            Pair<Integer, Integer> results = getAttackResults(gs);
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

    @Override
    public Action copy() {
        Attack attack = new Attack(this.unitId);
        attack.setTargetId(this.targetId);
        return attack;
    }

    public String toString() { return "ATTACK by unit " + this.unitId + " to unit " + this.targetId;}

    public boolean isRetaliation(GameState gs) {
        if(isFeasible(gs)) {
            Unit attacker = (Unit) gs.getActor(this.unitId);
            Unit target = (Unit) gs.getActor(this.targetId);
            int attackResult = getAttackResults(gs).getFirst();
            if (target.getCurrentHP() > attackResult) {
                double distance = Vector2d.chebychevDistance(attacker.getPosition(), target.getPosition());
                return distance <= target.RANGE;
            }
        }
        return false;
    }

    /**
     * Calculates the damage dealt by the attacker and by the defender.
     * @param gs - current game state
     * @return Pair, where first element is the attack power (attackResult) and second is defence power (defenceResult)
     */
    private Pair<Integer, Integer> getAttackResults(GameState gs) {
        Unit attacker = (Unit) gs.getActor(this.unitId);
        Unit target = (Unit) gs.getActor(this.targetId);

        double attackForce = attacker.ATK*((double) attacker.getCurrentHP()/ attacker.getMaxHP());
        double defenceForce =target.DEF*((double)target.getCurrentHP()/target.getMaxHP());
        double accelerator = TribesConfig.ATTACK_MODIFIER;

        //If target unit in city border increase defence force by 300% if city has walls or 50% if city does not have walls

        int cityID = gs.getBoard().getCityIdAt(target.getPosition().x, target.getPosition().y);
        boolean isCityCenter = gs.getBoard().getTerrainAt(target.getPosition().x, target.getPosition().y) == Types.TERRAIN.CITY;
        if (isCityCenter && cityID != -1){
            ArrayList<Integer> citesID = gs.getTribe(target.getTribeId()).getCitiesID();
            if (citesID.contains(cityID)){
                City c = gs.getBoard().getCityInBorders(target.getPosition().x, target.getPosition().y);
                defenceForce *= c.hasWalls() ? TribesConfig.DEFENCE_IN_WALLS : TribesConfig.DEFENCE;
            }
        }
        double totalDamage =attackForce+defenceForce;

        int attackResult = (int) Math.round((attackForce/totalDamage)* attacker.ATK*accelerator);
        int defenceResult = (int) Math.round((defenceForce / totalDamage) * target.DEF *accelerator);

        return new Pair<>(attackResult, defenceResult);
    }
}
