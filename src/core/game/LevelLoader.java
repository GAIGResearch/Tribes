package core.game;

import core.TribesConfig;
import core.Types;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Unit;
import utils.Vector2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

class LevelLoader
{
    private Dimension size;

    LevelLoader()
    {
        size = new Dimension();
    }

    /**
     * Builds a level, receiving a file name.
     * @param lines lines containing the level
     */
    Board buildLevel(String[] lines, Random rnd, GameState gs) {

        // Dimensions of the level read from the file.
        size.width = lines.length;
        size.height = lines.length;

        Tribe[] tribes = extractTribes(lines, gs.getTribesConfig());
        Board board = new Board();

        int tribeCounter = 0;
        int numTribes = tribes.length;

        board.init(size.width, tribes);

        //Go through every token in the level file
        for (int i = 0; i < size.height; ++i) {
            String line = lines[i];

            String[] tile = line.split(",");
            for(int j = 0; j < tile.length; ++j)
            {
                //Format <terrain_char>:[<resource_char>]
                // (<resource_char> is optional)
                // Retrieve the chars and assign the corresponding enum values in the board.
                String[] tileSplit = tile[j].split(":");
                char terrainChar = tileSplit[0].charAt(0);

                if(terrainChar == Types.TERRAIN.CITY.getMapChar())
                {
                    int tribeType = Integer.parseInt(tileSplit[1]);
                    int tribeID = -1;

                    for(Tribe t : tribes)
                    {
                        if(t.getType().getKey() == tribeType)
                            tribeID = t.getTribeId();
                    }

                    if(tribeCounter==numTribes)
                    {
                        //If we've already allocated all the cities to the number of tribes, turn this
                        //extra city into a village.
                        terrainChar = Types.TERRAIN.VILLAGE.getMapChar();
                    }else
                    {
                        //A city to create. Add it and assign it to the next tribe.
                        City c = new City(i, j, tribeID);
                        c.setCapital(true);
                        board.addCityToTribe(c,rnd, gs.getTribesConfig());

                        //Also, each tribe starts with a unit in the same location where the city is
                        Types.UNIT unitType = tribes[tribeID].getType().getStartingUnit();
                        Unit unit = Types.UNIT.createUnit(new Vector2d(i,j), 0, false, c.getActorId(), tribeID, unitType,gs);
                        board.addUnit(c, unit);

                        //City tiles
                        board.assignCityTiles(c, c.getBound(),gs.getTribesConfig());

                        tribeCounter++;
                    }
                }

                board.setTerrainAt(i,j, Types.TERRAIN.getType(terrainChar));

                if(tileSplit.length == 2)
                {
                    char resourceChar = tileSplit[1].charAt(0);
                    board.setResourceAt(i,j,Types.RESOURCE.getType(resourceChar));
                }
            }
        }
        return board;
    }

    /**
     * Extracts the tribes from the file lines initializing them
     * @param lines information
     * @return initialized array of tribes.
     */
    private Tribe[] extractTribes(String[] lines, TribesConfig tc)
    {
        ArrayList<Types.TRIBE> tribes_list = new ArrayList<>();
        for (int i = 0; i < size.height; ++i) {
            String line = lines[i];
            String[] tile = line.split(",");
            for (String s : tile) {
                String[] tileSplit = s.split(":");
                char terrainChar = tileSplit[0].charAt(0);
                boolean isCity = terrainChar == Types.TERRAIN.CITY.getMapChar();

                if (isCity) {
                    int tribeID = Integer.parseInt(tileSplit[1]);
                    tribes_list.add(Types.TRIBE.getTypeByKey(tribeID));
                }
            }
        }

        Tribe[] tribesArray = new Tribe[tribes_list.size()];
        int i = 0;
        for(Types.TRIBE trType : tribes_list)
        {
            tribesArray[i++] = new Tribe(trType,tc);
        }
        return tribesArray;
    }

}
