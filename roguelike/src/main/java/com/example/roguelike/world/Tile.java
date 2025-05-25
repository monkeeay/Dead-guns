package com.example.roguelike.world;

import java.awt.Color;

/**
 * Represents the different types of tiles in the game world.
 * Each tile has a color and a walkability property.
 */
public enum Tile {
    FLOOR(Color.LIGHT_GRAY, true),
    WALL(Color.DARK_GRAY, false),
    EMPTY(Color.BLACK, false);

    private final Color color;
    private final boolean walkable;

    Tile(Color color, boolean walkable) {
        this.color = color;
        this.walkable = walkable;
    }

    /**
     * Gets the color of the tile.
     * @return The color of the tile.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Checks if the tile is walkable.
     * @return True if the tile is walkable, false otherwise.
     */
    public boolean isWalkable() {
        return walkable;
    }
}
