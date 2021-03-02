package players.portfolio;

import core.Types;
import core.actions.Action;
import core.actors.Actor;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import players.portfolio.scripts.AttackClosestScr;
import players.portfolio.scripts.AttackWeakestScr;
import players.portfolio.scripts.Script;

import java.util.ArrayList;
import java.util.TreeMap;

public class SimplePortfolio extends Portfolio
{
    TreeMap<Types.ACTION, Script[]> portfolio;

    public SimplePortfolio()
    {
        //UNIT: Attack, Capture, Convert, Disband, Examine, HealOthers, MakeVeteran, Move, Recover, Upgrade
        //CITY: Build, BurnForest,
        //TRIBE: ResearchTech, BuildRoad, EndTurn


        //Wrapper scripts:
        //UNIT: Capture, Disband, Examine, HealOthers, MakeVeteran, Recover, Upgrade
        //CITY: -
        //TRIBE: EndTurn

        //Controversial scripts:
        //UNIT: Attack, Convert, Move
        //CITY: Build, BurnForest, ClearForest, Destroy, GrowForest, LevelUp, ResourceGathering, Spawn
        //TRIBE: ResearchTech, BuildRoad


        portfolio = new TreeMap<>();

        portfolio.put(Types.ACTION.CAPTURE, new Script[]{new Script()});
        portfolio.put(Types.ACTION.DISBAND, new Script[]{new Script()});
        portfolio.put(Types.ACTION.EXAMINE, new Script[]{new Script()});
        portfolio.put(Types.ACTION.HEAL_OTHERS, new Script[]{new Script()});
        portfolio.put(Types.ACTION.MAKE_VETERAN, new Script[]{new Script()});
        portfolio.put(Types.ACTION.RECOVER, new Script[]{new Script()});
        portfolio.put(Types.ACTION.UPGRADE_BOAT, new Script[]{new Script()});
        portfolio.put(Types.ACTION.UPGRADE_SHIP, new Script[]{new Script()});
        portfolio.put(Types.ACTION.END_TURN, new Script[]{new Script()});

        portfolio.put(Types.ACTION.ATTACK, new Script[]{new AttackClosestScr(),
                                                        new AttackWeakestScr()});

    }

    public ArrayList<ActionAssignment> produceActionAssignments(GameState state)
    {
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
        for (Types.ACTION actType : portfolio.keySet()) {

            Script[] allScriptsActionType = portfolio.get(actType);
            for(Script s : allScriptsActionType)
            {
                ArrayList<Action> scriptActions = new ArrayList<>();
                for(Action act : actions) {

                    if(act.getActionType() == actType)
                    {
                        if(allScriptsActionType.length > 1)
                            scriptActions.add(act);
                        else
                            s.setAction(act);
                    }
                }
                s.setActions(scriptActions);
                ActionAssignment aas = new ActionAssignment(a, s);
                actionList.add(aas);
            }
        }
    }


    private void extract2(ArrayList<ActionAssignment> actionList, ArrayList<Action> actions, Actor a)
    {
        for(Action act : actions)
        {
            if (portfolio.containsKey(act.getActionType())) {
                for (Script s : portfolio.get(act.getActionType()))
                {
                    ActionAssignment aas = new ActionAssignment(a, s);
                    actionList.add(aas);

                    switch (act.getActionType()) {
                        case CAPTURE, DISBAND, EXAMINE, HEAL_OTHERS, MAKE_VETERAN, RECOVER,
                                UPGRADE_BOAT, UPGRADE_SHIP, END_TURN -> s.setAction(act);
                    }
                }
            }
        }

    }
}
