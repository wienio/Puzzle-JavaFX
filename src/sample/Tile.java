package sample;

import javafx.scene.shape.Rectangle;

import java.awt.image.BufferedImage;


/**
 * Created by Wienio on 2017-04-08.
 */
public class Tile extends Rectangle {
    private BufferedImage part;
    private int num;

    public BufferedImage getPart() {
        return part;
    }

    public void setPart(BufferedImage part) {
        this.part = part;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Tile(double width, double height, BufferedImage part, int num) {
        super(width,height);
        this.part=part;
        this.num=num;
    }

    public Tile (BufferedImage part, int num) {
        this.part=part;
        this.num = num;
    }
}
