package players.portfolio.scripts;

import core.actions.Action;
import core.actions.cityactions.LevelUp;
import core.actors.Actor;
import core.game.GameState;

import java.util.ArrayList;
import java.util.Random;

import static core.Types.CITY_LEVEL_UP.*;

public class LevelUpMilitaryScr extends BaseScript {

    //Selects the action that levels up following a growth strategy: city_wall, border expansion, superunit.
    // If workshop or explorer, picks workshop.

    private Random rnd;

    public LevelUpMilitaryScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        for(Action act : actions) {
            LevelUp action = (LevelUp) act;
            if (action.getBonus() == CITY_WALL || action.getBonus() == BORDER_GROWTH || action.getBonus() == SUPERUNIT || action.getBonus() == WORKSHOP)
                return act;
        }
        return null;
    }

}
