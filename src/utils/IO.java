package utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 04/10/13
 * Time: 16:56
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class IO
{
    /**
     * Default constructor
     */
    public IO(){}

    /**
     * Reads a file and returns its content as a String[]
     * @param filename file to read
     * @return file content as String[], one line per element
     */
    public String[] readFile(String filename)
    {
        ArrayList<String> lines = new ArrayList<String>();
        try{
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
            in.close();
        }catch(Exception e)
        {
            System.out.println("Error reading the file " + filename + ": " + e.toString());
            e.printStackTrace();
            return null;
        }
        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Reads 'filename' and returns it as a JSON Object.
     * @param filename path to the file.
     * @return a JSON Object.
     */
    public JSONObject readJSON(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
            return new JSONObject(result);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}
