package core.actions.cityactions;

import core.actions.Action;
import core.actors.City;
import core.game.GameState;

import java.util.ArrayList;
import java.util.LinkedList;

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

        //Level Up
        allActions.addAll(new LevelUp(city).computeActionVariants(gs));

        if(allActions.size() > 0)
        {
            //Level up is special. Nothing else can be done here
            levelUpFlag = true;
            return allActions;
        }

        //Build
        allActions.addAll(new Build(city).computeActionVariants(gs));

        //Burn forest
        allActions.addAll(new BurnForest(city).computeActionVariants(gs));

        //Clear Forest
        allActions.addAll(new ClearForest(city).computeActionVariants(gs));

        //Destroy
        allActions.addAll(new Destroy(city).computeActionVariants(gs));

        //Grow Forest
        allActions.addAll(new GrowForest(city).computeActionVariants(gs));

        //ResourceGathering
        allActions.addAll(new ResourceGathering(city).computeActionVariants(gs));

        //Spawn
        allActions.addAll(new Spawn(city).computeActionVariants(gs));

        return allActions;
    }


    public boolean cityLevelsUp() {
        return levelUpFlag;
    }
}
