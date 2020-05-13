package players.mcts;


import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class MCTSParams {


    public final int STOP_TIME = 0;
    public final int STOP_ITERATIONS = 1;
    public final int STOP_FMCALLS = 2;

    public final int ENTROPY_HEURISTIC = 0;
    public final int SIMPLE_HEURISTIC = 1;

    public double epsilon = 1e-6;

    // Parameters
    public double K = Math.sqrt(2);
    public int rollout_depth = 10;//10;
    public int heuristic_method = SIMPLE_HEURISTIC;

    // Budget settings
    public int stop_type = STOP_TIME;
    public int num_iterations = 200;
    public int num_fmcalls = 2000;
    public int num_time = 40;

    private int FORCE_TURN_END = 5;

    public void setParameterValue(String param, Object value) {
        switch(param) {
            case "K": K = (double) value; break;
            case "rollout_depth": rollout_depth = (int) value; break;
            case "heuristic_method": heuristic_method = (int) value; break;
        }
    }

    public Object getParameterValue(String param) {
        switch(param) {
            case "K": return K;
            case "rollout_depth": return rollout_depth;
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
        parameterValues.put("heuristic_method", new Integer[]{ENTROPY_HEURISTIC, SIMPLE_HEURISTIC});
        return parameterValues;
    }

    public Pair<String, ArrayList<Object>> getParameterParent(String parameter) {
        return null;  // No parameter dependencies
    }

    public Map<Object, ArrayList<String>> getParameterChildren(String root) {
        return new HashMap<>();  // No parameter dependencies
    }

    public Map<String, String[]> constantNames() {
        HashMap<String, String[]> names = new HashMap<>();
        names.put("heuristic_method", new String[]{"CUSTOM_HEURISTIC", "ADVANCED_HEURISTIC"});
        return names;
    }

    public int getFORCE_TURN_END() {
        return FORCE_TURN_END;
    }
}
