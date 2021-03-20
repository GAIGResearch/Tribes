package utils.stats;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actions.cityactions.Build;
import core.actions.cityactions.Spawn;
import core.actions.tribeactions.ResearchTech;
import core.actors.Building;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;

import java.util.*;

import static core.Types.TECHNOLOGY.*;

public class GameplayStats {

    //Info about player and turns
    private int maxTurn;
    private int playerID;

    //Data
    private TreeMap<Types.ACTION, HashMap<Integer, Integer>> actionsBreakdown;  //ACTION -> {[turn, count], [turn, count]...}
    private TreeMap<Types.ACTION, Integer> actionsCount;                        //ACTION -> turn count.
    private TreeMap<String, HashMap<Integer, Integer>> statsCount;              //String -> {[turn, count], [turn, count]...}
    private TreeMap<String, Integer> actionSubtypeCount;      //String (actionsubtype) -> count
    private TreeMap<String, HashMap<Integer, Integer>> actionSubtypeCountTurn;      //String (actionsubtype) -> {[turn, count], [turn, count]...}


    //Array helpers for data collection
    private final String[] stats = new String[]{"Production", "Num cities",
            "Num units", "Num units WARRIOR", "Num units RIDER", "Num units DEFENDER", "Num units SWORDMAN", "Num units ARCHER",
            "Num units CATAPULT", "Num units KNIGHT", "Num units MIND_BENDER", "Num units BOAT", "Num units SHIP", "Num units BATTLESHIP", "Num units SUPERUNIT",
            "Num techs", "Num techs farm", "Num techs mountain", "Num techs naval", "Num techs range", "Num techs roads",
            "Num monuments", "Num temples", "Num buildings", "Num buildings PORT", "Num buildings MINE", "Num buildings FORGE", "Num buildings FARM", "Num buildings WINDMILL",
            "Num buildings CUSTOMS_HOUSE", "Num buildings LUMBER_HUT", "Num buildings SAWMILL",
    };
    private final List<Types.TECHNOLOGY> farmTechs = Arrays.asList(ORGANIZATION, FARMING, SHIELDS, CONSTRUCTION);
    private final List<Types.TECHNOLOGY> mountainTechs = Arrays.asList(CLIMBING, MINING, MEDITATION, SMITHERY, PHILOSOPHY);
    private final List<Types.TECHNOLOGY> navalTechs = Arrays.asList(FISHING, WHALING, SAILING, NAVIGATION, AQUATISM);
    private final List<Types.TECHNOLOGY> rangeTechs = Arrays.asList(HUNTING, ARCHERY, FORESTRY, SPIRITUALISM, MATHEMATICS);
    private final List<Types.TECHNOLOGY> roadTechs = Arrays.asList(RIDING, ROADS, FREE_SPIRIT, CHIVALRY, TRADE);

    private final String[] actionSubtypes = new String[]{
            "Spawn WARRIOR", "Spawn RIDER", "Spawn DEFENDER", "Spawn SWORDMAN", "Spawn ARCHER",
            "Spawn CATAPULT", "Spawn KNIGHT", "Spawn MIND_BENDER",
            "Build monuments", "Build temples", "Build PORT", "Build MINE", "Build FORGE", "Build FARM", "Build WINDMILL",
            "Build CUSTOMS_HOUSE", "Build LUMBER_HUT", "Build SAWMILL",
            "Research farm", "Research mountain", "Research naval", "Research range", "Research roads",
    };
    
    
    public GameplayStats(int playerID)
    {
        maxTurn = -1;
        this.playerID = playerID;
        init();
    }

    private void init()
    {
        actionsBreakdown = new TreeMap<>();
        actionsCount = new TreeMap<>();
        for(Types.ACTION actionType : Types.ACTION.values())
        {
            actionsBreakdown.put(actionType, new HashMap<>());
            actionsCount.put(actionType, 0);
        }

        statsCount = new TreeMap<>();
        for(String st : stats)
        {
            statsCount.put(st, new HashMap<>());
        }

        actionSubtypeCountTurn = new TreeMap<>();
        actionSubtypeCount = new TreeMap<>();
        for(String st: actionSubtypes)
        {
            actionSubtypeCountTurn.put(st, new HashMap<>());
            actionSubtypeCount.put(st, 0);
        }

    }

    public void logAction(Action act, int turn)
    {
        Types.ACTION actionType = act.getActionType();

        //Update total action count
        int totalActionCount = actionsCount.get(actionType);
        actionsCount.put(actionType, totalActionCount+1);

        int actionCountTurn = 0;
        HashMap<Integer, Integer> turnCount = actionsBreakdown.get(actionType);
        if(turnCount.containsKey(turn))
            actionCountTurn = turnCount.get(turn);

        //Update action count for this turn.
        turnCount.put(turn, actionCountTurn+1);

        //Special actions for which we want to log subtypes:
        if(actionType == Types.ACTION.RESEARCH_TECH || actionType == Types.ACTION.BUILD || actionType == Types.ACTION.SPAWN)
            logSubAction(act, turn);

        //Update last turn;
        if(turn > maxTurn) maxTurn = turn;
    }

