package players.rhea;

import core.actions.Action;

import java.util.ArrayList;

public class Genome implements Comparable<Genome>{

    private ArrayList<Action> actions;
    private double value = 0;

    public Genome(ArrayList<Action> actions) {
        this.actions = actions;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void shift()
    {
        ArrayList<Action> newList = new ArrayList<>();
        for(int i = 1; i < actions.size(); i++)
        {
            newList.add(actions.get(i));
        }
        actions = newList;
    }

    @Override
    public int compareTo(Genome o) {
        return (int)(o.getValue() - this.getValue());
    }

    @Override
    public String toString() {
        return "Genome{" +
                "actions=" + actions +
                ", value=" + value +
                '}';
    }
}
