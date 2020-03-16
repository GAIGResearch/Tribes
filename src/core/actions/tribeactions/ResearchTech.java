package core.actions.tribeactions;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.GameState;

import java.util.LinkedList;

public class ResearchTech extends TribeAction {

    private Types.TECHNOLOGY tech;

    public ResearchTech(Tribe tribe)
    {
        this.tribe = tribe;
    }

    public void setTech(Types.TECHNOLOGY tech) {this.tech = tech;}
    public Types.TECHNOLOGY getTech() {return this.tech;}

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {

        LinkedList<Action> actions = new LinkedList<>();
        TechnologyTree techTree = this.tribe.getTechTree();
        int stars = tribe.getStars();
        int numCities = tribe.getCitiesID().size();

        //Technically, we can do faster than this (by pruning branches of the
        // tech tree that are not reachable), although this makes the code more general.
        for(Types.TECHNOLOGY tech : Types.TECHNOLOGY.values())
        {
            if(stars >= tech.getCost(numCities) && techTree.isResearchable(tech))
            {
                ResearchTech newAction = new ResearchTech(this.tribe);
                newAction.setTech(tech);
                actions.add(newAction);
            }
        }
        return actions;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        if(tech == null)
            return false;

        if(tribe.getStars() >= tech.getCost(tribe.getCitiesID().size()))
            return tribe.getTechTree().isResearchable(this.tech);
        return false;
    }


    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs))
        {
            int cost = tech.getCost(tribe.getCitiesID().size());
            tribe.addStars(-cost);
            boolean researched = tribe.getTechTree().doResearch(tech);
            if(!researched)
            {
                //This shouldn't happen.
                System.out.println("WARNING: Researchable research not researched!");
                return false;
            }else if (tribe.getTechTree().isEverythingResearched())
            {
                tribe.allResearched();
            }
            return true;
        }
        return false;
    }

    public String toString()
    {
        return "Action to research " + tech.toString();
    }
}
