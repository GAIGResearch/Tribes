package core.actions.cityactions;

import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.game.GameState;
import core.Types.CITY_LEVEL_UP;

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
                //TODO: Create explorer
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
                //TODO: Spawn a superunit

                // Can cause push if there's another unit in city; see Push Grid at: https://polytopia.fandom.com/wiki/Giant

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
