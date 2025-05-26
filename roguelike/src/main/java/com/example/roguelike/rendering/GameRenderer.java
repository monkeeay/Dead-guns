package com.example.roguelike.rendering;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.List;
import java.util.Random; 
import com.example.roguelike.entities.Player;
// import com.example.roguelike.entities.PlayerAppearance; // Old system
import com.example.roguelike.entities.Enemy;
import com.example.roguelike.entities.EnemyType; 
// import com.example.roguelike.entities.EnemyAppearance; // Old system, not used
import com.example.roguelike.items.Item; 
// import com.example.roguelike.items.WorldItem; // Removed for simplification
// import com.example.roguelike.world.Room; // GameMap will be used
import com.example.roguelike.rendering.ProceduralSpriteData; 
import com.example.roguelike.world.GameMap; 
import com.example.roguelike.world.Tile;
import com.example.roguelike.world.TileType; 
import com.example.roguelike.core.GameManager; // Added
import com.example.roguelike.core.GameState; // Added

public class GameRenderer extends JPanel {
    private GameManager gameManager; // Added
    private Player player;
    private GameMap gameMap;
    private List<Enemy> enemies;
    private List<Item> itemsOnMap; 
    // private boolean isGameOver = false; // Replaced by gameManager.getCurrentGameState()

    // Tile Constants
    public static final int TILE_WIDTH = 20;
    public static final int TILE_HEIGHT = 20;

    // Updated constructor to take GameManager
    public GameRenderer(GameManager manager, GameMap map, Player player, List<Enemy> enemies, List<Item> items) { 
        this.gameManager = manager;
        this.gameMap = map;
        this.player = player;
        this.enemies = enemies;
        this.itemsOnMap = items; 
    }

    // Method to update game data when a new game starts
    public void updateGameData(GameMap map, Player player, List<Enemy> enemies, List<Item> itemsOnMap) {
        this.gameMap = map;
        this.player = player;
        this.enemies = enemies;
        this.itemsOnMap = itemsOnMap;
    }

    // setGameOver is no longer needed directly, GameState handles it
    // public void setGameOver(boolean gameOver) {
    //     isGameOver = gameOver;
    // }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameState currentState = gameManager.getCurrentGameState();

