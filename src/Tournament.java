import core.Constants;
import core.Types;
import core.game.Game;
import core.game.TribeResult;
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
import utils.stats.MultiStatSummary;

import java.util.*;

import static core.Types.GAME_MODE.*;
import static core.Types.TRIBE.*;

/**
 * Entry point of the framework.
 */
public class Tournament {

    private static int MAX_LENGTH;
    private static boolean FORCE_TURN_END;
    private static boolean MCTS_ROLLOUTS;
    private static int POP_SIZE;

    private static Agent _getAgent(PlayerType playerType, long agentSeed)
    {
        switch (playerType)
        {
            case DONOTHING: return new DoNothingAgent(agentSeed);
            case RANDOM: return new RandomAgent(agentSeed);
            case SIMPLE: return new SimpleAgent(agentSeed);
            case OSLA:
                OSLAParams oslaParams = new OSLAParams();
                oslaParams.stop_type = oslaParams.STOP_FMCALLS; //Upper bound
                oslaParams.heuristic_method = oslaParams.DIFF_HEURISTIC;
                return new OneStepLookAheadAgent(agentSeed, oslaParams);
            case MC:
                MCParams mcparams = new MCParams();
                mcparams.stop_type = mcparams.STOP_FMCALLS;
                mcparams.heuristic_method = mcparams.DIFF_HEURISTIC;
                mcparams.PRIORITIZE_ROOT = true;
                mcparams.ROLLOUT_LENGTH = MAX_LENGTH;
                mcparams.FORCE_TURN_END = FORCE_TURN_END ? 5 : mcparams.ROLLOUT_LENGTH + 1;
                return new MonteCarloAgent(agentSeed, mcparams);
            case MCTS:
                MCTSParams mctsParams = new MCTSParams();
                mctsParams.stop_type = mctsParams.STOP_FMCALLS;
                mctsParams.heuristic_method = mctsParams.DIFF_HEURISTIC;
                mctsParams.PRIORITIZE_ROOT = true;
                mctsParams.ROLLOUT_LENGTH = MAX_LENGTH;
                mctsParams.FORCE_TURN_END = FORCE_TURN_END ? 5 : mctsParams.ROLLOUT_LENGTH + 1;
                mctsParams.ROLOUTS_ENABLED = MCTS_ROLLOUTS;
                return new MCTSPlayer(agentSeed, mctsParams);
            case OEP:
                OEPParams oepParams = new OEPParams();
                return new OEPAgent(agentSeed, oepParams);
            case RHEA:
                RHEAParams rheaParams = new RHEAParams();
                rheaParams.stop_type = rheaParams.STOP_FMCALLS;
                rheaParams.heuristic_method = rheaParams.DIFF_HEURISTIC;
                rheaParams.INDIVIDUAL_LENGTH = MAX_LENGTH;
                rheaParams.FORCE_TURN_END = rheaParams.INDIVIDUAL_LENGTH + 1;
                rheaParams.POP_SIZE = POP_SIZE;
                return new RHEAAgent(agentSeed, rheaParams);
            case PORTFOLIO_MCTS:
                PortfolioMCTSParams portfolioMCTSParams = new PortfolioMCTSParams();
                portfolioMCTSParams.stop_type = portfolioMCTSParams.STOP_FMCALLS;
                portfolioMCTSParams.PRIORITIZE_ROOT = false;
                portfolioMCTSParams.heuristic_method = portfolioMCTSParams.DIFF_HEURISTIC;
                portfolioMCTSParams.ROLLOUT_LENGTH = 20;
                portfolioMCTSParams.setPortfolio(new SimplePortfolio(agentSeed));
                return new PortfolioMCTSPlayer(agentSeed, portfolioMCTSParams);
        }
        return null;
    }

