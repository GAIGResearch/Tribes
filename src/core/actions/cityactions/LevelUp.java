package core.actions.cityactions;

import core.Types;
import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.GameState;
import core.Types.CITY_LEVEL_UP;
import utils.Vector2d;

import java.util.LinkedList;


public class LevelUp extends CityAction {

    private CITY_LEVEL_UP bonus;

    public LevelUp(City c)
    {
        super.city = c;
    }

    @Override
    public LinkedList<Action> computeActionVariants(GameState gs) {

        LinkedList<Action> actions = new LinkedList<>();

        if (isFeasible(gs))
        {
            int curLevel = city.getLevel();
            LinkedList<CITY_LEVEL_UP> bonuses = CITY_LEVEL_UP.getActions(curLevel);
            for (CITY_LEVEL_UP bonus : bonuses) {
                LevelUp lUp = new LevelUp(city);
                lUp.setBonus(bonus);
                actions.add(lUp);
            }
        }

        return actions;
    }

    @Override
    public boolean isFeasible(GameState gs) {
        return city.canLevelUp();
    }

    @Override
    public boolean execute(GameState gs) {

        switch(bonus)
        {
            case WORKSHOP:
                city.addProduction(1);
                break;
            case EXPLORER:
                gs.getBoard().launchExplorer(city.getX(), city.getY(), city.getTribeId());
                break;
            case CITY_WALL:
                city.setWalls(true);
                break;
            case RESOURCES:
                Tribe tribe = gs.getBoard().getTribe(city.getTribeId());
                tribe.addStars(5);
                break;
            case POP_GROWTH:
                city.addPopulation(3);
                break;
            case BORDER_GROWTH:
                gs.getBoard().expandBorder(city);
                break;
            case PARK:
                tribe = gs.getBoard().getTribe(city.getTribeId());
                tribe.addScore(250);
                break;
            case SUPERUNIT:

                Vector2d cityPos = new Vector2d(city.getX(), city.getY());
                Unit unitInCity = gs.getBoard().getUnitAt(city.getX(), city.getY());

                //This can probably be encapsulated
                Unit superUnit = Types.UNIT.createUnit(cityPos, 0, false, city.getActorID(), city.getTribeId(), Types.UNIT.SUPERUNIT);
                gs.getBoard().addUnitToBoard(superUnit);
                gs.getBoard().addUnitToCity(superUnit, city);

                if(unitInCity != null)
                {
                    gs.getBoard().pushUnit(unitInCity.getTribeId(), unitInCity, city.getX(), city.getY());
                }

                break;
        }


        return true;
    }

    public CITY_LEVEL_UP getBonus() {
        return bonus;
    }

    public void setBonus(CITY_LEVEL_UP bonus) {
        this.bonus = bonus;
    }
}
