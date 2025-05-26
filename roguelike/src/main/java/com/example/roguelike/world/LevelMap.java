package com.example.roguelike.world;

import java.awt.Graphics;
import java.util.List; // Added

public interface LevelMap {
    void render(Graphics g);
    boolean isPositionValid(int x, int y); // For collision detection or movement validation (grid coordinates)
    Tile getTileAt(int x, int y); // Grid coordinates
    int getGridWidth();
    int getGridHeight();
    List<com.example.roguelike.entities.Enemy> getEnemies();
    List<com.example.roguelike.items.WorldItem> getWorldItems(); // Added
    void removeItemFromWorld(com.example.roguelike.items.WorldItem worldItem); // Added
    // Add other methods as they become necessary
}
