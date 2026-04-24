import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Player {

    GamePanel gp;
    KeyHandler keyH;

    public int x, y;
    public int speed;
    public Rectangle solidArea;

    int playerSize = 32;
    int offset = 8;

    int bombCooldown = 0;

    // NOWE: Status życia gracza
    public boolean isDead = false;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        setDefaultValues();
        solidArea = new Rectangle(offset, offset, playerSize, playerSize);
    }

    public void setDefaultValues() {
        x = 0;
        y = 0;
        speed = 4;
        bombCooldown = 0;
        isDead = false; // Na starcie gracz zawsze żyje
    }

    public void update() {
        // NOWE: Jeśli gracz nie żyje, całkowicie blokujemy jego akcje
        if (isDead) {
            return;
        }

        if (bombCooldown > 0) {
            bombCooldown--;
        }

        if (keyH.spacePressed && bombCooldown == 0 && gp.bombs.size() < 1) {
            int centerCol = (x + gp.tileSize / 2) / gp.tileSize;
            int centerRow = (y + gp.tileSize / 2) / gp.tileSize;

            gp.bombs.add(new Bomb(gp, centerCol, centerRow));
            bombCooldown = 30;
        }

        int nextX = x;
        int nextY = y;

        if (keyH.upPressed) nextY -= speed;
        if (keyH.downPressed) nextY += speed;
        if (keyH.leftPressed) nextX -= speed;
        if (keyH.rightPressed) nextX += speed;

        if (!checkCollision(nextX, y)) {
            x = nextX;
        }
        if (!checkCollision(x, nextY)) {
            y = nextY;
        }
    }

    private boolean checkCollision(int targetX, int targetY) {
        int leftX = targetX + solidArea.x;
        int rightX = targetX + solidArea.x + solidArea.width;
        int topY = targetY + solidArea.y;
        int bottomY = targetY + solidArea.y + solidArea.height;

        if (leftX < 0 || rightX > gp.screenWidth || topY < 0 || bottomY > gp.screenHeight) {
            return true;
        }

        int leftCol = leftX / gp.tileSize;
        int rightCol = (rightX - 1) / gp.tileSize;
        int topRow = topY / gp.tileSize;
        int bottomRow = (bottomY - 1) / gp.tileSize;

        int tileNum1 = gp.map[leftCol][topRow];
        int tileNum2 = gp.map[rightCol][topRow];
        int tileNum3 = gp.map[leftCol][bottomRow];
        int tileNum4 = gp.map[rightCol][bottomRow];

        return (tileNum1 != 0 || tileNum2 != 0 || tileNum3 != 0 || tileNum4 != 0);
    }

    public void draw(Graphics2D g2) {
        // NOWE: Zmiana koloru po śmierci
        if (isDead) {
            g2.setColor(Color.DARK_GRAY); // Spalony ludek
        } else {
            g2.setColor(Color.WHITE);     // Żywy ludek
        }

        g2.fillRect(x + offset, y + offset, playerSize, playerSize);
    }
}