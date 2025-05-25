package com.example.roguelike.world;

import com.example.roguelike.entities.Enemy;
import com.example.roguelike.items.Item;
import com.example.roguelike.items.ItemGenerator;
import com.example.roguelike.items.WorldItem;
import com.example.roguelike.rendering.GameRenderer; // Added for TILE_WIDTH/HEIGHT
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneratedLevel implements LevelMap {

    private Tile[][] tiles;
    private int gridWidth;
    private int gridHeight;
    private int playerStartX;
    private int playerStartY;
    private List<Enemy> enemies;
    private List<WorldItem> worldItems; // Added
    private Random random = new Random();

    public GeneratedLevel(int gridWidth, int gridHeight, int numRooms, int minRoomSize, int maxRoomSize) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.enemies = new ArrayList<>();
        this.worldItems = new ArrayList<>(); // Initialize worldItems list

        BasicDungeonGenerator generator = new BasicDungeonGenerator();
        DungeonGenerationResult result = generator.generateLevel(gridWidth, gridHeight, numRooms, minRoomSize, maxRoomSize);
        this.tiles = result.tiles();
        List<RectangularRoom> rooms = result.rooms();

        if (rooms.isEmpty()) {
            // Fallback: place player in center and a floor tile
            this.playerStartX = gridWidth / 2;
            this.playerStartY = gridHeight / 2;
            if (playerStartX >= 0 && playerStartX < gridWidth && playerStartY >= 0 && playerStartY < gridHeight) {
                tiles[playerStartX][playerStartY] = new Tile(TileType.FLOOR);
            }
        } else {
            // Player starts in the center of the first room
            RectangularRoom playerRoom = rooms.get(0);
            this.playerStartX = playerRoom.getCenterX();
            this.playerStartY = playerRoom.getCenterY();
            // Ensure player start is actually FLOOR (it should be by generator logic)
            if (playerStartX >= 0 && playerStartX < gridWidth && playerStartY >= 0 && playerStartY < gridHeight) { // Bounds check
                if (tiles[playerStartX][playerStartY] == null || tiles[playerStartX][playerStartY].getType() != TileType.FLOOR) {
                    tiles[playerStartX][playerStartY] = new Tile(TileType.FLOOR); // Force it if not
                }
            }


            // Spawn enemies and items in rooms
            for (int i = 0; i < rooms.size(); i++) {
                RectangularRoom room = rooms.get(i);
                // Skip player's starting room for enemies
                if (i > 0) { 
                    int enemiesToSpawn = random.nextInt(2) + 1; // 1 or 2 enemies
                    for (int j = 0; j < enemiesToSpawn; j++) {
                        int enemyX = room.x + 1 + random.nextInt(Math.max(1, room.width - 2));
                        int enemyY = room.y + 1 + random.nextInt(Math.max(1, room.height - 2));
                        if (enemyX >= 0 && enemyX < gridWidth && enemyY >= 0 && enemyY < gridHeight && // Bounds check
                            tiles[enemyX][enemyY] != null && tiles[enemyX][enemyY].getType() == TileType.FLOOR) {
                            // Modifying to satisfy new Enemy constructor for compilation purposes.
                            // Passing null for player as it's not available here.
                            enemies.add(new Enemy(enemyX, enemyY, random.nextLong(), null)); 
                        }
                    }
                }

                // Spawn items in all rooms (or some rooms, based on preference)
                // Let's try to spawn 1 item per room with a 50% chance
                if (random.nextBoolean()) {
                    int itemX = room.x + 1 + random.nextInt(Math.max(1, room.width - 2));
                    int itemY = room.y + 1 + random.nextInt(Math.max(1, room.height - 2));
                    if (itemX >= 0 && itemX < gridWidth && itemY >= 0 && itemY < gridHeight && // Bounds check
                        tiles[itemX][itemY] != null && tiles[itemX][itemY].getType() == TileType.FLOOR) {
                        // Ensure item does not spawn on player's start or where an enemy just spawned
                        boolean positionOccupied = (itemX == playerStartX && itemY == playerStartY);
                        for(Enemy e : enemies) {
                            if (e.getX() == itemX && e.getY() == itemY) {
                                positionOccupied = true;
                                break;
                            }
                        }
                        if (!positionOccupied) {
                             worldItems.add(new WorldItem(itemX, itemY, ItemGenerator.generateRandomItem()));
                        }
                    }
                }
            }
        }
    }
    
    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<WorldItem> getWorldItems() { // Added getter
        return worldItems;
    }

    public void removeItemFromWorld(WorldItem worldItem) { // Added method
        worldItems.remove(worldItem);
    }

    @Override
    public Tile getTileAt(int x, int y) {
        if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
            return tiles[x][y];
        }
        // Return a WALL tile for out-of-bounds, as VOID is removed from TileType
        return new Tile(TileType.WALL); 
    }

    @Override
    public int getGridWidth() {
        return gridWidth;
    }

    @Override
    public int getGridHeight() {
        return gridHeight;
    }

    @Override
    public boolean isPositionValid(int x, int y) {
        if (x < 0 || x >= gridWidth || y < 0 || y < gridHeight) {
            return false; // Out of bounds
        }
        Tile tile = tiles[x][y];
        // A tile is walkable if it's not null, and its type is FLOOR
        return tile != null && tile.getType() == TileType.FLOOR;
    }

    @Override
    public void render(Graphics g) {
        // This method is part of LevelMap interface.
        // Rendering is now primarily handled by GameRenderer using GameMap.
        // This implementation can be simplified or left empty if GeneratedLevel
        // is not directly rendered. For now, let's comment out the old logic
        // to avoid compilation errors with the new Tile structure.

        // int TILE_SIZE = GameRenderer.TILE_WIDTH; // Use GameRenderer constants
        // for (int i = 0; i < gridWidth; i++) {
        //     for (int j = 0; j < gridHeight; j++) {
        //         Tile tile = tiles[i][j];
        //         if (tile == null) continue;
                
        //         // Simplified rendering based on TileType for this deprecated method
        //         if (tile.getType() == TileType.FLOOR) {
        //             g.setColor(java.awt.Color.LIGHT_GRAY);
        //         } else if (tile.getType() == TileType.WALL) {
        //             g.setColor(java.awt.Color.DARK_GRAY);
        //         } else if (tile.getType() == TileType.VOID) {
        //             g.setColor(java.awt.Color.BLACK);
        //         }
        //         g.fillRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        //     }
        // }
    }

    public int getPlayerStartX() {
        return playerStartX;
    }

    public int getPlayerStartY() {
        return playerStartY;
    }
}
