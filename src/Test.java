import core.Types;
import core.actions.Action;
import core.actions.tribeactions.ResearchTech;
import core.actors.Tribe;

import java.util.LinkedList;

import static core.Types.TECHNOLOGY.*;

public class Test
{
    public static void testResearchActions()
    {
        Tribe t = new Tribe(Types.TRIBE.XIN_XI);
        t.addCity(0); //We need to add a city or I'm actually unable to research anything.

        LinkedList<Action> allResearchable = new ResearchTech(t).computeActionVariants(null);
        for(Action act : allResearchable)
        {
            System.out.println(act.toString());
        }

        ResearchTech rt = new ResearchTech(t);
        rt.setTech(MINING);
        System.out.println("Tech feasible: " + rt.toString() + ", " + rt.isFeasible(null));

        rt.setTech(SHIELDS);
        System.out.println("Tech feasible: " + rt.toString() + ", " + rt.isFeasible(null));

        t.setStars(10);

        rt.setTech(MINING);
        System.out.println("Tech feasible: " + rt.toString() + ", " + rt.isFeasible(null));

        rt.setTech(SHIELDS);
        System.out.println("Tech feasible: " + rt.toString() + ", " + rt.isFeasible(null));

        System.out.println("Managed to research: " + rt.toString() + ", " + rt.execute(null));

        rt.setTech(MINING);
        System.out.println("Managed to research: " + rt.toString() + ", " + rt.execute(null));

        t.setStars(10);

        allResearchable = new ResearchTech(t).computeActionVariants(null);
        for(Action act : allResearchable)
        {
            System.out.println(act.toString());
        }

    }


    public static void main(String[] args)
    {
        testResearchActions();
    }

}
