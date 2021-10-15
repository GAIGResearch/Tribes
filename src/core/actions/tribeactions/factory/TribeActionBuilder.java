package core.actions.tribeactions.factory;

import core.actions.Action;
import core.actions.tribeactions.DeclareWar;
import core.actions.tribeactions.factory.BuildRoadFactory;
import core.actions.tribeactions.factory.EndTurnFactory;
import core.actions.tribeactions.factory.ResearchTechFactory;
import core.actors.Tribe;
import core.game.GameState;

import java.util.ArrayList;

public class TribeActionBuilder
{

    public ArrayList<Action> getActions(GameState gs, Tribe tribe)
    {
        ArrayList<Action> allActions = new ArrayList<>();

        if(tribe.getTribeId() != gs.getActiveTribeID())
        {
            System.out.println("ERROR: creating actions for a tribe that is not the active one.");
            return allActions;
        }

        //Build Road
        allActions.addAll(new BuildRoadFactory().computeActionVariants(tribe, gs));

        //Research Tech
        allActions.addAll(new ResearchTechFactory().computeActionVariants(tribe, gs));

        //Declare War
        allActions.addAll(new DeclareWarFactory().computeActionVariants(tribe, gs));

        //Send Stars
        allActions.addAll(new SendStarsFactory().computeActionVariants(tribe, gs));

        //End Turn Action
        allActions.addAll(new EndTurnFactory().computeActionVariants(tribe, gs));

        return allActions;
    }

}
