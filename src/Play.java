import core.Constants;
import core.Types;
import core.game.Game;
import core.actors.Tribe;
import core.game.TribeResult;
import players.*;
import players.heuristics.AlgParams;
import players.mc.MCParams;
import players.mc.MonteCarloAgent;
import players.mcts.MCTSParams;
import players.mcts.MCTSPlayer;
import players.oep.OEPAgent;
import players.oep.OEPParams;
import players.osla.OSLAParams;
import players.osla.OneStepLookAheadAgent;
import utils.MultiStatSummary;
import utils.StatSummary;

import java.util.*;

import static core.Types.GAME_MODE.*;
import static core.Types.TRIBE.*;

/**
 * Entry point of the framework.
 */
public class Play {

    public static boolean RUN_VERBOSE = true;
    public static long AGENT_SEED = -1, GAME_SEED = -1;

    enum PlayerType
    {
        HUMAN,
        RANDOM,
        OSLA,
        MC,
        SIMPLE,
        MCTS,
        OEP
    }

//    Game seed: 1590514560867
//    Agents random seed: 1590486463964
//    Level seed: 1591330872230


    private static Agent _getAgent(PlayerType playerType, long agentSeed, ActionController ac)
    {
        switch (playerType)
        {
            case HUMAN: return new HumanAgent(ac);
            case RANDOM: return new RandomAgent(agentSeed);
            case OSLA:
                OSLAParams oslaParams = new OSLAParams();
                oslaParams.stop_type = oslaParams.STOP_FMCALLS; //Upper bound
                return new OneStepLookAheadAgent(agentSeed, oslaParams);
            case MC:
                MCParams mcparams = new MCParams();
                mcparams.stop_type = mcparams.STOP_FMCALLS;
//                mcparams.stop_type = mcparams.STOP_ITERATIONS;
                mcparams.PRIORITIZE_ROOT = false;
                return new MonteCarloAgent(agentSeed, mcparams);
            case SIMPLE: return new SimpleAgent(agentSeed);
            case MCTS:
                MCTSParams mctsParams = new MCTSParams();
                mctsParams.stop_type = mctsParams.STOP_FMCALLS;
                return new MCTSPlayer(agentSeed, mctsParams);
            case OEP:
                OEPParams oepParams = new OEPParams();
                return new OEPAgent(agentSeed, oepParams);
        }
        return null;
    }


    public static void main(String[] args) {

        Types.GAME_MODE gameMode = CAPITALS; //SCORE;

//        String filename = "levels/SampleLevel2p.csv";
        String[] filenames = new String[]{"levels/SampleLevel2p.csv", "levels/MinimalLevel2.csv"};

//        String filename = "levels/SampleLevel.csv";
//        String filename = "levels/MinimalLevel.csv";
//        String filename = "levels/MinimalLevel_water.csv";
//        String filename = "levels/MinimalLevel2.csv";

        String saveGameFile = "save/1589357287411/4_0/game.json";


        //FIVE WAYS OF RUNNING Tribes:
        long seeds[] = new long[]{
                1590191438878L, 1590791907337L,
                1591330872230L, 1590557911279L,
                1589827394219L, 1590597667781L,
                1588955551452L, 1588591994323L,
                1590218550448L, 1592282275322L,
                1592432219672L, 1590938785410L,
                1589359308213L, 1591528602817L,
                1592393638354L, 1588485987095L,
                1588564020405L, 1589717827778L,
                1592435145738L, 1592040799152L,
                1588965819946L, 1589014941900L,
                1590182659177L, 1590912178111L,
                1588407146837L
        };

//        Arrays.fill(seeds, -1);
//
//        Game seed: 1590562564866
//        Agents random seed: 1590529495634
//        Level seed: 1590791907337

        //1. Play one game with visuals using the Level Generator:
//        AGENT_SEED = 1591455948310L; GAME_SEED = 1590563762657L;
//        play(new Types.TRIBE[]{XIN_XI, IMPERIUS}, -1, new PlayerType[]{PlayerType.MC, PlayerType.SIMPLE}, gameMode);
//        play(new Types.TRIBE[]{XIN_XI, IMPERIUS, BARDUR}, -1, new PlayerType[]{PlayerType.HUMAN, PlayerType.OSLA, PlayerType.OSLA}, gameMode);
//        play(new Types.TRIBE[]{XIN_XI, IMPERIUS, BARDUR, OUMAJI}, -1, new PlayerType[]{PlayerType.SIMPLE, PlayerType.SIMPLE, PlayerType.SIMPLE, PlayerType.SIMPLE}, gameMode);

        //2. Play one game with visuals from a file:
//        play(filename[0], new PlayerType[]{PlayerType.SIMPLE, PlayerType.OSLA, PlayerType.RANDOM, PlayerType.HUMAN}, gameMode);
//        play(filename[0], new PlayerType[]{PlayerType.SIMPLE, PlayerType.SIMPLE, PlayerType.SIMPLE, PlayerType.SIMPLE}, gameMode);
//        play(filename[0], new PlayerType[]{PlayerType.HUMAN, PlayerType.SIMPLE}, gameMode);


        //3. Play N games without visuals using level seeds for the generator.:
        int nReps = 10; //4;
        run(new Types.TRIBE[]{XIN_XI, IMPERIUS}, seeds, new PlayerType[]{PlayerType.SIMPLE, PlayerType.MC}, gameMode, nReps, true);
//        run(new Types.TRIBE[]{XIN_XI, IMPERIUS, BARDUR}, new long[]{0,-1}, new PlayerType[]{PlayerType.SIMPLE, PlayerType.OSLA, PlayerType.OSLA}, gameMode, nReps, true);
//        run(new Types.TRIBE[]{XIN_XI, IMPERIUS, BARDUR, OUMAJI}, new long[]{0,-1}, new PlayerType[]{PlayerType.SIMPLE, PlayerType.OSLA, PlayerType.OSLA, PlayerType.OSLA}, gameMode, nReps, true);


        //4. Play N games without visuals from file(s):
//        int nReps = 4;
//        run(filenames, new PlayerType[]{PlayerType.SIMPLE, PlayerType.OSLA}, gameMode, nReps, true);
//        run(filenames, new PlayerType[]{PlayerType.SIMPLE, PlayerType.OSLA, PlayerType.RANDOM, PlayerType.OSLA}, gameMode, nReps, true);

        //5. Play one game with visuals from a savegame
//        load(new PlayerType[]{PlayerType.SIMPLE, PlayerType.OSLA, PlayerType.RANDOM, PlayerType.OSLA}, saveGameFile);
    }

