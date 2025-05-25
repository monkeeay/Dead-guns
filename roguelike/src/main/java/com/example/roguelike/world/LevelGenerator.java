package com.example.roguelike.world;

import com.example.roguelike.GameConfig;
import com.example.roguelike.Player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates procedural levels with rooms and corridors.
 */
public class LevelGenerator {

    private Random random = new Random();

    /**
     * Generates a new level consisting of rooms and corridors on the given TileMap.
     * Also places the player in the first generated room.
     *
     * @param tileMap The TileMap to generate the level on.
     * @param player  The Player to be placed in the level.
     */
    public void generateLevel(TileMap tileMap, Player player) {
        // Initialize the entire map with walls
        for (int x = 0; x < tileMap.getWidth(); x++) {
            for (int y = 0; y < tileMap.getHeight(); y++) {
                tileMap.setTile(x, y, Tile.WALL);
            }
        }

        List<Rect> placedRooms = new ArrayList<>();

        for (int i = 0; i < GameConfig.MAX_ROOMS; i++) {
            int roomWidth = random.nextInt(GameConfig.MAX_ROOM_SIZE - GameConfig.MIN_ROOM_SIZE + 1) + GameConfig.MIN_ROOM_SIZE;
            int roomHeight = random.nextInt(GameConfig.MAX_ROOM_SIZE - GameConfig.MIN_ROOM_SIZE + 1) + GameConfig.MIN_ROOM_SIZE;

            // Ensure room is within map boundaries (subtract 1 to keep a border wall)
            int roomX = random.nextInt(tileMap.getWidth() - roomWidth - 1) + 1;
            int roomY = random.nextInt(tileMap.getHeight() - roomHeight - 1) + 1;

            Rect newRoom = new Rect(roomX, roomY, roomWidth, roomHeight);

            boolean intersects = false;
            for (Rect existingRoom : placedRooms) {
                if (newRoom.intersects(existingRoom)) {
                    intersects = true;
                    break;
                }
            }

            if (!intersects) {
                placedRooms.add(newRoom);
                carveRoom(tileMap, newRoom);
            }
        }

        // Carve corridors between rooms
        for (int j = 1; j < placedRooms.size(); j++) {
            Point prevRoomCenter = placedRooms.get(j - 1).getCenter();
            Point currentRoomCenter = placedRooms.get(j).getCenter();
            carveCorridor(tileMap, prevRoomCenter.x, prevRoomCenter.y, currentRoomCenter.x, currentRoomCenter.y);
        }

        // Place player in the center of the first room
        if (!placedRooms.isEmpty()) {
            Rect firstRoom = placedRooms.get(0);
            Point playerStartTile = firstRoom.getCenter();

            // Convert tile coordinates to pixel coordinates for player
            player.setX(playerStartTile.x * GameConfig.TILE_WIDTH + GameConfig.TILE_WIDTH / 2 - GameConfig.PLAYER_SIZE / 2);
            player.setY(playerStartTile.y * GameConfig.TILE_HEIGHT + GameConfig.TILE_HEIGHT / 2 - GameConfig.PLAYER_SIZE / 2);
            
            // Ensure the player's starting tile is actually walkable (it should be if room was carved)
            if (tileMap.getTile(playerStartTile.x, playerStartTile.y) != Tile.FLOOR) {
                 // Fallback: if center is somehow not floor, find first floor tile in room
                for(int tx = firstRoom.x; tx < firstRoom.x + firstRoom.width; tx++){
                    for(int ty = firstRoom.y; ty < firstRoom.y + firstRoom.height; ty++){
                        if(tileMap.getTile(tx, ty) == Tile.FLOOR){
                            player.setX(tx * GameConfig.TILE_WIDTH + GameConfig.TILE_WIDTH / 2 - GameConfig.PLAYER_SIZE / 2);
                            player.setY(ty * GameConfig.TILE_HEIGHT + GameConfig.TILE_HEIGHT / 2 - GameConfig.PLAYER_SIZE / 2);
                            return;
                        }
                    }
                }
            }

        } else {
            // Fallback: If no rooms were generated, place player at a default position on a floor tile
            // This part might need adjustment if the map can be all walls.
            // For now, assuming at least one room or a default floor tile exists.
            // Or, we could try to find the first available floor tile.
            player.setX(GameConfig.TILE_WIDTH + GameConfig.TILE_WIDTH / 2 - GameConfig.PLAYER_SIZE / 2);
            player.setY(GameConfig.TILE_HEIGHT + GameConfig.TILE_HEIGHT / 2 - GameConfig.PLAYER_SIZE / 2);
            if(tileMap.getTile(1,1) == Tile.WALL) { // If default (1,1) is wall, make it floor
                 tileMap.setTile(1,1, Tile.FLOOR);
            }
        }
    }

    /**
     * Carves a room into the TileMap by setting its tiles to FLOOR.
     *
     * @param tileMap The TileMap to carve the room on.
     * @param room    The Rect representing the room to carve.
     */
    private void carveRoom(TileMap tileMap, Rect room) {
        for (int x = room.x; x < room.x + room.width; x++) {
            for (int y = room.y; y < room.y + room.height; y++) {
                // Ensure carving is within map bounds, though Rect generation should handle this
                if (x >= 0 && x < tileMap.getWidth() && y >= 0 && y < tileMap.getHeight()) {
                    tileMap.setTile(x, y, Tile.FLOOR);
                }
            }
        }
    }

    /**
     * Carves an L-shaped corridor between two points on the TileMap.
     *
     * @param tileMap The TileMap to carve the corridor on.
     * @param x1      The x-coordinate of the start point (tile units).
     * @param y1      The y-coordinate of the start point (tile units).
     * @param x2      The x-coordinate of the end point (tile units).
     * @param y2      The y-coordinate of the end point (tile units).
     */
    private void carveCorridor(TileMap tileMap, int x1, int y1, int x2, int y2) {
        // Carve horizontal segment first
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            if (x >= 0 && x < tileMap.getWidth() && y1 >= 0 && y1 < tileMap.getHeight()) {
                tileMap.setTile(x, y1, Tile.FLOOR);
            }
        }
        // Then carve vertical segment
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            if (x2 >= 0 && x2 < tileMap.getWidth() && y >= 0 && y < tileMap.getHeight()) {
                tileMap.setTile(x2, y, Tile.FLOOR);
            }
        }
    }
}
