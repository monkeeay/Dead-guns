package com.example.roguelike.world;

import java.awt.Point;

/**
 * Represents a rectangle with integer coordinates and dimensions, used for room layout.
 * Coordinates and dimensions are in tile units.
 */
public class Rect {
    public int x, y, width, height;

    /**
     * Constructs a new Rect.
     * @param x The x-coordinate of the top-left corner (in tiles).
     * @param y The y-coordinate of the top-left corner (in tiles).
     * @param width The width of the rectangle (in tiles).
     * @param height The height of the rectangle (in tiles).
     */
    public Rect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Checks if this rectangle overlaps with another rectangle.
     * @param other The other rectangle to check for intersection.
     * @return True if the rectangles intersect, false otherwise.
     */
    public boolean intersects(Rect other) {
        // Check if one rectangle is to the left of the other
        if (this.x + this.width <= other.x || other.x + other.width <= this.x) {
            return false;
        }
        // Check if one rectangle is above the other
        if (this.y + this.height <= other.y || other.y + other.height <= this.y) {
            return false;
        }
        return true;
    }

    /**
     * Calculates the center point of the rectangle.
     * @return A Point object representing the center coordinates (in tiles).
     */
    public Point getCenter() {
        int centerX = this.x + this.width / 2;
        int centerY = this.y + this.height / 2;
        return new Point(centerX, centerY);
    }
}
