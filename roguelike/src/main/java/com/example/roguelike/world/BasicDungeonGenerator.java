package com.example.roguelike.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Color; // Moved import to the top

public class BasicDungeonGenerator {

    private Random random = new Random();

    public DungeonGenerationResult generateLevel(int gridWidth, int gridHeight, int numRooms, int minRoomSize, int maxRoomSize) {
        Tile[][] tiles = new Tile[gridWidth][gridHeight];
        List<MapFeature> features = new ArrayList<>();

        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                // Initialize with WALLs, as VOID is removed from TileType for this subtask
                tiles[x][y] = new Tile(TileType.WALL); 
            }
        }

        List<RectangularRoom> rooms = new ArrayList<>();
        int maxRetries = 50; 

        for (int i = 0; i < numRooms; i++) {
            for (int retry = 0; retry < maxRetries; retry++) {
                int roomWidth = random.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;
                int roomHeight = random.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;
                int roomX = random.nextInt(gridWidth - roomWidth - 1) + 1; 
                int roomY = random.nextInt(gridHeight - roomHeight - 1) + 1;

                RectangularRoom newRoom = new RectangularRoom(roomX, roomY, roomWidth, roomHeight);

                boolean intersects = false;
                for (RectangularRoom existingRoom : rooms) {
                    if (newRoom.intersects(existingRoom)) {
                        intersects = true;
                        break;
                    }
                }

                if (!intersects) {
                    rooms.add(newRoom);
                    carveRoom(tiles, newRoom); // Pass tiles and newRoom
                    break; 
                }
            }
        }

        for (int i = 1; i < rooms.size(); i++) {
            RectangularRoom prevRoom = rooms.get(i - 1);
            RectangularRoom currentRoom = rooms.get(i);
            // Pass features list to carveCorridor
            carveCorridor(tiles, features, prevRoom.getCenterX(), prevRoom.getCenterY(), currentRoom.getCenterX(), currentRoom.getCenterY());
        }
        
        for (RectangularRoom room : rooms) {
            if (random.nextInt(100) < 30) { 
                int trapX = room.x + 1 + random.nextInt(Math.max(1, room.width - 2));
                int trapY = room.y + 1 + random.nextInt(Math.max(1, room.height - 2));
                if (trapX < gridWidth && trapY < gridHeight && tiles[trapX][trapY] != null && tiles[trapX][trapY].getType() == TileType.FLOOR) {
                    boolean featureExists = false;
                    for(MapFeature f : features) {
                        if(f.getX() == trapX && f.getY() == trapY) {
                            featureExists = true;
                            break;
                        }
                    }
                    if (!featureExists) {
                        features.add(new MapFeature(trapX, trapY, MapFeatureType.TRAP_ARMED, '^', '.', Color.YELLOW));
                    }
                }
            }
        }

        return new DungeonGenerationResult(tiles, rooms, features);
    }

    private void carveRoom(Tile[][] tiles, RectangularRoom room) {
        // Carve room, including walls, then floor inside
        for (int y = room.y; y < room.y + room.height; y++) {
            for (int x = room.x; x < room.x + room.width; x++) {
                if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length) {
                    if (x == room.x || x == room.x + room.width - 1 || y == room.y || y == room.y + room.height - 1) {
                        tiles[x][y] = new Tile(TileType.WALL); // Wall on the border
                    } else {
                        tiles[x][y] = new Tile(TileType.FLOOR); // Floor inside
                    }
                }
            }
        }
    }

    private void carveCorridor(Tile[][] tiles, List<MapFeature> features, int x1, int y1, int x2, int y2) {
        int currentX = x1;
        int currentY = y1;

        while (currentX != x2) {
            if (currentX < x2) currentX++; else currentX--;
            safeSetTileOrDoor(tiles, features, currentX, currentY, new Tile(TileType.FLOOR));
        }

        while (currentY != y2) {
            if (currentY < y2) currentY++; else currentY--;
            safeSetTileOrDoor(tiles, features, currentX, currentY, new Tile(TileType.FLOOR));
        }
    }
    
    // Simplified safeSetTileOrDoor based on the context that carveRoom creates WALLs
    private void safeSetTileOrDoor(Tile[][] tiles, List<MapFeature> features, int x, int y, Tile tileToCarve) {
        if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length) {
            if (tiles[x][y] != null && tiles[x][y].getType() == TileType.WALL) { // If corridor hits an existing wall
                // This is a potential door location. Add a DOOR feature.
                // Ensure no duplicate feature (e.g. if corridor segment retraces)
                boolean featureExists = false;
                for(MapFeature f : features) {
                    if(f.getX() == x && f.getY() == y && f.getType() == MapFeatureType.DOOR_CLOSED) {
                        featureExists = true;
                        break;
                    }
                }
                if(!featureExists) {
                    features.add(new MapFeature(x, y, MapFeatureType.DOOR_CLOSED, '+', '-', Color.ORANGE));
                }
                tiles[x][y] = new Tile(TileType.FLOOR); // Tile under the door is floor
            } else if (tiles[x][y] != null && tiles[x][y].getType() == TileType.WALL) { 
                // If carving into what was initially a WALL (or unexcavated space, now treated as WALL)
                tiles[x][y] = tileToCarve; // Usually Tile.FLOOR for corridors
            } else if (tiles[x][y] == null) { // If the tile was null
                 tiles[x][y] = tileToCarve;
            }
            // If it's already Tile.FLOOR (e.g. overlapping corridors), do nothing to change tile type.
        }
    }
} // This is the closing brace for the class BasicDungeonGenerator
