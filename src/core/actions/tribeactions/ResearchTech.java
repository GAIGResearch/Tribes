package core.actions.tribeactions;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.GameState;

import java.util.LinkedList;

public class ResearchTech extends TribeAction {

    private Types.TECHNOLOGY tech;

    public ResearchTech(int tribeId)
    {
        this.tribeId = tribeId;
    }
    public void setTech(Types.TECHNOLOGY tech) {this.tech = tech;}
    public Types.TECHNOLOGY getTech() {return this.tech;}


    @Override
    public boolean isFeasible(final GameState gs) {
        Tribe tribe = (Tribe) gs.getActor(tribeId);

        if(tech == null)
            return false;

        if(tribe.getStars() >= tech.getCost(tribe.getCitiesID().size()))
            return tribe.getTechTree().isResearchable(this.tech);
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        Tribe tribe = (Tribe) gs.getActor(tribeId);
        if(isFeasible(gs))
        {
            //Research tech
            int techCost = tech.getCost(tribe.getCitiesID().size());
            tribe.addStars(-techCost);
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

    @Override
    public Action copy() {
        ResearchTech resTech = new ResearchTech(this.tribeId);
        resTech.setTech(this.tech);
        return resTech;
    }

    public String toString()
    {
        return "Action to research " + tech.toString();
    }
}
