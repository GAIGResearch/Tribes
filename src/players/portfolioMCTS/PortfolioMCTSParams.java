package players.portfolioMCTS;

import players.heuristics.AlgParams;
import players.portfolio.Portfolio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class PortfolioMCTSParams extends AlgParams {

    // Parameters
    public double K = Math.sqrt(2);
    public int ROLLOUT_LENGTH = 10;//10;
    public boolean ROLOUTS_ENABLED = true;
    private Portfolio portfolio;

    /**
     * Function that returns array of scripts to use
     */
    public Portfolio getPortfolio()
    {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio)
    {
        this.portfolio = portfolio;
    }

    public void setParameterValue(String param, Object value) {
        switch(param) {
            case "K": K = (double) value; break;
            case "ROLLOUT_LENGTH": ROLLOUT_LENGTH = (int) value; break;
            case "heuristic_method": heuristic_method = (int) value; break;
        }
    }

    public Object getParameterValue(String param) {
        switch(param) {
            case "K": return K;
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

}
