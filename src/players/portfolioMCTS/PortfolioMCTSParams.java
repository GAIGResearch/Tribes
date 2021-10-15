package players.portfolioMCTS;

import players.heuristics.AlgParams;
import players.heuristics.PruneHeuristic;
import players.heuristics.PrunePortfolioHeuristic;
import players.portfolio.Portfolio;
import utils.stats.LinearRegression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("WeakerAccess")
public class PortfolioMCTSParams extends AlgParams {

    // Parameters
    public double C = Math.sqrt(2);
    public double K_init_mult = 0.5;
    public double T_mult = 2.0;
    public double A_mult = 1.5;
    public double B = 1.3;
    public int ROLLOUT_LENGTH = 20;//10;
    public boolean ROLOUTS_ENABLED = true;
    private Portfolio portfolio;
    public boolean PRUNING = true;
    public boolean PROGBIAS = true;
    public PrunePortfolioHeuristic pruneHeuristic;

    /**
     * Function that returns array of scripts to use
     */
    public Portfolio getPortfolio()
    {
        return portfolio;
    }

    @Override
    public PruneHeuristic getPruneHeuristic()
    {
        return pruneHeuristic;
    }

    public void setPortfolio(Portfolio portfolio)
    {
        this.portfolio = portfolio;
    }

    public void setParameterValue(String param, Object value) {
        switch(param) {
            case "C": C = (double) value; break;
            case "ROLLOUT_LENGTH": ROLLOUT_LENGTH = (int) value; break;
            case "heuristic_method": heuristic_method = (int) value; break;
        }
    }

    public Object getParameterValue(String param) {
        switch(param) {
            case "K": return C;
            case "ROLLOUT_LENGTH": return ROLLOUT_LENGTH;
            case "heuristic_method": return heuristic_method;
        }
        return null;
    }


    public ArrayList<String> getParameters() {
        ArrayList<String> paramList = new ArrayList<>();
        paramList.add("K");
        paramList.add("rollout_depth");
        paramList.add("heuristic_method");
        return paramList;
    }

    public Map<String, Object[]> getParameterValues() {
        HashMap<String, Object[]> parameterValues = new HashMap<>();
        parameterValues.put("K", new Double[]{1.0, Math.sqrt(2), 2.0});
        parameterValues.put("rollout_depth", new Integer[]{5, 8, 10, 12, 15});
        parameterValues.put("heuristic_method", new Integer[]{ENTROPY_HEURISTIC, SIMPLE_HEURISTIC, DIFF_HEURISTIC});
        return parameterValues;
    }

    public int getPruneT(int nChildren)
    {
        return (int) (nChildren * this.T_mult);
    }

    public int getPruneKinit(int nChildren)
    {
        int kInit = (int) (nChildren * this.K_init_mult);
        return Math.max(kInit, 1);
    }

    public int getPruneA(int nChildren)
    {
        return (int) (this.getPruneT(nChildren) * this.A_mult);
    }

    public int getUnpruneLimit(int nChildren, int nextK)
    {
        return (int) (getPruneA(nChildren) * Math.pow(B, nextK));
    }

    public void printPruneLine(int nChildren)
    {
        int MAX_LIM = 100;
        int T = this.getPruneT(nChildren);
        int kInit = this.getPruneKinit(nChildren);
        int A = this.getPruneA(nChildren);
        double B = this.B;
        int k = 0;
        System.out.print("Children: " + nChildren + ", limits: [T: " + T + ", k: " + kInit +"], ");
//            System.out.print("Children: " + nChildren + ", T: " + T + ", kInit: " + kInit + ", A: " + A + ", B " + B + ", limits: ");

        boolean stop = false;
        while (!stop) {
            int limit = this.getUnpruneLimit(nChildren, k);
            //System.out.print("[k: " + k + ", lim: " + limit + "]");
            stop = limit >= MAX_LIM;
            k++;
            if(!stop)
                System.out.print(limit + " ");
        }
        System.out.println();
    }

    public static void main(String args[])
    {
//        PortfolioMCTSParams params = new PortfolioMCTSParams();
//        for(int nChildren = 1; nChildren <= 50; nChildren++) {
//            params.printPruneLine(nChildren);
//        }


        double[] x = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] y = {0.0, 0.5, 1.0, 1.5, 2.0};
        LinearRegression lr = new LinearRegression(x, y);
        System.out.println(lr);
    }
}
