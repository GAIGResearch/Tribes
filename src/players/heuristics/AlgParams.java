package players.heuristics;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlgParams
{
    public final int STOP_TIME = 0;
    public final int STOP_ITERATIONS = 1;
    public final int STOP_FMCALLS = 2;

    public final int ENTROPY_HEURISTIC = 0;
    public final int SIMPLE_HEURISTIC = 1;
    public int heuristic_method = SIMPLE_HEURISTIC;

    public double epsilon = 1e-6;

    // Budget settings
    public int stop_type = STOP_TIME;
    public int num_iterations = 200;
    public int num_fmcalls = 2000;
    public int num_time = 40;
    public int FORCE_TURN_END = 5;


    public void setParameterValue(String param, Object value) { }
    public Object getParameterValue(String param) { return null; }
    public ArrayList<String> getParameters() { return null; }
    public Map<String, Object[]> getParameterValues() { return null; }
    public Pair<String, ArrayList<Object>> getParameterParent(String parameter) { return null; }
    public Map<Object, ArrayList<String>> getParameterChildren(String root) { return null; }

    public Map<String, String[]> constantNames() {
        HashMap<String, String[]> names = new HashMap<>();
        names.put("heuristic_method", new String[]{"SIMPLE_HEURISTIC", "ENTROPY_HEURISTIC"});
        return names;
    }

    public StateHeuristic getHeuristic(int playerID)
    {
        if (heuristic_method == ENTROPY_HEURISTIC)
            return new TribesEntropyHeuristic(playerID);
        else if (heuristic_method == SIMPLE_HEURISTIC) // New method: combined heuristics
            return new TribesSimpleHeuristic(playerID);
        return null;
    }

    public int getFORCE_TURN_END() {
        return FORCE_TURN_END;
    }
}
