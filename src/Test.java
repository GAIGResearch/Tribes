import core.Types;
import core.actions.Action;
import core.actions.tribeactions.ResearchTech;
import core.actions.tribeactions.factory.ResearchTechFactory;
import core.actions.unitactions.Recover;
import core.actions.unitactions.factory.RecoverFactory;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.actors.units.Warrior;
import utils.Vector2d;

import java.util.LinkedList;

import static core.Types.TECHNOLOGY.*;

public class Test
{
    public static void testResearchActions()
    {
        Tribe t = new Tribe(Types.TRIBE.XIN_XI);
        t.addCity(0); //We need to add a city or I'm actually unable to research anything.

        LinkedList<Action> allResearchable = new ResearchTechFactory().computeActionVariants(t, null);
        for(Action act : allResearchable)
        {
            System.out.println(act.toString());
        }

        ResearchTech rt = new ResearchTech(t.getActorId());
        rt.setTech(MINING);
        System.out.println("Tech feasible: " + rt.toString() + ", " + rt.isFeasible(null));

        rt.setTech(SHIELDS);
        System.out.println("Tech feasible: " + rt.toString() + ", " + rt.isFeasible(null));

        t.addStars(10);

        rt.setTech(MINING);
        System.out.println("Tech feasible: " + rt.toString() + ", " + rt.isFeasible(null));

        rt.setTech(SHIELDS);
        System.out.println("Tech feasible: " + rt.toString() + ", " + rt.isFeasible(null));

        System.out.println("Managed to research: " + rt.toString() + ", " + rt.execute(null));

        rt.setTech(MINING);
        System.out.println("Managed to research: " + rt.toString() + ", " + rt.execute(null));

        t.addStars(10);

        allResearchable = new ResearchTechFactory().computeActionVariants(t, null);
        for(Action act : allResearchable)
        {
            System.out.println(act.toString());
        }

        // Recover Testing
        // TODO: test in territory will add 4 when gs is add
        Unit warrior = new Warrior(new Vector2d(10, 20), 0, false, 1, 1);
        testRecovery(warrior);
        warrior.setCurrentHP(5);
        testRecovery(warrior);
        testRecovery(warrior);
        testRecovery(warrior);
        testRecovery(warrior);
   }

    private static void testRecovery(Unit warrior) {
        LinkedList<Action> warriorAction;
        if (new Recover(warrior.getActorId()).isFeasible(null)) {
            warriorAction = new RecoverFactory().computeActionVariants(warrior, null);
            System.out.println(warriorAction.size());
            warriorAction.get(0).execute(null);
            System.out.println("Increase HP");
        }else {
            System.out.println("The HP is full");
        }
        System.out.println(warrior.getCurrentHP());
    }

    public static void testBuild()
    {

    }


    public static void main(String[] args)
    {
        testResearchActions();
    }

}
