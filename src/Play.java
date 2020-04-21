import core.Types;
import core.actions.Action;
import core.game.Game;
import core.actors.Tribe;
import players.*;
import players.osla.OneStepLookAheadAgent;

import java.util.ArrayList;

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
        OSLA
    }

    public static void main(String[] args) {
        String filename = "SampleLevel2p.csv";
//        String filename = "SampleLevel.csv";
//        String filename = "MinimalLevel.csv";
//        String filename = "MinimalLevel_water.csv";
//        String filename = "MinimalLevel2.csv";

//        play(filename, new PlayerType[]{PlayerType.OSLA, PlayerType.OSLA, PlayerType.OSLA, PlayerType.HUMAN}, new Types.TRIBE[] {XIN_XI, IMPERIUS, BARDUR, OUMAJI});
//        play(filename, new PlayerType[]{PlayerType.HUMAN, PlayerType.HUMAN, PlayerType.HUMAN, PlayerType.HUMAN}, new Types.TRIBE[] {XIN_XI, IMPERIUS, BARDUR, OUMAJI});
//        play(filename, new PlayerType[]{PlayerType.HUMAN}, new Types.TRIBE[] {XIN_XI});
        play(filename, new PlayerType[]{PlayerType.OSLA, PlayerType.OSLA}, new Types.TRIBE[] {XIN_XI, OUMAJI});
//        play(filename, new PlayerType[]{PlayerType.RANDOM, PlayerType.RANDOM, PlayerType.RANDOM, PlayerType.HUMAN}, new Types.TRIBE[] {XIN_XI, IMPERIUS, BARDUR, OUMAJI});
    }

    private static void play(String filename, PlayerType[] playerTypes, Types.TRIBE[] tribes)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        long seed = 1587462619163L; //System.currentTimeMillis();
        long randomSeed = 1587462619163L; //System.currentTimeMillis();
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
        game.init(players, tribes_list, filename, seed);

        Run.runGame(game, ki, ac);
        System.out.println("Running Tribes...");
    }

    private static Agent getAgent(PlayerType playerType, long randomSeed, ActionController ac)
    {
        switch (playerType)
        {
            case HUMAN: return new HumanAgent(ac);
            case RANDOM: return new RandomAgent(randomSeed);
            case OSLA: return new OneStepLookAheadAgent(randomSeed);
        }
        return null;
    }

}