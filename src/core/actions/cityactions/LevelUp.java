package core.actions.cityactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import core.Types.CITY_LEVEL_UP;
import utils.Vector2d;

import java.util.LinkedList;


public class LevelUp extends CityAction {

    private CITY_LEVEL_UP bonus;

    public LevelUp(int cityId)
    {
        super.cityId = cityId;
    }
    public CITY_LEVEL_UP getBonus() {
        return bonus;
    }
    public void setBonus(CITY_LEVEL_UP bonus) {
        this.bonus = bonus;
    }

    @Override
    public boolean isFeasible(GameState gs) {
        City city = (City) gs.getActor(this.cityId);
        return city.canLevelUp() && bonus.validType(city.getLevel());
    }

    @Override
    public boolean execute(GameState gs) {

        if(!isFeasible(gs))
            return false;

        City city = (City) gs.getActor(this.cityId);
        Tribe tribe = gs.getBoard().getTribe(city.getTribeId());
        Vector2d cityPos = city.getPosition();

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

        if(bonus.grantsMonument())
            tribe.cityMaxedUp();

        tribe.addScore(bonus.getLevelUpPoints());
        city.addPointsWorth(bonus.getLevelUpPoints());
        city.levelUp();

        return true;
    }

    @Override
    public Action copy() {
        LevelUp lUp = new LevelUp(this.cityId);
        lUp.setBonus(this.bonus);
        lUp.setTargetPos(this.targetPos.copy());
        return lUp;
    }

    @Override
    public String toString()
    {
        return "LEVEL_UP by city " + this.cityId+ " with bonus " + bonus.toString();
    }
}