        if (currentState == GameState.MAIN_MENU) {
            drawMainMenu(g);
        } else if (currentState == GameState.GAME_OVER) {
            drawGameOverScreen(g);
        } else if (currentState == GameState.PLAYING) {
            if (gameMap == null || player == null) { // Ensure game data is loaded for PLAYING state
                // Optionally draw a loading screen or return
                return;
            }
            // Existing rendering logic for PLAYING state
            drawPlayingScreen(g);
        }
    }

    private void drawPlayingScreen(Graphics g) {
        // 1. Draw the GameMap tiles
        for (int x = 0; x < gameMap.getMapWidthInTiles(); x++) {
            for (int y = 0; y < gameMap.getMapHeightInTiles(); y++) {
                Tile tile = gameMap.getTile(x, y);
                if (tile != null) {
                    switch (tile.getType()) {
                        case FLOOR:
                            g.setColor(Color.LIGHT_GRAY);
                            g.fillRect(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                            break;
                        case WALL:
                            g.setColor(Color.DARK_GRAY);
                            g.fillRect(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                            break;
                        case TREASURE_FLOOR: // Added case for Treasure Floor
                            g.setColor(new Color(255, 215, 0)); // Gold color
                            g.fillRect(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                            break;
                        case DOOR_CLOSED:
                            g.setColor(Color.ORANGE); 
                            g.fillRect(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                            g.setColor(Color.BLACK); 
                            g.drawString("+", x * TILE_WIDTH + TILE_WIDTH / 3, y * TILE_HEIGHT + (2 * TILE_HEIGHT) / 3);
                            break;
                        case DOOR_OPEN:
                            g.setColor(new Color(200, 150, 100)); 
                            g.fillRect(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                            g.setColor(Color.BLACK); 
                            g.drawString("'", x * TILE_WIDTH + TILE_WIDTH / 3, y * TILE_HEIGHT + (2 * TILE_HEIGHT) / 3); 
                            break;
                        case WATER:
                            g.setColor(Color.BLUE.darker());
                            g.fillRect(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                            break;
                        case TRAP_HIDDEN:
                            // Looks like floor (or the tile it was placed on). For simplicity, draw as FLOOR.
                            // If it was placed on TREASURE_FLOOR, this logic would need to be more complex
                            // or TRAP_HIDDEN tiles would need to store their original appearance.
                            g.setColor(Color.LIGHT_GRAY); // Same as FLOOR
                            g.fillRect(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                            break;
                        case TRAP_REVEALED:
                            g.setColor(Color.LIGHT_GRAY); // Base floor color
                            g.fillRect(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                            g.setColor(Color.RED);
                            g.drawString("X", x * TILE_WIDTH + TILE_WIDTH / 3, y * TILE_HEIGHT + (2 * TILE_HEIGHT) / 3); 
                            break;
                    }
                }
            }
        }
        
        // 2. Draw the Player
        if (player != null && player.getSpriteData() != null) {
            // Draw normal sprite
            ProceduralSpriteData spriteData = player.getSpriteData();
            int partWidth = TILE_WIDTH / ProceduralSpriteData.SPRITE_GRID_SIZE;
            int partHeight = TILE_HEIGHT / ProceduralSpriteData.SPRITE_GRID_SIZE;

            for (int py = 0; py < ProceduralSpriteData.SPRITE_GRID_SIZE; py++) {
                for (int px = 0; px < ProceduralSpriteData.SPRITE_GRID_SIZE; px++) {
                    Color partColor = spriteData.getColorAt(px, py);
                    if (partColor != null) {
                        g.setColor(partColor);
                        g.fillRect(player.getX() * TILE_WIDTH + px * partWidth,
                                   player.getY() * TILE_HEIGHT + py * partHeight,
                                   partWidth, partHeight);
                    }
                }
            }
            // Damage Flash for Player
            if (player.wasJustDamaged()) {
                g.setColor(new Color(255, 255, 255, 128)); // Semi-transparent white
                g.fillRect(player.getX() * TILE_WIDTH, player.getY() * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                player.setJustDamaged(false); // Reset the flag
            }
        }
        
        // Draw Enemies
        if (this.enemies != null) {
            for (Enemy enemy : this.enemies) {
                if (enemy.getSpriteData() != null) {
                    // Draw normal sprite
                    ProceduralSpriteData spriteData = enemy.getSpriteData();
                    int partWidth = TILE_WIDTH / ProceduralSpriteData.SPRITE_GRID_SIZE;
                    int partHeight = TILE_HEIGHT / ProceduralSpriteData.SPRITE_GRID_SIZE;

                    for (int py = 0; py < ProceduralSpriteData.SPRITE_GRID_SIZE; py++) {
                        for (int px = 0; px < ProceduralSpriteData.SPRITE_GRID_SIZE; px++) {
                            Color partColor = spriteData.getColorAt(px, py);
                            if (partColor != null) {
                                g.setColor(partColor);
                                g.fillRect(enemy.getX() * TILE_WIDTH + px * partWidth,
                                           enemy.getY() * TILE_HEIGHT + py * partHeight,
                                           partWidth, partHeight);
                            }
                        }
                    }
                    // Damage Flash for Enemy
                    if (enemy.wasJustDamaged()) {
                        g.setColor(new Color(255, 255, 255, 128)); // Semi-transparent white
                        g.fillRect(enemy.getX() * TILE_WIDTH, enemy.getY() * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                        enemy.setJustDamaged(false); // Reset the flag
                    }
                }
            }
        }

        // 3. Optional: Draw Grid Lines for debugging
        g.setColor(Color.GRAY); 
        for (int i = 0; i <= gameMap.getMapWidthInTiles(); i++) { 
            g.drawLine(i * TILE_WIDTH, 0, i * TILE_WIDTH, gameMap.getMapHeightInTiles() * TILE_HEIGHT);
        }
        for (int i = 0; i <= gameMap.getMapHeightInTiles(); i++) { 
            g.drawLine(0, i * TILE_HEIGHT, gameMap.getMapWidthInTiles() * TILE_WIDTH, i * TILE_HEIGHT);
        }

        // Draw Items on Map
        if (this.itemsOnMap != null) {
            g.setColor(Color.YELLOW); // Item color
            g.setFont(new Font("Arial", Font.BOLD, TILE_HEIGHT * 2/3)); // Adjust font size
            for (Item item : this.itemsOnMap) {
                // Center the symbol in the tile
                String itemSymbol = String.valueOf(item.getSymbol());
                FontMetrics fm = g.getFontMetrics();
                int charWidth = fm.stringWidth(itemSymbol);
                int charHeight = fm.getAscent() - fm.getDescent(); // More accurate height
                
                int drawX = item.getX() * TILE_WIDTH + (TILE_WIDTH - charWidth) / 2;
                int drawY = item.getY() * TILE_HEIGHT + (TILE_HEIGHT + charHeight) / 2 - fm.getDescent() ; 
                g.drawString(itemSymbol, drawX, drawY);
            }
        }

        // UI Elements (Health, Inventory) - Kept pixel-based, drawn on top.
        if (player != null) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            
            int defenseValue = player.getBaseDefense();
            if (player.getEquippedArmor() != null) {
                defenseValue += player.getEquippedArmor().getDefenseBonus();
            }
            String stats = "HP: " + player.getHealth() + "/" + player.getMaxHealth() + 
                           "  Atk: " + player.getAttackPower() + 
                           "  Def: " + defenseValue + 
                           "  Lvl: " + player.getCurrentLevel() + 
                           "  XP: " + player.getExperiencePoints() + "/" + player.getExperienceToNextLevel() +
                           "  Potions: " + player.getConsumablesInventory().size();
            
            int panelHeight = getHeight();
            g.drawString(stats, 10, panelHeight - 10); 
        }
    }

    private void drawMainMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(Color.WHITE);
        String title = "Roguelike Adventure";
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(title)) / 2;
        int y = getHeight() / 3;
        g.drawString(title, x, y);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String startMsg = "Press [S] or [Enter] to Start";
        x = (getWidth() - metrics.stringWidth(startMsg)) / 2 + metrics.stringWidth(title)/3 ; // Adjust for font change
        y += metrics.getHeight() * 2;
        g.drawString(startMsg, x, y);

        String exitMsg = "Press [X] or [Esc] to Exit";
        x = (getWidth() - metrics.stringWidth(exitMsg)) / 2 + metrics.stringWidth(title)/3; // Adjust for font change
        y += metrics.getHeight();
        g.drawString(exitMsg, x, y);
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(Color.RED);
        String title = "Game Over!";
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(title)) / 2;
        int y = getHeight() / 3;
        g.drawString(title, x, y);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        
        if (player != null) { // Player might be null if game over before initialization
            String levelMsg = "You reached Level: " + player.getCurrentLevel();
            x = (getWidth() - metrics.stringWidth(levelMsg)) / 2 + metrics.stringWidth(title)/3;
            y += metrics.getHeight() * 1.5;
            g.drawString(levelMsg, x, y);
        }

        String restartMsg = "Press [R] or [Enter] to Restart";
        x = (getWidth() - metrics.stringWidth(restartMsg)) / 2 + metrics.stringWidth(title)/3;
        y += metrics.getHeight() * 1.5;
        g.drawString(restartMsg, x, y);

        String menuMsg = "Press [M] or [Esc] to Main Menu";
        x = (getWidth() - metrics.stringWidth(menuMsg)) / 2 + metrics.stringWidth(title)/3;
        y += metrics.getHeight();
        g.drawString(menuMsg, x, y);
    }
}
