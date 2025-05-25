package com.example.roguelike.world;

public class RectangularRoom {
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public RectangularRoom(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getCenterX() {
        return this.x + this.width / 2;
    }

    public int getCenterY() {
        return this.y + this.height / 2;
    }

    // Check for intersection with another room
    // Add a small margin (e.g., 1 tile) to prevent rooms from touching
    public boolean intersects(RectangularRoom other) {
        return (this.x < other.x + other.width + 1 &&
                this.x + this.width + 1 > other.x &&
                this.y < other.y + other.height + 1 &&
                this.y + this.height + 1 > other.y);
    }
}
