package utils;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class AIStats
{
    //Action branching factor (per tick)
    private HashMap<Integer, BigInteger> branchingFactorMult;
    private HashMap<Integer, ArrayList<BigInteger>> branchingFactorAll;
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
    public void addBranchingFactor(int turn, int value)
    {
        if(!branchingFactorMult.containsKey(turn))
        {
            branchingFactorMult.put(turn, BigInteger.valueOf(value));

            ArrayList<BigInteger> newList = new ArrayList<>();
            newList.add(BigInteger.valueOf(value));
            branchingFactorAll.put(turn, newList);
        }else{
            BigInteger prevValue = branchingFactorMult.get(turn);
            BigInteger newValue = prevValue.multiply(BigInteger.valueOf(value));
            branchingFactorMult.put(turn, newValue);

            ArrayList<BigInteger> list = branchingFactorAll.get(turn);
            list.add(BigInteger.valueOf(value));
        }
    }

    public void print()
    {
        NumberFormat formatter = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.ROOT));

        System.out.print("Branching Factor (Turn): " + playerId + ", "  + branchingFactorMult.size() + ", ");
        for(Integer it : branchingFactorMult.keySet())
        {
//            System.out.print(branchingFactorMult.get(it) + " ");
            BigInteger bi = branchingFactorMult.get(it);
            String valStr = formatter.format(bi);
            System.out.print(valStr + " ");
        }
        System.out.println();

        System.out.print("Branching Factor (Move_Avg): " + playerId + ", "  + branchingFactorMult.size() + ", ");
        for(Integer it : branchingFactorAll.keySet())
        {
            StatSummary ss = new StatSummary();
            for(BigInteger val : branchingFactorAll.get(it))
                ss.add(val);
//            System.out.print(ss.mean() + " (" + ss.stdErr() + ") ");
            System.out.printf("%.2f ", ss.mean());
        }
        System.out.println();


        System.out.print("#moves in turn: " + playerId + ", "  + branchingFactorMult.size() + ", ");
        for(Integer it : branchingFactorAll.keySet())
        {
            StatSummary ss = new StatSummary();
            for(BigInteger val : branchingFactorAll.get(it))
                ss.add(val);
//            System.out.print(ss.mean() + " (" + ss.stdErr() + ") ");
            System.out.printf("%d ", ss.n());
        }
        System.out.println();
    }

}
