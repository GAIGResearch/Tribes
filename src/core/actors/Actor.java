package core.actors;

public abstract class Actor {

    /**
     * Unique ID of this actor. It won't repeat through the game for any other.
     */
    protected int actorId;

    /**
     * Id of the tribe this actor belongs to.
     */
    protected int tribeId;


    public abstract Actor copy();

    public void setActorId(int actorID)
    {
        this.actorId = actorID;
    }
    public int getActorId() {return actorId;}

    public void setTribeId(int tribeID) {this.tribeId = tribeID;}
    public int getTribeId() {return tribeId;}

}
