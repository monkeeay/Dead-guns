package com.example.roguelike.rendering;
import java.awt.Color;

public class ProceduralSpriteData {
    public static final int SPRITE_GRID_SIZE = 5; // e.g., a 5x5 grid per tile
    private Color[][] gridColors; // Colors for each part of the 5x5 grid
    // Other potential fields: symmetry rules, base entity color for reference

    public ProceduralSpriteData() {
        gridColors = new Color[SPRITE_GRID_SIZE][SPRITE_GRID_SIZE];
        // Initialize with null or a transparent color
        for (int i = 0; i < SPRITE_GRID_SIZE; i++) {
            for (int j = 0; j < SPRITE_GRID_SIZE; j++) {
                gridColors[i][j] = null; // Explicitly null, meaning "off" or transparent
            }
        }
    }

    public void setColorAt(int x, int y, Color color) {
        if (x >= 0 && x < SPRITE_GRID_SIZE && y >= 0 && y < SPRITE_GRID_SIZE) {
            gridColors[x][y] = color;
        }
    }

    public Color getColorAt(int x, int y) {
        if (x >= 0 && x < SPRITE_GRID_SIZE && y >= 0 && y < SPRITE_GRID_SIZE) {
            return gridColors[x][y];
        }
        return null;
    }
}
