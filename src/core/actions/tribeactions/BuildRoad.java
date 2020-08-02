package core.actions.tribeactions;
import core.TribesConfig;
import core.actions.Action;
import core.actors.Tribe;
import core.game.GameState;
import utils.Vector2d;
import core.Types;

public class BuildRoad extends TribeAction {

    private Vector2d position;
    public BuildRoad(int tribeId)
    {
        super(Types.ACTION.BUILD_ROAD);
        this.tribeId = tribeId;
    }
    public void setPosition(Vector2d position){
        this.position = position.copy();
    }
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public boolean isFeasible(final GameState gs) {

        //Retrieve tribe
        Tribe tribe = gs.getTribe(tribeId);

        //This tribe should be able to build roads in general.
        if(!tribe.canBuildRoads())
            return false;

        //... and also in this position
        return gs.getBoard().canBuildRoadAt(tribeId, position.x, position.y);
    }

    @Override
    public Action copy() {
        BuildRoad buildRoad = new BuildRoad(this.tribeId);
        buildRoad.setPosition(position);
        return buildRoad;
    }

    public String toString() {
        return "BUILD_ROAD by tribe " + this.tribeId + " at location " + position;
    }
}