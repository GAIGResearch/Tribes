import core.Constants;
import core.Types;
import core.game.Game;
import org.json.JSONArray;
import org.json.JSONObject;
import players.*;
import players.mc.MCParams;
import players.mc.MonteCarloAgent;
import players.mcts.MCTSParams;
import players.mcts.MCTSPlayer;
import players.oep.OEPAgent;
import players.oep.OEPParams;
import players.osla.OSLAParams;
import players.osla.OneStepLookAheadAgent;
import players.portfolio.SimplePortfolio;
import players.portfolioMCTS.PortfolioMCTSParams;
import players.portfolioMCTS.PortfolioMCTSPlayer;
import players.rhea.RHEAAgent;
import players.rhea.RHEAParams;
import players.portfolio.RandomPortfolio;
import utils.file.IO;

import java.util.*;

import static core.Types.GAME_MODE.*;

/**
 * Entry point of the framework.
 */
public class Play {

    private static boolean RUN_VERBOSE = true;
    private static long AGENT_SEED = -1;
    private static long GAME_SEED = -1;

    public static void main(String[] args) {

        try {
            JSONObject config = new IO().readJSON("play.json");

            if (config != null && !config.isEmpty()) {
                String runMode = config.getString("Run Mode");
                Constants.VERBOSE = config.getBoolean("Verbose");

                JSONArray playersArray = (JSONArray) config.get("Players");
                JSONArray tribesArray = (JSONArray) config.get("Tribes");
                if (playersArray.length() != tribesArray.length())
                    throw new Exception("Number of players must be equal to number of tribes");

                int nPlayers = playersArray.length();
                Run.PlayerType[] playerTypes = new Run.PlayerType[nPlayers];
                Types.TRIBE[] tribes = new Types.TRIBE[nPlayers];

                for (int i = 0; i < nPlayers; ++i) {
                    playerTypes[i] = Run.parsePlayerTypeStr(playersArray.getString(i));
                    tribes[i] = Run.parseTribeStr(tribesArray.getString(i));
                }
                Types.GAME_MODE gameMode = config.getString("Game Mode").equalsIgnoreCase("Capitals") ?
                        CAPITALS : SCORE;

                Run.MAX_LENGTH = config.getInt("Search Depth");
                Run.FORCE_TURN_END = config.getBoolean("Force End");
                Run.MCTS_ROLLOUTS = config.getBoolean("Rollouts");
                Run.POP_SIZE = config.getInt("Population Size");

                //Portfolio and pruning variables:
                Run.PRUNING = config.getBoolean("Pruning");
                Run.PROGBIAS = config.getBoolean("Progressive Bias");
                Run.K_INIT_MULT = config.getDouble("K init mult");
                Run.T_MULT = config.getDouble("T mult");
                Run.A_MULT = config.getDouble("A mult");
                Run.B = config.getDouble("B");

                JSONArray weights = null;
                if(config.has("pMCTS Weights"))
                    weights = (JSONArray) config.get("pMCTS Weights");
                Run.pMCTSweights = Run.getWeights(weights);

                AGENT_SEED = config.getLong("Agents Seed");
                GAME_SEED = config.getLong("Game Seed");
                long levelSeed = config.getLong("Level Seed");

                //1. Play one game with visuals using the Level Generator:
                if (runMode.equalsIgnoreCase("PlayLG")) {
                    play(tribes, levelSeed, playerTypes, gameMode);

                //2. Play one game with visuals from a file:
                } else if (runMode.equalsIgnoreCase("PlayFile")) {
                    String levelFile = config.getString("Level File");
                    play(levelFile, playerTypes, gameMode);

                //3. Play one game with visuals from a savegame
                } else if (runMode.equalsIgnoreCase("Replay")) {
                    String saveGameFile = config.getString("Replay File Name");
                    load(playerTypes, saveGameFile);
                } else {
                    System.out.println("ERROR: run mode '" + runMode + "' not recognized.");
                }

            } else {
                System.out.println("ERROR: Couldn't find 'play.json'");
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void play(Types.TRIBE[] tribes, long levelSeed, Run.PlayerType[] playerTypes, Types.GAME_MODE gameMode)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        Game game = _prepareGame(tribes, levelSeed, playerTypes, gameMode, ac);
        Run.runGame(game, ki, ac);
    }

    private static void play(String levelFile, Run.PlayerType[] playerTypes, Types.GAME_MODE gameMode)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        Game game = _prepareGame(levelFile, playerTypes, gameMode, ac);
        Run.runGame(game, ki, ac);
    }


    private static void load(Run.PlayerType[] playerTypes, String saveGameFile)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        long agentSeed = AGENT_SEED == -1 ? System.currentTimeMillis() + new Random().nextInt() : AGENT_SEED;

        Game game = _loadGame(playerTypes, saveGameFile, agentSeed);
        Run.runGame(game, ki, ac);
    }


    private static Game _prepareGame(String levelFile, Run.PlayerType[] playerTypes, Types.GAME_MODE gameMode, ActionController ac)
    {
        long gameSeed = GAME_SEED == -1 ? System.currentTimeMillis() : GAME_SEED;
        if(RUN_VERBOSE) System.out.println("Game seed: " + gameSeed);

        ArrayList<Agent> players = getPlayers(playerTypes, ac);

        Game game = new Game();
        game.init(players, levelFile, gameSeed, gameMode);
        return game;
    }

    private static Game _prepareGame(Types.TRIBE[] tribes, long levelSeed, Run.PlayerType[] playerTypes, Types.GAME_MODE gameMode, ActionController ac)
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

    private static ArrayList<Agent> getPlayers(Run.PlayerType[] playerTypes, ActionController ac)
    {
        ArrayList<Agent> players = new ArrayList<>();
        long agentSeed = AGENT_SEED == -1 ? System.currentTimeMillis() + new Random().nextInt() : AGENT_SEED;

        if(RUN_VERBOSE)  System.out.println("Agents random seed: " + agentSeed);

        ArrayList<Integer> allIds = new ArrayList<>();
        for(int i = 0; i < playerTypes.length; ++i)
            allIds.add(i);

        for(int i = 0; i < playerTypes.length; ++i)
        {
            Agent ag = Run.getAgent(playerTypes[i], agentSeed);
            assert ag != null;
            ag.setPlayerIDs(i, allIds);
            players.add(ag);
        }
        return players;
    }

    private static Game _loadGame(Run.PlayerType[] playerTypes, String saveGameFile, long agentSeed)
    {
        ArrayList<Agent> players = new ArrayList<>();
        ArrayList<Integer> allIds = new ArrayList<>();
        for(int i = 0; i < playerTypes.length; ++i)
            allIds.add(i);

        for(int i = 0; i < playerTypes.length; ++i)
        {
            Agent ag = Run.getAgent(playerTypes[i], agentSeed);
            assert ag != null;
            ag.setPlayerIDs(i, allIds);
            players.add(ag);
        }

        Game game = new Game();
        game.init(players, saveGameFile);
        return game;
    }

}