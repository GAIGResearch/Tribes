package utils;

import java.util.ArrayList;
import java.util.HashMap;

public class AIStats
{
    //Action branching factor (per tick)
    private HashMap<Integer, Long> branchingFactorMult;
    private HashMap<Integer, ArrayList<Long>> branchingFactorAll;
    private int playerId;

    public AIStats(int playerId)
    {
        this.playerId = playerId;
        branchingFactorMult = new HashMap<>();
        branchingFactorAll = new HashMap<>();
    }

    /**
     * Adds a branching factor
     * @param turn turn for this branching factor.
     * @param value number of available actions at this point.
     */
    public void addBranchingFactor(int turn, long value)
    {
        if(!branchingFactorMult.containsKey(turn))
        {
            branchingFactorMult.put(turn, value);

            ArrayList<Long> newList = new ArrayList<>();
            newList.add(value);
            branchingFactorAll.put(turn, newList);
        }else{
            long prevValue = branchingFactorMult.get(turn);
            branchingFactorMult.put(turn, prevValue*value);

            ArrayList<Long> list = branchingFactorAll.get(turn);
            list.add(value);
        }
    }

    public void print()
    {
        System.out.print("Branching Factor (Turn): " + playerId + ", "  + branchingFactorMult.size() + ", ");
        for(Integer it : branchingFactorMult.keySet())
        {
            System.out.print(branchingFactorMult.get(it) + " ");
        }
        System.out.println();

        System.out.print("Branching Factor (Move Avg): " + playerId + ", "  + branchingFactorMult.size() + ", ");
        for(Integer it : branchingFactorAll.keySet())
        {
            StatSummary ss = new StatSummary();
            for(Long val : branchingFactorAll.get(it))
                ss.add(val);
            System.out.print(ss.mean() + " (" + ss.stdErr() + ") ");
        }
        System.out.println();
    }

}
