package core.actors.units;

import core.Types;
import core.actors.Actor;
import utils.Vector2d;

import static core.Types.TURN_STATUS.*;

public abstract class Unit extends Actor
{
    public int ATK;
    public int DEF;
    public int MOV;

    public final int RANGE;
    public final int COST;

    private int maxHP;
    private int currentHP;

    private int kills;
    private boolean isVeteran;
    private int cityId;

    private Types.TURN_STATUS status;


    public Unit(int atk, int def, int mov, int max_hp, int range, int cost, Vector2d pos, int kills, boolean isVeteran, int cityId, int tribeID){
        this.ATK = atk;
        this.DEF = def;
        this.MOV = mov;
        this.maxHP = max_hp;
        this.RANGE = range;
        this.COST = cost;

        this.currentHP = this.maxHP;
        this.position = pos;
        this.kills = kills;
        this.isVeteran = isVeteran;
        this.cityId = cityId;
        this.tribeId = tribeID;
        this.status = FINISHED;
    }

    public void setCurrentHP(int hp){
        currentHP = hp;
    }

    public void setMaxHP(int newHP) { maxHP = newHP; }

    public int getMaxHP() { return maxHP; }

    public int getCurrentHP(){
        return currentHP;
    }

    public void setKills(int nKills) {this.kills = nKills;}

    public int getKills() {
        return kills;
    }

    public void addKill() {
        this.kills++;
        //Persist skill
        if(getType() == Types.UNIT.KNIGHT) {
            this.setStatus(ATTACKED);
        }
    }

    public boolean isVeteran() {
        return isVeteran;
    }

    public void setVeteran(boolean veteran) {
        isVeteran = veteran;
    }

    public int getCityId(){
        return cityId;
    }

    public void setCityId(int cityId) {this.cityId = cityId;}

    public abstract Types.UNIT getType();

    public Types.TURN_STATUS getStatus() { return status; }

    public void setStatus(Types.TURN_STATUS status) { this.status = status;}

    /**
     * Checks if the unit can transition to the status indicated by @param transition.
     * @param transition the status to transition to.
     * @return if the unit can transition to @param transition or not.
    */
    private boolean canTransitionTo(Types.TURN_STATUS transition) {

        if(status == FINISHED)
            return false;

        switch (getType()) {
            //Either move or attack
            case MIND_BENDER:
            case CATAPULT:
            case DEFENDER:
                if(transition == MOVED && status == FRESH) { return true; }
                if(transition == ATTACKED && status == FRESH) { return true; }
                return false;
            //Rules for Dash
            case ARCHER:
            case BATTLESHIP:
            case BOAT:
            case SHIP:
            case WARRIOR:
            case SWORDMAN:
            case SUPERUNIT:
                if(transition == MOVED && status == FRESH) { return true; }
                if(transition == ATTACKED && status == FRESH) { return true; }
                if(transition == ATTACKED && status == MOVED) { return true; }
                return false;
            //Rules for Escape
            case RIDER:
                if(transition == MOVED && status == FRESH) { return true; }
                if(transition == MOVED && status == ATTACKED) { return true; }
                if(transition == MOVED && status == MOVED_AND_ATTACKED) { return true; }
                if(transition == ATTACKED && status == FRESH) { return true; }
                if(transition == ATTACKED && status == MOVED) { return true; }
                break;
            //Rules for Persist
            //Adding a kill for a knight resets its status to FRESH
            case KNIGHT:
                if(transition == MOVED && status == FRESH) { return true; }
                if(transition == ATTACKED && status == FRESH) { return true; }
                if(transition == ATTACKED && status == MOVED) { return true; }
                //A Knight can only have its status set to ATTACKED by addKill(). This 'special' status allows
                //a knight to attack again.
                if(transition == ATTACKED && status == ATTACKED) { return true; }
        }
        return false;
    }

    public void transitionToStatus(Types.TURN_STATUS newStatus) {
        if(canTransitionTo(newStatus)) {
            switch (getType()) {
                case MIND_BENDER:
                case CATAPULT:
                case DEFENDER:
                    this.status = FINISHED;
                    break;
                case ARCHER:
                case BATTLESHIP:
                case BOAT:
                case SHIP:
                case WARRIOR:
                case SWORDMAN:
                case SUPERUNIT:
                    if(newStatus == MOVED && this.status == FRESH) { this.status = MOVED; }
                    if(newStatus == ATTACKED && this.status == FRESH) { this.status = FINISHED; }
                    if(newStatus == ATTACKED && this.status == MOVED) { this.status = FINISHED; }
                    break;
                case RIDER:
                    if(newStatus == MOVED && this.status == FRESH) { this.status = MOVED; }
                    if(newStatus == MOVED && this.status == ATTACKED) { this.status = MOVED_AND_ATTACKED; }
                    if(newStatus == MOVED && this.status == MOVED_AND_ATTACKED) { this.status = FINISHED; }
                    if(newStatus == ATTACKED && this.status == FRESH) { this.status = ATTACKED; }
                    if(newStatus == ATTACKED && this.status == MOVED) { this.status = MOVED_AND_ATTACKED; }
                    break;
                case KNIGHT:
                    if(newStatus == MOVED && this.status == FRESH) { this.status = MOVED; }
                    if(newStatus == ATTACKED && this.status == FRESH) { this.status = FINISHED; }
                    if(newStatus == ATTACKED && this.status == MOVED) { this.status = FINISHED; }
                    //A Knight can only have its status set to ATTACKED by addKill(). This 'special' status allows
                    //a knight to attack again.
                    if(newStatus == ATTACKED && this.status == ATTACKED) { this.status = FINISHED; }
            }
        }
    }
    
    public boolean canAttack()
    {
        return this.canTransitionTo(ATTACKED);
    }
    
    public boolean canMove()
    {
        return this.canTransitionTo(MOVED);
    }

    public boolean isFinished() {
        return this.status == FINISHED;
    }

    public boolean isFresh() {
        return this.status == FRESH;
    }


    public abstract Unit copy(boolean hideInfo);


    Unit hide()
    {
        this.cityId = -1;
        this.kills = 0;
        return this;
    }
}