    private void logSubAction(Action act, int turn)
    {
        Types.ACTION actionType = act.getActionType();
        String key = "";
        if(actionType == Types.ACTION.RESEARCH_TECH)
        {
            ResearchTech rt = (ResearchTech)act;
            key = "Research";
            if(farmTechs.contains(rt.getTech())) key = "Research farm";
            if(mountainTechs.contains(rt.getTech())) key = "Research mountain";
            if(navalTechs.contains(rt.getTech())) key = "Research naval";
            if(rangeTechs.contains(rt.getTech())) key = "Research range";
            if(roadTechs.contains(rt.getTech())) key = "Research roads";

        }else if(actionType == Types.ACTION.SPAWN)
        {
            Spawn sp = (Spawn)act;
            key = "Spawn " + sp.getUnitType();

        }else if(actionType == Types.ACTION.BUILD)
        {
            Build build = (Build)act;
            if(build.getBuildingType().isTemple()) key = "Build temples";
            else if(build.getBuildingType().isMonument()) key = "Build monuments";
            else key = "Build " + build.getBuildingType();
        }

        //Global count:
        try {
            actionSubtypeCount.put(key, 1 + actionSubtypeCount.get(key));
        }catch (Exception e)
        {
            System.out.println(key);
            e.printStackTrace();
            actionSubtypeCount.put(key, 1 + actionSubtypeCount.get(key));
        }

        //Turn count:
        int num = 0;
        HashMap<Integer, Integer> counts = actionSubtypeCountTurn.get(key);
        if(counts.containsKey(turn))
            num = counts.get(turn);
        counts.put(turn, num+1);
    }


    public void logGameState(GameState gs)
    {
        int turn = gs.getTick();

        //Production is easy.
        statsCount.get("Production").put(turn, gs.getTribeProduction(playerID));

        //Cities is easy too.
        statsCount.get("Num cities").put(turn, gs.getCities(playerID).size());

        //Units
        HashMap<Types.UNIT, Integer> unitCount = new HashMap<>();
        for(Unit u : gs.getUnits(playerID))
        {
            Types.UNIT unitType = u.getType();
            int numU = 0;
            if(unitCount.containsKey(unitType))
                numU = unitCount.get(unitType);
            unitCount.put(unitType, numU+1);
        }
        for(Types.UNIT unitType : Types.UNIT.values())
        {
            String key = "Num units " + unitType;
            int num = 0;
            if(unitCount.containsKey(unitType))
                num = unitCount.get(unitType);
            statsCount.get(key).put(turn, num);
        }
        statsCount.get("Num units").put(turn, gs.getUnits(playerID).size());

        //Techs
        TechnologyTree tree = gs.getTribeTechTree(playerID);
        statsCount.get("Num techs").put(turn, tree.getNumResearched());
        boolean[] allResearched = tree.getResearched();
        int farmB=0, mountainB=0, navalB=0, rangeB=0, roadsB=0;
        for(Types.TECHNOLOGY t : Types.TECHNOLOGY.values())
        {
            if(allResearched[t.ordinal()])
            {
                if(farmTechs.contains(t)) farmB++;
                if(mountainTechs.contains(t)) mountainB++;
                if(navalTechs.contains(t)) navalB++;
                if(rangeTechs.contains(t)) rangeB++;
                if(roadTechs.contains(t)) roadsB++;
            }
        }
        statsCount.get("Num techs farm").put(turn, farmB);
        statsCount.get("Num techs mountain").put(turn, mountainB);
        statsCount.get("Num techs naval").put(turn, navalB);
        statsCount.get("Num techs range").put(turn, rangeB);
        statsCount.get("Num techs roads").put(turn, roadsB);

        //Buildings
        int totalNBuildings = 0, totalTemples = 0, totalMonuments = 0;
        HashMap<Types.BUILDING, Integer> buildingCount = new HashMap<>();
        for(City c : gs.getCities(playerID))
        {
            for(Building b : c.getBuildings())
            {
                Types.BUILDING buildingType = b.type;
                int numB = 0;
                if(buildingCount.containsKey(buildingType))
                    numB = buildingCount.get(buildingType);
                buildingCount.put(buildingType, numB+1);

                if(buildingType.isTemple()) totalTemples++;
                else if(buildingType.isMonument()) totalMonuments++;
                else totalNBuildings++;
            }
        }
        statsCount.get("Num buildings").put(turn, totalNBuildings);
        statsCount.get("Num temples").put(turn, totalTemples);
        statsCount.get("Num monuments").put(turn, totalMonuments);

        for(Types.BUILDING buildingType : Types.BUILDING.values())
        {
            if(!buildingType.isMonument() && !buildingType.isTemple()) {
                String key = "Num buildings " + buildingType;
                int num = 0;
                if(buildingCount.containsKey(buildingType))
                    num = buildingCount.get(buildingType);
                statsCount.get(key).put(turn, num);
            }
        }
    }


    public void print()
    {
        //ACTION COUNTS
        System.out.println("Total Action count: ");
        for(Types.ACTION actionType : actionsCount.keySet())
        {
            System.out.print(actionType + ":" + actionsCount.get(actionType) + ";");
            HashMap<Integer, Integer> turnCount = actionsBreakdown.get(actionType);
            printTurns(turnCount);
            System.out.println();
        }

        //GAME STATE STATS
        System.out.println("Game State stats: ");
        for(String key : statsCount.keySet())
        {
            System.out.print(key + ":");
            printTurns(statsCount.get(key));
            System.out.println();
        }

        //SUBACTIONS
        System.out.println("Subactions: ");
        for(String key : actionSubtypes)
        {
            System.out.print(key + ":" + actionSubtypeCount.get(key) + ";");
            HashMap<Integer, Integer> turnCount = actionSubtypeCountTurn.get(key);
            printTurns(turnCount);
            System.out.println();
        }

    }

    private void printTurns(HashMap<Integer, Integer> values)
    {
        for(int turn = 0;  turn< maxTurn; ++turn)
        {
            int valueInTurn = 0;
            if(values.containsKey(turn))
            {
                valueInTurn = values.get(turn);
            }
            System.out.print(valueInTurn + " ");
        }
    }


}
