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

    public void setStatus(Types.TURN_STATUS status) { this.status = status; }

    public abstract Unit copy();

}
