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

    public boolean isBot;
    public int botDifficulty;
    private int botTimer = 0;
    private int botDir = 0;
    private boolean botBomb = false;

    public Player(GamePanel gp, KeyHandler.PlayerInput input, int id, Color color, int startCol, int startRow, boolean isBot, int botDifficulty) {
        this.gp = gp;
        this.input = input;
        this.id = id;
        this.color = color;
        this.x = startCol * GamePanel.TILE_SIZE;
        this.y = startRow * GamePanel.TILE_SIZE;
        this.isBot = isBot;
        this.botDifficulty = botDifficulty;

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

    private boolean isTileSafe(int c, int r) {
        for (Explosion e : gp.explosions) {
            if (e.col == c && e.row == r) return false;
        }
        for (Bomb b : gp.bombs) {
            if (b.col == c && Math.abs(b.row - r) <= b.owner.bombPower) return false;
            if (b.row == r && Math.abs(b.col - c) <= b.owner.bombPower) return false;
        }
        return true;
    }

    public void update() {
        if (isDead) return;

        if (bombCooldown > 0) bombCooldown--;
        if (invincibleTimer > 0) invincibleTimer--;

        if (curseTimer > 0) curseTimer--;
        else activeCurse = -1;

        boolean up = false, down = false, left = false, right = false, bomb = false;

        if (isBot) {
            if (botDifficulty == 0) {
                if (botTimer <= 0) {
                    botDir = (int)(Math.random() * 4);
                    botTimer = 15 + (int)(Math.random() * 30);
                    botBomb = (Math.random() < 0.05);
                } else {
                    botTimer--;
                    botBomb = false;
                }
            }
            else {
                if (botTimer <= 0) {
                    botTimer = 15;
                    botBomb = false;

                    // ZMIANA: Poprawione odwołania do GamePanel.TILE_SIZE
                    int myCol = (x + GamePanel.TILE_SIZE / 2) / GamePanel.TILE_SIZE;
                    int myRow = (y + GamePanel.TILE_SIZE / 2) / GamePanel.TILE_SIZE;
                    boolean imSafe = isTileSafe(myCol, myRow);

                    Player target = null;
                    int minDist = 999;
                    for (Player p : gp.players) {
                        if (!p.isDead && !p.isBot) {
                            // ZMIANA: Poprawione odwołania do GamePanel.TILE_SIZE
                            int dist = Math.abs((p.x/GamePanel.TILE_SIZE) - myCol) + Math.abs((p.y/GamePanel.TILE_SIZE) - myRow);
                            if (dist < minDist) { minDist = dist; target = p; }
                        }
                    }

                    int bestDir = -1;
                    int bestScore = -9999;
                    int[][] dirs = {{0,-1}, {0,1}, {-1,0}, {1,0}};

                    for (int i=0; i<4; i++) {
                        int testCol = myCol + dirs[i][0];
                        int testRow = myRow + dirs[i][1];

                        if (testCol < 0 || testCol >= GamePanel.MAX_SCREEN_COL || testRow < 0 || testRow >= GamePanel.MAX_SCREEN_ROW) continue;

                        int tile = gp.map[testCol][testRow];
                        if (tile == 1) continue;

                        if (tile == 2) {
                            if (imSafe && bombsPlaced < maxBombs) botBomb = true;
                            continue;
                        }

                        boolean safeTile = isTileSafe(testCol, testRow);
                        int score = 0;

                        if (safeTile) score += 1000;
                        else score -= 1000;

                        if (target != null) {
                            // ZMIANA: Poprawione odwołania do GamePanel.TILE_SIZE
                            int distToTarget = Math.abs((target.x/GamePanel.TILE_SIZE) - testCol) + Math.abs((target.y/GamePanel.TILE_SIZE) - testRow);
                            score -= distToTarget * 10;
                        }

                        if (i == botDir) score += 5;

                        if (score > bestScore) {
                            bestScore = score;
                            bestDir = i;
                        }
                    }

                    if (bestDir != -1) botDir = bestDir;

                    if (target != null && minDist <= 2 && imSafe && bombsPlaced < maxBombs) {
                        botBomb = true;
                    }

                    if (imSafe && bestScore < 0) botDir = -1;
                } else {
                    botTimer--;
                    botBomb = false;
                }
            }

            if (botDir == 0) up = true;
            if (botDir == 1) down = true;
            if (botDir == 2) left = true;
            if (botDir == 3) right = true;
            bomb = botBomb;

        }
        else if (input != null) {
            up = input.up; down = input.down; left = input.left; right = input.right; bomb = input.bomb;
        }

        if (activeCurse == 3) {
            boolean tempUp = up, tempDown = down, tempLeft = left, tempRight = right;
            up = tempDown; down = tempUp; left = tempRight; right = tempLeft;
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
            // ZMIANA: Poprawione odwołania do GamePanel.TILE_SIZE
            int centerCol = (x + GamePanel.TILE_SIZE / 2) / GamePanel.TILE_SIZE;
            int centerRow = (y + GamePanel.TILE_SIZE / 2) / GamePanel.TILE_SIZE;
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
        else if (isBot) botTimer = 0;

        if (!checkCollision(x, nextY)) y = nextY;
        else if (isBot) botTimer = 0;
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
        if (isDead) g2.setColor(Color.DARK_GRAY);
        else if (invincibleTimer > 0 && invincibleTimer % 10 < 5) g2.setColor(Color.WHITE);
        else g2.setColor(color);

        g2.fillRect(x + drawOffset, y + drawOffset, drawSize, drawSize);

        if (armorCount > 0 && !isDead) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillArc(x + drawOffset, y + drawOffset - 4, drawSize, drawSize, 0, 180);
            g2.setColor(Color.DARK_GRAY);
            g2.drawArc(x + drawOffset, y + drawOffset - 4, drawSize, drawSize, 0, 180);
        }
    }
}