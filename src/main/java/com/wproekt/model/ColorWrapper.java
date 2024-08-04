package com.wproekt.model;

import java.awt.*;

public class ColorWrapper {

    public int red;
    public int green;
    public int blue;

    private Color color;

    public ColorWrapper(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        color = new Color(red, green, blue);
    }

    public int getRed(){
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public ColorWrapper brighter(){
        return new ColorWrapper(color.brighter().getRed(),color.brighter().getGreen(),color.brighter().getBlue());
    }

    public ColorWrapper darker(){
        return new ColorWrapper(color.darker().getRed(),color.darker().getGreen(),color.darker().getBlue());
    }
}
