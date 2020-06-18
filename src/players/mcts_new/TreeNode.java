package players.mcts_new;

import core.actions.Action;
import core.actions.tribeactions.EndTurn;
import core.game.GameState;
import players.mcts.MCTSParams;

import java.util.ArrayList;

public class TreeNode implements Comparable<TreeNode>{

    private boolean is_root;
    private boolean is_legal;
    private int depth;
    private int playerID;
    private MCTSParams params;

    private Action action;
    private GameState gameState;
    private ArrayList<TreeNode> exploredNodes = new ArrayList<>();
    private ArrayList<TreeNode> unexploredNodes  = new ArrayList<>();
    private ArrayList<TreeNode> invalidNodes  = new ArrayList<>();

    private double totValue;
    private int nVisits;

    private TreeNode parent;

    // Initial Node
    public TreeNode() {
        is_root = false;
        totValue = 0.0;
        nVisits = 0;
    }

    // Root Node Constructor
    public TreeNode(ArrayList<Action> actions, GameState gameState, int playerID, MCTSParams params) {
        is_root = true;
        is_legal = true;
        depth = 0;
        action = null;
        totValue = 0.0;
        nVisits = 0;
        parent = null;
        this.playerID = playerID;
        this.gameState = gameState;
        this.params = params;
    }



    // Transfer action to node and classify it to unexplored or invalid node
    public int action2Node(){
        return action2Node(gameState.getAllAvailableActions());
    }

    public int action2Node(ArrayList<Action> actions){
        int fm_call = 0;
        for (Action action: actions) {
            TreeNode node = new TreeNode();
            node.setAction(action);
            node.setParent(this);
            node.setDepth(depth + 1);
            node.setPlayerID(playerID);
            if (action.isFeasible(gameState)){
                node.is_legal = true;
                GameState child_gs = gameState.copy();
                child_gs.advance(action, true);
                fm_call += 1;
                node.setGameState(child_gs);
                node.setParams(params);
                unexploredNodes.add(node);
            }else{
                node.is_legal = false;
                invalidNodes.add(node);
            }
        }
        return fm_call;
    }

    // Explore node
    public TreeNode explore(int index){
        TreeNode exploreNode = unexploredNodes.get(index);
        exploredNodes.add(exploreNode);
        unexploredNodes.remove(index);
        return exploreNode;
    }

    public int getUnexploredNodeNum(){
        return unexploredNodes.size();
    }

    public boolean isIs_root() {
        return is_root;
    }

    public void setIs_root(boolean is_root) {
        this.is_root = is_root;
    }

    public boolean isIs_legal() {
        return is_legal;
    }

    public void setIs_legal(boolean is_legal) {
        this.is_legal = is_legal;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public double getTotValue() {
        return totValue;
    }

    public int getnVisits() {
        return nVisits;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public void setParams(MCTSParams params) {
        this.params = params;
    }

    // Get the number of available actions in nodes
    public int totalAction(){
        return exploredNodes.size() + unexploredNodes.size() + invalidNodes.size();
    }

    // Decide the node being explored or exploited
    public boolean isExpandable(){
        return totalAction() == 0 || unexploredNodes.size() > 0;
    }

    // Check if it is my turn or not
    public boolean isMyTurn(){
        if (action instanceof EndTurn){
            return !(gameState.getActiveTribeID() == playerID);
        }
        return gameState.getActiveTribeID() == playerID;
    }

    public double value(){
        return nVisits > 0 ? totValue/nVisits : 0;
    }

    public double uctValue(){
        return value() + (nVisits > 0 ? params.K *  Math.sqrt(Math.log(parent.getnVisits() + 1) / nVisits) : 0);
    }

    public ArrayList<TreeNode> getExploredNodes() {
        return exploredNodes;
    }

    public ArrayList<TreeNode> getUnexploredNodes() {
        return unexploredNodes;
    }

    public ArrayList<TreeNode> getInvalidNodes() {
        return invalidNodes;
    }

    public Action mostVisitedAction(){
        TreeNode mostVisitNode = exploredNodes.get(0);
        for (TreeNode t : exploredNodes){
            if (t.getnVisits() > mostVisitNode.getnVisits()){
                mostVisitNode = t;
            }
        }
        return mostVisitNode.getAction();
    }

    public void setTotValue(double totValue) {
        this.totValue = totValue;
    }

    public void setnVisits(int nVisits) {
        this.nVisits = nVisits;
    }

    @Override
    public int compareTo(TreeNode o) {
        if (isMyTurn()){
            return uctValue() < o.uctValue() ? 1: -1;
        }
        return uctValue() < o.uctValue() ? -1: 1;
    }

    @Override
    public String toString() {
        String nodeStatus = "EXPANDABLE";
        if (!isExpandable()){
            nodeStatus = "UNEXPANDABLE";
        }
        if (is_root){
            return "Root["+ nodeStatus +"]{" +
                    "total_actions: " + totalAction() +
                    ", exploredNodes: " + exploredNodes.size() +
                    ", unexploredNodes: " + unexploredNodes.size() +
                    ", invalidNodes: " + invalidNodes.size() +
                    "}";
        }else{
            return action.toString() + "["+ nodeStatus +"]"+ (isMyTurn() ? "":"[EnemyTurn]") +"{" +
                    "depth: " + depth +
                    ", total_actions: " + totalAction() +
                    ", exploredNodes: " + exploredNodes.size() +
                    ", unexploredNodes: " + unexploredNodes.size() +
                    ", invalidNodes: " + invalidNodes.size() +
                    ", visited_time: " + nVisits +
                    ", value: " + value() +
                    ", UCT_value: " + uctValue() +
                    "}";
        }

    }
}
