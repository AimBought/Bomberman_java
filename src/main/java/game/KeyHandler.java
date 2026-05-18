package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;

    public class PlayerInput {
        public int upCode, downCode, leftCode, rightCode, bombCode;
        public boolean up, down, left, right, bomb;
    }

    public PlayerInput p1 = new PlayerInput();
    public PlayerInput p2 = new PlayerInput();
    public PlayerInput p3 = new PlayerInput();
    public PlayerInput p4 = new PlayerInput();

    public boolean waitingForKey = false;
    public int keyToRebind = -1;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (gp.currentState == GameState.OPTIONS) {
            if (waitingForKey) {
                switch (keyToRebind) {
                    case 0: p1.upCode = code; break; case 1: p1.downCode = code; break; case 2: p1.leftCode = code; break; case 3: p1.rightCode = code; break; case 4: p1.bombCode = code; break;
                    case 5: p2.upCode = code; break; case 6: p2.downCode = code; break; case 7: p2.leftCode = code; break; case 8: p2.rightCode = code; break; case 9: p2.bombCode = code; break;
                    case 10: p3.upCode = code; break; case 11: p3.downCode = code; break; case 12: p3.leftCode = code; break; case 13: p3.rightCode = code; break; case 14: p3.bombCode = code; break;
                    case 15: p4.upCode = code; break; case 16: p4.downCode = code; break; case 17: p4.leftCode = code; break; case 18: p4.rightCode = code; break; case 19: p4.bombCode = code; break;
                }
                waitingForKey = false;
                gp.config.saveConfig(this);
                return;
            }

            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) gp.ui.commandNum = 20;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 20) gp.ui.commandNum = 0;
            }
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 20) {
                    gp.currentState = gp.previousState;
                    gp.ui.commandNum = (gp.previousState == GameState.MENU) ? 4 : 2;
                } else {
                    waitingForKey = true;
                    keyToRebind = gp.ui.commandNum;
                }
            }
            if (code == KeyEvent.VK_ESCAPE) {
                gp.currentState = gp.previousState;
                gp.ui.commandNum = (gp.previousState == GameState.MENU) ? 4 : 2;
            }
        }
        else if (gp.currentState == GameState.MENU) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) gp.ui.commandNum = 5;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 5) gp.ui.commandNum = 0;
            }
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                if (gp.ui.commandNum == 1) {
                    gp.numberOfPlayers--;
                    if (gp.numberOfPlayers < 2) gp.numberOfPlayers = 4;
                }
                if (gp.ui.commandNum == 2) {
                    gp.currentMapIndex--;
                    if (gp.currentMapIndex < 0) gp.currentMapIndex = gp.mapNames.size() - 1;
                }
                if (gp.ui.commandNum == 3) {
                    // ZMIANA: Mamy 3 tryby gry
                    gp.gameMode--;
                    if (gp.gameMode < 0) gp.gameMode = 2;
                }
            }
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                if (gp.ui.commandNum == 1) {
                    gp.numberOfPlayers++;
                    if (gp.numberOfPlayers > 4) gp.numberOfPlayers = 2;
                }
                if (gp.ui.commandNum == 2) {
                    gp.currentMapIndex++;
                    if (gp.currentMapIndex > gp.mapNames.size() - 1) gp.currentMapIndex = 0;
                }
                if (gp.ui.commandNum == 3) {
                    gp.gameMode++;
                    if (gp.gameMode > 2) gp.gameMode = 0;
                }
            }
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) { gp.resetGame(); gp.currentState = GameState.PLAY; }
                if (gp.ui.commandNum == 4) {
                    gp.previousState = GameState.MENU;
                    gp.ui.commandNum = 0;
                    gp.currentState = GameState.OPTIONS;
                }
                if (gp.ui.commandNum == 5) System.exit(0);
            }
        }
        else if (gp.currentState == GameState.PAUSE) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) gp.ui.commandNum = 3;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 3) gp.ui.commandNum = 0;
            }
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) gp.currentState = GameState.PLAY;
                if (gp.ui.commandNum == 1) { gp.resetGame(); gp.currentState = GameState.PLAY; }
                if (gp.ui.commandNum == 2) {
                    gp.previousState = GameState.PAUSE;
                    gp.ui.commandNum = 0;
                    gp.currentState = GameState.OPTIONS;
                }
                if (gp.ui.commandNum == 3) { gp.ui.commandNum = 0; gp.currentState = GameState.MENU; }
            }
            if (code == KeyEvent.VK_ESCAPE) { gp.currentState = GameState.PLAY; }
        }
        else if (gp.currentState == GameState.LOGO) {
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) gp.currentState = GameState.MENU;
        }
        else if (gp.currentState == GameState.PLAY) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.currentState = GameState.PAUSE;
                gp.ui.commandNum = 0;
            }

            if (code == p1.upCode) p1.up = true; if (code == p1.downCode) p1.down = true; if (code == p1.leftCode) p1.left = true; if (code == p1.rightCode) p1.right = true; if (code == p1.bombCode) p1.bomb = true;
            if (code == p2.upCode) p2.up = true; if (code == p2.downCode) p2.down = true; if (code == p2.leftCode) p2.left = true; if (code == p2.rightCode) p2.right = true; if (code == p2.bombCode) p2.bomb = true;
            if (code == p3.upCode) p3.up = true; if (code == p3.downCode) p3.down = true; if (code == p3.leftCode) p3.left = true; if (code == p3.rightCode) p3.right = true; if (code == p3.bombCode) p3.bomb = true;
            if (code == p4.upCode) p4.up = true; if (code == p4.downCode) p4.down = true; if (code == p4.leftCode) p4.left = true; if (code == p4.rightCode) p4.right = true; if (code == p4.bombCode) p4.bomb = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (gp.currentState == GameState.PLAY) {
            if (code == p1.upCode) p1.up = false; if (code == p1.downCode) p1.down = false; if (code == p1.leftCode) p1.left = false; if (code == p1.rightCode) p1.right = false; if (code == p1.bombCode) p1.bomb = false;
            if (code == p2.upCode) p2.up = false; if (code == p2.downCode) p2.down = false; if (code == p2.leftCode) p2.left = false; if (code == p2.rightCode) p2.right = false; if (code == p2.bombCode) p2.bomb = false;
            if (code == p3.upCode) p3.up = false; if (code == p3.downCode) p3.down = false; if (code == p3.leftCode) p3.left = false; if (code == p3.rightCode) p3.right = false; if (code == p3.bombCode) p3.bomb = false;
            if (code == p4.upCode) p4.up = false; if (code == p4.downCode) p4.down = false; if (code == p4.leftCode) p4.left = false; if (code == p4.rightCode) p4.right = false; if (code == p4.bombCode) p4.bomb = false;
        }
    }
}