package players;

import core.actions.Action;
import core.actions.tribeactions.TribeAction;
import core.game.GameState;

import java.util.ArrayDeque;
import java.util.Queue;

public class ActionController {

    private Queue<Action> actionsQueue;
    private Action lastActionPlayed;

    public ActionController()
    {
        actionsQueue = new ArrayDeque<>();
    }

    public void addAction(Action candidate, GameState gs)
    {
        if (candidate != null && gs.getActiveTribe() != null) {
            if (candidate instanceof TribeAction) {
                ((TribeAction) candidate).setTribeId(gs.getActiveTribeID());
            }
            actionsQueue.add(candidate);
        }
    }

    public Action getAction() {
        lastActionPlayed = actionsQueue.poll();
        return lastActionPlayed;
    }

    private ActionController(Queue<Action> otherQueue) {
        actionsQueue = new ArrayDeque<>(otherQueue);
    }

    public ActionController copy() {
        return new ActionController(actionsQueue);
    }

    public void reset() {
        actionsQueue.clear();
    }

    public Action getLastActionPlayed() {
        return lastActionPlayed;
    }

    public void setLastActionPlayed(Action a) {
        lastActionPlayed = a;
    }
}