package core.actors;

import utils.Vector2d;

public abstract class Actor {

    /**
     * Unique ID of this actor. It won't repeat through the game for any other.
     */
    protected int actorId = -1;

    /**
     * Id of the tribe this actor belongs to.
     */
    protected int tribeId = -1;

    /**
     * Position of this actor in the board
     * @return
     */
    protected Vector2d position;


    public abstract Actor copy(boolean hideInfo);

    public void setActorId(int actorID)
    {
        this.actorId = actorID;
    }
    public int getActorId() {return actorId;}

    public void setTribeId(int tribeID) {this.tribeId = tribeID;}
    public int getTribeId() {return tribeId;}

    public void setPosition(int x, int y) {position = new Vector2d(x, y);}
    public Vector2d getPosition() {
        return position;
    }

}
