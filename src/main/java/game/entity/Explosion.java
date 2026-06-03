package game.entity;

import game.core.GamePanel;

import java.awt.Color;
import java.awt.Graphics2D;

public class Explosion {

    public int col, row;
    public int timer = 30;
    public int maxTimer = 30;

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

        // Płynne wygaszanie płomieni - wyliczanie przezroczystości (Kanał Alpha)
        int alpha = (int)(((double)timer / maxTimer) * 255);
        if (alpha < 0) alpha = 0;
        if (alpha > 255) alpha = 255;

        // Zewnętrzny, ciemniejszy ogień
        g2.setColor(new Color(255, 69, 0, alpha));
        g2.fillRect(screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);

        // Środkowy, jasny ogień (odrobinę mniejszy)
        g2.setColor(new Color(255, 140, 0, alpha));
        g2.fillRect(screenX + 4, screenY + 4, GamePanel.TILE_SIZE - 8, GamePanel.TILE_SIZE - 8);

        // Gorący, żółty rdzeń eksplozji
        g2.setColor(new Color(255, 255, 0, alpha));
        g2.fillRect(screenX + 12, screenY + 12, GamePanel.TILE_SIZE - 24, GamePanel.TILE_SIZE - 24);
    }
}