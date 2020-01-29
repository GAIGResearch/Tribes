package utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ImageIO {

    private static ImageIO imageIO = null;
    private static HashMap<String, Image> images;

    public static ImageIO GetInstance()
    {
        if (imageIO == null)
        {
            imageIO = new ImageIO();
        }
        return imageIO;
    }

    private ImageIO()
    {
        images = new HashMap<String, Image>();
    }


    public Image getImage(String image_file)
    {
        if(images.containsKey(image_file))
            return images.get(image_file);

        try {

            if((new File(image_file).exists())) {
                Image g = javax.imageio.ImageIO.read(new File(image_file));
                images.put(image_file, g);
                return g;
            }

        } catch (IOException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }

}
