package core.actors;

import core.TribesConfig;
import core.Types;

public class Temple extends Building
{
    private int level = 0;
    private int turnsToScore;

    public Temple(int x, int y, Types.BUILDING type) {
        super(x, y, type);
        levelUp();
    }

    private void levelUp()
    {
        level++;
        turnsToScore = TribesConfig.TEMPLE_TURNS_TO_SCORE;
    }

    public int score()
    {
        if (level < 5)
        {
            turnsToScore--;
            if(turnsToScore == 0)
            {
                levelUp();
                return TribesConfig.TEMPLE_POINTS[level-1];
            }
        }
        return 0;
    }


    public int getPoints()
    {
        int totPoints = 0;
        for(int i = 0; i < level; ++i)
            totPoints += TribesConfig.TEMPLE_POINTS[i];
        return totPoints;
    }

    public Building copy()
    {
        Temple t = new Temple(position.x, position.y, type);
        t.turnsToScore = this.turnsToScore;
        t.level = this.level;
        return t;
    }
}
