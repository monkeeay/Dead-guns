package com.example.roguelike.world;

import java.util.List;

public record DungeonGenerationResult(Tile[][] tiles, List<RectangularRoom> rooms, List<MapFeature> features) {
}