    private static void play(Types.TRIBE[] tribes, long levelSeed, PlayerType[] playerTypes, Types.GAME_MODE gameMode)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        Game game = _prepareGame(tribes, levelSeed, playerTypes, gameMode, ac);
        Run.runGame(game, ki, ac);
    }

    private static void play(String levelFile, PlayerType[] playerTypes, Types.GAME_MODE gameMode)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        Game game = _prepareGame(levelFile, playerTypes, gameMode, ac);
        Run.runGame(game, ki, ac);
    }


    private static void load(PlayerType[] playerTypes, String saveGameFile)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        long agentSeed = AGENT_SEED == -1 ? System.currentTimeMillis() + new Random().nextInt() : AGENT_SEED;

        Game game = _loadGame(playerTypes, saveGameFile, agentSeed);
        Run.runGame(game, ki, ac);
    }


    private static void run(Types.TRIBE[] tribes, long[] levelSeeds, PlayerType[] playerTypes, Types.GAME_MODE gameMode, int repetitions, boolean shift)
    {
        MultiStatSummary[] stats = initMultiStats(playerTypes);

        for (long levelSeed : levelSeeds) {

            if(levelSeed == -1)
            {
                levelSeed = System.currentTimeMillis() + new Random().nextInt();
            }
            System.out.println("**** Playing level with seed " + levelSeed + " ****");

            for (int rep = 0; rep < repetitions; rep++) {
                System.out.print("Playing with [");
                for (int i = 0; i < playerTypes.length; ++i) {
                    System.out.print(i + ":" + playerTypes[i]);
                    if (i < playerTypes.length - 1)
                        System.out.print(", ");
                }
                System.out.println("]");

                Game game = _prepareGame(tribes, levelSeed, playerTypes, gameMode, null);
                Run.runGame(game);

                _addGameResults(game, stats);

                //Shift arrays for position changes.
                if (shift) {
                    shift(playerTypes);
                    shift(stats);
                }
            }
        }

        _printRunResults(playerTypes, stats);
    }

    private static void run(String[] levelFile, PlayerType[] playerTypes, Types.GAME_MODE gameMode, int repetitions, boolean shift)
    {
        MultiStatSummary[] stats = initMultiStats(playerTypes);

        for (String s : levelFile) {
            System.out.println("**** Playing level " + s + " ****");
            for (int rep = 0; rep < repetitions; rep++) {
                System.out.print("Playing with [");
                for (int i = 0; i < playerTypes.length; ++i) {
                    System.out.print(i + ":" + playerTypes[i]);
                    if (i < playerTypes.length - 1)
                        System.out.print(", ");
                }
                System.out.println("]");

                Game game = _prepareGame(s, playerTypes, gameMode, null);
                Run.runGame(game);

                _addGameResults(game, stats);

                //Shift arrays for position changes.
                if (shift) {
                    shift(playerTypes);
                    shift(stats);
                }
            }
        }

        _printRunResults(playerTypes, stats);
    }

    private static MultiStatSummary[] initMultiStats(PlayerType[] types)
    {
        MultiStatSummary[] stats = new MultiStatSummary[types.length];
        for(int i = 0; i < types.length; ++i)
        {
            stats[i] = new MultiStatSummary();
            stats[i].registerVariable("v");
            stats[i].registerVariable("s");
            stats[i].registerVariable("t");
            stats[i].registerVariable("c");
            stats[i].registerVariable("p");
        }
        return stats;
    }

    //Shifts the array to the right (towards the highest index). Array[length - 1] -> Array[0]
    private static void shift(Object[] array)
    {
        if(array.length <= 1)
            return;

        Object ptSwap = array[array.length-1];
        for(int i = array.length-1; i > 0; i--) array[i] = array[i-1];
        array[0] = ptSwap;
    }

    private static Game _prepareGame(String levelFile, PlayerType[] playerTypes, Types.GAME_MODE gameMode, ActionController ac)
    {
        long gameSeed = GAME_SEED == -1 ? System.currentTimeMillis() : GAME_SEED;
        if(RUN_VERBOSE) System.out.println("Game seed: " + gameSeed);

        ArrayList<Agent> players = getPlayers(playerTypes, ac);

        Game game = new Game();
        game.init(players, levelFile, gameSeed, gameMode);
        return game;
    }

    private static Game _prepareGame(Types.TRIBE[] tribes, long levelSeed, PlayerType[] playerTypes, Types.GAME_MODE gameMode, ActionController ac)
    {
        long gameSeed = GAME_SEED == -1 ? System.currentTimeMillis() : GAME_SEED;

        if(RUN_VERBOSE) System.out.println("Game seed: " + gameSeed);

        ArrayList<Agent> players = getPlayers(playerTypes, ac);

        Game game = new Game();

        long levelGenSeed = levelSeed;
        if(levelGenSeed == -1)
            levelGenSeed = System.currentTimeMillis() + new Random().nextInt();

        if(RUN_VERBOSE) System.out.println("Level seed: " + levelGenSeed);

        game.init(players, levelGenSeed, tribes, gameSeed, gameMode);

        return game;
    }

    private static ArrayList<Agent> getPlayers(PlayerType[] playerTypes, ActionController ac)
    {
        ArrayList<Agent> players = new ArrayList<>();
        long agentSeed = AGENT_SEED == -1 ? System.currentTimeMillis() + new Random().nextInt() : AGENT_SEED;

        if(RUN_VERBOSE)  System.out.println("Agents random seed: " + agentSeed);

        for(int i = 0; i < playerTypes.length; ++i)
        {
            Agent ag = _getAgent(playerTypes[i], agentSeed, ac);
            ag.setPlayerID(i);
            players.add(ag);
        }
        return players;
    }

    private static Game _loadGame(PlayerType[] playerTypes, String saveGameFile, long agentSeed)
    {
        ArrayList<Agent> players = new ArrayList<>();

        for(int i = 0; i < playerTypes.length; ++i)
        {
            Agent ag = _getAgent(playerTypes[i], agentSeed, null);
            ag.setPlayerID(i);
            players.add(ag);
        }

        Game game = new Game();
        game.init(players, saveGameFile);
        return game;
    }

    private static void _addGameResults(Game game, MultiStatSummary[] stats)
    {
        TreeSet<TribeResult> ranking = game.getCurrentRanking();
        for(TribeResult tr : ranking)
        {
            int tribeId = tr.getId();
            int victoryCount = tr.getResult() == Types.RESULT.WIN ? 1 : 0;
            stats[tribeId].getVariable("v").add(victoryCount);
            stats[tribeId].getVariable("s").add(tr.getScore());
            stats[tribeId].getVariable("t").add(tr.getNumTechsResearched());
            stats[tribeId].getVariable("c").add(tr.getNumCities());
            stats[tribeId].getVariable("p").add(tr.getProduction());
        }
    }

    private static void _printRunResults(PlayerType[] players, MultiStatSummary[] stats)
    {
        if(stats != null)
        {
            Arrays.sort(stats, (o1, o2) -> {
                if(o1.getVariable("v").sum() > o2.getVariable("v").sum())
                    return -1;
                else if(o1.getVariable("v").sum() < o2.getVariable("v").sum())
                    return 1;

                if(o1.getVariable("s").mean() > o2.getVariable("s").mean())
                    return -1;
                else if(o1.getVariable("s").mean() < o2.getVariable("s").mean())
                    return 1;

                if(o1.getVariable("t").mean() > o2.getVariable("t").mean())
                    return -1;
                else if(o1.getVariable("t").mean() < o2.getVariable("t").mean())
                    return 1;

                if(o1.getVariable("c").mean() > o2.getVariable("c").mean())
                    return -1;
                else if(o1.getVariable("c").mean() < o2.getVariable("c").mean())
                    return 1;

                if(o1.getVariable("p").mean() > o2.getVariable("p").mean())
                    return -1;
                else if(o1.getVariable("p").mean() < o2.getVariable("p").mean())
                    return 1;

                return 0;
            });

            System.out.println("--------- RESULTS ---------");
            for(int i = 0; i < stats.length; ++i)
            {
                System.out.printf("[N:%d];", stats[i].getVariable("v").n());
                System.out.printf("[W:%d];", (int) stats[i].getVariable("v").sum());
                System.out.printf("[S:%.2f];", stats[i].getVariable("s").mean());
                System.out.printf("[T:%.2f];", stats[i].getVariable("t").mean());
                System.out.printf("[C:%.2f];", stats[i].getVariable("c").mean());
                System.out.printf("[P:%.2f];", stats[i].getVariable("p").mean());
                System.out.printf("[Player:%s]", players[i]);
                System.out.println();
            }
        }

    }


}