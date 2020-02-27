import core.game.Game;
import players.ActionController;
import players.KeyController;
import utils.GUI;
import utils.WindowInput;

import static core.Constants.*;

public class Run {

    /**
     * Runs 1 game.
     * @param g - game to run
     * @param ki - Key controller
     * @param ac - Action controller
     */
    public static void runGame(Game g, KeyController ki, ActionController ac) {
        WindowInput wi = null;
        GUI frame = null;
        if (VISUALS) {
            frame = new GUI(g, "Tribes", ki, ac, true);
            wi = new WindowInput();
            wi.windowClosed = false;
            frame.addWindowListener(wi);
            frame.addKeyListener(ki);
        }

        g.run(frame, wi);
    }
}
