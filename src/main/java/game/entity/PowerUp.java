package game.entity;

import game.core.GamePanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class PowerUp {
    public int col, row;
    public int type;

    public PowerUp(int col, int row, int type) {
        this.col = col;
        this.row = row;
        this.type = type;
    }

    public void draw(Graphics2D g2) {
        int screenX = col * GamePanel.TILE_SIZE;
        int screenY = row * GamePanel.TILE_SIZE;
        int offset = 12;
        int size = GamePanel.TILE_SIZE - (offset * 2);

        if (type <= 2) {
            if (type == 0) g2.setColor(Color.MAGENTA);
            else if (type == 1) g2.setColor(Color.CYAN);
            else if (type == 2) g2.setColor(Color.GREEN);
            g2.fillRect(screenX + offset, screenY + offset, size, size);
        }
        else if (type >= 3 && type <= 5) {
            g2.setColor(Color.BLACK);
            g2.fillRect(screenX + offset, screenY + offset, size, size);

            if (type == 3) g2.setColor(Color.RED);
            else if (type == 4) g2.setColor(Color.BLUE);
            else if (type == 5) g2.setColor(Color.YELLOW);

            g2.drawRect(screenX + offset, screenY + offset, size, size);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("!", screenX + offset + 7, screenY + offset + 15);
        }
        else if (type == 6) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillArc(screenX + offset, screenY + offset, size, size, 0, 180);
        }
    }
}