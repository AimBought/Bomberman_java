import java.awt.Color;
import java.awt.Graphics2D;

public class Explosion {
    GamePanel gp;
    public int col, row;
    public int timer = 30; // Pół sekundy (60 klatek / 2)

    public Explosion(GamePanel gp, int col, int row) {
        this.gp = gp;
        this.col = col;
        this.row = row;
    }

    public void update() {
        timer--;
    }

    public void draw(Graphics2D g2) {
        // Rysujemy ogień jako żółto-pomarańczowy kwadrat
        int screenX = col * gp.tileSize;
        int screenY = row * gp.tileSize;

        g2.setColor(Color.ORANGE);
        g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
    }
}