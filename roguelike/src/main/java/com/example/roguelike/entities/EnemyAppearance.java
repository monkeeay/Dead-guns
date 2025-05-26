package com.example.roguelike.entities;

import java.awt.Color;

public class EnemyAppearance {
    private final char displayChar;
    private final Color foregroundColor;

    public EnemyAppearance(char displayChar, Color foregroundColor) {
        this.displayChar = displayChar;
        this.foregroundColor = foregroundColor;
    }

    public char getDisplayChar() {
        return displayChar;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }
}
