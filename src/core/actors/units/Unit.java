package core.actors.units;

import core.Types;
import core.actors.Actor;
import utils.Vector2d;

public abstract class Unit extends Actor
{
    public final int ATK;
    public final int DEF;
    public final int MOV;

    public final int RANGE;
    public final int COST;

    private int maxHP;
    private float currentHP;
    private Vector2d currentPosition;
    private int kills;
    private boolean isVeteran;
    private int citeID;
    private int tribeID;

    public Unit(int atk, int def, int mov, int max_hp, int range, int cost, Vector2d pos, int kills, boolean isVeteran, int citeID, int tribeID){
        this.ATK = atk;
        this.DEF = def;
        this.MOV = mov;
        this.maxHP = max_hp;
        this.RANGE = range;
        this.COST = cost;

        this.currentHP = this.maxHP;
        this.currentPosition = pos;
        this.kills = kills;
        this.isVeteran = isVeteran;
        this.citeID = citeID;
        this.tribeID = tribeID;
    }

    public void setCurrentHP(float hp){
        currentHP = hp;
    }

    public void setMaxHP(int newHP) { maxHP = newHP; }

    public int getMaxHP() { return maxHP; }

    public float getCurrentHP(){
        return currentHP;
    }

    public void setCurrentPosition(Vector2d position){
        currentPosition = position;
    }

    public Vector2d getCurrentPosition(){
        return currentPosition;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        this.kills++;
    }

    public boolean isVeteran() {
        return isVeteran;
    }

    public void setVeteran(boolean veteran) {
        isVeteran = veteran;
    }

    public int getCiteID(){
        return citeID;
    }

    public int getTribeID(){
        return tribeID;
    }

    public abstract Types.UNIT getType();

    public abstract Unit copy();

}
