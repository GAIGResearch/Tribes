package players;

import core.actions.Action;
import core.actions.cityactions.ResourceGathering;
import core.actions.unitactions.Attack;
import core.actions.unitactions.Move;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Archer;
import core.actors.units.Catapult;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import utils.ElapsedCpuTimer;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.LinkedList;

public class SimpleAgent extends Agent {

    private ArrayList<Vector2d> recentlyVisitedPositions;
    /**
     * Default constructor, to be called in subclasses (initializes player ID and random seed for this agent.
     *
     * @param seed - random seed for this player.
     */
    public SimpleAgent(long seed) {
        super(seed);
    }


    @Override
    public Agent copy() {
        SimpleAgent player = new SimpleAgent(seed);
        return player;
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect) {
        //Gather all available actions:
        ArrayList<Action> allActions = gs.getAllAvailableActions();
        //Initially pick a random action so that at least that can be returned
        Action bestAction = null;
        float bestActionScore = 0;
        for (Action a:allActions
             ) {
            float actionScore = evalAction(gs,a);
            if(actionScore > bestActionScore){
                bestAction = a;
                bestActionScore = actionScore;
            }

        }

        return bestAction;
    }




    float evalAction(GameState gs, Action a){

        float score = 0;
        Tribe thisTribe = gs.getActiveTribe();
        Board b = gs.getBoard();

        if(a instanceof Attack) {
            Unit attacker = (Unit) gs.getActor(((Attack) a).getUnitId());
            Unit defender = (Unit) gs.getActor(((Attack) a).getTargetId());
            if (!(attacker instanceof Archer) || !(attacker instanceof Catapult)) {
                if (attacker.getCurrentHP() >= defender.getCurrentHP()) {
                    if (attacker.ATK > defender.DEF) {
                        score += 2;
                    } else {
                        score += 1;
                    }
                } else if (attacker.getCurrentHP() < defender.getCurrentHP()) {
                    if (attacker.ATK > defender.DEF) {
                        score += 1;
                    }//else don't add anything to the score.
                }
            }
        }

        if(a instanceof Move){
            Vector2d dest = ((Move) a).getDestination();
            Unit thisUnit = (Unit) gs.getActor(((Move) a).getUnitId());
            Vector2d currentPos = thisUnit.getPosition();
            boolean[][] obsGrid = thisTribe.getObsGrid();
            for (int x =0; x<obsGrid.length; x++){
                for (int y =0; y<obsGrid.length; y++) {
                    if (obsGrid[x][y]) {
                        Unit enemy = b.getUnitAt(x, y);
                        if (enemy != null) {
                            if (enemy.getTribeId() != thisTribe.getTribeId()) { // We are in the range of an enemy
                                if (enemy.DEF < thisUnit.ATK && thisUnit.getCurrentHP()>=enemy.getCurrentHP()) { //Incentive to attack weaker enemy
                                    if (Vector2d.chebychevDistance(dest, enemy.getPosition()) < Vector2d.chebychevDistance(currentPos, enemy.getPosition())) {
                                        score += 2;
                                    }
                                }else{ // Incentive to move away from enemy
                                    if (Vector2d.chebychevDistance(dest, enemy.getPosition()) > Vector2d.chebychevDistance(currentPos, enemy.getPosition())) {
                                        score += 2;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(thisTribe.getObsGrid()[dest.x][dest.y] ==false){
                score +=3; //Incentive to explore;
            }
        }
        if(a instanceof ResourceGathering){
            if(thisTribe.getStars() < 7){
                score +=3;
            }
        }
        return score;
    }

}
