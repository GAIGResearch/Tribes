package players;

import core.actions.Action;
import core.game.GameState;
import utils.ElapsedCpuTimer;

public class HumanAgent extends Agent {

    ActionController ac;

    public HumanAgent(ActionController ac)
    {
        super(0);
        this.ac = ac;
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect) {
        return ac.getAction();
    }

    @Override
    public Agent copy() {
        return new HumanAgent(ac);
    }
}
