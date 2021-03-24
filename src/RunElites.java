import core.Constants;
import core.Types;
import core.game.Game;
import core.game.TribeResult;
import org.json.JSONArray;
import org.json.JSONObject;
import players.Agent;
import players.heuristics.PruneHeuristic;
import players.heuristics.PrunePortfolioHeuristic;
import players.portfolioMCTS.PortfolioMCTSPlayer;
import utils.file.IO;
import utils.mapelites.Feature;
import utils.mapelites.MapElites;
import utils.mapelites.Runner;
import utils.stats.GameplayStats;
import utils.stats.MultiStatSummary;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static core.Types.GAME_MODE.CAPITALS;
import static core.Types.GAME_MODE.SCORE;
import static core.Types.TRIBE.IMPERIUS;
import static core.Types.TRIBE.XIN_XI;

/**
 * Entry point of the framework.
 */
public class RunElites {

    private RunElites()
    {
        this.participants = new HashMap<>();
    }

    private RunElites(Types.GAME_MODE gameMode)
    {
        this.gameMode = gameMode;
        this.participants = new HashMap<>();
    }

    public static void main(String[] args) {

        RunElites runE = new RunElites();
        runE.run(args);

    }

    private void run(String[] args)
    {

        //Some defaults:
        Types.GAME_MODE gameMode = CAPITALS; //SCORE;
        int nRepetitions = 4;
        boolean shiftTribes = true;
        Constants.VERBOSE = true;
        int nWeights = 11;
        boolean master = true;
        Path mapPath = null;

        JSONObject config = null;
        if(args.length > 0)
            //First argument should be the name of the JSON file with the tournament configuration
            config = new IO().readJSON(args[0]);

        //READ ALL THE CONFIG.
        if(config == null || config.isEmpty())
        {
            this.setPlayers(new Run.PlayerType[]{Run.PlayerType.MC, Run.PlayerType.MC});
            this.setTribes(new Types.TRIBE[]{XIN_XI, IMPERIUS});
        }else {
            try {

                this.gameMode = config.getString("Game Mode").equalsIgnoreCase("Capitals") ?
                        CAPITALS : SCORE;

                nRepetitions = config.getInt("Repetitions");

                Run.MAX_LENGTH = config.getInt("Search Depth");
                Run.FORCE_TURN_END = config.getBoolean("Force End");
                Run.MCTS_ROLLOUTS = config.getBoolean("Rollouts");

                //Portfolio and pruning variables:
                Run.PRUNING = config.getBoolean("Pruning");
                Run.PROGBIAS = config.getBoolean("Progressive Bias");
                Run.K_INIT_MULT = config.getDouble("K init mult");
                Run.T_MULT = config.getDouble("T mult");
                Run.A_MULT = config.getDouble("A mult");
                Run.B = config.getDouble("B");
                nWeights = config.getInt("nWeights");
                master = config.getBoolean("Master");

                Run.POP_SIZE = config.getInt("Population Size");
                shiftTribes = config.getBoolean("Shift Tribes");
                int numElitesIterations = config.getInt("Elites iterations");
                int numRandomInits = config.getInt("Random inits");
                JSONArray eliteFeatures = (JSONArray) config.get("Elites features");

                JSONArray playersArray = (JSONArray) config.get("Players");
                JSONArray tribesArray = (JSONArray) config.get("Tribes");
                if (playersArray.length() != tribesArray.length())
                    throw new Exception("Number of players must be equal to number of tribes");

                String mapFolder = config.getString("Map folder");
                mapPath = Paths.get(mapFolder);
                try{
                    if(!Files.exists(mapPath))
                        Files.createDirectory(mapPath);
                }catch (Exception e)
                {
                    System.out.println("Couldn't create path " + mapPath.toString());
                }

                int nPlayers = playersArray.length();
                Run.PlayerType[] playerTypes = new Run.PlayerType[nPlayers];
                Types.TRIBE[] tribes = new Types.TRIBE[nPlayers];

                for (int i = 0; i < nPlayers; ++i) {
                    playerTypes[i] = Run.parsePlayerTypeStr(playersArray.getString(i));
                    tribes[i] = Run.parseTribeStr(tribesArray.getString(i));
                }

                this.setPlayers(playerTypes);
                this.setTribes(tribes);
                this.setIterations(numElitesIterations);
                this.setRandomInits(numRandomInits);
                this.setFeatures(eliteFeatures);
                this.setNumRepetitions(nRepetitions);
                this.setShiftTribes(shiftTribes);

                Constants.VERBOSE = config.getBoolean("Verbose");
                JSONArray seeds = (JSONArray) config.get("Level Seeds");
                this.setSeeds(seeds);

            } catch (Exception e) {
                System.out.println("Malformed JSON config file: " + e);
                e.printStackTrace();
                printRunHelp(args);
            }
        }

        //All ready, running.


        Runner runner = new Runner() {
            @Override
            public ArrayList<GameplayStats> run(double[] weights) {

                int starter = 0;
                ArrayList<GameplayStats> gameplays = new ArrayList<>();
                for (long levelSeed : seeds) {

                    if(levelSeed == -1)
                    {
                        levelSeed = System.currentTimeMillis() + new Random().nextInt();
                    }
                    //System.out.println("**** Playing level with seed " + levelSeed + " ****");

                    for (int rep = 0; rep < numRepetitions; rep++) {

                        HashMap<Types.TRIBE, Participant> assignment = new HashMap<>();
                        int next = starter;
                        Run.PlayerType[] players = new Run.PlayerType[participants.size()];

                        int playersIn = 0;
                        //System.out.print("Playing with [");
                        while(playersIn < participants.size())
                        {
                            Participant p = participants.get(next);
                            //System.out.print(p.participantId + ":" + p.playerType + "(" + tribes[playersIn] + ")");
                            players[playersIn] = p.playerType;
                            assignment.put(tribes[playersIn], p);

                            playersIn++;
                            next = (next + 1) % participants.size();

                            if (playersIn < participants.size())
                                System.out.print(", ");
                        }

                        Game game = _prepareGame(tribes, levelSeed, players, gameMode);

                        int id = -1;
                        for(int i = 0; i < game.getPlayers().length; i++)
                        {
                            Agent ag = game.getPlayers()[i];
                            if(id == -1 && ag instanceof PortfolioMCTSPlayer)
                            {
                                id = i;
                                PortfolioMCTSPlayer pmp = (PortfolioMCTSPlayer)ag;
                                PrunePortfolioHeuristic ph = (PrunePortfolioHeuristic) pmp.getParams().getPruneHeuristic();
                                ph.setWeights(weights);
                            }
                        }

                        try {
                            Run.runGame(game);
                            _addGameResults(game, assignment);

                            gameplays.add(game.getGamePlayStats(id));

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

                }
                return gameplays;
            }
        };

        String runStr = args[0].substring(0, args[0].lastIndexOf('.'));
        MapElites me = new MapElites(features, nWeights, master, mapPath);
        me.runAlgorithm(numIterations, numRandomInits, runner, runStr);
    }

    public void setPlayers(Run.PlayerType[] playerTypes)
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

    private Game _prepareGame(Types.TRIBE[] tribes, long levelSeed, Run.PlayerType[] playerTypes, Types.GAME_MODE gameMode)
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

    private ArrayList<Agent> getPlayers(Run.PlayerType[] playerTypes)
    {
        ArrayList<Agent> players = new ArrayList<>();
        long agentSeed = System.currentTimeMillis();

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




    private void setShiftTribes(boolean shiftTribes) {
        this.shift = shiftTribes;
    }

    private void setNumRepetitions(int nRepetitions) {
        this.numRepetitions = nRepetitions;
    }

    private void setFeatures(JSONArray eliteFeatures)
    {
        features = new Feature[eliteFeatures.length()];
        for(int i = 0; i < features.length; i++)
        {
            String str = eliteFeatures.getString(i);
            features[i] = Feature.getFeatureByName(str);
        }
    }

    private void setIterations(int numElitesIterations) {
        this.numIterations = numElitesIterations;
    }

    private void setRandomInits(int numRandomInits)
    {
        this.numRandomInits = numRandomInits;
    }

    private Types.GAME_MODE gameMode;
    private boolean RUN_VERBOSE = true;
    private HashMap<Integer, Participant> participants;
    private MultiStatSummary[] stats;
    private Types.TRIBE[] tribes;
    private long[] seeds;
    private int numIterations;
    private int numRandomInits;
    private Feature[] features;
    private int numRepetitions;
    private boolean shift;



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
        Run.PlayerType playerType;
        int participantId;

        Participant(Run.PlayerType playerType, int participantId)
        {
            this.playerType = playerType;
            this.participantId = participantId;
        }
    }


}