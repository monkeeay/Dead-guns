package com.example.roguelike;

import javax.swing.JFrame;
import java.awt.Dimension;
import com.example.roguelike.world.TileMap;
import com.example.roguelike.world.LevelGenerator; // Import LevelGenerator

/**
 * Manages the game state and major components, including the procedurally generated tile map.
 */
public class Game {

    private Player player;
    private GamePanel gamePanel;
    private TileMap tileMap;
    private JFrame frame;
    private LevelGenerator levelGenerator; // Added LevelGenerator field

    public Game() {
        // Initialize game components
        int mapWidth = GameConfig.SCREEN_WIDTH / GameConfig.TILE_WIDTH;
        int mapHeight = GameConfig.SCREEN_HEIGHT / GameConfig.TILE_HEIGHT;
        this.tileMap = new TileMap(mapWidth, mapHeight); // Initialize TileMap first

        // Initialize Player with a temporary position, LevelGenerator will update it
        this.player = new Player(0, 0); 

        this.levelGenerator = new LevelGenerator(); // Initialize LevelGenerator
        this.levelGenerator.generateLevel(this.tileMap, this.player); // Generate level and place player

        // Initialize GamePanel after the map is generated and player is placed
        this.gamePanel = new GamePanel(this.player, this.tileMap);
        this.gamePanel.setPreferredSize(new Dimension(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT));

        // Setup the window
        setupWindow();
    }

    // Removed createSimpleRoom method as level generation is now handled by LevelGenerator

    private void setupWindow() {
        frame = new JFrame("Roguelike Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this.gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void startGameLoop() {
        while (true) {
            gamePanel.repaint();
            try {
                Thread.sleep(16); // Approximately 60 FPS
            } catch (InterruptedException e) {
                System.err.println("Game loop interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    // Getter for TileMap, needed by InputHandler
    public TileMap getTileMap() {
        return tileMap;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public JFrame getFrame() {
        return frame;
    }
}
