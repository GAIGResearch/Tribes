import core.Types;
import core.game.Game;
import org.json.JSONArray;
import org.json.JSONObject;
import players.*;
import players.emcts.EMCTSAgent;
import players.emcts.EMCTSParams;
import players.mc.MCParams;
import players.mc.MonteCarloAgent;
import players.mcts.MCTSParams;
import players.mcts.MCTSPlayer;
import players.oep.OEPAgent;
import players.oep.OEPParams;
import players.osla.OSLAParams;
import players.osla.OneStepLookAheadAgent;
import players.rhea.RHEAAgent;
import players.rhea.RHEAParams;
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

                JSONArray playersArray = (JSONArray) config.get("Players");
                JSONArray tribesArray = (JSONArray) config.get("Tribes");
                if (playersArray.length() != tribesArray.length())
                    throw new Exception("Number of players must be equal to number of tribes");

                int nPlayers = playersArray.length();
                Tournament.PlayerType[] playerTypes = new Tournament.PlayerType[nPlayers];
                Types.TRIBE[] tribes = new Types.TRIBE[nPlayers];

                for (int i = 0; i < nPlayers; ++i) {
                    playerTypes[i] = Run.parsePlayerTypeStr(playersArray.getString(i));
                    tribes[i] = Run.parseTribeStr(tribesArray.getString(i));
                }
                Types.GAME_MODE gameMode = config.getString("Game Mode").equalsIgnoreCase("Capitals") ?
                        CAPITALS : SCORE;

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

    private static void play(Types.TRIBE[] tribes, long levelSeed, Tournament.PlayerType[] playerTypes, Types.GAME_MODE gameMode)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        Game game = _prepareGame(tribes, levelSeed, playerTypes, gameMode, ac);
        Run.runGame(game, ki, ac);
    }

    private static void play(String levelFile, Tournament.PlayerType[] playerTypes, Types.GAME_MODE gameMode)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        Game game = _prepareGame(levelFile, playerTypes, gameMode, ac);
        Run.runGame(game, ki, ac);
    }


    private static void load(Tournament.PlayerType[] playerTypes, String saveGameFile)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        long agentSeed = AGENT_SEED == -1 ? System.currentTimeMillis() + new Random().nextInt() : AGENT_SEED;

        Game game = _loadGame(playerTypes, saveGameFile, agentSeed);
        Run.runGame(game, ki, ac);
    }


    private static Game _prepareGame(String levelFile, Tournament.PlayerType[] playerTypes, Types.GAME_MODE gameMode, ActionController ac)
    {
        long gameSeed = GAME_SEED == -1 ? System.currentTimeMillis() : GAME_SEED;
        if(RUN_VERBOSE) System.out.println("Game seed: " + gameSeed);

        ArrayList<Agent> players = getPlayers(playerTypes, ac);

        Game game = new Game();
        game.init(players, levelFile, gameSeed, gameMode);
        return game;
    }

    private static Game _prepareGame(Types.TRIBE[] tribes, long levelSeed, Tournament.PlayerType[] playerTypes, Types.GAME_MODE gameMode, ActionController ac)
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

    private static ArrayList<Agent> getPlayers(Tournament.PlayerType[] playerTypes, ActionController ac)
    {
        ArrayList<Agent> players = new ArrayList<>();
        long agentSeed = AGENT_SEED == -1 ? System.currentTimeMillis() + new Random().nextInt() : AGENT_SEED;

        if(RUN_VERBOSE)  System.out.println("Agents random seed: " + agentSeed);

        ArrayList<Integer> allIds = new ArrayList<>();
        for(int i = 0; i < playerTypes.length; ++i)
            allIds.add(i);

        for(int i = 0; i < playerTypes.length; ++i)
        {
            Agent ag = _getAgent(playerTypes[i], agentSeed, ac);
            assert ag != null;
            ag.setPlayerIDs(i, allIds);
            players.add(ag);
        }
        return players;
    }

    private static Game _loadGame(Tournament.PlayerType[] playerTypes, String saveGameFile, long agentSeed)
    {
        ArrayList<Agent> players = new ArrayList<>();
        ArrayList<Integer> allIds = new ArrayList<>();
        for(int i = 0; i < playerTypes.length; ++i)
            allIds.add(i);

        for(int i = 0; i < playerTypes.length; ++i)
        {
            Agent ag = _getAgent(playerTypes[i], agentSeed, null);
            assert ag != null;
            ag.setPlayerIDs(i, allIds);
            players.add(ag);
        }

        Game game = new Game();
        game.init(players, saveGameFile);
        return game;
    }


    private static Agent _getAgent(Tournament.PlayerType playerType, long agentSeed, ActionController ac)
    {
        switch (playerType)
        {
            case DONOTHING: return new DoNothingAgent(agentSeed);
            case HUMAN: return new HumanAgent(ac);
            case RANDOM: return new RandomAgent(agentSeed);
            case OSLA:
                OSLAParams oslaParams = new OSLAParams();
                oslaParams.stop_type = oslaParams.STOP_FMCALLS; //Upper bound
                oslaParams.heuristic_method = oslaParams.DIFF_HEURISTIC;
                return new OneStepLookAheadAgent(agentSeed, oslaParams);
            case MC:
                MCParams mcparams = new MCParams();
                mcparams.stop_type = mcparams.STOP_FMCALLS;
//                mcparams.stop_type = mcparams.STOP_ITERATIONS;
                mcparams.heuristic_method = mcparams.DIFF_HEURISTIC;
                mcparams.PRIORITIZE_ROOT = true;
                mcparams.ROLLOUT_LENGTH = 10;
                mcparams.FORCE_TURN_END = 5;//mcparams.ROLLOUT_LENGTH+2;
                return new MonteCarloAgent(agentSeed, mcparams);
            case SIMPLE: return new SimpleAgent(agentSeed);
            case MCTS:
                MCTSParams mctsParams = new MCTSParams();
                mctsParams.stop_type = mctsParams.STOP_FMCALLS;
                mctsParams.PRIORITIZE_ROOT = true;
                mctsParams.heuristic_method = mctsParams.DIFF_HEURISTIC;
                mctsParams.ROLLOUT_LENGTH = 20;
//                mctsParams.ROLOUTS_ENABLED = false;
                mctsParams.FORCE_TURN_END = 25;
                return new MCTSPlayer(agentSeed, mctsParams);
            case RHEA:
                RHEAParams rheaParams = new RHEAParams();
                rheaParams.stop_type = rheaParams.STOP_FMCALLS;
                rheaParams.heuristic_method = rheaParams.DIFF_HEURISTIC;
                rheaParams.INDIVIDUAL_LENGTH = 20;
                rheaParams.FORCE_TURN_END = rheaParams.INDIVIDUAL_LENGTH + 1;
                rheaParams.POP_SIZE = 1;
                return new RHEAAgent(agentSeed, rheaParams);
            case OEP:
                OEPParams oepParams = new OEPParams();
                oepParams.stop_type = oepParams.STOP_FMCALLS;
                oepParams.heuristic_method = oepParams.DIFF_HEURISTIC;
                return new OEPAgent(agentSeed, oepParams);
            case EMCTS:
                EMCTSParams emctsParams = new EMCTSParams();
                emctsParams.stop_type = emctsParams.STOP_FMCALLS;
                emctsParams.heuristic_method = emctsParams.DIFF_HEURISTIC;
                return new EMCTSAgent(agentSeed,emctsParams);
        }
        return null;
    }
}