package com.example.roguelike;

import com.example.roguelike.world.TileMap;
import com.example.roguelike.graphics.Sprite; // Import Sprite
import java.awt.Color; // Import Color

/**
 * Represents the player entity in the game.
 * Handles player movement and collision with the tile-based map.
 */
public class Player extends Entity {

    public Player(int x, int y) {
        super(x, y);
        // Set a specific sprite for the Player
        this.sprite = new Sprite('@', Color.BLUE); 
    }

    /**
     * Moves the player by dx and dy, checking for collisions with the tile map.
     * The player's x and y coordinates are pixel-based.
     * Collision is checked based on the target tile the player is trying to move to.
     *
     * @param dx      The change in x-coordinate (pixels).
     * @param dy      The change in y-coordinate (pixels).
     * @param tileMap The map to check for walkable tiles.
     */
    public void move(int dx, int dy, TileMap tileMap) {
        // Calculate the new potential pixel position
        int newX = this.x + dx;
        int newY = this.y + dy;

        // Determine the tile coordinates of the new potential position.
        // This checks the top-left corner of the player's bounding box for simplicity.
        // More sophisticated collision detection might check all corners or the center.
        int targetTileX = newX / GameConfig.TILE_WIDTH;
        int targetTileY = newY / GameConfig.TILE_HEIGHT;
        
        // Check if the target tile for horizontal movement is walkable
        if (dx != 0) {
            int horizontalTargetTileX = (this.x + dx + (dx > 0 ? GameConfig.PLAYER_SIZE -1 : 0) ) / GameConfig.TILE_WIDTH;
            int currentTileYPlayerTop = this.y / GameConfig.TILE_HEIGHT;
            int currentTileYPlayerBottom = (this.y + GameConfig.PLAYER_SIZE -1) / GameConfig.TILE_HEIGHT;

            if (tileMap.getTile(horizontalTargetTileX, currentTileYPlayerTop).isWalkable() &&
                tileMap.getTile(horizontalTargetTileX, currentTileYPlayerBottom).isWalkable()) {
                this.x = newX; // Allow horizontal movement
            }
        }

        // Determine the tile coordinates of the new potential position for vertical movement.
        // Recalculate newX based on potentially updated this.x
        newX = this.x; // Use current x for y-movement check

        if (dy != 0) {
            int verticalTargetTileY = (this.y + dy + (dy > 0 ? GameConfig.PLAYER_SIZE -1 : 0)) / GameConfig.TILE_HEIGHT;
            int currentTileXPlayerLeft = this.x / GameConfig.TILE_WIDTH;
            int currentTileXPlayerRight = (this.x + GameConfig.PLAYER_SIZE -1) / GameConfig.TILE_WIDTH;
            
            if (tileMap.getTile(currentTileXPlayerLeft, verticalTargetTileY).isWalkable() &&
                tileMap.getTile(currentTileXPlayerRight, verticalTargetTileY).isWalkable()) {
                this.y = newY; // Allow vertical movement
            }
        }
    }
}
