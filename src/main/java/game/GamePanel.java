package game;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {

    public static final int ORIGINAL_TILE_SIZE = 16;
    public static final int SCALE = 3;
    public static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;

    public static final int MAX_SCREEN_COL = 15;
    public static final int MAX_SCREEN_ROW = 13;

    public static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
    public static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;
    public static final int HUD_HEIGHT = TILE_SIZE * 2;
    public static final int TOTAL_SCREEN_HEIGHT = SCREEN_HEIGHT + HUD_HEIGHT;

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

    public int numberOfPlayers = 1;
    public int numberOfBots = 3;
    public int botDifficulty = 1;

    public ArrayList<String> mapNames = new ArrayList<>();
    public int currentMapIndex = 0;
    public int gameMode = 1;

    public int stormPhase = 0;
    public int stormTimer = 0;
    public int[][] paintedMap;
    public int paintTimeLeft = 3600;

    public int playTimer = 0;
    public int winnerId = -1;
    public int deathTimer = 300;
    public boolean roundEnded = false;

    // Zmienne Edytora
    public int editorCursorCol = 0;
    public int editorCursorRow = 0;
    public int[][] editorMap = new int[MAX_SCREEN_COL][MAX_SCREEN_ROW];
    public String newMapName = ""; // NOWE: Przechowuje wpisywaną nazwę

    Color grassColor1 = new Color(34, 139, 34);
    Color grassColor2 = new Color(40, 150, 40);
    Color wallBase = new Color(90, 90, 90);
    Color wallHighlight = new Color(130, 130, 130);
    Color wallShadow = new Color(50, 50, 50);
    Color crateBase = new Color(139, 69, 19);
    Color crateBorder = new Color(80, 40, 10);

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

    public void scanMapFolder() {
        mapNames.clear();
        try {
            InputStream in = getClass().getResourceAsStream("/maps");
            if(in != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String resource;
                while ((resource = br.readLine()) != null) {
                    if(resource.endsWith(".txt")) mapNames.add(resource.substring(0, resource.length() - 4));
                }
                br.close();
            }

            File customDir = new File("custom_maps");
            if (customDir.exists() && customDir.isDirectory()) {
                File[] files = customDir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.getName().endsWith(".txt")) {
                            mapNames.add("custom:" + f.getName().substring(0, f.getName().length() - 4));
                        }
                    }
                }
            }

            if(mapNames.isEmpty()) mapNames.add("Brak Mapy");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // NOWE: Mechanika inicjalizacji edytora na pustej lub istniejącej planszy
    public void initEditor(boolean editExisting) {
        editorMap = new int[MAX_SCREEN_COL][MAX_SCREEN_ROW];
        editorCursorCol = 0;
        editorCursorRow = 0;
        newMapName = "";

        if (editExisting && !mapNames.isEmpty()) {
            loadMap();
            for (int r = 0; r < MAX_SCREEN_ROW; r++) {
                for (int c = 0; c < MAX_SCREEN_COL; c++) editorMap[c][r] = map[c][r];
            }
            String currentName = mapNames.get(currentMapIndex);
            if (currentName.startsWith("custom:")) newMapName = currentName.substring(7);
        }

        currentState = GameState.EDITOR;
    }

    // NOWE: Zapis z podaną nazwą
    public void saveEditorMap() {
        File folder = new File("custom_maps");
        if (!folder.exists()) folder.mkdir();

        String fileName = newMapName.trim().isEmpty() ? "Mapa_" + (System.currentTimeMillis() % 1000) : newMapName.trim();
        fileName += ".txt";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder, fileName)));
            for (int r = 0; r < MAX_SCREEN_ROW; r++) {
                for (int c = 0; c < MAX_SCREEN_COL; c++) {
                    if (editorMap[c][r] == 1) bw.write("#");
                    else if (editorMap[c][r] == 2) bw.write("X");
                    else bw.write("O");
                }
                bw.newLine();
            }
            bw.close();

            scanMapFolder();
            // Wyszukaj i ustaw index na zapisaną mapę
            for (int i = 0; i < mapNames.size(); i++) {
                if (mapNames.get(i).equals("custom:" + fileName.replace(".txt", ""))) {
                    currentMapIndex = i;
                    break;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // NOWE: Bezpieczne usunięcie wybranej mapy z dysku
    public void deleteCustomMap() {
        if (mapNames.isEmpty()) return;
        String name = mapNames.get(currentMapIndex);
        if (name.startsWith("custom:")) {
            File f = new File("custom_maps/" + name.substring(7) + ".txt");
            if (f.exists()) f.delete();
            scanMapFolder();
            currentMapIndex = 0;
        }
    }

    public void resetGame() {
        players.clear();
        bombs.clear();
        explosions.clear();
        powerUps.clear();
        playTimer = 0; deathTimer = 300; roundEnded = false; winnerId = -1;
        stormPhase = 0; stormTimer = 0;
        paintedMap = new int[MAX_SCREEN_COL][MAX_SCREEN_ROW]; paintTimeLeft = 3600;

        int[][] spawns = { {0, 0}, {MAX_SCREEN_COL - 1, MAX_SCREEN_ROW - 1}, {MAX_SCREEN_COL - 1, 0}, {0, MAX_SCREEN_ROW - 1} };
        Color[] colors = {Color.WHITE, new Color(50, 150, 255), Color.RED, Color.YELLOW};
        KeyHandler.PlayerInput[] inputs = {keyH.p1, keyH.p2, keyH.p3, keyH.p4};

        int currentId = 1;
        for(int i = 0; i < numberOfPlayers; i++) { players.add(new Player(this, inputs[i], currentId, colors[currentId-1], spawns[currentId-1][0], spawns[currentId-1][1], false, 0)); currentId++; }
        for(int i = 0; i < numberOfBots; i++) { if(currentId > 4) break; players.add(new Player(this, null, currentId, colors[currentId-1], spawns[currentId-1][0], spawns[currentId-1][1], true, botDifficulty)); currentId++; }

        loadMap();
    }

    public void loadMap() {
        if (gameMode == 3) {
            for (int r = 0; r < MAX_SCREEN_ROW; r++) for (int c = 0; c < MAX_SCREEN_COL; c++) map[c][r] = 0;
            return;
        }

        if(mapNames.isEmpty() || mapNames.get(0).equals("Brak Mapy")) return;
        try {
            String name = mapNames.get(currentMapIndex);
            BufferedReader br;

            if (name.startsWith("custom:")) {
                File f = new File("custom_maps/" + name.substring(7) + ".txt");
                br = new BufferedReader(new FileReader(f));
            } else {
                InputStream is = getClass().getResourceAsStream("/maps/" + name + ".txt");
                br = new BufferedReader(new InputStreamReader(is));
            }

            for (int row = 0; row < MAX_SCREEN_ROW; row++) {
                String line = br.readLine();
                if(line == null) break;
                for (int col = 0; col < MAX_SCREEN_COL; col++) {
                    if(col >= line.length()) break;
                    char c = line.charAt(col);

                    if (c == '#') map[col][row] = 1;
                    else if (c == 'X') map[col][row] = 2;
                    else if (c == 'O') map[col][row] = 0;
                    else {
                        boolean safe = (col<=1&&row==0)||(col==0&&row==1) || (col>=MAX_SCREEN_COL-2&&row==MAX_SCREEN_ROW-1)||(col==MAX_SCREEN_COL-1&&row==MAX_SCREEN_ROW-2) || (col>=MAX_SCREEN_COL-2&&row==0)||(col==MAX_SCREEN_COL-1&&row==1) || (col<=1&&row==MAX_SCREEN_ROW-1)||(col==0&&row==MAX_SCREEN_ROW-2);
                        if (!safe) map[col][row] = (Math.random() < 0.4) ? 2 : 0;
                        else map[col][row] = 0;
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
            if (!roundEnded) {
                playTimer++;
                if (gameMode == 2) { stormTimer++; if (stormTimer >= 600) { stormPhase++; stormTimer = 0; } }
                if (gameMode == 3) {
                    paintTimeLeft--;
                    if (playTimer % 180 == 0) {
                        int rCol = (int)(Math.random() * MAX_SCREEN_COL);
                        int rRow = (int)(Math.random() * MAX_SCREEN_ROW);
                        double rand = Math.random();
                        int type = (rand < 0.5) ? (int)(Math.random() * 3) : ((rand < 0.8) ? 3 + (int)(Math.random() * 3) : 6);
                        powerUps.add(new PowerUp(rCol, rRow, type));
                    }
                }
            }

            int aliveCount = 0; int lastAliveId = 0;
            for (Player p : players) {
                if (!p.isDead) { p.update(); checkPowerUpCollection(p); checkPlayerDeath(p); if (!p.isDead) { aliveCount++; lastAliveId = p.id; } }
            }
            for (int i = 0; i < bombs.size(); i++) { Bomb b = bombs.get(i); b.update(); if (b.timer <= 0) { explodeBomb(b); b.owner.bombsPlaced--; bombs.remove(i); i--; } }
            for (int i = 0; i < explosions.size(); i++) { Explosion e = explosions.get(i); e.update(); if (e.timer <= 0) { explosions.remove(i); i--; } }

            if (!roundEnded) {
                if (gameMode == 3) { if (paintTimeLeft <= 0 || aliveCount == 0) { roundEnded = true; calculatePaintWinner(); } }
                else { if (aliveCount <= 1) { roundEnded = true; winnerId = (aliveCount == 1) ? lastAliveId : 0; } }
            }
            if (roundEnded) { deathTimer--; if (deathTimer <= 0) { currentState = GameState.MENU; ui.commandNum = 0; } }
        }
    }

    public void calculatePaintWinner() {
        int[] scores = new int[5];
        for(int x = 0; x < MAX_SCREEN_COL; x++) for(int y = 0; y < MAX_SCREEN_ROW; y++) if(paintedMap[x][y] > 0) scores[paintedMap[x][y]]++;
        int maxScore = -1; int winId = 0; boolean tie = false;
        for(int i = 1; i <= 4; i++) {
            if(scores[i] > maxScore) { maxScore = scores[i]; winId = i; tie = false; } else if (scores[i] == maxScore) tie = true;
        }
        winnerId = tie ? 0 : winId;
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
                powerUps.remove(i); i--;
            }
        }
    }

    public void checkPlayerDeath(Player p) {
        if (gameMode == 2 && !p.isDead) {
            int pCol = (p.x + p.solidArea.x + p.solidArea.width / 2) / TILE_SIZE;
            int pRow = (p.y + p.solidArea.y + p.solidArea.height / 2) / TILE_SIZE;
            if (pCol < stormPhase || pCol >= MAX_SCREEN_COL - stormPhase || pRow < stormPhase || pRow >= MAX_SCREEN_ROW - stormPhase) { p.isDead = true; return; }
        }
        if (p.invincibleTimer > 0) return;
        if (gameMode == 3) return;

        Rectangle playerRect = new Rectangle(p.x + p.solidArea.x, p.y + p.solidArea.y, p.solidArea.width, p.solidArea.height);
        for (Explosion e : explosions) {
            Rectangle expRect = new Rectangle(e.col * TILE_SIZE, e.row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            if (playerRect.intersects(expRect)) {
                if (p.armorCount > 0) { p.armorCount--; p.invincibleTimer = 60; } else { p.isDead = true; }
                break;
            }
        }
    }

    public void explodeBomb(Bomb b) {
        int c = b.col; int r = b.row; int power = b.owner.bombPower;
        if (gameMode == 3) paintedMap[c][r] = b.owner.id;
        explosions.add(new Explosion(c, r));
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            for (int i = 1; i <= power; i++) {
                int nextCol = c + dir[0] * i; int nextRow = r + dir[1] * i;
                if (nextCol < 0 || nextCol >= MAX_SCREEN_COL || nextRow < 0 || nextRow >= MAX_SCREEN_ROW) break;
                if (gameMode == 3) paintedMap[nextCol][nextRow] = b.owner.id;

                for (int j = 0; j < powerUps.size(); j++) { if (powerUps.get(j).col == nextCol && powerUps.get(j).row == nextRow) { powerUps.remove(j); j--; } }
                int tileType = map[nextCol][nextRow];

                if (tileType == 1) break;
                else if (tileType == 2) {
                    map[nextCol][nextRow] = 0; explosions.add(new Explosion(nextCol, nextRow));
                    if ((gameMode == 1 || gameMode == 2) && Math.random() < 0.35) {
                        double rand = Math.random();
                        int type = (rand < 0.5) ? (int)(Math.random() * 3) : ((rand < 0.8) ? 3 + (int)(Math.random() * 3) : 6);
                        powerUps.add(new PowerUp(nextCol, nextRow, type));
                    }
                    break;
                } else { explosions.add(new Explosion(nextCol, nextRow)); }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int winW = getWidth();
        int winH = getHeight();

        AffineTransform originalTransform = g2.getTransform();
        Shape originalClip = g2.getClip();

        g2.setColor(new Color(15, 10, 30));
        g2.fillRect(0, 0, winW, winH);

        g2.setColor(new Color(30, 20, 50));
        for(int i = 0; i < winW; i += 64) g2.drawLine(i, 0, i, winH);
        for(int i = 0; i < winH; i += 64) g2.drawLine(0, i, winW, i);

        double scaleRatio = Math.min((double) winW / SCREEN_WIDTH, (double) winH / TOTAL_SCREEN_HEIGHT);
        int drawX = (int) ((winW - (SCREEN_WIDTH * scaleRatio)) / 2);
        int drawY = (int) ((winH - (TOTAL_SCREEN_HEIGHT * scaleRatio)) / 2);

        g2.translate(drawX, drawY);
        g2.scale(scaleRatio, scaleRatio);

        g2.setColor(Color.WHITE);
        g2.drawRect(-1, -1, SCREEN_WIDTH + 1, TOTAL_SCREEN_HEIGHT + 1);

        g2.setClip(0, 0, SCREEN_WIDTH, TOTAL_SCREEN_HEIGHT);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, SCREEN_WIDTH, TOTAL_SCREEN_HEIGHT);

        if (currentState == GameState.PLAY || currentState == GameState.PAUSE) {

            for (int x = 0; x < MAX_SCREEN_COL; x++) {
                for (int y = 0; y < MAX_SCREEN_ROW; y++) {
                    if ((x + y) % 2 == 0) g2.setColor(grassColor1); else g2.setColor(grassColor2);
                    g2.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }

            if (gameMode == 3) {
                for (int x = 0; x < MAX_SCREEN_COL; x++) {
                    for (int y = 0; y < MAX_SCREEN_ROW; y++) {
                        int ownerId = paintedMap[x][y];
                        if (ownerId > 0) {
                            Color c = Color.WHITE;
                            for (Player p : players) if (p.id == ownerId) c = p.color;
                            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 180));
                            g2.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        }
                    }
                }
            }

            for (int x = 0; x < MAX_SCREEN_COL; x++) {
                for (int y = 0; y < MAX_SCREEN_ROW; y++) {
                    int screenX = x * TILE_SIZE; int screenY = y * TILE_SIZE;
                    if (map[x][y] == 1) {
                        g2.setColor(wallBase); g2.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                        g2.setColor(wallHighlight); g2.fillRect(screenX, screenY, TILE_SIZE, 4); g2.fillRect(screenX, screenY, 4, TILE_SIZE);
                        g2.setColor(wallShadow); g2.fillRect(screenX, screenY + TILE_SIZE - 4, TILE_SIZE, 4); g2.fillRect(screenX + TILE_SIZE - 4, screenY, 4, TILE_SIZE);
                    } else if (map[x][y] == 2) {
                        g2.setColor(crateBase); g2.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                        g2.setColor(crateBorder); g2.drawRect(screenX, screenY, TILE_SIZE, TILE_SIZE); g2.drawRect(screenX + 2, screenY + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.drawLine(screenX, screenY, screenX + TILE_SIZE, screenY + TILE_SIZE); g2.drawLine(screenX + TILE_SIZE, screenY, screenX, screenY + TILE_SIZE);
                    }
                }
            }

            for (PowerUp p : powerUps) p.draw(g2); for (Bomb b : bombs) b.draw(g2); for (Explosion e : explosions) e.draw(g2); for (Player p : players) p.draw(g2);

            if (gameMode == 2 && stormPhase > 0) {
                g2.setColor(new Color(128, 0, 128, 150));
                for (int x = 0; x < MAX_SCREEN_COL; x++) {
                    for (int y = 0; y < MAX_SCREEN_ROW; y++) {
                        if (x < stormPhase || x >= MAX_SCREEN_COL - stormPhase || y < stormPhase || y >= MAX_SCREEN_ROW - stormPhase) {
                            g2.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        }
                    }
                }
            }
        }
        else if (currentState == GameState.EDITOR || currentState == GameState.EDITOR_NAMING) {
            for (int x = 0; x < MAX_SCREEN_COL; x++) {
                for (int y = 0; y < MAX_SCREEN_ROW; y++) {
                    if ((x + y) % 2 == 0) g2.setColor(grassColor1); else g2.setColor(grassColor2);
                    g2.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                    int screenX = x * TILE_SIZE; int screenY = y * TILE_SIZE;
                    if (editorMap[x][y] == 1) {
                        g2.setColor(wallBase); g2.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                        g2.setColor(wallHighlight); g2.fillRect(screenX, screenY, TILE_SIZE, 4); g2.fillRect(screenX, screenY, 4, TILE_SIZE);
                        g2.setColor(wallShadow); g2.fillRect(screenX, screenY + TILE_SIZE - 4, TILE_SIZE, 4); g2.fillRect(screenX + TILE_SIZE - 4, screenY, 4, TILE_SIZE);
                    } else if (editorMap[x][y] == 2) {
                        g2.setColor(crateBase); g2.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                        g2.setColor(crateBorder); g2.drawRect(screenX, screenY, TILE_SIZE, TILE_SIZE); g2.drawRect(screenX + 2, screenY + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.drawLine(screenX, screenY, screenX + TILE_SIZE, screenY + TILE_SIZE); g2.drawLine(screenX + TILE_SIZE, screenY, screenX, screenY + TILE_SIZE);
                    }
                }
            }

            if (currentState == GameState.EDITOR) {
                g2.setColor(Color.RED); g2.setStroke(new BasicStroke(4));
                g2.drawRect(editorCursorCol * TILE_SIZE, editorCursorRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g2.setStroke(new BasicStroke(1));

                g2.setColor(new Color(0, 0, 0, 200)); g2.fillRect(0, 0, SCREEN_WIDTH, 26);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("EDYCJA: [1] Mur  [2] Skrzynia  [3] Usuń    DALEJ: [ENTER]    WYJDŹ: [ESC]", 15, 18);
            }
        }

        ui.draw(g2);

        g2.setTransform(originalTransform);
        g2.setClip(originalClip);
        ui.drawSidebars(g2, winW, winH, drawX);

        g2.dispose();
    }
}