package core.game;

import core.Types;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Unit;
import utils.IO;
import utils.Vector2d;

import java.awt.*;

public class LevelLoader
{

    private Dimension size;

    public LevelLoader()
    {
        size = new Dimension();
    }

    /**
     * Builds a level, receiving a file name.
     * @param tribes tribes to play in this game
     * @param gamelvl file name containing the level.
     */
    public Board buildLevel(Tribe[] tribes, String gamelvl, long seed) {

        Board board = new Board();
        String[] lines = new IO().readFile(gamelvl);

        // Dimensions of the level read from the file.
        size.width = lines.length;
        size.height = lines.length;
        int tribeID = 0;
        int numTribes = tribes.length;

        board.init(size.width, tribes);

        //Go through evert token in the level file
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
                    if(tribeID==numTribes)
                    {
                        //If we've already allocated all the cities to the number of tribes, turn this
                        //extra city into a village.
                        terrainChar = Types.TERRAIN.VILLAGE.getMapChar();
                    }else
                    {
                        //A city to create. Add it and assign it to the next tribe.
                        City c = new City(i, j, tribeID);
                        board.addCityToTribe(c, tribeID);

                        //Also, each tribe starts with a unit in the same location where the city is
                        Types.UNIT unitType = tribes[tribeID].getType().getStartingUnit();
                        Unit unit = unitType.createUnit(new Vector2d(i,j), 0, false, c.getActorID(), tribeID, unitType);
                        board.addUnitToBoard(unit);

                        tribeID++;
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

        board.setCityBorders();
        return board;
    }


}
