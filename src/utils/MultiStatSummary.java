package utils;

import java.util.HashMap;

public class MultiStatSummary
{
    private HashMap<String, StatSummary> variables;

    public MultiStatSummary()
    {
        variables = new HashMap<>();
    }

    public void registerVariable(String key)
    {
        variables.put(key, new StatSummary(key));
    }

    public StatSummary getVariable(String key)
    {
        return variables.get(key);
    }

}
