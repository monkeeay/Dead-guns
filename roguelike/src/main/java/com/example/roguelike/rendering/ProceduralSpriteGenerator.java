package com.example.roguelike.rendering;

import com.example.roguelike.entities.PlayerAppearance;
import com.example.roguelike.entities.EnemyAppearance; // Corrected import
import java.awt.Color;
import java.util.Random;

public class ProceduralSpriteGenerator {
    private static final Random random = new Random();
    private static final char[] CHARS = {'@', 'P', '옷'};
    private static final Color[] FG_COLORS = {Color.CYAN, Color.GREEN, Color.YELLOW};
    private static final Color[] DETAIL_COLORS = {Color.RED, Color.MAGENTA, Color.ORANGE};

    // Enemy specific sprite options
    private static final char[] ENEMY_CHARS = {'e', 'g', 's', 'o', 'K'}; // orc, kobold
    private static final Color[] ENEMY_FG_COLORS = {Color.RED, new Color(0, 128, 0) /*dark green*/, Color.PINK, Color.ORANGE};


    public static PlayerAppearance generatePlayerAppearance() {
        char selectedChar = CHARS[random.nextInt(CHARS.length)];
        Color selectedFgColor = FG_COLORS[random.nextInt(FG_COLORS.length)];
        Color selectedDetailColor = DETAIL_COLORS[random.nextInt(DETAIL_COLORS.length)];
        return new PlayerAppearance(selectedChar, selectedFgColor, selectedDetailColor);
    }

    public static EnemyAppearance generateEnemyAppearance() {
        char selectedChar = ENEMY_CHARS[random.nextInt(ENEMY_CHARS.length)];
        Color selectedFgColor = ENEMY_FG_COLORS[random.nextInt(ENEMY_FG_COLORS.length)];
        return new EnemyAppearance(selectedChar, selectedFgColor);
    }
}
