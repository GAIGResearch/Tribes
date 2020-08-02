package core.actions.tribeactions.command;

import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.tribeactions.ResearchTech;
import core.actors.Tribe;
import core.game.GameState;

public class ResearchTechCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        ResearchTech action = (ResearchTech)a;
        if(action.isFeasible(gs))
        {
            //Research tech
            Types.TECHNOLOGY tech = action.getTech();
            int tribeId = action.getTribeId();
            Tribe tribe = gs.getTribe(tribeId);
            int techCost = tech.getCost(tribe.getNumCities(), tribe.getTechTree());
            tribe.subtractStars(techCost);
            tribe.getTechTree().doResearch(tech);

            //Flag if research tree is completed.
            if (tribe.getTechTree().isEverythingResearched())
            {
                tribe.allResearched();
            }
            return true;
        }
        return false;
    }
}
