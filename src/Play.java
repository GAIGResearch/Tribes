import core.Types;
import core.actions.Action;
import core.game.Game;
import core.actors.Tribe;
import players.*;

import java.util.ArrayList;

import static core.Types.TRIBE.*;
import static core.Types.TRIBE.OUMAJI;

/**
 * Entry point of the framework.
 */
public class Play {


    public static void main(String[] args) {
//        String filename = "SampleLevel2p.csv";
        String filename = "SampleLevel.csv";
//        String filename = "MinimalLevel.csv";

//        play(filename, new boolean[]{true,true}, new Types.TRIBE[] {IMPERIUS, OUMAJI});
        play(filename, new boolean[]{true, true, true, true}, new Types.TRIBE[] {XIN_XI, IMPERIUS, BARDUR, OUMAJI});
//        play(filename, new boolean[]{true,}, new Types.TRIBE[] {OUMAJI});
    }

    public static void play(String filename, boolean[] humans, Types.TRIBE[] tribes)
    {
        KeyController ki = new KeyController(true);
        ActionController ac = new ActionController();

        long seed = System.currentTimeMillis();
        long randomSeed = System.currentTimeMillis();
        System.out.println("Game seed: " + seed + ", random seed: " + randomSeed);

        ArrayList<Agent> players = new ArrayList<>();
        ArrayList<Tribe> tribes_list = new ArrayList<>();

        for(int i = 0; i < humans.length; ++i)
        {
            Agent ag = humans[i] ? new HumanAgent(ac) : new RandomAgent(randomSeed);
            players.add(ag);
            tribes_list.add(new Tribe(tribes[i]));
        }

        Game game = new Game();
        game.init(players, tribes_list, filename, seed);

        Run.runGame(game, ki, ac);
        System.out.println("Running Tribes...");
    }


}