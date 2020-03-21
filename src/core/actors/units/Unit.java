package core.actors.units;

import core.Types;
import core.actors.Actor;
import utils.Vector2d;

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
    private int cityID;
    private boolean isKilled;

    private Types.TURN_STATUS status;


    public Unit(int atk, int def, int mov, int max_hp, int range, int cost, Vector2d pos, int kills, boolean isVeteran, int cityID, int tribeID){
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
        this.cityID = cityID;
        this.tribeId = tribeID;
        this.isKilled = false;
        this.status = Types.TURN_STATUS.FRESH;
    }

    public void setCurrentHP(int hp){
        currentHP = hp;
    }

    public void setMaxHP(int newHP) { maxHP = newHP; }

    public int getMaxHP() { return maxHP; }

    public int getCurrentHP(){
        return currentHP;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        this.kills++;
        //Persist skill
        if(getType() == Types.UNIT.KNIGHT) {
            this.status = Types.TURN_STATUS.ATTACKED;
        }
    }

    public boolean isVeteran() {
        return isVeteran;
    }

    public void setVeteran(boolean veteran) {
        isVeteran = veteran;
    }

    public int getCityID(){
        return cityID;
    }

    public abstract Types.UNIT getType();

    public Types.TURN_STATUS getStatus() { return status; }

    /**
     * Checks if the unit can transition to the status indicated by @param transition.
     * @param transition the status to transition to.
     * @return if the unit can transition to @param transition or not.
    */
    public boolean checkStatus(Types.TURN_STATUS transition) {
        switch (getType()) {
            //Either move or attack
            case MIND_BENDER:
            case CATAPULT:
            case DEFENDER:
                if(transition == Types.TURN_STATUS.MOVED && status == Types.TURN_STATUS.FRESH) { return true; }
                if(transition == Types.TURN_STATUS.ATTACKED && status == Types.TURN_STATUS.FRESH) { return true; }
                break;
            //Rules for Dash
            case ARCHER:
            case BATTLESHIP:
            case BOAT:
            case SHIP:
            case WARRIOR:
            case SWORDMAN:
                if(transition == Types.TURN_STATUS.MOVED && status == Types.TURN_STATUS.FRESH) { return true; }
                if(transition == Types.TURN_STATUS.ATTACKED && status == Types.TURN_STATUS.FRESH) { return true; }
                if(transition == Types.TURN_STATUS.ATTACKED && status == Types.TURN_STATUS.MOVED) { return true; }
                break;
            //Rules for Escape
            case RIDER:
                if(transition == Types.TURN_STATUS.MOVED && status == Types.TURN_STATUS.FRESH) { return true; }
                if(transition == Types.TURN_STATUS.MOVED && status == Types.TURN_STATUS.ATTACKED) { return true; }
                if(transition == Types.TURN_STATUS.MOVED && status == Types.TURN_STATUS.MOVED_AND_ATTACKED) { return true; }
                if(transition == Types.TURN_STATUS.ATTACKED && status == Types.TURN_STATUS.FRESH) { return true; }
                if(transition == Types.TURN_STATUS.ATTACKED && status == Types.TURN_STATUS.MOVED) { return true; }
                break;
            //Rules for Persist
            //Adding a kill for a knight resets its status to FRESH
            case KNIGHT:
                if(transition == Types.TURN_STATUS.MOVED && status == Types.TURN_STATUS.FRESH) { return true; }
                if(transition == Types.TURN_STATUS.ATTACKED && status == Types.TURN_STATUS.FRESH) { return true; }
                if(transition == Types.TURN_STATUS.ATTACKED && status == Types.TURN_STATUS.MOVED) { return true; }
                //A Knight can only have its status set to ATTACKED by addKill(). This 'special' status allows
                //a knight to attack again.
                if(transition == Types.TURN_STATUS.ATTACKED && status == Types.TURN_STATUS.ATTACKED) { return true; }
        }
        return false;
    }

    public void setStatus(Types.TURN_STATUS newStatus) {
        this.status = newStatus;
//        if(checkStatus(newStatus)) {
//            switch (getType()) {
//                case MIND_BENDER:
//                case CATAPULT:
//                case DEFENDER:
//                    this.status = Types.TURN_STATUS.FINISHED;
//                    break;
//                case ARCHER:
//                case BATTLESHIP:
//                case BOAT:
//                case SHIP:
//                case WARRIOR:
//                case SWORDMAN:
//                    if(newStatus == Types.TURN_STATUS.MOVED && getStatus() == Types.TURN_STATUS.FRESH) { this.status = Types.TURN_STATUS.MOVED; }
//                    if(newStatus == Types.TURN_STATUS.ATTACKED && getStatus() == Types.TURN_STATUS.FRESH) { this.status = Types.TURN_STATUS.FINISHED; }
//                    if(newStatus == Types.TURN_STATUS.ATTACKED && getStatus() == Types.TURN_STATUS.MOVED) { this.status = Types.TURN_STATUS.FINISHED; }
//                    break;
//                case RIDER:
//                    if(newStatus == Types.TURN_STATUS.MOVED && getStatus() == Types.TURN_STATUS.FRESH) { this.status = Types.TURN_STATUS.MOVED; }
//                    if(newStatus == Types.TURN_STATUS.MOVED && getStatus() == Types.TURN_STATUS.ATTACKED) { this.status = Types.TURN_STATUS.MOVED_AND_ATTACKED; }
//                    if(newStatus == Types.TURN_STATUS.MOVED && getStatus() == Types.TURN_STATUS.MOVED_AND_ATTACKED) { this.status = Types.TURN_STATUS.FINISHED; }
//                    if(newStatus == Types.TURN_STATUS.ATTACKED && getStatus() == Types.TURN_STATUS.FRESH) { this.status = Types.TURN_STATUS.ATTACKED; }
//                    if(newStatus == Types.TURN_STATUS.ATTACKED && getStatus() == Types.TURN_STATUS.MOVED) { this.status = Types.TURN_STATUS.MOVED_AND_ATTACKED; }
//                    break;
//                case KNIGHT:
//                    if(newStatus == Types.TURN_STATUS.MOVED && getStatus() == Types.TURN_STATUS.FRESH) { this.status = Types.TURN_STATUS.MOVED; }
//                    if(newStatus == Types.TURN_STATUS.ATTACKED && getStatus() == Types.TURN_STATUS.FRESH) { this.status = Types.TURN_STATUS.FINISHED; }
//                    if(newStatus == Types.TURN_STATUS.ATTACKED && getStatus() == Types.TURN_STATUS.MOVED) { this.status = Types.TURN_STATUS.FINISHED; }
//                    //A Knight can only have its status set to ATTACKED by addKill(). This 'special' status allows
//                    //a knight to attack again.
//                    if(newStatus == Types.TURN_STATUS.ATTACKED && getStatus() == Types.TURN_STATUS.ATTACKED) { this.status = Types.TURN_STATUS.FINISHED; }
//            }
//        }
    }

    public abstract Unit copy();

    public void setIsKilled(boolean isKilled){
        this.isKilled = isKilled;
    }

    public boolean getIsKilled(){
        return isKilled;
    }


}
