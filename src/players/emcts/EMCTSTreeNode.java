package players.emcts;

import core.actions.Action;
import core.game.GameState;

import java.util.ArrayList;

public class EMCTSTreeNode {

    private ArrayList<Action> sequence;
    private EMCTSTreeNode parent;
    private ArrayList<EMCTSTreeNode> children;
    private double value; // the heuristic value of the node
    private double score;// the exploration score of the node
    private int times_visited;
    private GameState gs;

    public EMCTSTreeNode(ArrayList<Action> sequence, EMCTSTreeNode parent){
        this.sequence = sequence;
        this.parent = parent;
        children = new ArrayList<>();
    }


    public void addChild(EMCTSTreeNode child){
        this.children.add(child);
    }

    public double getValue() {
        return value;
    }

    public EMCTSTreeNode getParent() {
        return parent;
    }

    public ArrayList<Action> getSequence() {
        return sequence;
    }

    public ArrayList<EMCTSTreeNode> getChildren() {
        this.times_visited ++; // if you are accessing children, you have visited the node.
        return children;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getScore() {
        return score;
    }

    public int getTimes_visited() {
        return times_visited;
    }

    public void setScore(int score) {
        this.score = score;
    }

    private int getParentVisited(){
        if(parent == null){ // thus root node
            return 1;
        }else{
            return parent.getTimes_visited();
        }
    }

    public void visited(){ times_visited++; } // if needs be

    public void refreshScore(float bias){
        this.score = value + bias * Math.sqrt((Math.log((getParentVisited())/times_visited)));
    }

    public void setGs(GameState gs) {
        this.gs = gs;
    }

    public GameState getGs() {
        return gs;
    }

    public Action returnNext(){
        Action action = sequence.get(0);
        sequence.remove(0);
        return action;
    }
}
