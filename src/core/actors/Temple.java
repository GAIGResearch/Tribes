package core.actors;

import core.TribesConfig;
import core.Types;
import org.json.JSONObject;

public class Temple extends Building
{
    //Level of this Temple
    private int level = 0;

    //Number of turns require to produce score for the tribe that owns this temple.
    private int turnsToScore;

    //TribesConfig Object
    private TribesConfig tc;

    /**
     * Creates a new temple
     * @param x x position for this temple
     * @param y y position for this temple.
     * @param type type of temple
     * @param cityId id of the city that owns this temple.
     */
    public Temple(int x, int y, Types.BUILDING type, int cityId, TribesConfig tc) {
        super(x, y, type, cityId,tc);
        this.tc = tc;
        levelUp();
    }

    /**
     * Creates a temple from a JSON object.
     * @param obj JSON object to read the temple details from.
     * @param cityID id of the city this temple belongs to.
     */
    public Temple(JSONObject obj, int cityID, TribesConfig tc){
        super(obj.getInt("x"), obj.getInt("y"), Types.BUILDING.getTypeByKey(obj.getInt("type")), cityID, tc);
        level = obj.getInt("level");
        turnsToScore = obj.getInt("turnsToScore");
    }

    /**
     * Levels the temple up, also resetting how many turns are needed for the next score up.
     */
    private void levelUp()
    {
        level++;
        turnsToScore = tc.TEMPLE_TURNS_TO_SCORE;
    }

    /**
     * Advances the turn and returns the score that this temple produces in its current state.
     * @return the score that this temple produces in its current state.
     */
    public int newTurn()
    {
        if (level < 5)
        {
            turnsToScore--;
            if(turnsToScore == 0)
            {
                levelUp();
                return tc.TEMPLE_POINTS[level-1];
            }
        }
        return 0;
    }

    /**
     * Indicates how many points this temple is worth.
     * @return the points this temple is worth.
     */
    int getPoints()
    {
        int totPoints = 0;
        for(int i = 0; i < level; ++i)
            totPoints += tc.TEMPLE_POINTS[i];
        return totPoints;
    }

    /**
     * Returns a copy of this temple
     * @return a copy of this temple
     */
    public Temple copy()
    {
        Temple t = new Temple(position.x, position.y, type, cityId, tc.copy());
        t.turnsToScore = this.turnsToScore;
        t.level = this.level;
        return t;
    }

    /* Getters */
    public int getLevel() {
        return level;
    }

    public int getTurnsToScore() {
        return turnsToScore;
    }

}
