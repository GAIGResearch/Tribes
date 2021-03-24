package utils.file;

import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 04/10/13
 * Time: 16:56
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
     * Writes in a file data passed as a String[]
     * @param filename file to write to.
     * @param lines content to write
     * @return true if all worked
     */
    public boolean writeFile(String filename, ArrayList<String> lines)
    {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename), false));
            for(String line : lines)
            {
                writer.write(line + "\n");
            }
            writer.close();
        }catch(Exception e)
        {
            System.out.println("Error writing the file " + filename + ": " + e.toString());
            e.printStackTrace();
            return false;
        }
        return true;
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
