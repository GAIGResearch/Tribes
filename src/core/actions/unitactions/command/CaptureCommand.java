package core.actions.unitactions.command;

import core.Diplomacy;
import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.unitactions.Capture;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

public class CaptureCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        Capture action = (Capture) a;
        //Check if action is feasible before execution
        if (action.isFeasible(gs)) {
            Types.TERRAIN captureType = action.getCaptureType();
            int unitId = action.getUnitId();
            int targetCityId = action.getTargetCity();
            // Change city tribe id to execute action
            Unit unit = (Unit) gs.getActor(unitId);
            Board b = gs.getBoard();
            Tribe thisTribe = b.getTribe(unit.getTribeId());

            if (captureType == Types.TERRAIN.CITY) {
                City targetCity = (City) gs.getActor(targetCityId);
                Tribe targetTribe = b.getTribe(targetCity.getTribeId());

                //Update scores
                targetTribe.subtractScore(targetCity.getPointsWorth());
                thisTribe.addScore(targetCity.getPointsWorth());

                //Updating diplomacy
                Diplomacy d = gs.getBoard().getDiplomacy();
                // Updating the relationship of the tribes, deducting 30 for a capture
                d.updateAllegiance(-30, thisTribe.getTribeId(), targetTribe.getTribeId());
                // Checks consequences of the update
                d.checkConsequences(-30, thisTribe.getTribeId(), targetTribe.getTribeId());

                //the unit that captures exhausts their turn
                unit.setStatus(Types.TURN_STATUS.FINISHED);

                return b.capture(gs, thisTribe, targetCity.getPosition().x, targetCity.getPosition().y);
            } else if (captureType == Types.TERRAIN.VILLAGE) {
                //the unit that captures exhausts their turn
                unit.setStatus(Types.TURN_STATUS.FINISHED);

                Vector2d unitPos = unit.getPosition();
                return b.capture(gs, thisTribe, unitPos.x, unitPos.y);
            }

        }
        return false;
    }
}
