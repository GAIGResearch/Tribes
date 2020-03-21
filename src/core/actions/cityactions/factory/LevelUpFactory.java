package core.actions.cityactions.factory;

import core.Types;
import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.cityactions.LevelUp;
import core.actors.Actor;
import core.actors.City;
import core.game.GameState;

import java.util.LinkedList;

public class LevelUpFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        City city = (City) actor;

        LinkedList<Types.CITY_LEVEL_UP> bonuses = Types.CITY_LEVEL_UP.getActions(city.getLevel());
        for (Types.CITY_LEVEL_UP bonus : bonuses) {
            LevelUp lUp = new LevelUp(city.getActorId());
            lUp.setBonus(bonus);
            if(lUp.isFeasible(gs)) {
                actions.add(lUp);
            }
        }

        return actions;
    }

}
