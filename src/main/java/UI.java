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
            case SUMMARY: drawSummaryScreen(g2); break; // Zostawione w razie czego, ale już tam nie wchodzimy
            case PLAY:
            case PAUSE:
                drawPlayHUD(g2);
                if(gp.currentState == GameState.PAUSE) drawPauseScreen(g2);
                break;
        }
    }

    private void drawPlayHUD(Graphics2D g2) {
        int yStart = gp.screenHeight;

        // Rysowanie paska HUD
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, yStart - 1, gp.screenWidth, gp.hudHeight + 2);
        g2.setColor(Color.WHITE);
        g2.drawLine(0, yStart - 1, gp.screenWidth, yStart - 1);

        int sectionWidth = gp.screenWidth / gp.players.size();
        g2.setFont(tinyFont);

        for (int i = 0; i < gp.players.size(); i++) {
            Player p = gp.players.get(i);
            int startX = i * sectionWidth;

            if (i > 0) g2.drawLine(startX, yStart, startX, gp.totalScreenHeight);

            g2.setFont(smallFont);
            g2.setColor(p.color);
            g2.drawString("P" + p.id + (p.isDead ? " (MARTWY)" : ""), startX + 10, yStart + 25);

            g2.setFont(tinyFont);
            g2.setColor(Color.WHITE);
            g2.drawString("Zasięg: " + p.bombPower, startX + 10, yStart + 45);
            g2.drawString("Bomby: " + p.maxBombs, startX + 10, yStart + 65);
            g2.drawString("Szybkość: " + p.speed, startX + 10, yStart + 85);
        }

        // NOWE: TIMER GRY na samej górze ekranu (małe estetyczne pudełko)
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(gp.screenWidth / 2 - 50, 0, 100, 30);
        g2.setColor(Color.WHITE);
        g2.drawRect(gp.screenWidth / 2 - 50, 0, 100, 30);

        g2.setFont(smallFont);
        String timeText;

        // Zależnie od tego, czy gra trwa, formatujemy czas w górę lub odliczamy 5 sekund w dół
        if (!gp.roundEnded) {
            int seconds = (gp.playTimer / 60) % 60;
            int minutes = (gp.playTimer / 60) / 60;
            timeText = String.format("%02d:%02d", minutes, seconds);
        } else {
            int secondsLeft = (gp.deathTimer / 60) + 1;
            timeText = "00:0" + secondsLeft;
        }

        int timeX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(timeText) / 2;
        g2.drawString(timeText, timeX, 22);


        // NOWE: NAPIS KTO WYGRAŁ NA ŚRODKU MAPY
        if (gp.roundEnded && gp.currentState != GameState.PAUSE) {
            g2.setFont(titleFont);
            String winnerText;
            Color textColor = Color.WHITE;

            if (gp.winnerId == 0) {
                winnerText = "REMIS!";
                textColor = Color.LIGHT_GRAY;
            } else {
                winnerText = "WYGRYWA P" + gp.winnerId + "!";
                // Szukamy koloru zwycięzcy
                for (Player p : gp.players) {
                    if (p.id == gp.winnerId) textColor = p.color;
                }
            }

            int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(winnerText) / 2;
            int y = gp.screenHeight / 2;

            // Rysowanie cienia (lekko przesuniętego), by tekst nie zlał się z tłem gry
            g2.setColor(Color.BLACK);
            g2.drawString(winnerText, x + 4, y + 4);

            // Rysowanie właściwego tekstu w kolorze zwycięzcy
            g2.setColor(textColor);
            g2.drawString(winnerText, x, y);
        }
    }

    private void drawPauseScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.totalScreenHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        String text = "PAUZA";
        int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        int y = gp.totalScreenHeight / 2 - 100;
        g2.drawString(text, x, y);

        g2.setFont(normalFont);

        String opt0 = "Wznów Grę";
        int opt0X = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(opt0) / 2;
        g2.drawString(opt0, opt0X, y + 80);
        if (commandNum == 0) g2.drawString(">", opt0X - 30, y + 80);

        String opt1 = "Restartuj Rundę";
        int opt1X = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(opt1) / 2;
        g2.drawString(opt1, opt1X, y + 130);
        if (commandNum == 1) g2.drawString(">", opt1X - 30, y + 130);

        String opt2 = "Opcje Sterowania";
        int opt2X = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(opt2) / 2;
        g2.drawString(opt2, opt2X, y + 180);
        if (commandNum == 2) g2.drawString(">", opt2X - 30, y + 180);

        String opt3 = "Wyjdź do Menu";
        int opt3X = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(opt3) / 2;
        g2.drawString(opt3, opt3X, y + 230);
        if (commandNum == 3) g2.drawString(">", opt3X - 30, y + 230);
    }

    private void drawOptionsScreen(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        String title = "STEROWANIE";
        g2.drawString(title, gp.screenWidth/2 - g2.getFontMetrics().stringWidth(title)/2, 60);

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
                xPos = gp.screenWidth/2 - g2.getFontMetrics().stringWidth(options[i])/2;
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
                    g2.drawString("Wciśnij klawisz...", gp.screenWidth/2 - 70, 420);
                }
            }
        }
    }

    private void drawLogoScreen(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        String text = "STUDIO XYZ PREZENTUJE";
        int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        int y = gp.totalScreenHeight / 2;
        g2.drawString(text, x, y);
        g2.setFont(normalFont);
        String subText = "Wciśnij SPACJĘ aby kontynuować";
        int subX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(subText) / 2;
        g2.drawString(subText, subX, y + 50);
    }

    private void drawMenuScreen(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        String title = "BOMBERMAN";
        int titleX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, 150);

        g2.setFont(normalFont);

        String opt0 = "Nowa Gra";
        int opt0X = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(opt0) / 2;
        g2.drawString(opt0, opt0X, 230);
        if (commandNum == 0) g2.drawString(">", opt0X - 30, 230);

        String opt1 = "< Ilość Graczy: " + gp.numberOfPlayers + " >";
        int opt1X = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(opt1) / 2;
        g2.drawString(opt1, opt1X, 280);
        if (commandNum == 1) g2.drawString(">", opt1X - 30, 280);

        String mapName = gp.mapNames.isEmpty() ? "Brak Mapy" : gp.mapNames.get(gp.currentMapIndex);
        String opt2 = "< Mapa: " + mapName + " >";
        int opt2X = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(opt2) / 2;
        g2.drawString(opt2, opt2X, 330);
        if (commandNum == 2) g2.drawString(">", opt2X - 30, 330);

        String opt3 = "Opcje";
        int opt3X = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(opt3) / 2;
        g2.drawString(opt3, opt3X, 380);
        if (commandNum == 3) g2.drawString(">", opt3X - 30, 380);

        String opt4 = "Wyjście";
        int opt4X = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(opt4) / 2;
        g2.drawString(opt4, opt4X, 430);
        if (commandNum == 4) g2.drawString(">", opt4X - 30, 430);
    }

    // Metoda przestarzała, ale zostawiona awaryjnie w kodzie
    private void drawSummaryScreen(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.setFont(titleFont);
        String text = "KONIEC RUNDY";
        int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        int y = gp.totalScreenHeight / 2;
        g2.drawString(text, x, y);
    }
}