    public static void main(String[] args) {

        //Some defaults:
        Types.GAME_MODE gameMode = CAPITALS; //SCORE;
        Tournament t = new Tournament(gameMode);
        int nRepetitions = 4;
        boolean shiftTribes = true;
        Constants.VERBOSE = true;

        JSONObject config = null;
        if(args.length > 0)
            //First argument should be the name of the JSON file with the tournament configuration
            config = new IO().readJSON(args[0]);

        if(config == null || config.isEmpty())
        {
            t.setPlayers(new PlayerType[]{PlayerType.MC, PlayerType.MC});
            t.setTribes(new Types.TRIBE[]{XIN_XI, IMPERIUS});
        }else {
            try {

                gameMode = config.getString("Game Mode").equalsIgnoreCase("Capitals") ?
                        CAPITALS : SCORE;
                t = new Tournament(gameMode);
                nRepetitions = config.getInt("Repetitions");

                MAX_LENGTH = config.getInt("Search Depth");
                FORCE_TURN_END = config.getBoolean("Force End");
                MCTS_ROLLOUTS = config.getBoolean("Rollouts");
                POP_SIZE = config.getInt("Population Size");
                shiftTribes = config.getBoolean("Shift Tribes");

                JSONArray playersArray = (JSONArray) config.get("Players");
                JSONArray tribesArray = (JSONArray) config.get("Tribes");
                if (playersArray.length() != tribesArray.length())
                    throw new Exception("Number of players must be equal to number of tribes");

                int nPlayers = playersArray.length();
                PlayerType[] playerTypes = new PlayerType[nPlayers];
                Types.TRIBE[] tribes = new Types.TRIBE[nPlayers];

                for (int i = 0; i < nPlayers; ++i) {
                    playerTypes[i] = Run.parsePlayerTypeStr(playersArray.getString(i));
                    tribes[i] = Run.parseTribeStr(tribesArray.getString(i));
                }

                t.setPlayers(playerTypes);
                t.setTribes(tribes);

                Constants.VERBOSE = config.getBoolean("Verbose");
                JSONArray seeds = (JSONArray) config.get("Level Seeds");
                t.setSeeds(seeds);

            } catch (Exception e) {
                System.out.println("Malformed JSON config file: " + e);
                e.printStackTrace();
                printRunHelp(args);
            }
        }

        //All ready, running.
        t.run(nRepetitions, shiftTribes);
    }

    private Types.GAME_MODE gameMode;
    private boolean RUN_VERBOSE = true;
    private HashMap<Integer, Participant> participants;
    private MultiStatSummary[] stats;
    private Types.TRIBE[] tribes;
    private long[] seeds;


    private Tournament(Types.GAME_MODE gameMode)
    {
        this.gameMode = gameMode;
        this.participants = new HashMap<>();
    }

    public void setPlayers(PlayerType[] playerTypes)
    {
        stats = new MultiStatSummary[playerTypes.length];
        for(int i = 0; i < playerTypes.length; ++i)
        {
            Participant p = new Participant(playerTypes[i], i);
            participants.put(i, p);
            stats[i] = initMultiStat(p);
        }
    }

    public void setTribes(Types.TRIBE[] tribes) {
        this.tribes = tribes;
    }

    private void setSeeds(JSONArray seeds) {
        this.seeds = new long[seeds.length()];
        for (int i = 0; i < this.seeds.length; ++i)
        {
            this.seeds[i] = Long.parseLong(seeds.getString(i));
        }
    }


