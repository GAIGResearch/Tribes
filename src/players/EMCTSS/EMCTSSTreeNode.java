package players.EMCTSS;

import core.actions.Action;
import core.game.GameState;

import java.util.ArrayList;

public class EMCTSSTreeNode {

    private ArrayList<Action> sequence;
    private players.EMCTSS.EMCTSSTreeNode parent;
    private ArrayList<players.EMCTSS.EMCTSSTreeNode> children;
    private double value; // the heuristic value of the node
    private double score;// the exploration score of the node
    private int times_visited;
    private GameState gs;

    public EMCTSSTreeNode(ArrayList<Action> sequence, players.EMCTSS.EMCTSSTreeNode parent){
        this.sequence = sequence;
        this.parent = parent;
        children = new ArrayList<>();
    }


    public void addChild(players.EMCTSS.EMCTSSTreeNode child){
        this.children.add(child);
    }

    public double getValue() {
        return value;
    }

    public players.EMCTSS.EMCTSSTreeNode getParent() {
        return parent;
    }

    public ArrayList<Action> getSequence() {
        return sequence;
    }

    public ArrayList<players.EMCTSS.EMCTSSTreeNode> getChildren() {
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

    public void setSequence(ArrayList<Action> sequence) {
        this.sequence = sequence;
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
    //used to erase links to all other objects this JVM should clear it
    public void unlink(){
        this.parent = null;
        this.children = null;
        this.gs = null;
        this.sequence=null;
    }
}
