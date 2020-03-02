package core.actions.tribeactions;

import core.actions.Action;
import core.actions.unitactions.*;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.GameState;

import java.util.ArrayList;

public class TribeActionBuilder
{

    public ArrayList<Action> getActions(GameState gs, Tribe tribe)
    {
        ArrayList<Action> allActions = new ArrayList<>();

        //Build Road
        allActions.addAll(new BuildRoad(tribe).computeActionVariants(gs));

        //Research Tech
        allActions.addAll(new ResearchTech(tribe).computeActionVariants(gs));

        //End Turn Action
        allActions.addAll(new EndTurnAction(tribe).computeActionVariants(gs));


        return allActions;
    }

}
