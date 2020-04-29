package players;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.*;
import core.actions.tribeactions.BuildRoad;
import core.actions.tribeactions.ResearchTech;
import core.actions.unitactions.*;
import core.actors.Building;
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
        int nActions = allActions.size();
        //Initially pick a random action so that at least that can be returned
        Action bestAction = allActions.get(gs.getRandomGenerator().nextInt(nActions));
        int bestActionScore = 0;
        for (Action a:allActions
             ) {
            int actionScore = evalAction(gs,a);
            if(actionScore > bestActionScore){
                bestAction = a;
                bestActionScore = actionScore;
            }
        }
//        System.out.println("[Tribe: " + playerID + "] Tick " +  gs.getTick() + ", num actions: " + nActions + ". Executing " + bestAction);


        return bestAction;
    }




    int evalAction(GameState gs, Action a){

        int score = 0;
        Tribe thisTribe = gs.getActiveTribe();

        if(a instanceof Attack) {
            score = evalAttack(a,gs);
        }

        if(a instanceof Move){
            score = evalMove(a,gs, thisTribe);
        }
        if(a instanceof Capture){
            score =5;
        }
        if(a instanceof Convert){
            score = evalConvert(a,gs,thisTribe);
        }
        if(a instanceof ResourceGathering){
            if(thisTribe.getStars() > 5){
                score =3;
            }else {
                score =1;
            }
        }
        if(a instanceof Examine){
            score = 5;
        }
        if(a instanceof HealOthers){
            score = evalHeal(a,gs,thisTribe);
        }

        if(a instanceof MakeVeteran){
            score = 5;
        }

        if(a instanceof Recover){
            score = evalRecover(a,gs,thisTribe);
        }
        if(a instanceof Upgrade){
            score = evalUpgrade(a,gs,thisTribe);
        }

        if(a instanceof Build){
            score= evalBuild(a,gs,thisTribe);
        }

        if(a instanceof BurnForest){
            score = evalBurn(a,gs,thisTribe);
        }

        if(a instanceof ClearForest){
            //TODO

        }
        if(a instanceof Destroy){
            //TODO

        }
        if(a instanceof GrowForest){
            //TODO

        }
        if(a instanceof LevelUp){
            //TODO

        }

        if(a instanceof Spawn){
            //TODO

        }

        if(a instanceof BuildRoad){
            //TODO

        }

        if(a instanceof ResearchTech){
            //TODO

        }

        return score;


    }

    private int evalBurn(Action a, GameState gs, Tribe thisTribe) {
        int score = 0;
        if(thisTribe.getStars() > 5) {
            score = 1;
            City c = gs.getBoard().getCityInBorders(((BurnForest) a).getTargetPos().x, ((BurnForest) a).getTargetPos().y);
            if (c != null) {
                LinkedList<Building> buildings = c.getBuildings();
                int noOfFarms = 0;
                for (Building b : buildings
                ) {

                    if (b.type == Types.BUILDING.FARM) {
                        noOfFarms += 1;
                    }

                }
                if (thisTribe.getMaxProduction(gs) > 5 && noOfFarms < 2) {
                    score = 3;
                }
            }
        }
        return score;
    }

    private int evalBuild(Action a, GameState gs, Tribe thisTribe) {
        //TODO
        return 0;
    }

    private int evalUpgrade(Action a, GameState gs, Tribe thisTribe) {
        //TODO
        return 0;
    }


    private int evalRecover(Action a, GameState gs, Tribe thisTribe) {
        //TODO
        return 0;
    }


    private int evalHeal(Action a, GameState gs, Tribe thisTribe) {
        //TODO
        return 0;
    }

    private int evalConvert(Action a, GameState gs, Tribe thisTribe) {
        Unit defender = (Unit) gs.getActor(((Convert) a).getTargetId());
        switch (defender.getType()){
            case WARRIOR:
            case RIDER:
            case ARCHER:
            case DEFENDER:
                return 2;
            case SUPERUNIT:
                return 6; //heavily weight superunit capture
            case SWORDMAN:
            case BATTLESHIP:
                return 4;
            case KNIGHT:
                return 5;
            case MIND_BENDER:
            case BOAT:
                return 1;
            case SHIP:
                return 3;
        }
        return 0;
    }


    public int evalMove(Action a, GameState gs, Tribe thisTribe){
        Vector2d dest = ((Move) a).getDestination();
        Unit thisUnit = (Unit) gs.getActor(((Move) a).getUnitId());
        Vector2d currentPos = thisUnit.getPosition();
        Board b = gs.getBoard();
        int score = 0;
        boolean[][] obsGrid = thisTribe.getObsGrid();
        for (int x =0; x<obsGrid.length; x++){
            for (int y =0; y<obsGrid.length; y++) {
                if (obsGrid[x][y]) {
                    Unit enemy = b.getUnitAt(x, y);
                    if (enemy != null) {
                        if (enemy.getTribeId() != thisTribe.getTribeId()) { // We are in the range of an enemy
                            if (enemy.DEF < thisUnit.ATK && thisUnit.getCurrentHP()>=enemy.getCurrentHP()) { //Incentive to attack weaker enemy
                                if (Vector2d.chebychevDistance(dest, enemy.getPosition()) < Vector2d.chebychevDistance(currentPos, enemy.getPosition())) {
                                    score += 3;
                                }
                            }else{ // Incentive to move away from enemy
                                if (Vector2d.chebychevDistance(dest, enemy.getPosition()) > Vector2d.chebychevDistance(currentPos, enemy.getPosition())) {
                                    score += 3;
                                }
                            }
                        }
                    }
                }
            }
        }
        for(int i = 0; i<thisUnit.RANGE; i++) {
            try {
                if (!obsGrid[dest.x+thisUnit.RANGE][dest.y+thisUnit.RANGE]||!obsGrid[dest.x-thisUnit.RANGE][dest.y-thisUnit.RANGE] ) {
                    score = 3; //Incentive to explore;
                    break;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                continue;
            }

        }
        return score;
    }

    public int evalAttack(Action a, GameState gs){
        int score = 0;
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
        return score;
    }

}
