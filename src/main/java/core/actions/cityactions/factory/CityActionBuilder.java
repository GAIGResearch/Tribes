package core.actions.cityactions.factory;

import core.actions.Action;
import core.actors.City;
import core.game.GameState;

import java.util.ArrayList;

public class CityActionBuilder
{
    private boolean levelUpFlag;

    public CityActionBuilder()
    {
        levelUpFlag = false;
    }

    public ArrayList<Action> getActions(GameState gs, City city)
    {
        ArrayList<Action> allActions = new ArrayList<>();

        if(city.getTribeId() != gs.getActiveTribeID())
        {
            System.out.println("ERROR: creating actions for a city this tribe does not control.");
            return allActions;
        }


        //Level Up
        allActions.addAll(new LevelUpFactory().computeActionVariants(city, gs));

        if(allActions.size() > 0)
        {
            //Level up is special. Nothing else can be done here
            levelUpFlag = true;
            return allActions;
        }

        //Build
        allActions.addAll(new BuildFactory().computeActionVariants(city, gs));

        //Burn forest
        allActions.addAll(new BurnForestFactory().computeActionVariants(city, gs));

        //Clear Forest
        allActions.addAll(new ClearForestFactory().computeActionVariants(city, gs));

        //Destroy
        allActions.addAll(new DestroyFactory().computeActionVariants(city, gs));

        //Grow Forest
        allActions.addAll(new GrowForestFactory().computeActionVariants(city, gs));

        //ResourceGathering
        allActions.addAll(new ResourceGatheringFactory().computeActionVariants(city, gs));

        //Spawn
        allActions.addAll(new SpawnFactory().computeActionVariants(city, gs));

        return allActions;
    }


    public boolean cityLevelsUp() {
        return levelUpFlag;
    }
}
