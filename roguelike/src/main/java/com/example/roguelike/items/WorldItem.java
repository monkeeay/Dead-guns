package com.example.roguelike.items;

public class WorldItem {
    private final int x;
    private final int y;
    private final Item item;

    public WorldItem(int x, int y, Item item) {
        this.x = x;
        this.y = y;
        this.item = item;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Item getItem() {
        return item;
    }
}
