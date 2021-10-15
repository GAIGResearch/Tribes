package utils.mapelites;

public class MapRecord
{
    private EliteRecord[][] map2d;
    private EliteRecord[][][] map3d;
    private boolean is3d;

    public MapRecord(Feature[] featuresInfo)
    {
        if(featuresInfo.length == 2)
            map2d = new EliteRecord[featuresInfo[0].featureArraySize()][featuresInfo[1].featureArraySize()];
        else if(featuresInfo.length == 3)
        {
            is3d = true;
            map3d = new EliteRecord[featuresInfo[0].featureArraySize()][featuresInfo[1].featureArraySize()][featuresInfo[2].featureArraySize()];
        }
    }

    public EliteRecord getCell(int[] coord)
    {
        if(coord.length == 2)
            return map2d[coord[0]][coord[1]];
        else if(coord.length == 3)
            return map3d[coord[0]][coord[1]][coord[2]];
        return null;
    }

    public void setCell(int[] coord, EliteRecord elite)
    {
        if(coord.length == 2)
            map2d[coord[0]][coord[1]] = elite;
        else if(coord.length == 3)
            map3d[coord[0]][coord[1]][coord[2]] = elite;
    }

    public void printData(String filename)
    {
        if(is3d) printData3D(filename);
        else printData2D(filename);
    }

    private void printData2D(String filename)
    {
        for (EliteRecord[] mapElite : map2d) {
            for (EliteRecord elite : mapElite) {
                if (elite != null) {
                    System.out.print(" X ");
                } else {
                    System.out.print(" - ");
                }
            }
            System.out.println("\n");
        }
        System.out.println("Elites info: ");
        for (int x = 0; x < map2d.length; x++) {
            for (int y = 0; y < map2d[x].length; y++) {
                if (map2d[x][y] != null) {
                    String coordStr = "(" + x + ", " + y + ")";
                    EliteRecord elite = map2d[x][y];
                    elite.printInfo(filename, coordStr);
                }
            }
        }
    }

    private void printData3D(String filename)
    {
        System.out.println("Elites info: ");
        for (int x = 0; x < map3d.length; x++) {
            for (int y = 0; y < map3d[x].length; y++) {
                for (int z = 0; z < map3d[x][y].length; z++) {
                    if (map3d[x][y][z] != null) {
                        String coordStr = "(" + x + ", " + y + ", " + z + ")";
                        EliteRecord elite = map3d[x][y][z];
                        elite.printInfo(filename, coordStr);
                    }
                }
            }
        }
    }

}
