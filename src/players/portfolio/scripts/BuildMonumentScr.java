package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;

import java.util.Random;

import static core.Types.BUILDING.*;

public class BuildMonumentScr extends BaseScript {

    //Selects the action that builds a monument in the best possible place.

    private Random rnd;

    public BuildMonumentScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        //Looks for the first monument that can be built in an idle space. Returns null if no idle spaces.
        Types.BUILDING[] targets = new Types.BUILDING[]{ALTAR_OF_PEACE, EMPERORS_TOMB, EYE_OF_GOD, GATE_OF_POWER,
                PARK_OF_FORTUNE, TOWER_OF_WISDOM, GRAND_BAZAR,};
        for(Types.BUILDING target : targets){
            Action buildMonument = new BuildingFunc().buildInIdle(target, gs, actions, rnd);
            if(buildMonument != null)
                return buildMonument;
        }
        return null;
    }

}
