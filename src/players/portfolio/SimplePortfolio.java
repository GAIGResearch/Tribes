package players.portfolio;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.Build;
import core.actors.Actor;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import players.portfolio.scripts.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;

import static core.Types.BUILDING.PORT;

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
        //portfolio.put(Types.ACTION.DESTROY, new Script[]{new BaseScript(rnd)});
        //portfolio.put(Types.ACTION.DISBAND, new Script[]{new BaseScript()});

        //Wrapper scripts:
        portfolio.put(Types.ACTION.CAPTURE, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.EXAMINE, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.HEAL_OTHERS, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.MAKE_VETERAN, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.RECOVER, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.UPGRADE_BOAT, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.UPGRADE_SHIP, new BaseScript[]{new BaseScript()});
        portfolio.put(Types.ACTION.END_TURN, new BaseScript[]{new BaseScript()});

        //Single-action scripts
        portfolio.put(Types.ACTION.BURN_FOREST, new BaseScript[]{new BurnForestScr(rnd)});
        portfolio.put(Types.ACTION.GROW_FOREST, new BaseScript[]{new GrowForestScr(rnd)});
        portfolio.put(Types.ACTION.RESOURCE_GATHERING, new BaseScript[]{new ResourceGatheringScr(rnd)});
        portfolio.put(Types.ACTION.BUILD_ROAD, new BaseScript[]{new BuildRoadScr(rnd)});

        //Multi-action scripts:
        portfolio.put(Types.ACTION.ATTACK, new BaseScript[]{ //4
                new AttackClosestScr(),
                new AttackWeakestScr(),
                new AttackMaxDamageScr(),
                new AttackStrongestScr()
        });

        portfolio.put(Types.ACTION.CLEAR_FOREST, new BaseScript[]{ //4
                new ClearForestForCustomScr(rnd),
                new ClearForestForForgeScr(rnd),
                new ClearForestForSawmillScr(rnd),
                new ClearForestForWindmillScr(rnd)
                });

        portfolio.put(Types.ACTION.CONVERT, new BaseScript[]{ //3
                new ConvertStrongestScr(),
                new ConvertHighestHPScr(),
                new ConvertHighestDefenceScr(),
        });

        portfolio.put(Types.ACTION.SPAWN, new BaseScript[]{ //6
                new SpawnStrongestScr(),
                new SpawnDefensiveScr(),
                new SpawnCheapestScr(),
                new SpawnFastestScr(),
                new SpawnMaxHPScr(),
                new SpawnRangeScr()
        });

        portfolio.put(Types.ACTION.BUILD, new BaseScript[]{ //10
                new BuildCustomHouseScr(rnd),
                new BuildSawmillScr(rnd),
                new BuildWindmillScr(rnd),
                new BuildForgeScr(rnd),
                new BuildFarmScr(rnd),
                new BuildMineScr(rnd),
                new BuildLumberHutScr(rnd),
                new BuildPortScr(rnd),
                new BuildMonumentScr(rnd),
                new BuildTempleScr(rnd),
        });


        portfolio.put(Types.ACTION.LEVEL_UP, new BaseScript[]{ //2
                new LevelUpGrowthScr(rnd),
                new LevelUpMilitaryScr(rnd)
        });



        portfolio.put(Types.ACTION.RESEARCH_TECH, new BaseScript[]{ //5
                new ResearchFarmsScr(rnd),
                new ResearchNavalScr(rnd),
                new ResearchMountainsScr(rnd),
                new ResearchRangeScr(rnd),
                new ResearchRoadsScr(rnd),
        });



        portfolio.put(Types.ACTION.MOVE, new BaseScript[]{ //9
                new MoveToCaptureScr(rnd),
                new MoveToOwnCityCentreScr(rnd),
                new MoveDefensivelyScr(rnd),
                new MoveOffensivelyScr(rnd),
                new MoveToDisembarkScr(rnd),
                new MoveToEmbarkScr(rnd),
                new MoveToAttackRangeScr(rnd),
                new MoveToConvergeScr(rnd),
                new MoveToDivergeScr(rnd)
        });
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
                extract(state, list, unitActions, u);
        }

        for(City c : state.getCities(state.getActiveTribeID()))
        {
            ArrayList<Action> cityActions = state.getCityActions(c);
            if(cityActions != null && cityActions.size() > 0)
                extract(state, list, cityActions, c);
        }

        ArrayList<Action> tribeActions = state.getTribeActions();
            extract(state, list, tribeActions, state.getActiveTribe());

        return list;
    }


    private void extract(GameState state, ArrayList<ActionAssignment> actionList, ArrayList<Action> actions, Actor a)
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
                    if(aas.process(state)) {
                        if(!actionList.contains(aas))
                            actionList.add(aas);
                    }
                }
            }
        }
    }

    public TreeMap<Types.ACTION, BaseScript[]> getPortfolio() {
        return portfolio;
    }

    @Override
    public BaseScript[] scripts(Types.ACTION actionType) {
        return portfolio.get(actionType);
    }
}
