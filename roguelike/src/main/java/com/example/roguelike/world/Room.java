package com.example.roguelike.world;

// No longer implements LevelMap or WorldElement, it's a simple data class for room definition.
public class Room {
    // x, y, width, height are now in tile coordinates/dimensions
    private int x;
    private int y;
    private int width;
    private int height;

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean intersects(Room other) {
        // Check if this room's rectangle intersects with other room's rectangle
        // Consider a 1-tile buffer to prevent rooms from touching directly (walls would overlap)
        return (this.x < other.x + other.width + 1 &&
                this.x + this.width + 1 > other.x &&
                this.y < other.y + other.height + 1 &&
                this.y + this.height + 1 > other.y);
    }
}
