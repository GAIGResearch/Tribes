import core.Constants;
import core.Types;
import core.game.Game;
import core.actors.Tribe;
import core.game.TribeResult;
import players.*;
import players.mc.MCParams;
import players.mc.MonteCarloAgent;
import players.mcts.MCTSParams;
import players.mcts.MCTSPlayer;
import players.oep.OEPAgent;
import players.oep.OEPParams;
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

    public static void main(String[] args) {

        Types.GAME_MODE gameMode = CAPITALS; //SCORE;

//        String filename = "levels/SampleLevel2p.csv";
        String filename = "levels/test.csv";

//        String filename = "levels/SampleLevel.csv";
//        String filename = "levels/MinimalLevel.csv";
//        String filename = "levels/MinimalLevel_water.csv";
//        String filename = "levels/MinimalLevel2.csv";

        String saveGameFile = "save/1589357287411/4_0/game.json";


        //THREE WAYS OF RUNNING Tribes:

        //1. Play one game with visuals:
        play(filename, new PlayerType[]{PlayerType.SIMPLE, PlayerType.OSLA, PlayerType.RANDOM, PlayerType.OSLA}, gameMode);
//        play(filename, new PlayerType[]{PlayerType.SIMPLE, PlayerType.SIMPLE, PlayerType.SIMPLE, PlayerType.SIMPLE}, gameMode);
//        play(filename, new PlayerType[]{PlayerType.HUMAN, PlayerType.SIMPLE}, gameMode);

        //2. Play N games without visuals
//        int nReps = 12;
//        run(filename, new PlayerType[]{PlayerType.SIMPLE, PlayerType.OSLA}, gameMode, nReps, true);
//        run(filename, new PlayerType[]{PlayerType.SIMPLE, PlayerType.OSLA, PlayerType.RANDOM, PlayerType.OSLA}, gameMode, nReps, true);

        //3. Play one game with visuals from a savegame
//        load(new PlayerType[]{PlayerType.SIMPLE, PlayerType.OSLA, PlayerType.RANDOM, PlayerType.OSLA}, saveGameFile);
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

        long agentSeed = System.currentTimeMillis();

        Game game = _loadGame(playerTypes, saveGameFile, agentSeed);
        Run.runGame(game, ki, ac);
    }

    private static void run(String levelFile, PlayerType[] playerTypes, Types.GAME_MODE gameMode, int repetitions, boolean shift)
    {
        MultiStatSummary[] stats = new MultiStatSummary[playerTypes.length];

        for(int i = 0; i < playerTypes.length; ++i)
        {
            stats[i] = new MultiStatSummary();
            stats[i].registerVariable("v");
            stats[i].registerVariable("s");
            stats[i].registerVariable("t");
            stats[i].registerVariable("c");
            stats[i].registerVariable("p");
        }

        Constants.VERBOSE = false;
        for(int rep = 0; rep < repetitions; rep++)
        {
            System.out.print("Playing with [");
            for(int i =0; i < playerTypes.length; ++i)
            {
                System.out.print(i + ":" + playerTypes[i]);
                if(i < playerTypes.length-1)
                    System.out.print(", ");
            }
            System.out.println("]");

            Game game = _prepareGame(levelFile, playerTypes, gameMode, null);
            Run.runGame(game);

            _addGameResults(game, stats);

            //Shift arrays for position changes.
            if(shift) {
                shift(playerTypes);
                shift(stats);
            }
        }

        _printRunResults(playerTypes, stats);
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

        long gameSeed = System.currentTimeMillis();
        long agentSeed = System.currentTimeMillis() + new Random().nextInt();
        if(Constants.VERBOSE)
            System.out.println("Game seed: " + gameSeed + ", agent random seed: " + agentSeed);

        ArrayList<Agent> players = new ArrayList<>();

        for(int i = 0; i < playerTypes.length; ++i)
        {
            Agent ag = _getAgent(playerTypes[i], agentSeed, ac);
            ag.setPlayerID(i);
            players.add(ag);
        }

        Game game = new Game();
        game.init(players, levelFile, gameSeed, gameMode);
        return game;
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

    private static Agent _getAgent(PlayerType playerType, long agentSeed, ActionController ac)
    {
        switch (playerType)
        {
            case HUMAN: return new HumanAgent(ac);
            case RANDOM: return new RandomAgent(agentSeed);
            case OSLA: return new OneStepLookAheadAgent(agentSeed);
            case MC:
                MCParams mcparams = new MCParams();
                return new MonteCarloAgent(agentSeed, mcparams);
            case SIMPLE: return new SimpleAgent(agentSeed);
            case MCTS:
                MCTSParams mctsParams = new MCTSParams();
                return new MCTSPlayer(agentSeed, mctsParams);
            case OEP:
                OEPParams oepParams = new OEPParams();
                return new OEPAgent(agentSeed, oepParams);
        }
        return null;
    }

}