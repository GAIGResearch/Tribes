package players.oep;

import core.actions.Action;
import core.game.GameState;

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
        if(actions.size() == 0){return null;}
        Action action = actions.get(0);
        actions.remove(0);
        return action;
    }

    public void shift(){
        ArrayList<Action> newAc = new ArrayList<>();
        for(int i = 1; i < actions.size(); i++){
            newAc.add(actions.get(i));
        }
        actions = newAc;
    }

    @Override
    public int compareTo(Individual i) {
        if(this == i){return  0;}
        if(i.getValue() > this.getValue()){return 1;}
        if(i.getValue() < this.getValue()){return -1;}
        else{return 0;}
        //return (int)(i.getValue() - this.getValue());
    }
}
