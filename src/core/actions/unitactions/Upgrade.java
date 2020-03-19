package core.actions.unitactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.ArrayList;
import java.util.LinkedList;

public class Upgrade extends UnitAction
{
    public Upgrade(Unit target)
    {
        super.unit = target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> upgradeActions = new LinkedList<>();
        Upgrade action = new Upgrade(unit);

        if(isFeasible(gs)){
            upgradeActions.add(action);
        }
        return upgradeActions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        int stars = gs.getTribe(unit.getTribeId()).getStars();

        switch (unit.getType()){
            case BOAT:
                if(stars >= TribesConfig.SHIP_COST) { return true; }
            case SHIP:
                if(stars >= TribesConfig.BATTLESHIP_COST) { return true; }
        }

        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        Tribe tribe = gs.getTribe(unit.getTribeId());
        Board board = gs.getBoard();
        City city = (City) board.getActor(unit.getCityID());

        if(isFeasible(gs)){
            switch (unit.getType()){
                case BOAT:
                    tribe.subtractStars(TribesConfig.SHIP_COST);
                    Unit ship = Types.UNIT.createUnit(unit.getPosition(), unit.getKills(), unit.isVeteran(), unit.getCityID(), unit.getTribeId(), Types.UNIT.SHIP);
                    ship.setCurrentHP(unit.getCurrentHP());
                    board.addUnit(city, ship);
                case SHIP:
                    tribe.subtractStars(TribesConfig.BATTLESHIP_COST);
                    Unit battleship = Types.UNIT.createUnit(unit.getPosition(), unit.getKills(), unit.isVeteran(), unit.getCityID(), unit.getTribeId(), Types.UNIT.BATTLESHIP);
                    battleship.setCurrentHP(unit.getCurrentHP());
                    board.addUnit(city, battleship);
            }
            board.removeUnitFromBoard(unit);
            board.removeUnitFromCity(unit, city);
            return true;
        }


        return false;
    }
}
