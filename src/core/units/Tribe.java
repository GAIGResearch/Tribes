package core.units;

import core.Types;

public class Tribe {

    private Types.RESULT winner = Types.RESULT.INCOMPLETE;
    private int score = 0;

    //ID of this tribe. It corresponds with the ID of the player who controls it.
    private int tribeID;


    public Types.RESULT getWinner() {return winner;}
    public int getScore() {return score;}

    public void setTribeID(int tribeID) {
        this.tribeID = tribeID;
    }

    public int getTribeID() {
        return tribeID;
    }
}
