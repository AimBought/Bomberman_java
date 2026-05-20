package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class UI {
    GamePanel gp;
    Font titleFont, normalFont, smallFont, tinyFont;

    public int commandNum = 0;

    public UI(GamePanel gp) {
        this.gp = gp;
        titleFont = new Font("Arial", Font.BOLD, 48);
        normalFont = new Font("Arial", Font.PLAIN, 28);
        smallFont = new Font("Arial", Font.PLAIN, 18);
        tinyFont = new Font("Arial", Font.PLAIN, 14);
    }

    public void draw(Graphics2D g2) {
        switch (gp.currentState) {
            case LOGO: drawLogoScreen(g2); break;
            case MENU: drawMenuScreen(g2); break;
            case OPTIONS: drawOptionsScreen(g2); break;
            case PLAY:
            case PAUSE:
                drawPlayHUD(g2);
                if(gp.currentState == GameState.PAUSE) drawPauseScreen(g2);
                break;
        }
    }

    private void drawPlayHUD(Graphics2D g2) {
        int yStart = GamePanel.SCREEN_HEIGHT;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, yStart - 1, GamePanel.SCREEN_WIDTH, GamePanel.HUD_HEIGHT + 2);
        g2.setColor(Color.WHITE);
        g2.drawLine(0, yStart - 1, GamePanel.SCREEN_WIDTH, yStart - 1);

        int sectionWidth = GamePanel.SCREEN_WIDTH / gp.players.size();
        g2.setFont(tinyFont);

        for (int i = 0; i < gp.players.size(); i++) {
            Player p = gp.players.get(i);
            int startX = i * sectionWidth;

            if (i > 0) g2.drawLine(startX, yStart, startX, GamePanel.TOTAL_SCREEN_HEIGHT);

            g2.setFont(smallFont);
            g2.setColor(p.color);
            String title = "P" + p.id;
            if (p.isBot) title += " [BOT]";

            if (gp.gameMode == 3) {
                int score = 0;
                for(int x = 0; x < GamePanel.MAX_SCREEN_COL; x++) {
                    for(int y = 0; y < GamePanel.MAX_SCREEN_ROW; y++) {
                        if(gp.paintedMap[x][y] == p.id) score++;
                    }
                }
                title += " (" + score + " PKT)";
            }

            if (p.isDead) {
                title += " (MARTWY)";
            } else if (p.activeCurse != -1) {
                int secondsLeft = (p.curseTimer / 60) + 1;
                title += " (KLĄTWA: " + secondsLeft + "s)";
            }
            g2.drawString(title, startX + 10, yStart + 25);

            if (p.armorCount > 0 && !p.isDead) {
                int armorX = startX + 15 + g2.getFontMetrics().stringWidth(title);
                for (int j = 0; j < p.armorCount; j++) {
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.fillArc(armorX + (j * 15), yStart + 12, 12, 14, 0, 180);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawArc(armorX + (j * 15), yStart + 12, 12, 14, 0, 180);
                }
            }

            g2.setFont(tinyFont);
            g2.setColor(Color.WHITE);
            g2.drawString("Zasięg: " + p.bombPower, startX + 10, yStart + 45);
            g2.drawString("Bomby: " + p.maxBombs, startX + 10, yStart + 65);
            g2.drawString("Szybkość: " + p.speed, startX + 10, yStart + 85);
        }

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(GamePanel.SCREEN_WIDTH / 2 - 50, 0, 100, 30);
        g2.setColor(Color.WHITE);
        g2.drawRect(GamePanel.SCREEN_WIDTH / 2 - 50, 0, 100, 30);

        g2.setFont(smallFont);
        String timeText;
        if (!gp.roundEnded) {
            int t = (gp.gameMode == 3) ? gp.paintTimeLeft : gp.playTimer;
            int seconds = (t / 60) % 60;
            int minutes = (t / 60) / 60;
            timeText = String.format("%02d:%02d", minutes, seconds);
        } else {
            int secondsLeft = (gp.deathTimer / 60) + 1;
            timeText = "00:0" + secondsLeft;
        }

        int timeX = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(timeText) / 2;
        g2.drawString(timeText, timeX, 22);

        if (gp.gameMode == 2 && !gp.roundEnded) {
            int stormSecondsLeft = 10 - (gp.stormTimer / 60);
            String stormText = "Burza za: " + stormSecondsLeft + "s";
            g2.setColor(new Color(255, 100, 255));
            g2.setFont(tinyFont);
            int stX = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(stormText) / 2;
            g2.drawString(stormText, stX, 45);
        }

        if (gp.roundEnded && gp.currentState != GameState.PAUSE) {
            g2.setFont(titleFont);
            String winnerText;
            Color textColor = Color.WHITE;

            if (gp.winnerId == 0) {
                winnerText = "REMIS!";
                textColor = Color.LIGHT_GRAY;
            } else {
                winnerText = "WYGRYWA P" + gp.winnerId + "!";
                for (Player p : gp.players) if (p.id == gp.winnerId) textColor = p.color;
            }

            int x = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(winnerText) / 2;
            int y = GamePanel.SCREEN_HEIGHT / 2;

            g2.setColor(Color.BLACK);
            g2.drawString(winnerText, x + 4, y + 4);
            g2.setColor(textColor);
            g2.drawString(winnerText, x, y);
        }
    }

    private void drawPauseScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.TOTAL_SCREEN_HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        String text = "PAUZA";
        int x = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        int y = GamePanel.TOTAL_SCREEN_HEIGHT / 2 - 100;
        g2.drawString(text, x, y);

        g2.setFont(normalFont);

        String opt0 = "Wznów Grę";
        int opt0X = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(opt0) / 2;
        g2.drawString(opt0, opt0X, y + 80);
        if (commandNum == 0) g2.drawString(">", opt0X - 30, y + 80);

        String opt1 = "Restartuj Rundę";
        int opt1X = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(opt1) / 2;
        g2.drawString(opt1, opt1X, y + 130);
        if (commandNum == 1) g2.drawString(">", opt1X - 30, y + 130);

        String opt2 = "Opcje Sterowania";
        int opt2X = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(opt2) / 2;
        g2.drawString(opt2, opt2X, y + 180);
        if (commandNum == 2) g2.drawString(">", opt2X - 30, y + 180);

        String opt3 = "Wyjdź do Menu";
        int opt3X = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(opt3) / 2;
        g2.drawString(opt3, opt3X, y + 230);
        if (commandNum == 3) g2.drawString(">", opt3X - 30, y + 230);
    }

    private void drawOptionsScreen(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        String title = "STEROWANIE";
        g2.drawString(title, GamePanel.SCREEN_WIDTH/2 - g2.getFontMetrics().stringWidth(title)/2, 60);

        g2.setFont(smallFont);

        String[] options = {
                "P1 Góra: " + KeyEvent.getKeyText(gp.keyH.p1.upCode), "P1 Dół: " + KeyEvent.getKeyText(gp.keyH.p1.downCode), "P1 Lewo: " + KeyEvent.getKeyText(gp.keyH.p1.leftCode), "P1 Prawo: " + KeyEvent.getKeyText(gp.keyH.p1.rightCode), "P1 Bomba: " + KeyEvent.getKeyText(gp.keyH.p1.bombCode),
                "P2 Góra: " + KeyEvent.getKeyText(gp.keyH.p2.upCode), "P2 Dół: " + KeyEvent.getKeyText(gp.keyH.p2.downCode), "P2 Lewo: " + KeyEvent.getKeyText(gp.keyH.p2.leftCode), "P2 Prawo: " + KeyEvent.getKeyText(gp.keyH.p2.rightCode), "P2 Bomba: " + KeyEvent.getKeyText(gp.keyH.p2.bombCode),
                "P3 Góra: " + KeyEvent.getKeyText(gp.keyH.p3.upCode), "P3 Dół: " + KeyEvent.getKeyText(gp.keyH.p3.downCode), "P3 Lewo: " + KeyEvent.getKeyText(gp.keyH.p3.leftCode), "P3 Prawo: " + KeyEvent.getKeyText(gp.keyH.p3.rightCode), "P3 Bomba: " + KeyEvent.getKeyText(gp.keyH.p3.bombCode),
                "P4 Góra: " + KeyEvent.getKeyText(gp.keyH.p4.upCode), "P4 Dół: " + KeyEvent.getKeyText(gp.keyH.p4.downCode), "P4 Lewo: " + KeyEvent.getKeyText(gp.keyH.p4.leftCode), "P4 Prawo: " + KeyEvent.getKeyText(gp.keyH.p4.rightCode), "P4 Bomba: " + KeyEvent.getKeyText(gp.keyH.p4.bombCode),
                "Powrót"
        };

        for (int i = 0; i < options.length; i++) {
            int col = i / 5;
            int row = i % 5;
            int xPos = 40 + (col * 160);
            int yPos = 120 + (row * 40);

            if (i == 20) {
                xPos = GamePanel.SCREEN_WIDTH/2 - g2.getFontMetrics().stringWidth(options[i])/2;
                yPos = 360;
            }

            if (i >= 10 && i < 15 && gp.numberOfPlayers < 3) g2.setColor(Color.GRAY);
            else if (i >= 15 && i < 20 && gp.numberOfPlayers < 4) g2.setColor(Color.GRAY);
            else g2.setColor(Color.WHITE);

            g2.drawString(options[i], xPos, yPos);

            if (commandNum == i) {
                g2.drawString(">", xPos - 15, yPos);
                if (gp.keyH.waitingForKey && i != 20) {
                    g2.setColor(Color.YELLOW);
                    g2.drawString("Wciśnij klawisz...", GamePanel.SCREEN_WIDTH/2 - 70, 420);
                }
            }
        }
    }

    private void drawLogoScreen(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        String text = "STUDIO XYZ PREZENTUJE";
        int x = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        int y = GamePanel.TOTAL_SCREEN_HEIGHT / 2;
        g2.drawString(text, x, y);
        g2.setFont(normalFont);
        String subText = "Wciśnij SPACJĘ aby kontynuować";
        int subX = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(subText) / 2;
        g2.drawString(subText, subX, y + 50);
    }

    private void drawMenuScreen(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        String title = "BOMBERMAN";
        int titleX = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, 100);

        g2.setFont(normalFont);

        // ZMIANA: Skompresowane odstępy, żeby wszystko weszło
        int startY = 170;
        int spacing = 40;

        String opt0 = "Nowa Gra";
        int opt0X = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(opt0) / 2;
        g2.drawString(opt0, opt0X, startY);
        if (commandNum == 0) g2.drawString(">", opt0X - 30, startY);

        String opt1 = "< Ilość Graczy: " + gp.numberOfPlayers + " >";
        int opt1X = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(opt1) / 2;
        g2.drawString(opt1, opt1X, startY + spacing);
        if (commandNum == 1) g2.drawString(">", opt1X - 30, startY + spacing);

        String opt2 = "< Ilość Botów: " + gp.numberOfBots + " >";
        int opt2X = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(opt2) / 2;
        g2.drawString(opt2, opt2X, startY + spacing * 2);
        if (commandNum == 2) g2.drawString(">", opt2X - 30, startY + spacing * 2);

        // NOWE: Poziom Botów
        String diffName = (gp.botDifficulty == 0) ? "Łatwy" : "Trudny";
        String opt3 = "< Poziom Botów: " + diffName + " >";
        int opt3X = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(opt3) / 2;
        g2.drawString(opt3, opt3X, startY + spacing * 3);
        if (commandNum == 3) g2.drawString(">", opt3X - 30, startY + spacing * 3);

        String mapName = gp.mapNames.isEmpty() ? "Brak Mapy" : gp.mapNames.get(gp.currentMapIndex);
        String opt4 = "< Mapa: " + mapName + " >";
        int opt4X = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(opt4) / 2;
        g2.drawString(opt4, opt4X, startY + spacing * 4);
        if (commandNum == 4) g2.drawString(">", opt4X - 30, startY + spacing * 4);

        String modeName = "Klasyczny";
        if (gp.gameMode == 1) modeName = "Ulepszenia";
        if (gp.gameMode == 2) modeName = "Battle Royale";
        if (gp.gameMode == 3) modeName = "Paint";

        String opt5 = "< Tryb: " + modeName + " >";
        int opt5X = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(opt5) / 2;
        g2.drawString(opt5, opt5X, startY + spacing * 5);
        if (commandNum == 5) g2.drawString(">", opt5X - 30, startY + spacing * 5);

        String opt6 = "Opcje";
        int opt6X = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(opt6) / 2;
        g2.drawString(opt6, opt6X, startY + spacing * 6);
        if (commandNum == 6) g2.drawString(">", opt6X - 30, startY + spacing * 6);

        String opt7 = "Wyjście";
        int opt7X = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(opt7) / 2;
        g2.drawString(opt7, opt7X, startY + spacing * 7);
        if (commandNum == 7) g2.drawString(">", opt7X - 30, startY + spacing * 7);
    }
}