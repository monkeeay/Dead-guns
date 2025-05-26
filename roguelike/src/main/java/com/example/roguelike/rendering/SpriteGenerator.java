package com.example.roguelike.rendering;

import com.example.roguelike.entities.EnemyType; // If enemy sprites vary significantly
import java.awt.Color;
import java.util.Random;

public class SpriteGenerator {
    public static ProceduralSpriteData generatePlayerSprite(long seed) {
        ProceduralSpriteData sprite = new ProceduralSpriteData();
        Random random = new Random(seed);
        
        Color primaryColor = new Color(random.nextInt(150) + 50, random.nextInt(150) + 50, random.nextInt(150) + 105); // Ensure some brightness
        Color accentColor = primaryColor.brighter();
        if (random.nextBoolean()) accentColor = primaryColor.darker();

        // Example: Simple symmetric pattern for player (humanoid-ish)
        // Body
        for(int y=1; y<4; y++) sprite.setColorAt(2, y, primaryColor); // Central column for body
        // Head
        sprite.setColorAt(2,0, accentColor); 
        // Arms (symmetric)
        sprite.setColorAt(1,1, primaryColor); 
        sprite.setColorAt(3,1, primaryColor);
        // Optionally, extend arms
        // sprite.setColorAt(0,2, primaryColor.darker()); 
        // sprite.setColorAt(4,2, primaryColor.darker());
        // Legs (symmetric)
        sprite.setColorAt(1,4, primaryColor); 
        sprite.setColorAt(3,4, primaryColor);
        
        return sprite;
    }

    public static ProceduralSpriteData generateEnemySprite(long seed, EnemyType type, Color baseColorHint) {
        ProceduralSpriteData sprite = new ProceduralSpriteData();
        Random random = new Random(seed);
        Color primaryColor = baseColorHint != null ? baseColorHint : new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color accentColor = primaryColor.darker();
        if (primaryColor.equals(accentColor)) accentColor = primaryColor.brighter(); // Ensure accent is different

        if (type == EnemyType.GRUNT) { // More blocky/solid
            for(int r=0; r<ProceduralSpriteData.SPRITE_GRID_SIZE; r++) {
                for(int c=0; c<ProceduralSpriteData.SPRITE_GRID_SIZE; c++) {
                    if (random.nextDouble() < 0.6) { // 60% chance to fill a part
                        sprite.setColorAt(c, r, primaryColor);
                    }
                }
            }
            // Add a central accent or "eye"
            sprite.setColorAt(2,2,accentColor);
            sprite.setColorAt(1,2,accentColor.brighter()); // Maybe some side accents
            sprite.setColorAt(3,2,accentColor.brighter());

        } else if (type == EnemyType.SCOUT) { // More sparse/agile looking, maybe V-shape or X-shape
            // Example: V-shape
            sprite.setColorAt(0,0, primaryColor);
            sprite.setColorAt(4,0, primaryColor);
            sprite.setColorAt(1,1, primaryColor.brighter());
            sprite.setColorAt(3,1, primaryColor.brighter());
            sprite.setColorAt(2,2, accentColor); // Central point
            // Add some more sparse elements
            sprite.setColorAt(1,3, primaryColor);
            sprite.setColorAt(3,3, primaryColor);

        }
        return sprite;
    }
}
