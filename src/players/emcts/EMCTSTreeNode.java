package players.emcts;

import core.actions.Action;

import java.util.ArrayList;

public class EMCTSTreeNode {

    private ArrayList<Action> sequence;
    private EMCTSTreeNode pearent;
    private ArrayList<EMCTSTreeNode> children;

    public void addChild(EMCTSTreeNode child){
        this.children.add(child);
    }
}
