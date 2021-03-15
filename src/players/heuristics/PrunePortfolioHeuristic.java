package players.heuristics;

import core.Types;
import core.game.GameState;
import players.portfolio.ActionAssignment;
import players.portfolio.Portfolio;
import players.portfolio.scripts.BaseScript;
import players.portfolioMCTS.PortfolioTreeNode;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;

public class PrunePortfolioHeuristic implements PruneHeuristic
{
    TreeMap<Types.ACTION, Double> weights;

    public PrunePortfolioHeuristic(Portfolio p)
    {
        TreeMap<Types.ACTION, BaseScript[]> portfolio = p.getPortfolio();
        weights = new TreeMap<>();
        int nEntries = portfolio.size();
        double rndWeight = 1.0/nEntries;

        for(Types.ACTION action : portfolio.keySet())
        {
            weights.put(action, rndWeight);
        }
    }

    public double evaluatePrune(GameState state, ActionAssignment aas)
    {
        Types.ACTION actionType = aas.getAction().getActionType();
        return weights.get(actionType);
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
                rank.put(i, h);
            }
        }

        SortedSet<Map.Entry<Integer, Double>> ts = entriesSortedByValues(rank);
        int n = 0;
        Map.Entry<Integer, Double> entry = ts.first();

        while (n < k && n < ts.size())
        {
            int idx = entry.getKey();
            pruned[idx] = false;
            n++;
        }

        return pruned;
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
                        return res;
//                        return res != 0 ? res : 1;
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
