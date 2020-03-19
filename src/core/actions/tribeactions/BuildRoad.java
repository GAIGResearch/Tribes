package core.actions.tribeactions;

import core.TechnologyTree;
import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.GameState;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.LinkedList;

public class BuildRoad extends TribeAction {

    private Vector2d position;

    public BuildRoad(Tribe tribe)
    {
        this.tribe = tribe;
    }

    public void setPosition(int x, int y){
        this.position = new Vector2d(x, y);
    }
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {

        LinkedList<Action> actions = new LinkedList<>();
        TechnologyTree techTree = this.tribe.getTechTree();

        boolean canBuildRoad = techTree.isResearched(Types.TECHNOLOGY.ROADS);
        boolean hasMoney = tribe.getStars() >= TribesConfig.ROAD_COST;

        if(!canBuildRoad || !hasMoney)
            return actions;

        //We have tech and money, let's find where can this be built
        ArrayList<Vector2d> positions = gs.getBoard().getBuildRoadPositions(tribe.getTribeId());
        for(Vector2d v : positions)
        {
            BuildRoad br = new BuildRoad(tribe);
            br.setPosition(v.x, v.y);
            actions.add(br);
        }

        return actions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        TechnologyTree techTree = this.tribe.getTechTree();
        boolean canBuildRoad = techTree.isResearched(Types.TECHNOLOGY.ROADS);
        boolean hasMoney = tribe.getStars() >= TribesConfig.ROAD_COST;
        boolean boardAllows = gs.getBoard().canBuildRoadAt(tribe.getTribeId(), position.x, position.y);
        return canBuildRoad && hasMoney && boardAllows;
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs))
        {
            tribe.addStars(-TribesConfig.ROAD_COST);
            gs.getBoard().addRoad(position.x, position.y);
            return true;
        }
        return false;
    }
}