package game;

import java.awt.Color;
import java.awt.Graphics2D;

public class Explosion {
    public int col, row;
    public int timer = 30;

    public Explosion(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public void update() {
        timer--;
    }

    public void draw(Graphics2D g2) {
        int screenX = col * GamePanel.TILE_SIZE;
        int screenY = row * GamePanel.TILE_SIZE;

        g2.setColor(Color.ORANGE);
        g2.fillRect(screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
    }
}