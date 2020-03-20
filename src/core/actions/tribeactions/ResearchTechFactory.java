package core.actions.tribeactions;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actions.ActionFactory;
import core.actors.Actor;
import core.actors.Tribe;
import core.game.GameState;

import java.util.LinkedList;

public class ResearchTechFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {

        Tribe tribe = (Tribe) actor;
        LinkedList<Action> actions = new LinkedList<>();
        TechnologyTree techTree = tribe.getTechTree();
        int stars = tribe.getStars();
        int numCities = tribe.getCitiesID().size();

        //Technically, we can do faster than this (by pruning branches of the
        // tech tree that are not reachable), although this makes the code more general.
        for(Types.TECHNOLOGY tech : Types.TECHNOLOGY.values())
        {
            if(stars >= tech.getCost(numCities) && techTree.isResearchable(tech))
            {
                ResearchTech newAction = new ResearchTech(tribe.getTribeId());
                newAction.setTech(tech);
                actions.add(newAction);
            }
        }
        return actions;
    }

}
