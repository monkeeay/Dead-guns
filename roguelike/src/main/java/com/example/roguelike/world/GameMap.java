package com.example.roguelike.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.example.roguelike.rendering.GameRenderer; // For TILE_WIDTH/HEIGHT

public class GameMap {
    private Tile[][] tiles;
    private int mapWidthInTiles; // e.g., 800 / TILE_WIDTH
    private int mapHeightInTiles; // e.g., 600 / TILE_HEIGHT
    private List<Room> rooms; // Keep track of generated rooms

    // Constructor now takes pixel dimensions of the game area
    public GameMap(int gameAreaPixelWidth, int gameAreaPixelHeight) {
        this.mapWidthInTiles = gameAreaPixelWidth / GameRenderer.TILE_WIDTH;
        this.mapHeightInTiles = gameAreaPixelHeight / GameRenderer.TILE_HEIGHT;
        this.tiles = new Tile[mapWidthInTiles][mapHeightInTiles];
        this.rooms = new ArrayList<>();
        initializeMap();
        generateRoomsAndCorridors();
        placeEnvironmentalFeatures(new Random()); // Added call
    }

    private void initializeMap() {
        for (int x = 0; x < mapWidthInTiles; x++) {
            for (int y = 0; y < mapHeightInTiles; y++) {
                tiles[x][y] = new Tile(TileType.WALL); // Start with all walls
            }
        }
    }

    private void generateRoomsAndCorridors() {
        Random random = new Random();
        int numberOfRooms = 5; // Generate a few rooms
        int minRoomSize = 3;
        int maxRoomSize = 7;

        for (int i = 0; i < numberOfRooms; i++) {
            int roomWidth = random.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;
            int roomHeight = random.nextInt(maxRoomSize - minRoomSize + 1) + minRoomSize;
            int roomX = random.nextInt(mapWidthInTiles - roomWidth - 1) + 1; 
            int roomY = random.nextInt(mapHeightInTiles - roomHeight - 1) + 1;

            Room newRoomDetails = new Room(roomX, roomY, roomWidth, roomHeight); 

            // Check for overlap with existing rooms using the bounding box
            boolean overlaps = false;
            for (Room existingRoom : rooms) {
                if (newRoomDetails.intersects(existingRoom)) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) {
                // Randomly choose room type
                double roomTypeRoll = random.nextDouble();
                if (roomTypeRoll < 0.25) { // 25% chance for circular
                    createCircularRoomOnMap(newRoomDetails);
                } else if (roomTypeRoll < 0.50) { // 25% chance for cross-shaped (total 50% non-rectangular)
                    createCrossShapedRoomOnMap(newRoomDetails, random);
                } else { // 50% chance for rectangular
                    createRectangularRoomOnMap(newRoomDetails);
                }
                
                // Decide if this room should be special (e.g., first room, or random chance)
                if (rooms.isEmpty() || random.nextDouble() < 0.2) { // 20% chance for subsequent rooms
                    if (random.nextBoolean()) {
                        makeRoomSpecial(newRoomDetails, "treasure", random);
                    } else {
                        makeRoomSpecial(newRoomDetails, "fountain", random);
                    }
                }

                if (!rooms.isEmpty()) {
                    connectRooms(rooms.get(rooms.size() - 1), newRoomDetails);
                }
                rooms.add(newRoomDetails);
            }
        }
    }

    // Renamed from createRoomOnMap for clarity
    private void createRectangularRoomOnMap(Room room) {
        for (int x = room.getX(); x < room.getX() + room.getWidth(); x++) {
            for (int y = room.getY(); y < room.getY() + room.getHeight(); y++) {
                if (x >= 0 && x < mapWidthInTiles && y >= 0 && y < mapHeightInTiles) {
                   tiles[x][y].setType(TileType.FLOOR); // Use setType as tiles are pre-initialized
                }
            }
        }
    }

