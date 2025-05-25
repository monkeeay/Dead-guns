package com.example.roguelike;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import com.example.roguelike.world.TileMap;
import com.example.roguelike.world.Tile;

/**
 * Responsible for rendering the game state, including the tile map and player.
 */
public class GamePanel extends JPanel {
    private Player player;
    private TileMap tileMap;

    /**
     * Constructs a GamePanel.
     * @param player The player entity.
     * @param tileMap The game's tile map.
     */
    public GamePanel(Player player, TileMap tileMap) {
        this.player = player;
        this.tileMap = tileMap;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call superclass method first

        // Draw the tile map
        if (tileMap != null) {
            for (int y = 0; y < tileMap.getHeight(); y++) {
                for (int x = 0; x < tileMap.getWidth(); x++) {
                    Tile tile = tileMap.getTile(x, y);
                    if (tile != null) {
                        g.setColor(tile.getColor());
                        g.fillRect(x * GameConfig.TILE_WIDTH, 
                                     y * GameConfig.TILE_HEIGHT, 
                                     GameConfig.TILE_WIDTH, 
                                     GameConfig.TILE_HEIGHT);
                    }
                }
            }
        }

        // Draw the player
        if (player != null && player.getSprite() != null) {
            // The player's x and y are the top-left of their bounding box.
            // The sprite's draw method should handle positioning the character within that box.
            player.getSprite().draw(g, player.getX(), player.getY());
        }
    }
}
