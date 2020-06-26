package players.oep;

import core.actions.Action;

import java.util.ArrayList;

public class Genome implements Comparable<Genome>{

    private ArrayList<Action> actions;
    private int visit = 0;
    private double value = 0;

    public Genome(ArrayList<Action> actions) {
        this.actions = actions;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public int getVisit() {
        return visit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void visited(){
        visit++;
    }

    @Override
    public int compareTo(Genome o) {
        return (int)(o.getValue() - this.getValue());
    }

    @Override
    public String toString() {
        return "Genome{" +
                "actions=" + actions +
                ", visit=" + visit +
                ", value=" + value +
                '}';
    }
}
