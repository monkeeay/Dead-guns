package com.example.roguelike;

import com.example.roguelike.graphics.Sprite; // Import Sprite
import java.awt.Color; // Import Color for default sprite

/**
 * Represents a basic game entity with a position and a sprite.
 */
public class Entity {
    protected int x;
    protected int y;
    protected Sprite sprite; // Added sprite field

    /**
     * Constructs an Entity with a default sprite.
     * @param x The initial x-coordinate (pixel).
     * @param y The initial y-coordinate (pixel).
     */
    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
        // Default sprite for an entity - can be overridden by subclasses
        this.sprite = new Sprite('?', Color.WHITE); 
    }

    /**
     * Constructs an Entity with a specific sprite.
     * @param x The initial x-coordinate (pixel).
     * @param y The initial y-coordinate (pixel).
     * @param sprite The sprite for this entity.
     */
    public Entity(int x, int y, Sprite sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
