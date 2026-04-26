import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;

    // Musimy przekazać GamePanel, żeby wiedzieć w jakim jesteśmy stanie
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // 1. LOGIKA DLA MENU GŁÓWNEGO
        if (gp.currentState == GameState.MENU) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) gp.ui.commandNum = 2; // Zapętlenie do dołu
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 2) gp.ui.commandNum = 0; // Zapętlenie do góry
            }
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) {
                    gp.resetGame(); // Przygotowuje mapę od nowa przed startem
                    gp.currentState = GameState.PLAY;
                }
                if (gp.ui.commandNum == 1) gp.currentState = GameState.OPTIONS;
                if (gp.ui.commandNum == 2) System.exit(0);
            }
        }
        // 2. LOGIKA DLA EKRANU LOGO
        else if (gp.currentState == GameState.LOGO) {
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
                gp.currentState = GameState.MENU;
            }
        }
        // 3. LOGIKA DLA OPCJI I PODSUMOWANIA (Powrót)
        else if (gp.currentState == GameState.OPTIONS || gp.currentState == GameState.SUMMARY) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.currentState = GameState.MENU;
            }
        }
        // 4. LOGIKA PODCZAS GRY
        else if (gp.currentState == GameState.PLAY) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = true;
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = true;
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = true;
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
            if (code == KeyEvent.VK_SPACE) spacePressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (gp.currentState == GameState.PLAY) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = false;
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = false;
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = false;
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = false;
            if (code == KeyEvent.VK_SPACE) spacePressed = false;
        }
    }
}