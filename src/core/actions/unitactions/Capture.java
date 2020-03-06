package core.actions.unitactions;

import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.City;
import core.actors.units.Unit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Capture extends UnitAction
{
    private City targetCity; // This can be a city or a village.

    public Capture(Unit invader)
    {
        super.unit = invader;
    }

    public void setTargetCity(City targetCity) {this.targetCity = targetCity;}
    public City getTargetCity() {
        return targetCity;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        // get city from board, check if action is feasible and add to list
        Board b = gs.getBoard();
        LinkedList<Action> captures = new LinkedList<>();
        City c = b.getCityInBorders(this.unit.getPosition().x, this.unit.getPosition().y);
        Capture capture = new Capture(this.unit);
        capture.setTargetCity(c);
        if(capture.isFeasible(gs)){
            captures.add(capture);
        }

        return captures;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        // If unit not in city, city belongs to the units tribe or if city is null then action is not feasible
        Board b = gs.getBoard();
        if(targetCity == null || b.getUnitAt(targetCity.getPosition().x,targetCity.getPosition().y) == null)
            return false;
        else if(targetCity.getPosition().x != unit.getPosition().x || targetCity.getPosition().y !=unit.getPosition().y)
            return false;
        else if(targetCity.getTribeId() == this.unit.getTribeId())
            return false;

        return true;
    }

    @Override
    public boolean execute(GameState gs) {
        // Change city tribe id to execute action
        Board b = gs.getBoard();
        Tribe t = b.getTribe(this.unit.getTribeId());
        Types.TERRAIN ter = gs.getBoard().getTerrainAt(targetCity.getPosition().x, targetCity.getPosition().y);
        boolean isVillage = ter == Types.TERRAIN.VILLAGE;
        boolean isCapture =  b.capture(t,this.targetCity.getPosition().x,this.targetCity.getPosition().y);
        if (isCapture && isVillage){
            City capital = (City) gs.getActor(t.getCapitalID());
            if (capital.getPopulation() > 0){
                capital.subtractPopulation(1);
            }else{
                ArrayList<Integer> citiesId = t.getCitiesID();
                citiesId.remove((Integer)t.getCapitalID());
                //TODO: Using Random
                Random r = new Random();
                while (citiesId.size() > 0){
                    int index = r.nextInt(citiesId.size());
                    City c = (City) gs.getActor(citiesId.get(index));
                    if (c.getPopulation() > 0){
                        c.subtractPopulation(1);
                        break;
                    }
                    citiesId.remove(index);
                }
                // TODO: Issue: What if every city's population is 0
            }
        }
        return isCapture;
    }
}