    private void run(int repetitions, boolean shift)
    {
        int starter = 0;
        int nseed = 0;
        for (long levelSeed : seeds) {

            if(levelSeed == -1)
            {
                levelSeed = System.currentTimeMillis() + new Random().nextInt();
            }
            System.out.println("**** Playing level with seed " + levelSeed + " ****");

            for (int rep = 0; rep < repetitions; rep++) {

                HashMap<Types.TRIBE, Participant> assignment = new HashMap<>();
                int next = starter;
                PlayerType[] players = new PlayerType[participants.size()];

                int playersIn = 0;
                System.out.print("Playing with [");
                while(playersIn < participants.size())
                {
                    Participant p = participants.get(next);
                    System.out.print(p.participantId + ":" + p.playerType + "(" + tribes[playersIn] + ")");
                    players[playersIn] = p.playerType;
                    assignment.put(tribes[playersIn], p);

                    playersIn++;
                    next = (next + 1) % participants.size();

                    if (playersIn < participants.size())
                        System.out.print(", ");
                }
                System.out.println("] (" + (nseed*repetitions + rep + 1) + "/" + (seeds.length*repetitions) + ")");

                Game game = _prepareGame(tribes, levelSeed, players, gameMode);

                try {
                    Run.runGame(game);

                    _addGameResults(game, assignment);

                    //Shift arrays for position changes.
                    if (shift) {
                        starter = (starter + 1) % participants.size();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Error running a game, trying again.");
                    rep--;
                }

            }

            nseed++;
        }

        _printRunResults();

    }

    private MultiStatSummary initMultiStat(Participant p)
    {
        MultiStatSummary mss = new MultiStatSummary(p);
        mss.registerVariable("v");
        mss.registerVariable("s");
        mss.registerVariable("t");
        mss.registerVariable("c");
        mss.registerVariable("p");
        return mss;
    }

    private Game _prepareGame(Types.TRIBE[] tribes, long levelSeed, PlayerType[] playerTypes, Types.GAME_MODE gameMode)
    {
        long gameSeed = System.currentTimeMillis();

        if(RUN_VERBOSE) System.out.println("Game seed: " + gameSeed);

        ArrayList<Agent> players = getPlayers(playerTypes);

        Game game = new Game();

        long levelGenSeed = levelSeed;
        if(levelGenSeed == -1)
            levelGenSeed = System.currentTimeMillis() + new Random().nextInt();

        if(RUN_VERBOSE) System.out.println("Level seed: " + levelGenSeed);

        game.init(players, levelGenSeed, tribes, gameSeed, gameMode);

        return game;
    }

    private ArrayList<Agent> getPlayers(PlayerType[] playerTypes)
    {
        ArrayList<Agent> players = new ArrayList<>();
        long agentSeed = System.currentTimeMillis();

        if(RUN_VERBOSE)  System.out.println("Agents random seed: " + agentSeed);

        ArrayList<Integer> allIds = new ArrayList<>();
        for(int i = 0; i < playerTypes.length; ++i)
            allIds.add(i);

        for(int i = 0; i < playerTypes.length; ++i)
        {
            Agent ag = _getAgent(playerTypes[i], agentSeed);
            assert ag != null;
            ag.setPlayerIDs(i, allIds);
            players.add(ag);
        }
        return players;
    }


    private void _addGameResults(Game game, HashMap<Types.TRIBE, Participant> assignment)
    {
        TreeSet<TribeResult> ranking = game.getCurrentRanking();
        for(TribeResult tr : ranking)
        {
            Types.TRIBE tribe = game.getBoard().getTribe(tr.getId()).getType();
            int pId = assignment.get(tribe).participantId;

            int victoryCount = tr.getResult() == Types.RESULT.WIN ? 1 : 0;
            stats[pId].getVariable("v").add(victoryCount);
            stats[pId].getVariable("s").add(tr.getScore());
            stats[pId].getVariable("t").add(tr.getNumTechsResearched());
            stats[pId].getVariable("c").add(tr.getNumCities());
            stats[pId].getVariable("p").add(tr.getProduction());
        }
    }

    private void _printRunResults()
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
            for (MultiStatSummary stat : stats) {
                Participant thisParticipant = (Participant) stat.getOwner();
                int w = (int) stat.getVariable("v").sum();
                int n = stat.getVariable("v").n();
                double perc_w = 100.0 * (double)w/n;

                System.out.printf("[N:%d];", n);
                System.out.printf("[%%:%.2f];", perc_w);
                System.out.printf("[W:%d];", w);
                System.out.printf("[S:%.2f];", stat.getVariable("s").mean());
                System.out.printf("[T:%.2f];", stat.getVariable("t").mean());
                System.out.printf("[C:%.2f];", stat.getVariable("c").mean());
                System.out.printf("[P:%.2f];", stat.getVariable("p").mean());
                System.out.printf("[Player:%d:%s]", thisParticipant.participantId, thisParticipant.playerType);
                System.out.println();
            }
        }

    }



    private static void printRunHelp(String[] args)
    {
        System.out.print("Invalid Arguments ");
        for(String s : args) {
            System.out.print(s + " ");
        }
        System.out.println(". Usage: ");
        System.out.println("'java Tournament <jsonConfigFile>', where: ");
        System.out.println("\t<jsonConfigFile> is the JSON file with the tournament configuration.");
        System.out.println("Example: java -jar tournament.json");
    }

    /// ----- Players and participants -----

    public enum PlayerType
    {
        DONOTHING,
        HUMAN,
        RANDOM,
        OSLA,
        MC,
        SIMPLE,
        MCTS,
        RHEA,
        OEP,
        PORTFOLIO_MCTS
    }

    private static class Participant
    {
        PlayerType playerType;
        int participantId;

        Participant(PlayerType playerType, int participantId)
        {
            this.playerType = playerType;
            this.participantId = participantId;
        }
    }


}