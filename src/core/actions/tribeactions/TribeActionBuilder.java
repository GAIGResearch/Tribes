package core.actions.tribeactions;

import core.actions.Action;
import core.actors.Tribe;
import core.game.GameState;

import java.util.ArrayList;

public class TribeActionBuilder
{

    public ArrayList<Action> getActions(GameState gs, Tribe tribe)
    {
        ArrayList<Action> allActions = new ArrayList<>();
        int tribeId = tribe.getTribeId();

        //Build Road
        allActions.addAll(new BuildRoadFactory().computeActionVariants(tribe, gs));

        //Research Tech
        allActions.addAll(new ResearchTechFactory().computeActionVariants(tribe, gs));

        //End Turn Action
        allActions.addAll(new EndTurnFactory().computeActionVariants(tribe, gs));

        return allActions;
    }

}
