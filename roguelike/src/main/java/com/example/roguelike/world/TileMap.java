package com.example.roguelike.world;

/**
 * Represents the game map composed of tiles.
 */
public class TileMap {
    private Tile[][] tiles;
    private int width; // Map width in tiles
    private int height; // Map height in tiles

    /**
     * Constructs a new TileMap with the given dimensions.
     * Initializes all tiles to EMPTY by default.
     *
     * @param width  The width of the map in tiles.
     * @param height The height of the map in tiles.
     */
    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        // Initialize all tiles to EMPTY or a default pattern
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    tiles[x][y] = Tile.WALL; // Create a border of walls
                } else {
                    tiles[x][y] = Tile.FLOOR; // Fill the rest with floor
                }
            }
        }
    }

    /**
     * Gets the tile at the specified grid coordinates.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The Tile at the given coordinates, or Tile.EMPTY if out of bounds.
     */
    public Tile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y];
        }
        return Tile.EMPTY; // Return EMPTY for out-of-bounds requests
    }

    /**
     * Sets the tile at the specified grid coordinates.
     *
     * @param x    The x-coordinate of the tile.
     * @param y    The y-coordinate of the tile.
     * @param tile The Tile to set at the given coordinates.
     */
    public void setTile(int x, int y, Tile tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = tile;
        }
        // Optionally, log or throw an exception for out-of-bounds attempts
    }

    /**
     * Gets the width of the map in tiles.
     *
     * @return The width of the map.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the map in tiles.
     *
     * @return The height of the map.
     */
    public int getHeight() {
        return height;
    }
}
