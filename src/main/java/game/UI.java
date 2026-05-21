package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class UI {
    GamePanel gp;
    Font titleFont, normalFont, smallFont, tinyFont;

    public int commandNum = 0;

    Color bgColor = new Color(20, 25, 40);
    Color goldColor = new Color(255, 215, 0);
    Color highlightColor = new Color(0, 255, 255);

    public UI(GamePanel gp) {
        this.gp = gp;
        titleFont = new Font("Impact", Font.BOLD, 56);
        normalFont = new Font("Arial", Font.BOLD, 28);
        smallFont = new Font("Arial", Font.BOLD, 18);
        tinyFont = new Font("Arial", Font.PLAIN, 13);
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

    // NOWE: Metoda rysująca na pustych bokach ekranu (wywoływana z GamePanel.java)
    public void drawSidebars(Graphics2D g2, int winW, int winH, int sideWidth) {
        // Rysujemy tylko w trakcie gry i jeśli pasek z boku ma przynajmniej 200px szerokości
        if ((gp.currentState != GameState.PLAY && gp.currentState != GameState.PAUSE) || sideWidth < 200) return;

        // 1. LEWY PASEK (Szczegóły Trybu Gry)
        int leftX = 30;
        int leftY = 80;

        g2.setFont(new Font("Impact", Font.PLAIN, 28));
        g2.setColor(goldColor);
        g2.drawString("INFORMACJE", leftX, leftY);

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(Color.WHITE);

        String modeName = "Klasyczny";
        String modeDesc = "Zwykłe bomby.";
        if (gp.gameMode == 1) { modeName = "Ulepszenia"; modeDesc = "Skrzynki dają nagrody!"; }
        if (gp.gameMode == 2) { modeName = "Battle Royale"; modeDesc = "Uciekaj przed burzą!"; }
        if (gp.gameMode == 3) { modeName = "Paint"; modeDesc = "Zamaluj najwięcej planszy!"; }

        g2.setColor(highlightColor);
        g2.drawString("Tryb: " + modeName, leftX, leftY + 30);
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        g2.drawString(modeDesc, leftX, leftY + 50);

        leftY += 100;
        g2.setFont(new Font("Impact", Font.PLAIN, 28));
        g2.setColor(goldColor);
        g2.drawString("MAPA", leftX, leftY);

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(highlightColor);
        String mapName = gp.mapNames.isEmpty() ? "Brak" : gp.mapNames.get(gp.currentMapIndex);
        g2.drawString(mapName, leftX, leftY + 30);

        // 2. PRAWY PASEK (Legenda Przedmiotów)
        if (gp.gameMode == 0) return; // W trybie klasycznym nie ma przedmiotów

        int rightX = winW - sideWidth + 30;
        int rightY = 80;
        int spacing = 50;

        g2.setFont(new Font("Impact", Font.PLAIN, 28));
        g2.setColor(goldColor);
        g2.drawString("LEGENDA", rightX, rightY);

        g2.setFont(new Font("Arial", Font.BOLD, 14));
        int iconSize = 24;

        // Ikona 1: Zasięg
        g2.setColor(Color.MAGENTA); g2.fillRect(rightX, rightY + spacing, iconSize, iconSize);
        g2.setColor(Color.WHITE); g2.drawString("Zasięg Bomb", rightX + 35, rightY + spacing + 17);

        // Ikona 2: Bomby
        g2.setColor(Color.CYAN); g2.fillRect(rightX, rightY + spacing * 2, iconSize, iconSize);
        g2.setColor(Color.WHITE); g2.drawString("Dodatkowa Bomba", rightX + 35, rightY + spacing * 2 + 17);

        // Ikona 3: Szybkość
        g2.setColor(Color.GREEN); g2.fillRect(rightX, rightY + spacing * 3, iconSize, iconSize);
        g2.setColor(Color.WHITE); g2.drawString("Szybkość Ruchu", rightX + 35, rightY + spacing * 3 + 17);

        // Ikona 4: Pancerz
        g2.setColor(Color.LIGHT_GRAY); g2.fillArc(rightX, rightY + spacing * 4 + 4, iconSize, iconSize, 0, 180);
        g2.setColor(Color.DARK_GRAY); g2.drawArc(rightX, rightY + spacing * 4 + 4, iconSize, iconSize, 0, 180);
        g2.setColor(Color.WHITE); g2.drawString("Pancerz / Tarcza", rightX + 35, rightY + spacing * 4 + 17);

        // Ikona 5: Klątwa
        g2.setColor(Color.BLACK); g2.fillRect(rightX, rightY + spacing * 5, iconSize, iconSize);
        g2.setColor(Color.RED); g2.drawRect(rightX, rightY + spacing * 5, iconSize, iconSize);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("!", rightX + 8, rightY + spacing * 5 + 18);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(Color.WHITE); g2.drawString("Klątwa (Ostrożnie!)", rightX + 35, rightY + spacing * 5 + 17);
    }

    private void drawShadowedText(Graphics2D g2, String text, int x, int y, Color mainColor) {
        g2.setColor(Color.BLACK);
        g2.drawString(text, x + 4, y + 4);
        g2.setColor(mainColor);
        g2.drawString(text, x, y);
    }

    private void drawPlayHUD(Graphics2D g2) {
        int yStart = GamePanel.SCREEN_HEIGHT;

        g2.setColor(new Color(15, 15, 20));
        g2.fillRect(0, yStart - 1, GamePanel.SCREEN_WIDTH, GamePanel.HUD_HEIGHT + 2);

        g2.setColor(goldColor);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, yStart, GamePanel.SCREEN_WIDTH, yStart);
        g2.setStroke(new BasicStroke(1));

        int sectionWidth = GamePanel.SCREEN_WIDTH / gp.players.size();
        g2.setFont(tinyFont);

        for (int i = 0; i < gp.players.size(); i++) {
            Player p = gp.players.get(i);
            int startX = i * sectionWidth;

            if (i > 0) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawLine(startX, yStart, startX, GamePanel.TOTAL_SCREEN_HEIGHT);
            }

            g2.setFont(smallFont);
            String title = "P" + p.id;
            if (p.isBot) title += " [BOT]";

            if (gp.gameMode == 3) {
                int score = 0;
                for(int x = 0; x < GamePanel.MAX_SCREEN_COL; x++) {
                    for(int y = 0; y < GamePanel.MAX_SCREEN_ROW; y++) if(gp.paintedMap[x][y] == p.id) score++;
                }
                title += " (" + score + ")";
            }

            g2.setColor(Color.BLACK);
            g2.drawString(title, startX + 11, yStart + 23);
            g2.setColor(p.color);
            g2.drawString(title, startX + 10, yStart + 22);

            g2.setFont(tinyFont);
            int statusY = yStart + 42;

            if (p.isDead) {
                g2.setColor(Color.RED);
                g2.drawString("STATUS: MARTWY", startX + 10, statusY);
            } else if (p.activeCurse != -1) {
                g2.setColor(Color.YELLOW);
                int secondsLeft = (p.curseTimer / 60) + 1;
                g2.drawString("KLĄTWA: " + secondsLeft + "s", startX + 10, statusY);
            } else if (p.armorCount > 0) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString("PANCERZ: ", startX + 10, statusY);
                for (int j = 0; j < p.armorCount; j++) {
                    int armorX = startX + 75 + (j * 15);
                    g2.fillArc(armorX, statusY - 10, 10, 12, 0, 180);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawArc(armorX, statusY - 10, 10, 12, 0, 180);
                    g2.setColor(Color.LIGHT_GRAY);
                }
            } else {
                g2.setColor(Color.GRAY);
                g2.drawString("STATUS: CZYSTY", startX + 10, statusY);
            }

            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString("Zasięg: " + p.bombPower + "   Bomby: " + p.maxBombs, startX + 10, yStart + 62);
            g2.drawString("Szybkość: " + p.speed, startX + 10, yStart + 82);
        }

        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(GamePanel.SCREEN_WIDTH / 2 - 50, 5, 100, 30, 15, 15);
        g2.setColor(goldColor);
        g2.drawRoundRect(GamePanel.SCREEN_WIDTH / 2 - 50, 5, 100, 30, 15, 15);

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
        g2.drawString(timeText, timeX, 26);

        if (gp.gameMode == 2 && !gp.roundEnded) {
            int stormSecondsLeft = 10 - (gp.stormTimer / 60);
            String stormText = "Burza za: " + stormSecondsLeft + "s";
            g2.setFont(smallFont);
            int stX = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(stormText) / 2;
            drawShadowedText(g2, stormText, stX, 55, new Color(255, 100, 255));
        }

        if (gp.roundEnded && gp.currentState != GameState.PAUSE) {
            g2.setFont(titleFont);
            String winnerText = (gp.winnerId == 0) ? "REMIS!" : "WYGRYWA P" + gp.winnerId + "!";
            Color textColor = (gp.winnerId == 0) ? Color.LIGHT_GRAY : Color.WHITE;
            for (Player p : gp.players) if (p.id == gp.winnerId) textColor = p.color;

            int x = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(winnerText) / 2;
            int y = GamePanel.SCREEN_HEIGHT / 2;

            drawShadowedText(g2, winnerText, x, y, textColor);
        }
    }

    private void drawPauseScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.TOTAL_SCREEN_HEIGHT);

        g2.setFont(titleFont);
        String text = "PAUZA";
        int x = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        int y = GamePanel.TOTAL_SCREEN_HEIGHT / 2 - 120;
        drawShadowedText(g2, text, x, y, goldColor);

        g2.setFont(normalFont);
        String[] options = {"Wznów Grę", "Restartuj Rundę", "Opcje Sterowania", "Wyjdź do Menu"};

        for (int i = 0; i < options.length; i++) {
            int optX = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(options[i]) / 2;
            int optY = y + 80 + (i * 50);

            if (commandNum == i) {
                g2.setColor(highlightColor);
                g2.drawString(">> " + options[i] + " <<", optX - 40, optY);
            } else {
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString(options[i], optX, optY);
            }
        }
    }

    private void drawOptionsScreen(Graphics2D g2) {
        g2.setColor(bgColor);
        g2.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.TOTAL_SCREEN_HEIGHT);

        g2.setFont(titleFont);
        String title = "STEROWANIE";
        drawShadowedText(g2, title, GamePanel.SCREEN_WIDTH/2 - g2.getFontMetrics().stringWidth(title)/2, 80, goldColor);

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
            int xPos = 50 + (col * 180);
            int yPos = 140 + (row * 50);

            if (i == 20) {
                xPos = GamePanel.SCREEN_WIDTH/2 - g2.getFontMetrics().stringWidth(options[i])/2;
                yPos = 400;
            }

            if (i >= 10 && i < 15 && gp.numberOfPlayers < 3) g2.setColor(new Color(60, 60, 60));
            else if (i >= 15 && i < 20 && gp.numberOfPlayers < 4) g2.setColor(new Color(60, 60, 60));
            else g2.setColor(Color.WHITE);

            if (commandNum == i) {
                g2.setColor(highlightColor);
                g2.drawString("> " + options[i], xPos - 20, yPos);
                if (gp.keyH.waitingForKey && i != 20) {
                    drawShadowedText(g2, "Naciśnij nowy klawisz...", GamePanel.SCREEN_WIDTH/2 - 100, 460, Color.YELLOW);
                }
            } else {
                g2.drawString(options[i], xPos, yPos);
            }
        }
    }

    private void drawLogoScreen(Graphics2D g2) {
        g2.setColor(bgColor);
        g2.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.TOTAL_SCREEN_HEIGHT);

        g2.setFont(titleFont);
        String text = "STUDIO XYZ";
        int x = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        drawShadowedText(g2, text, x, GamePanel.TOTAL_SCREEN_HEIGHT / 2 - 30, highlightColor);

        g2.setFont(normalFont);
        String subText = "Wciśnij SPACJĘ aby rozpocząć";
        int subX = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(subText) / 2;
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString(subText, subX, GamePanel.TOTAL_SCREEN_HEIGHT / 2 + 30);
    }

    private void drawMenuScreen(Graphics2D g2) {
        g2.setColor(bgColor);
        g2.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.TOTAL_SCREEN_HEIGHT);

        g2.setFont(titleFont);
        String title = "BOMBERMAN";
        int titleX = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        drawShadowedText(g2, title, titleX, 100, goldColor);

        g2.setFont(normalFont);

        String mapName = gp.mapNames.isEmpty() ? "Brak Mapy" : gp.mapNames.get(gp.currentMapIndex);
        String modeName = "Klasyczny";
        if (gp.gameMode == 1) modeName = "Ulepszenia";
        if (gp.gameMode == 2) modeName = "Battle Royale";
        if (gp.gameMode == 3) modeName = "Paint";

        String[] options = {
                "Rozpocznij Grę",
                "< Ilość Graczy: " + gp.numberOfPlayers + " >",
                "< Ilość Botów: " + gp.numberOfBots + " >",
                "< Poziom Botów: " + ((gp.botDifficulty == 0) ? "Łatwy" : "Trudny") + " >",
                "< Mapa: " + mapName + " >",
                "< Tryb: " + modeName + " >",
                "Opcje Sterowania",
                "Wyjście"
        };

        int startY = 170;
        int spacing = 45;

        for (int i = 0; i < options.length; i++) {
            int x = GamePanel.SCREEN_WIDTH / 2 - g2.getFontMetrics().stringWidth(options[i]) / 2;
            int y = startY + (i * spacing);

            if (commandNum == i) {
                g2.setColor(highlightColor);
                g2.drawString(">> " + options[i] + " <<", x - 40, y);
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(options[i], x, y);
            }
        }
    }
}