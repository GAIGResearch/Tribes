package players.heuristics;

import core.Types;
import core.game.GameState;
import jdk.nashorn.api.tree.Tree;
import players.portfolio.ActionAssignment;
import players.portfolio.Portfolio;
import players.portfolio.scripts.BaseScript;
import players.portfolioMCTS.PortfolioTreeNode;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;
import static core.Types.ACTION.*;

public class PrunePortfolioHeuristic implements PruneHeuristic
{
    TreeMap<Types.ACTION, Double> weights;
    TreeMap<String, Double> weightScripts;
    Portfolio portfolio;

    public PrunePortfolioHeuristic(Portfolio p)
    {
        this.portfolio = p;
        TreeMap<Types.ACTION, BaseScript[]> portfolio = p.getPortfolio();
        weights = new TreeMap<>();
        weightScripts = new TreeMap<>();
        double DEFAULT_W = 0.5;

        for(Types.ACTION action : portfolio.keySet())
        {
            weights.put(action, DEFAULT_W);
        }
    }

    public double getWeight(ActionAssignment aas)
    {
        return weights.get(aas.getAction().getActionType());
    }


    public double evaluatePrune(GameState state, ActionAssignment aas)
    {
        Types.ACTION actionType = aas.getAction().getActionType();
        double value = aas.getValue();
        double w = weights.get(actionType);
        if(w == -1)
        {
            w = weightScripts.get(aas.getScript().getClass().toString());
        }
        return w * value;
    }

    public boolean[] prune (PortfolioTreeNode parent, ArrayList<ActionAssignment> actions, GameState gameState, int k) {
        PortfolioTreeNode[] children = parent.getChildren();
        boolean[] pruned = new boolean[children.length];
        Arrays.fill(pruned, true);

        TreeMap<Integer, Double> rank = new TreeMap<>();
        for(int i = 0; i < children.length; ++i)
        {
            if(children[i] != null)
            {
                ActionAssignment aas = actions.get(i);
                double h = evaluatePrune(gameState, aas);
                //h = noise(h, 0.1, new Random().nextDouble());
                rank.put(i, h);
            }
        }

        SortedSet<Map.Entry<Integer, Double>> ts = entriesSortedByValues(rank);
        int n = 0;
        for(Map.Entry<Integer, Double> entry : ts)
        {
            if(n < k)
            {
                int idx = entry.getKey();
                pruned[idx] = false;
                n++;
            }else break;
        }

        return pruned;
    }

    private double noise(double input, double epsilon, double random)
    {
        return (input + epsilon) * (1.0 + epsilon * (random - 0.5));
    }

    public boolean[] unprune (PortfolioTreeNode parent, ArrayList<ActionAssignment> actions, GameState gameState, boolean[] pruned, Random rnd)
    {
        PortfolioTreeNode[] children = parent.getChildren();
        ArrayList<Integer> candidates = new ArrayList<>();
        double highestH = Double.NEGATIVE_INFINITY;
        for(int i = 0; i < children.length; ++i)
        {
            if(children[i] != null && pruned[i])
            {
                ActionAssignment aas = actions.get(i);
                double h = evaluatePrune(gameState, aas);

                if(h > highestH)
                {
                    candidates.clear();
                    candidates.add(i);
                    highestH = h;
                }else if (h == highestH)
                {
                    candidates.add(i);
                }
            }
        }

        int num = candidates.size();
        if(num > 0)
        {
            int selectedIdx = rnd.nextInt(num);
            int selected = candidates.get(selectedIdx);
            pruned[selected] = false;
        }
        return pruned;
    }


    static <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e1.getValue().compareTo(e2.getValue());
//                        return res;
                        return res != 0 ? -res : -1;
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    private int setWeightsAction(double[] weightValues, int curIdx, Types.ACTION actionType)
    {
        weights.put(actionType, -1.0);
        for(BaseScript bs : portfolio.scripts(actionType))
        {
            String str = bs.getClass().toString();
            weightScripts.put(str, weightValues[curIdx++]);
        }
        return curIdx;
    }

    public void setWeights(double[] weightValues) {

        int widx = 0;
//        widx = setWeightsAction(weightValues, widx, ATTACK);        //4
//        widx = setWeightsAction(weightValues, widx, CONVERT);       //3
        widx = setWeightsAction(weightValues, widx, SPAWN);         //6
//        widx = setWeightsAction(weightValues, widx, LEVEL_UP);      //2
        widx = setWeightsAction(weightValues, widx, RESEARCH_TECH); //5
//        widx = setWeightsAction(weightValues, widx, MOVE);          //9
//        widx = setWeightsAction(weightValues, widx, CLEAR_FOREST);  //4
//        widx = setWeightsAction(weightValues, widx, BUILD);         //10


//        weights.put(ATTACK, -1.0);
//        for(BaseScript bs : portfolio.scripts(ATTACK))
//        {
//            String str = bs.getClass().toString();
//            weightScripts.put(str, weightValues[widx++]);
//        }

//
//        if(weightValues.length != this.weights.size())
//            System.out.println("ERROR: mismatch number of weights and portfolio action types");
//
//        int i = 0;
//        for(Types.ACTION action : this.weights.keySet())
//        {
//            weights.put(action, weightValues[i]);
//            i++;
//        }
    }
}
