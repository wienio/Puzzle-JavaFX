package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.ImagePattern;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wienio on 2017-04-09.
 */
public class SubImage {
    static public List<Tile> getTiles(File file) {
        List<Tile> temporaryList = new ArrayList(9);
        int counter = 0 ;

        for (int i = 0 ; i < 3 ; ++i) {
            for (int j = 0 ; j < 3 ; ++j ) {
                BufferedImage bi = getImageFromFile(file);
                BufferedImage part = bi.getSubimage(i*140, j*140, 140, 140);
                Tile tileToSetOmWindow = new Tile(140, 140, part, counter++);
                tileToSetOmWindow.setFill(new ImagePattern(SwingFXUtils.toFXImage(tileToSetOmWindow.getPart(), null)));
                tileToSetOmWindow.setLayoutX(14 + 151*i);
                tileToSetOmWindow.setLayoutY(28 + 147*j);
                temporaryList.add(tileToSetOmWindow);
            }
        }
        return temporaryList;
    }

    static public BufferedImage getImageFromFile (File file) {
        try {
            BufferedImage bi = ImageIO.read(file);

            if (bi.getHeight() != 420 && bi.getWidth() != 420) {
                Image temp = bi.getScaledInstance(420,420, Image.SCALE_SMOOTH);
                BufferedImage bi2 = new BufferedImage(420,420, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = bi2.createGraphics();
                graphics.drawImage(temp,0,0,null);
                graphics.dispose();
                return bi2;
            }
            return bi;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
