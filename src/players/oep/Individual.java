package players.oep;

import core.actions.Action;
import core.game.GameState;
import players.rhea.Genome;

import java.util.ArrayList;

public class Individual implements Comparable<Individual>{

    private ArrayList<Action> actions;
    private GameState gs;
    private double value = 0;

    public Individual(ArrayList<Action> actions) {
        this.actions = actions;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public GameState getGs(){
        return gs;
    }

    public double getValue(){
        return value;
    }

    public void setGs(GameState gs) {
        this.gs = gs;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }

    public Action returnNext(){
        Action action = actions.get(0);
        actions.remove(0);
        return action;
    }

    @Override
    public int compareTo(Individual i) {
        return (int)(i.getValue() - this.getValue());
    }
}
