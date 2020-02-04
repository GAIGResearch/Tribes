import core.game.Game;
import players.KeyController;
import utils.GUI;
import utils.WindowInput;

import static core.Constants.*;

public class Run {

    /**
     * Runs 1 game.
     * @param g - game to run
     * @param ki - Key controller
     */
    public static void runGame(Game g, KeyController ki) {
        WindowInput wi = null;
        GUI frame = null;
        if (VISUALS) {
            frame = new GUI(g, "Tribes", ki, false);
            wi = new WindowInput();
            wi.windowClosed = false;
            frame.addWindowListener(wi);
            frame.addKeyListener(ki);
        }

        g.run(frame, wi);
    }
}
