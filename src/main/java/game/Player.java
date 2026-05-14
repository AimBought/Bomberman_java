package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Player {

    GamePanel gp;
    KeyHandler.PlayerInput input;

    public int id;
    public Color color;
    public int x, y;

    int drawSize = 32;
    int drawOffset = 8;

    public Rectangle solidArea;

    int bombCooldown = 0;
    public boolean isDead = false;

    public int speed;
    public int maxBombs;
    public int bombPower;
    public int bombsPlaced = 0;

    public int armorCount = 0;
    public int invincibleTimer = 0;

    public int curseTimer = 0;
    public int activeCurse = -1;
    public int lastDir = 2;

    public Player(GamePanel gp, KeyHandler.PlayerInput input, int id, Color color, int startCol, int startRow) {
        this.gp = gp;
        this.input = input;
        this.id = id;
        this.color = color;
        this.x = startCol * GamePanel.TILE_SIZE;
        this.y = startRow * GamePanel.TILE_SIZE;

        setDefaultValues();
        solidArea = new Rectangle(10, 10, 28, 28);
    }

    public void setDefaultValues() {
        speed = 4;
        maxBombs = 1;
        bombPower = 1;
        bombCooldown = 0;
        bombsPlaced = 0;
        isDead = false;
        armorCount = 0;
        invincibleTimer = 0;
        curseTimer = 0;
        activeCurse = -1;
    }

    public void update() {
        if (isDead) return;

        if (bombCooldown > 0) bombCooldown--;
        if (invincibleTimer > 0) invincibleTimer--;

        if (curseTimer > 0) curseTimer--;
        else activeCurse = -1;

        boolean up = input.up;
        boolean down = input.down;
        boolean left = input.left;
        boolean right = input.right;
        boolean bomb = input.bomb;

        if (activeCurse == 3) {
            up = input.down; down = input.up; left = input.right; right = input.left;
        }
        else if (activeCurse == 4) {
            if (up) lastDir = 1; else if (down) lastDir = 2; else if (left) lastDir = 3; else if (right) lastDir = 4;
            else {
                if (lastDir == 1) up = true; if (lastDir == 2) down = true; if (lastDir == 3) left = true; if (lastDir == 4) right = true;
            }
        }
        else if (activeCurse == 5) {
            bomb = false;
        }

        if (bomb && bombCooldown == 0 && bombsPlaced < maxBombs) {
            int centerCol = (x + GamePanel.TILE_SIZE / 2) / GamePanel.TILE_SIZE;
            int centerRow = (y + GamePanel.TILE_SIZE / 2) / GamePanel.TILE_SIZE;
            // ZMIANA: Bomba nie potrzebuje już gp w parametrze
            gp.bombs.add(new Bomb(centerCol, centerRow, this));
            bombsPlaced++;
            bombCooldown = 30;
        }

        int nextX = x;
        int nextY = y;

        if (up) nextY -= speed;
        if (down) nextY += speed;
        if (left) nextX -= speed;
        if (right) nextX += speed;

        if (!checkCollision(nextX, y)) x = nextX;
        if (!checkCollision(x, nextY)) y = nextY;
    }

    private boolean checkCollision(int targetX, int targetY) {
        int leftX = targetX + solidArea.x;
        int rightX = targetX + solidArea.x + solidArea.width;
        int topY = targetY + solidArea.y;
        int bottomY = targetY + solidArea.y + solidArea.height;

        if (leftX < 0 || rightX > GamePanel.SCREEN_WIDTH || topY < 0 || bottomY > GamePanel.SCREEN_HEIGHT) return true;

        int leftCol = leftX / GamePanel.TILE_SIZE;
        int rightCol = (rightX - 1) / GamePanel.TILE_SIZE;
        int topRow = topY / GamePanel.TILE_SIZE;
        int bottomRow = (bottomY - 1) / GamePanel.TILE_SIZE;

        return (gp.map[leftCol][topRow] != 0 || gp.map[rightCol][topRow] != 0 ||
                gp.map[leftCol][bottomRow] != 0 || gp.map[rightCol][bottomRow] != 0);
    }

    public void draw(Graphics2D g2) {
        if (isDead) {
            g2.setColor(Color.DARK_GRAY);
        } else if (invincibleTimer > 0 && invincibleTimer % 10 < 5) {
            g2.setColor(Color.WHITE);
        } else {
            g2.setColor(color);
        }

        g2.fillRect(x + drawOffset, y + drawOffset, drawSize, drawSize);

        if (armorCount > 0 && !isDead) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillArc(x + drawOffset, y + drawOffset - 4, drawSize, drawSize, 0, 180);
            g2.setColor(Color.DARK_GRAY);
            g2.drawArc(x + drawOffset, y + drawOffset - 4, drawSize, drawSize, 0, 180);
        }
    }
}