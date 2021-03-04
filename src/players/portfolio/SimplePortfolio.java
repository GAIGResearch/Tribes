package players.portfolio;

import core.Types;
import core.actions.Action;
import core.actors.Actor;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import players.portfolio.scripts.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class SimplePortfolio extends Portfolio
{
    TreeMap<Types.ACTION, BaseScript[]> portfolio;
    private final Random rnd;

    public SimplePortfolio(long rndSeed)
    {
        this.rnd = new Random(rndSeed);
        initPortfolio();
    }

    @Override
    public void initPortfolio()
    {
        //Scripts:
        //UNIT: Attack, Convert, Move
        //CITY: Build, BurnForest, ClearForest, Destroy, GrowForest, LevelUp, ResourceGathering, Spawn
        //TRIBE: ResearchTech, BuildRoad, EndTurn

        portfolio = new TreeMap<>();
        //portfolio.put(Types.ACTION.DESTROY, new Script[]{new RandomScr(rnd)});
        //portfolio.put(Types.ACTION.DISBAND, new Script[]{new Script()});

        //Wrapper scripts:
        portfolio.put(Types.ACTION.CAPTURE, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.EXAMINE, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.HEAL_OTHERS, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.MAKE_VETERAN, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.RECOVER, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.UPGRADE_BOAT, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.UPGRADE_SHIP, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.END_TURN, new BaseScript[]{new BaseScript()});

        //Multi-action scripts:
        portfolio.put(Types.ACTION.ATTACK, new BaseScript[]{
                new AttackClosestScr(),
                new AttackWeakestScr(),
                new AttackMaxDamageScr(),
                new AttackStrongestScr()
        });
        portfolio.put(Types.ACTION.CLEAR_FOREST, new BaseScript[]{
                new ClearForestByProdScr(rnd),
                new ClearForestForCustomScr(rnd),
                new ClearForestForForgeScr(rnd),
                new ClearForestForSawmillScr(rnd),
                new ClearForestForWindmillScr(rnd)
                });

        portfolio.put(Types.ACTION.CONVERT, new BaseScript[]{
                new ConvertStrongestScr(),
                new ConvertHighestHPScr(),
                new ConvertHighestDefenceScr(),
        });

        portfolio.put(Types.ACTION.SPAWN, new BaseScript[]{
                new SpawnStrongestScr(),
                new SpawnDefensiveScr(),
                new SpawnCheapestScr(),
                new SpawnFastestScr(),
                new SpawnMaxHPScr(),
                new SpawnRangeScr()
        });



        portfolio.put(Types.ACTION.MOVE, new BaseScript[]{new RandomScr(rnd)});



        portfolio.put(Types.ACTION.BUILD_ROAD, new BaseScript[]{new RandomScr(rnd)});
        portfolio.put(Types.ACTION.RESEARCH_TECH, new BaseScript[]{new RandomScr(rnd)});
        portfolio.put(Types.ACTION.BUILD, new BaseScript[]{new RandomScr(rnd)});
        portfolio.put(Types.ACTION.BURN_FOREST, new BaseScript[]{new RandomScr(rnd)});
        portfolio.put(Types.ACTION.GROW_FOREST, new BaseScript[]{new RandomScr(rnd)});
        portfolio.put(Types.ACTION.LEVEL_UP, new BaseScript[]{new RandomScr(rnd)});
        portfolio.put(Types.ACTION.RESOURCE_GATHERING, new BaseScript[]{new RandomScr(rnd)});

    }

    @Override
    public ArrayList<ActionAssignment> produceActionAssignments(GameState state)
    {
        initPortfolio();
        ArrayList<ActionAssignment> list = new ArrayList<>();

        for(Unit u : state.getUnits(state.getActiveTribeID()))
        {
            ArrayList<Action> unitActions = state.getUnitActions(u);
            if(unitActions != null && unitActions.size() > 0)
                extract(list, unitActions, u);
        }

        for(City c : state.getCities(state.getActiveTribeID()))
        {
            ArrayList<Action> cityActions = state.getCityActions(c);
            if(cityActions != null &&cityActions.size() > 0)
                extract(list, cityActions, c);
        }

        ArrayList<Action> tribeActions = state.getTribeActions();
            extract(list, tribeActions, state.getActiveTribe());

        return list;
    }


    private void extract(ArrayList<ActionAssignment> actionList, ArrayList<Action> actions, Actor a)
    {
        //Extract assignments for all action types.
        for (Types.ACTION actType : portfolio.keySet()) {
            //Each action type will be handled by N scripts
            for(BaseScript s : portfolio.get(actType))
            {
                //Extract the available actions that correspond to the given type and add them to a list ...
                ArrayList<Action> scriptActions = new ArrayList<>();
                for(Action act : actions) {
                    if(act.getActionType() == actType)
                        scriptActions.add(act);
                }

                if(scriptActions.size() > 0) {
                    // ... so the script can have access to them
                    s.setActions(scriptActions);

                    //Assign actor to script and return the assignment.
                    ActionAssignment aas = new ActionAssignment(a, s);
                    actionList.add(aas);
                }
            }
        }
    }

}