    private void createCircularRoomOnMap(Room roomDetails) {
        int centerX = roomDetails.getX() + roomDetails.getWidth() / 2;
        int centerY = roomDetails.getY() + roomDetails.getHeight() / 2;
        int radius = Math.min(roomDetails.getWidth(), roomDetails.getHeight()) / 2;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                if (x >= 0 && x < mapWidthInTiles && y >= 0 && y < mapHeightInTiles) {
                    if ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) <= radius * radius) {
                        tiles[x][y].setType(TileType.FLOOR);
                    }
                }
            }
        }
    }

    private void createCrossShapedRoomOnMap(Room roomDetails, Random random) {
        int centerX = roomDetails.getX() + roomDetails.getWidth() / 2;
        int centerY = roomDetails.getY() + roomDetails.getHeight() / 2;
        int armLength = Math.min(roomDetails.getWidth(), roomDetails.getHeight()) / 2; // Length of each arm from center
        int armThickness = Math.max(1, Math.min(roomDetails.getWidth(), roomDetails.getHeight()) / 3); // Ensure thickness is at least 1

        // Horizontal arm
        for (int x = centerX - armLength; x <= centerX + armLength; x++) {
            for (int yOffset = -armThickness / 2; yOffset <= armThickness / 2; yOffset++) {
                int currentY = centerY + yOffset;
                if (x >= 0 && x < mapWidthInTiles && currentY >= 0 && currentY < mapHeightInTiles) {
                    tiles[x][currentY].setType(TileType.FLOOR);
                }
            }
        }
        // Vertical arm
        for (int y = centerY - armLength; y <= centerY + armLength; y++) {
            for (int xOffset = -armThickness / 2; xOffset <= armThickness / 2; xOffset++) {
                int currentX = centerX + xOffset;
                if (currentX >= 0 && currentX < mapWidthInTiles && y >= 0 && y < mapHeightInTiles) {
                    tiles[currentX][y].setType(TileType.FLOOR);
                }
            }
        }
    }
    
    private void makeRoomSpecial(Room room, String featureType, Random random) {
        if ("fountain".equals(featureType)) {
            int centerX = room.getX() + room.getWidth() / 2;
            int centerY = room.getY() + room.getHeight() / 2;
            // Ensure fountain is within room bounds and on floor
            if (room.getWidth() >=2 && room.getHeight() >= 2) { // Need at least 2x2 for a simple fountain
                 for (int dx = 0; dx < 2; dx++) {
                    for (int dy = 0; dy < 2; dy++) {
                        int tileX = centerX -1 + dx;
                        int tileY = centerY -1 + dy;
                         if (tileX >= room.getX() && tileX < room.getX() + room.getWidth() &&
                            tileY >= room.getY() && tileY < room.getY() + room.getHeight() &&
                            tileX < mapWidthInTiles && tileY < mapHeightInTiles && tiles[tileX][tileY].getType() == TileType.FLOOR) {
                            tiles[tileX][tileY].setType(TileType.WALL);
                        }
                    }
                }
            }
        } else if ("treasure".equals(featureType)) {
            for (int x = room.getX(); x < room.getX() + room.getWidth(); x++) {
                for (int y = room.getY(); y < room.getY() + room.getHeight(); y++) {
                    if (x >= 0 && x < mapWidthInTiles && y >= 0 && y < mapHeightInTiles) {
                        // Only change actual floor tiles of the room, not surrounding walls if room is not rectangular
                        if (tiles[x][y].getType() == TileType.FLOOR) { 
                            tiles[x][y].setType(TileType.TREASURE_FLOOR);
                        }
                    }
                }
            }
        }
    }


    private void connectRooms(Room room1, Room room2) {
        Random random = new Random();
        int r1centerX = room1.getX() + room1.getWidth() / 2;
        int r1centerY = room1.getY() + room1.getHeight() / 2;
        int r2centerX = room2.getX() + room2.getWidth() / 2;
        int r2centerY = room2.getY() + room2.getHeight() / 2;

        // Randomly decide to carve H then V, or V then H
        if (random.nextBoolean()) {
            carveHorizontalCorridor(r1centerX, r2centerX, r1centerY);
            carveVerticalCorridor(r1centerY, r2centerY, r2centerX);
        } else {
            carveVerticalCorridor(r1centerY, r2centerY, r1centerX);
            carveHorizontalCorridor(r1centerX, r2centerX, r2centerY);
        }
    }

    private void carveHorizontalCorridor(int xFrom, int xTo, int y) {
        int startX = Math.min(xFrom, xTo);
        int endX = Math.max(xFrom, xTo);
        for (int x = startX; x <= endX; x++) {
            if (x >= 0 && x < mapWidthInTiles && y >= 0 && y < mapHeightInTiles) {
                if (x == xTo) { // Endpoint of this segment
                    if (tiles[x][y] != null && tiles[x][y].getType() == TileType.WALL) {
                        tiles[x][y].setType(TileType.DOOR_CLOSED);
                    } else { // If it's not a wall (e.g. already floor from another room/corridor), make it floor
                        tiles[x][y] = new Tile(TileType.FLOOR);
                    }
                } else { // Intermediate part of corridor
                    tiles[x][y] = new Tile(TileType.FLOOR);
                }
            }
        }
    }

    private void carveVerticalCorridor(int yFrom, int yTo, int x) {
        int startY = Math.min(yFrom, yTo);
        int endY = Math.max(yFrom, yTo);
        for (int y = startY; y <= endY; y++) {
             if (x >= 0 && x < mapWidthInTiles && y >= 0 && y < mapHeightInTiles) {
                if (y == yTo) { // Endpoint of this segment
                    if (tiles[x][y] != null && tiles[x][y].getType() == TileType.WALL) {
                        tiles[x][y].setType(TileType.DOOR_CLOSED);
                    } else {
                        tiles[x][y] = new Tile(TileType.FLOOR);
                    }
                } else { // Intermediate part of corridor
                    tiles[x][y] = new Tile(TileType.FLOOR);
                }
            }
        }
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < mapWidthInTiles && y >= 0 && y < mapHeightInTiles) {
            return tiles[x][y];
        }
        return null; // Or a default non-walkable tile
    }

    public boolean isWalkable(int x, int y) {
        Tile tile = getTile(x, y);
        return tile != null && tile.isWalkable();
    }

    public int getMapWidthInTiles() { return mapWidthInTiles; }
    public int getMapHeightInTiles() { return mapHeightInTiles; }
    public List<Room> getRooms() { return rooms; } // For placing player

    private void placeEnvironmentalFeatures(Random random) {
        // Place Water Patches
        int numberOfWaterPatches = 3 + random.nextInt(3); // 3-5 patches
        for (int i = 0; i < numberOfWaterPatches; i++) {
            if (rooms.isEmpty()) break;
            Room targetRoom = rooms.get(random.nextInt(rooms.size()));
            int patchSize = 2 + random.nextInt(2); // 2x2 or 3x3
            int startX = targetRoom.getX() + random.nextInt(Math.max(1, targetRoom.getWidth() - patchSize +1));
            int startY = targetRoom.getY() + random.nextInt(Math.max(1, targetRoom.getHeight() - patchSize+1));

            for (int x = startX; x < startX + patchSize; x++) {
                for (int y = startY; y < startY + patchSize; y++) {
                    if (x >= 0 && x < mapWidthInTiles && y >= 0 && y < mapHeightInTiles &&
                        tiles[x][y].getType() == TileType.FLOOR) { // Only replace floor
                        tiles[x][y] = new Tile(TileType.WATER);
                    }
                }
            }
        }

        // Place Traps
        int numberOfTraps = 5 + random.nextInt(6); // 5-10 traps
        for (int i = 0; i < numberOfTraps; i++) {
            if (rooms.isEmpty()) break;
            Room targetRoom = rooms.get(random.nextInt(rooms.size()));
            int trapX = targetRoom.getX() + random.nextInt(targetRoom.getWidth());
            int trapY = targetRoom.getY() + random.nextInt(targetRoom.getHeight());

            // Ensure not on player start (player starts in first room center)
            // This check is simplified; a more robust check would involve GameManager passing player start.
            boolean isPlayerStart = false;
            if (!rooms.isEmpty() && targetRoom == rooms.get(0)) {
                if (trapX == rooms.get(0).getX() + rooms.get(0).getWidth()/2 &&
                    trapY == rooms.get(0).getY() + rooms.get(0).getHeight()/2) {
                    isPlayerStart = true;
                }
            }
            
            if (!isPlayerStart && trapX < mapWidthInTiles && trapY < mapHeightInTiles &&
                (tiles[trapX][trapY].getType() == TileType.FLOOR || tiles[trapX][trapY].getType() == TileType.TREASURE_FLOOR)) {
                tiles[trapX][trapY] = new Tile(TileType.TRAP_HIDDEN);
            }
        }
    }
}
