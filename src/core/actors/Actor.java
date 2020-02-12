package core.actors;

public abstract class Actor {

    /**
     * Unique ID of this actor. It won't repeat through the game for any other.
     */
    protected int actorID;

    public abstract Actor copy();

    public void setActorID(int actorID)
    {
        this.actorID = actorID;
    }

    public int getActorID() {return actorID;}
}
