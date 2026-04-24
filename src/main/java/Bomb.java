import java.awt.Color;
import java.awt.Graphics2D;

public class Bomb {
    GamePanel gp;
    public int col, row;
    public int timer = 180; // 3 sekundy (60 klatek * 3)

    public Bomb(GamePanel gp, int col, int row) {
        this.gp = gp;
        this.col = col;
        this.row = row;
    }

    public void update() {
        timer--;
    }

    public void draw(Graphics2D g2) {
        // Rysujemy bombę jako czerwone koło na środku kafelka
        int screenX = col * gp.tileSize;
        int screenY = row * gp.tileSize;

        g2.setColor(Color.RED);
        // Margines 8 pikseli, żeby bomba była trochę mniejsza niż kafelek
        g2.fillOval(screenX + 8, screenY + 8, gp.tileSize - 16, gp.tileSize - 16);
    }
}