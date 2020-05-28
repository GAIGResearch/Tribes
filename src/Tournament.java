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

import java.util.*;

import static core.Types.GAME_MODE.CAPITALS;
import static core.Types.TRIBE.*;

/**
 * Entry point of the framework.
 */
public class Tournament {

    private static Agent _getAgent(PlayerType playerType, long agentSeed, ActionController ac)
    {
        switch (playerType)
        {
            case DONOTHING: return new DoNothingAgent(agentSeed);
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
                mcparams.PRIORITIZE_ROOT = true;
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

        Tournament t = new Tournament(gameMode);
//
        t.setPlayers(new PlayerType[]{PlayerType.OSLA, PlayerType.MC});
        t.setTribes(new Types.TRIBE[]{XIN_XI, IMPERIUS});

//        t.setPlayers(new PlayerType[]{PlayerType.SIMPLE, PlayerType.DONOTHING, PlayerType.OSLA});
//        t.setTribes(new Types.TRIBE[]{XIN_XI, IMPERIUS, BARDUR});

        t.setSeeds(seeds);
        int nRepetitions = 4;
        boolean shiftTribes = true;

        t.run(nRepetitions, shiftTribes);
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
                System.out.println("]");

                Game game = _prepareGame(tribes, levelSeed, players, gameMode, null);
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

        for(int i = 0; i < playerTypes.length; ++i)
        {
            Agent ag = _getAgent(playerTypes[i], agentSeed, ac);
            ag.setPlayerID(i);
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
                System.out.printf("[N:%d];", stat.getVariable("v").n());
                System.out.printf("[W:%d];", (int) stat.getVariable("v").sum());
                System.out.printf("[S:%.2f];", stat.getVariable("s").mean());
                System.out.printf("[T:%.2f];", stat.getVariable("t").mean());
                System.out.printf("[C:%.2f];", stat.getVariable("c").mean());
                System.out.printf("[P:%.2f];", stat.getVariable("p").mean());
                System.out.printf("[Player:%d:%s]", thisParticipant.participantId, thisParticipant.playerType);
                System.out.println();
            }
        }

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