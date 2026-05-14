package game;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {

    // --- ZMIANA: STAŁE SYSTEMOWE ---
    public static final int ORIGINAL_TILE_SIZE = 16;
    public static final int SCALE = 3;
    public static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;

    public static final int MAX_SCREEN_COL = 15;
    public static final int MAX_SCREEN_ROW = 13;

    public static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
    public static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;
    public static final int HUD_HEIGHT = TILE_SIZE * 2;
    public static final int TOTAL_SCREEN_HEIGHT = SCREEN_HEIGHT + HUD_HEIGHT;
    // -------------------------------

    int FPS = 60;
    Thread gameThread;
    public int[][] map;

    public GameState currentState = GameState.LOGO;
    public GameState previousState = GameState.MENU;

    public Config config = new Config();
    public KeyHandler keyH = new KeyHandler(this);
    public UI ui = new UI(this);

    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Bomb> bombs = new ArrayList<>();
    public ArrayList<Explosion> explosions = new ArrayList<>();
    public ArrayList<PowerUp> powerUps = new ArrayList<>();

    public int numberOfPlayers = 2;
    public ArrayList<String> mapNames = new ArrayList<>();
    public int currentMapIndex = 0;
    public int gameMode = 1;

    public int playTimer = 0;
    public int winnerId = -1;
    public int deathTimer = 300;
    public boolean roundEnded = false;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, TOTAL_SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        config.loadConfig(keyH);

        map = new int[MAX_SCREEN_COL][MAX_SCREEN_ROW];
        scanMapFolder();
    }

    private void scanMapFolder() {
        try {
            InputStream in = getClass().getResourceAsStream("/maps");
            if(in == null) return;
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String resource;
            while ((resource = br.readLine()) != null) {
                if(resource.endsWith(".txt")) {
                    mapNames.add(resource.substring(0, resource.length() - 4));
                }
            }
            br.close();
            if(mapNames.isEmpty()) mapNames.add("Brak Mapy");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void resetGame() {
        players.clear();
        bombs.clear();
        explosions.clear();
        powerUps.clear();

        playTimer = 0;
        deathTimer = 300;
        roundEnded = false;
        winnerId = -1;

        players.add(new Player(this, keyH.p1, 1, Color.WHITE, 0, 0));
        players.add(new Player(this, keyH.p2, 2, new Color(50, 150, 255), MAX_SCREEN_COL - 1, MAX_SCREEN_ROW - 1));
        if (numberOfPlayers >= 3) players.add(new Player(this, keyH.p3, 3, Color.RED, MAX_SCREEN_COL - 1, 0));
        if (numberOfPlayers >= 4) players.add(new Player(this, keyH.p4, 4, Color.YELLOW, 0, MAX_SCREEN_ROW - 1));

        loadMap();
    }

    public void loadMap() {
        if(mapNames.isEmpty() || mapNames.get(0).equals("Brak Mapy")) return;
        try {
            String fileName = "/maps/" + mapNames.get(currentMapIndex) + ".txt";
            InputStream is = getClass().getResourceAsStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            for (int row = 0; row < MAX_SCREEN_ROW; row++) {
                String line = br.readLine();
                if(line == null) break;
                for (int col = 0; col < MAX_SCREEN_COL; col++) {
                    if(col >= line.length()) break;
                    char c = line.charAt(col);
                    if (c == '#') {
                        map[col][row] = 1;
                    } else {
                        boolean safe = (col<=1&&row==0)||(col==0&&row==1) || (col>=MAX_SCREEN_COL-2&&row==MAX_SCREEN_ROW-1)||(col==MAX_SCREEN_COL-1&&row==MAX_SCREEN_ROW-2) || (col>=MAX_SCREEN_COL-2&&row==0)||(col==MAX_SCREEN_COL-1&&row==1) || (col<=1&&row==MAX_SCREEN_ROW-1)||(col==0&&row==MAX_SCREEN_ROW-2);
                        if (!safe) {
                            if (Math.random() < 0.4) map[col][row] = 2;
                            else map[col][row] = 0;
                        } else map[col][row] = 0;
                    }
                }
            }
            br.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        if (currentState == GameState.PLAY) {
            if (!roundEnded) playTimer++;

            int aliveCount = 0;
            int lastAliveId = 0;

            for (Player p : players) {
                if (!p.isDead) {
                    p.update();
                    checkPowerUpCollection(p);
                    checkPlayerDeath(p);
                    if (!p.isDead) { aliveCount++; lastAliveId = p.id; }
                }
            }

            for (int i = 0; i < bombs.size(); i++) {
                Bomb b = bombs.get(i);
                b.update();
                if (b.timer <= 0) { explodeBomb(b); b.owner.bombsPlaced--; bombs.remove(i); i--; }
            }
            for (int i = 0; i < explosions.size(); i++) {
                Explosion e = explosions.get(i);
                e.update();
                if (e.timer <= 0) { explosions.remove(i); i--; }
            }

            if (aliveCount <= 1 && !roundEnded) {
                roundEnded = true;
                winnerId = (aliveCount == 1) ? lastAliveId : 0;
            }

            if (roundEnded) {
                deathTimer--;
                if (deathTimer <= 0) { currentState = GameState.MENU; ui.commandNum = 0; }
            }
        }
    }

    public void checkPowerUpCollection(Player p) {
        Rectangle playerRect = new Rectangle(p.x + p.solidArea.x, p.y + p.solidArea.y, p.solidArea.width, p.solidArea.height);
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp pu = powerUps.get(i);
            Rectangle puRect = new Rectangle(pu.col * TILE_SIZE, pu.row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            if (playerRect.intersects(puRect)) {

                if (pu.type == 0) p.bombPower++;
                else if (pu.type == 1) p.maxBombs++;
                else if (pu.type == 2) { p.speed++; if (p.speed > 8) p.speed = 8; }
                else if (pu.type >= 3 && pu.type <= 5) { p.activeCurse = pu.type; p.curseTimer = 600; }
                else if (pu.type == 6) { p.armorCount++; }

                powerUps.remove(i);
                i--;
            }
        }
    }

    public void checkPlayerDeath(Player p) {
        if (p.invincibleTimer > 0) return;

        Rectangle playerRect = new Rectangle(p.x + p.solidArea.x, p.y + p.solidArea.y, p.solidArea.width, p.solidArea.height);
        for (Explosion e : explosions) {
            Rectangle expRect = new Rectangle(e.col * TILE_SIZE, e.row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            if (playerRect.intersects(expRect)) {
                if (p.armorCount > 0) {
                    p.armorCount--;
                    p.invincibleTimer = 60;
                } else {
                    p.isDead = true;
                }
                break;
            }
        }
    }

    public void explodeBomb(Bomb b) {
        int c = b.col;
        int r = b.row;
        int power = b.owner.bombPower;

        // ZMIANA: Konstruktory nie wymagają już `this`
        explosions.add(new Explosion(c, r));
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            for (int i = 1; i <= power; i++) {
                int nextCol = c + dir[0] * i;
                int nextRow = r + dir[1] * i;

                if (nextCol < 0 || nextCol >= MAX_SCREEN_COL || nextRow < 0 || nextRow >= MAX_SCREEN_ROW) break;

                for (int j = 0; j < powerUps.size(); j++) {
                    if (powerUps.get(j).col == nextCol && powerUps.get(j).row == nextRow) {
                        powerUps.remove(j);
                        j--;
                    }
                }

                int tileType = map[nextCol][nextRow];

                if (tileType == 1) break;
                else if (tileType == 2) {
                    map[nextCol][nextRow] = 0;
                    explosions.add(new Explosion(nextCol, nextRow));

                    if (gameMode == 1 && Math.random() < 0.35) {
                        double rand = Math.random();
                        int type;
                        if (rand < 0.5) type = (int)(Math.random() * 3);
                        else if (rand < 0.8) type = 3 + (int)(Math.random() * 3);
                        else type = 6;

                        powerUps.add(new PowerUp(nextCol, nextRow, type));
                    }
                    break;
                }
                else {
                    explosions.add(new Explosion(nextCol, nextRow));
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        double scaleRatio = Math.min((double) getWidth() / SCREEN_WIDTH, (double) getHeight() / TOTAL_SCREEN_HEIGHT);
        int drawX = (int) ((getWidth() - (SCREEN_WIDTH * scaleRatio)) / 2);
        int drawY = (int) ((getHeight() - (TOTAL_SCREEN_HEIGHT * scaleRatio)) / 2);

        g2.translate(drawX, drawY);
        g2.scale(scaleRatio, scaleRatio);
        g2.setClip(0, 0, SCREEN_WIDTH, TOTAL_SCREEN_HEIGHT);

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, SCREEN_WIDTH, TOTAL_SCREEN_HEIGHT);

        if (currentState == GameState.PLAY || currentState == GameState.PAUSE) {
            for (int x = 0; x < MAX_SCREEN_COL; x++) {
                for (int y = 0; y < MAX_SCREEN_ROW; y++) {
                    int screenX = x * TILE_SIZE;
                    int screenY = y * TILE_SIZE;

                    if (map[x][y] == 1) {
                        g2.setColor(Color.GRAY);
                        g2.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                    } else if (map[x][y] == 2) {
                        g2.setColor(new Color(139, 69, 19));
                        g2.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
            for (PowerUp p : powerUps) p.draw(g2);
            for (Bomb b : bombs) b.draw(g2);
            for (Explosion e : explosions) e.draw(g2);
            for (Player p : players) p.draw(g2);
        }

        ui.draw(g2);
        g2.dispose();
    }
}