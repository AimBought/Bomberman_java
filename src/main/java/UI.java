import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class UI {
    GamePanel gp;
    Font titleFont;
    Font normalFont;

    public int commandNum = 0;

    public UI(GamePanel gp) {
        this.gp = gp;
        // ZMIANA: Zmniejszono czcionkę z 60 na 48
        titleFont = new Font("Arial", Font.BOLD, 48);
        normalFont = new Font("Arial", Font.PLAIN, 28);
    }

    public void draw(Graphics2D g2) {
        switch (gp.currentState) {
            case LOGO:
                drawLogoScreen(g2);
                break;
            case MENU:
                drawMenuScreen(g2);
                break;
            case OPTIONS:
                drawOptionsScreen(g2);
                break;
            case SUMMARY:
                drawSummaryScreen(g2);
                break;
            case PLAY:
                break;
        }
    }

    private void drawLogoScreen(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        String text = "STUDIO XYZ PREZENTUJE";

        int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        int y = gp.screenHeight / 2;

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

        String opt1 = "Nowa Gra";
        int opt1X = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(opt1) / 2;
        g2.drawString(opt1, opt1X, 300);
        if (commandNum == 0) g2.drawString(">", opt1X - 30, 300);

        String opt2 = "Opcje";
        int opt2X = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(opt2) / 2;
        g2.drawString(opt2, opt2X, 350);
        if (commandNum == 1) g2.drawString(">", opt2X - 30, 350);

        String opt3 = "Wyjście";
        int opt3X = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(opt3) / 2;
        g2.drawString(opt3, opt3X, 400);
        if (commandNum == 2) g2.drawString(">", opt3X - 30, 400);
    }

    private void drawOptionsScreen(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        g2.drawString("OPCJE", 100, 150);
        g2.setFont(normalFont);
        g2.drawString("Tutaj będą ustawienia.", 100, 250);
        g2.drawString("Wciśnij ESC, aby wrócić.", 100, 300);
    }

    private void drawSummaryScreen(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.setFont(titleFont);
        String text = "KONIEC RUNDY";
        int x = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(text) / 2;
        g2.drawString(text, x, gp.screenHeight / 2);

        g2.setColor(Color.WHITE);
        g2.setFont(normalFont);
        String subText = "Wciśnij ESC, aby wrócić do MENU";
        int subX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(subText) / 2;
        g2.drawString(subText, subX, gp.screenHeight / 2 + 50);
    }
}