import core.Types;
import core.actions.Action;
import core.game.Game;
import core.actors.Tribe;
import players.*;
import players.mc.MonteCarloAgent;
import players.osla.OneStepLookAheadAgent;

import java.util.ArrayList;

import static core.Types.GAME_MODE.CAPITALS;
import static core.Types.TRIBE.*;
import static core.Types.TRIBE.OUMAJI;

/**
 * Entry point of the framework.
 */
public class Play {

    enum PlayerType
    {
        HUMAN,
        RANDOM,
        OSLA,
        MC
    }

    public static void main(String[] args) {

        Types.GAME_MODE gameMode = CAPITALS;

        String filename = "SampleLevel2p.csv";
//        String filename = "SampleLevel.csv";
//        String filename = "MinimalLevel.csv";
//        String filename = "MinimalLevel_water.csv";
//        String filename = "MinimalLevel2.csv";

//        play(filename, new PlayerType[]{PlayerType.OSLA, PlayerType.OSLA, PlayerType.OSLA, PlayerType.OSLA}, new Types.TRIBE[] {XIN_XI, IMPERIUS, BARDUR, OUMAJI}, gameMode);
//        play(filename, new PlayerType[]{PlayerType.HUMAN, PlayerType.HUMAN, PlayerType.HUMAN, PlayerType.HUMAN}, new Types.TRIBE[] {XIN_XI, IMPERIUS, BARDUR, OUMAJI}, gameMode);
//        play(filename, new PlayerType[]{PlayerType.HUMAN}, new Types.TRIBE[] {XIN_XI}, gameMode);
        play(filename, new PlayerType[]{PlayerType.MC, PlayerType.MC}, new Types.TRIBE[] {XIN_XI, OUMAJI}, gameMode);
//        play(filename, new PlayerType[]{PlayerType.RANDOM, PlayerType.RANDOM, PlayerType.RANDOM, PlayerType.HUMAN}, new Types.TRIBE[] {XIN_XI, IMPERIUS, BARDUR, OUMAJI}, gameMode);
    }

    private static void play(String filename, PlayerType[] playerTypes, Types.TRIBE[] tribes, Types.GAME_MODE gameMode)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        long seed = System.currentTimeMillis();
        long randomSeed = System.currentTimeMillis();
        System.out.println("Game seed: " + seed + ", random seed: " + randomSeed);

        ArrayList<Agent> players = new ArrayList<>();
        ArrayList<Tribe> tribes_list = new ArrayList<>();

        for(int i = 0; i < playerTypes.length; ++i)
        {
            Agent ag = getAgent(playerTypes[i], randomSeed, ac);
            ag.setPlayerID(i);
            players.add(ag);
            tribes_list.add(new Tribe(tribes[i]));
        }

        Game game = new Game();
        game.init(players, tribes_list, filename, seed, gameMode);

        Run.runGame(game, ki, ac);

        Types.RESULT[] results = game.getWinnerStatus();
        int[] scores = game.getScores();
        System.out.println("Tribes game over.");
        for(int i = 0; i < playerTypes.length; ++i)
        {
            System.out.println("Tribe " + i + " (" + tribes[i] + "): " + results[i] + ", " + scores[i] + " points.");
        }
    }

    private static Agent getAgent(PlayerType playerType, long randomSeed, ActionController ac)
    {
        switch (playerType)
        {
            case HUMAN: return new HumanAgent(ac);
            case RANDOM: return new RandomAgent(randomSeed);
            case OSLA: return new OneStepLookAheadAgent(randomSeed);
            case MC: return new MonteCarloAgent(randomSeed);
        }
        return null;
    }

}