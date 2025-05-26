package com.example.roguelike.entities;

import java.awt.Color;

public class PlayerAppearance {
    private final char displayChar;
    private final Color foregroundColor;
    private final Color detailColor;

    public PlayerAppearance(char displayChar, Color foregroundColor, Color detailColor) {
        this.displayChar = displayChar;
        this.foregroundColor = foregroundColor;
        this.detailColor = detailColor;
    }

    public char getDisplayChar() {
        return displayChar;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public Color getDetailColor() {
        return detailColor;
    }
}
