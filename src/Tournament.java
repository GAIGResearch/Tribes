import core.Types;
import core.game.Game;
import core.game.TribeResult;
import players.*;
import players.mc.MCParams;
import players.mc.MonteCarloAgent;
import players.mcts.MCTSParams;
import players.mcts.MCTSPlayer;
import players.oep.OEPAgent;
import players.oep.OEPParams;
import players.osla.OSLAParams;
import players.osla.OneStepLookAheadAgent;
import utils.MultiStatSummary;

import javax.swing.plaf.metal.MetalBorders;
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

    private static Agent _getAgent(PlayerType playerType, long agentSeed, ActionController ac)
    {
        switch (playerType)
        {
            case HUMAN: return new HumanAgent(ac);
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
        }
        return null;
    }

    public static void main(String[] args) {


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

        Types.GAME_MODE gameMode = CAPITALS; //SCORE;
        Tournament t = new Tournament(gameMode);
        int nRepetitions = 4;
        if(args.length == 0)
        {
            t.setPlayers(new PlayerType[]{PlayerType.MC, PlayerType.MC});
            t.setTribes(new Types.TRIBE[]{XIN_XI, IMPERIUS});
        }else
        {
            try {
                gameMode = (Integer.parseInt(args[0]) == 0) ? CAPITALS : SCORE;
                t = new Tournament(gameMode);
                nRepetitions = Integer.parseInt(args[1]);

                MAX_LENGTH = Integer.parseInt(args[2]);;
                FORCE_TURN_END = Integer.parseInt(args[3]) == 1;
                MCTS_ROLLOUTS = Integer.parseInt(args[4]) == 1;

                int nPlayers = (args.length - 5) / 2;
                PlayerType[] playerTypes = new PlayerType[nPlayers];
                Types.TRIBE[] tribes = new Types.TRIBE[nPlayers];

                for (int i = 0; i < nPlayers; ++i) {
                    playerTypes[i] = parsePlayerTypeStr(args[5 + i]);
                    tribes[i] = parseTribeStr(args[5 + nPlayers + i]);
                }

                t.setPlayers(playerTypes);
                t.setTribes(tribes);


            }catch(Exception e)
            {
                printRunHelp(args);
                System.exit(-1);
            }
        }

        boolean shiftTribes = true;
        t.setSeeds(seeds);
        t.run(nRepetitions, shiftTribes);
    }

    private static PlayerType parsePlayerTypeStr(String arg) throws Exception
    {
        int data = Integer.parseInt(arg);
        switch(data)
        {
            case 0: return PlayerType.DONOTHING;
            case 1: return PlayerType.RANDOM;
            case 2: return PlayerType.SIMPLE;
            case 3: return PlayerType.OSLA;
            case 4: return PlayerType.MC;
            case 5: return PlayerType.MCTS;
            case 6: return PlayerType.OEP;
        }
        throw new Exception("Error: unrecognized Player Type: " + data);
    }

    private static Types.TRIBE parseTribeStr(String arg) throws Exception
    {
        int data = Integer.parseInt(arg);
        switch(data)
        {
            case 0: return XIN_XI;
            case 1: return IMPERIUS;
            case 2: return BARDUR;
            case 3: return OUMAJI;
        }
        throw new Exception("Error: unrecognized Tribe: " + data);
    }


    private Types.GAME_MODE gameMode;
    private boolean RUN_VERBOSE = true;
    private long AGENT_SEED = -1, GAME_SEED = -1;
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

    private void setSeeds(long[] seeds) {
        this.seeds = seeds;
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

                Game game = _prepareGame(tribes, levelSeed, players, gameMode, null);
                Run.runGame(game);

                _addGameResults(game, assignment);

                //Shift arrays for position changes.
                if (shift) {
                    starter = (starter + 1) % participants.size();
                }
            }

            nseed++;
        }

        _printRunResults();

    }


    public void run(String[] levelFiles, int repetitions, boolean shift)
    {
        int starter = 0;
        for (String s : levelFiles) {

            System.out.println("**** Playing level with file " + s + " ****");

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
                System.out.println("]");

                Game game = _prepareGame(s, players, gameMode, null);
                Run.runGame(game);

                _addGameResults(game, assignment);

                //Shift arrays for position changes.
                if (shift) {
                    starter = (starter + 1) % participants.size();
                }
            }
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

    private Game _prepareGame(String levelFile, PlayerType[] playerTypes, Types.GAME_MODE gameMode, ActionController ac)
    {
        long gameSeed = GAME_SEED == -1 ? System.currentTimeMillis() : GAME_SEED;
        if(RUN_VERBOSE) System.out.println("Game seed: " + gameSeed);

        ArrayList<Agent> players = getPlayers(playerTypes, ac);

        Game game = new Game();
        game.init(players, levelFile, gameSeed, gameMode);
        return game;
    }

    private Game _prepareGame(Types.TRIBE[] tribes, long levelSeed, PlayerType[] playerTypes, Types.GAME_MODE gameMode, ActionController ac)
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

    private ArrayList<Agent> getPlayers(PlayerType[] playerTypes, ActionController ac)
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



    private static void printRunHelp(String args[])
    {
        System.out.print("Invalid Arguments ");
        for(String s : args) {
            System.out.print(s + " ");
        }
        System.out.println(". Usage: ");

        System.out.println("'java Tournament <g> <r> <max> <force> <mcts_rollouts> <p1> <p2> [...] <t1> <t2> [...]', where: ");
        System.out.println("\t<g> is the game mode; 0: capitals, 1: score");
        System.out.println("\t<r> is the number of repetitions per level");
        System.out.println("\t<max> max length of a rollout");
        System.out.println("\t<force> forces a turn end after this amount of moves in a rollout");
        System.out.println("\t<mcts_rollouts> mcts rollouts are toggled on (1) or off (0)");
        System.out.println("\t<p_i> is player type i:");
        System.out.println("\t\t0: DoNothing player");
        System.out.println("\t\t1: Random player");
        System.out.println("\t\t2: RuleBased player");
        System.out.println("\t\t3: OneStepLookAhead player");
        System.out.println("\t\t4: Monte Carlo player");
        System.out.println("\t\t5: Monte Carlo Tree Search player");
        System.out.println("\t\t6: Online Evolutionary Planning player");
        System.out.println("\t<t_i> is tribe i:");
        System.out.println("\t\t0: Xin Xi tribe");
        System.out.println("\t\t1: Imperius tribe");
        System.out.println("\t\t2: Bardur tribe");
        System.out.println("\t\t3: Oumaji tribe");
        System.out.println("\tThe number of tribes and players must be equal.");
        System.out.println("Example: java -jar Tribes.jar 0 10 2 3 0 1");
    }

    /// ----- Players and participants -----

    enum PlayerType
    {
        DONOTHING,
        HUMAN,
        RANDOM,
        OSLA,
        MC,
        SIMPLE,
        MCTS,
        OEP;
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