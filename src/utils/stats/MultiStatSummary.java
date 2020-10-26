package utils.stats;

import java.util.HashMap;

public class MultiStatSummary
{
    private HashMap<String, StatSummary> variables;
    private Object owner;

    public MultiStatSummary(Object owner)
    {
        variables = new HashMap<>();
        this.owner = owner;
    }

    public void registerVariable(String key)
    {
        variables.put(key, new StatSummary(key));
    }

    public StatSummary getVariable(String key)
    {
        return variables.get(key);
    }

    public Object getOwner() {
        return owner;
    }
}
