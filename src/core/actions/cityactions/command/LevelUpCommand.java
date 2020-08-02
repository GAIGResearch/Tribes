package core.actions.cityactions.command;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.cityactions.LevelUp;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.GameState;
import utils.Vector2d;
import core.Types.CITY_LEVEL_UP;

public class LevelUpCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        LevelUp action = (LevelUp)a;
        if(!action.isFeasible(gs))
            return false;

        int cityId = action.getCityId();
        City city = (City) gs.getActor(cityId);
        Tribe tribe = gs.getBoard().getTribe(city.getTribeId());
        Vector2d cityPos = city.getPosition();
        CITY_LEVEL_UP bonus = action.getBonus();

        if(bonus.grantsMonument())
            tribe.cityMaxedUp();

        tribe.addScore(bonus.getLevelUpPoints());
        city.addPointsWorth(bonus.getLevelUpPoints());
        city.levelUp();

        switch(bonus)
        {
            case WORKSHOP:
                city.addProduction(TribesConfig.CITY_LEVEL_UP_WORKSHOP_PROD);
                break;
            case EXPLORER:
                gs.getBoard().launchExplorer(cityPos.x, cityPos.y, city.getTribeId(), gs.getRandomGenerator());
                break;
            case CITY_WALL:
                city.setWalls(true);
                break;
            case RESOURCES:
                tribe.addStars(TribesConfig.CITY_LEVEL_UP_RESOURCES);
                break;
            case POP_GROWTH:
                city.addPopulation(tribe, TribesConfig.CITY_LEVEL_UP_POP_GROWTH);
                break;
            case BORDER_GROWTH:
                gs.getBoard().expandBorder(city);
                break;
            case PARK:
                tribe.addScore(TribesConfig.CITY_LEVEL_UP_PARK);
                city.addPointsWorth(TribesConfig.CITY_LEVEL_UP_PARK);
                break;
            case SUPERUNIT:
                Unit unitInCity = gs.getBoard().getUnitAt(cityPos.x, cityPos.y);
                if(unitInCity != null)
                {
                    gs.pushUnit(unitInCity, cityPos.x, cityPos.y);
                }

                Unit superUnit = Types.UNIT.createUnit(cityPos, 0, false, city.getActorId(), city.getTribeId(), Types.UNIT.SUPERUNIT);
                gs.getBoard().addUnit(city, superUnit);
                break;
        }

        return true;
    }
}
