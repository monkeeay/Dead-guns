package com.example.roguelike;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import com.example.roguelike.world.TileMap; // Required for TileMap

/**
 * Handles user input for controlling the player, interacting with the TileMap.
 */
public class InputHandler implements KeyListener {

    private Player player;
    private TileMap tileMap; // Replaces currentRoom with TileMap

    /**
     * Constructs an InputHandler.
     * @param player The player entity to control.
     * @param tileMap The game's tile map for collision detection.
     */
    public InputHandler(Player player, TileMap tileMap) {
        this.player = player;
        this.tileMap = tileMap;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used in this game
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // Pass the tileMap to the player's move method
        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
            player.move(0, -GameConfig.PLAYER_SPEED, tileMap);
        } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
            player.move(0, GameConfig.PLAYER_SPEED, tileMap);
        } else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            player.move(-GameConfig.PLAYER_SPEED, 0, tileMap);
        } else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            player.move(GameConfig.PLAYER_SPEED, 0, tileMap);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used in this game
    }

    /**
     * Attaches this KeyListener to the specified UI component.
     * @param component The component to listen for key events on (e.g., JFrame).
     */
    public void attach(java.awt.Component component) {
        component.addKeyListener(this);
        component.setFocusable(true); // Ensure the component can receive focus
        component.requestFocusInWindow(); // Request focus for the component
    }
}
