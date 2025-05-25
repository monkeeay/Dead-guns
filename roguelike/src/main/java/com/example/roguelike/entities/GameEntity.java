package com.example.roguelike.entities;

import com.example.roguelike.world.GameMap; // Required import

public interface GameEntity {
    int getX();
    int getY();
    void update(GameMap map); 

    // Combat related methods
    int getHealth();
    void takeDamage(int amount);
    boolean isAlive();

    // void render(Graphics g); // Rendering will be handled by GameRenderer
}
