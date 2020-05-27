package players.mc;

import players.heuristics.AlgParams;

public class MCParams extends AlgParams
{
    public int ROLLOUT_LENGTH = 20;
    public int N_ROLLOUT_MULT = 3;
    public boolean PRIORITIZE_ROOT = false;
}
