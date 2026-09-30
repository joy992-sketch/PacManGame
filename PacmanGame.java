import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

/**
 * Pac-Man Arcade Edition (Google Doodle theme) - Java Swing port.
 *
 * Compile: javac PacmanGame.java
 * Run: java PacmanGame
 *
 * Optional (for the exact retro fonts): put PressStart2P-Regular.ttf and
 * VT323-Regular.ttf in a folder called "fonts" next to this file.
 */
public class PacmanGame extends JPanel {

    // ------------------------------------------------------------------ constants
    static final int W = 928, H = 772;
    static final int TILE = 20;

    // 1: Wall, 0: Dot, 2: Power Pellet, 3: Empty, 4: Ghost House
    static final int[][] MAP = {
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1 },
            { 1, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0,
                    0, 2, 1 },
            { 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1,
                    1, 0, 1 },
            { 1, 0, 1, 3, 1, 0, 1, 0, 1, 3, 1, 0, 1, 0, 1, 3, 1, 0, 1, 1, 0, 1, 3, 1, 0, 1, 0, 1, 3, 1, 0, 1, 0, 1, 3,
                    1, 0, 1 },
            { 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1,
                    1, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 1 },
            { 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 4, 4, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1,
                    1, 0, 1 },
            { 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 4, 4, 4, 4, 4, 4, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0,
                    0, 0, 1 },
            { 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0,
                    1, 1, 1 },
            { 1, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,
                    0, 2, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1 }
    };
    static final int COLS = MAP[0].length, ROWS = MAP.length;

    // Tailwind palette
    static final int SLATE950 = 0x020617, SLATE900 = 0x0f172a, SLATE800 = 0x1e293b, SLATE600 = 0x475569,
            SLATE500 = 0x64748b, SLATE400 = 0x94a3b8, SLATE300 = 0xcbd5e1, SLATE200 = 0xe2e8f0;
    static final int PURPLE950 = 0x3b0764, PURPLE900 = 0x581c87, PURPLE600 = 0x9333ea, PURPLE500 = 0xa855f7,
            PURPLE400 = 0xc084fc, PURPLE200 = 0xe9d5ff;
    static final int YELLOW500 = 0xeab308, YELLOW400 = 0xfacc15, YELLOW300 = 0xfde047;
    static final int CYAN950 = 0x083344, CYAN900 = 0x164e63, CYAN800 = 0x155e75, CYAN500 = 0x06b6d4,
            CYAN400 = 0x22d3ee, CYAN300 = 0x67e8f9, CYAN200 = 0xa5f3fc;
    static final int PINK950 = 0x500724, PINK900 = 0x831843, PINK800 = 0x9d174d, PINK500 = 0xec4899,
            PINK400 = 0xf472b6, PINK200 = 0xfbcfe8;
    static final int GREEN950 = 0x052e16, GREEN800 = 0x166534, GREEN400 = 0x4ade80;
    static final int RED950 = 0x450a0a, RED500 = 0xef4444, RED200 = 0xfecaca;
    static final int BLUE600 = 0x2563eb, GRAY400 = 0x9ca3af, BODY = 0x050508;

    static final FontRenderContext FRC = new FontRenderContext(null, true, true);

    // ------------------------------------------------------------------ fonts
    static Font PIXEL, VT;

    static Font loadFont(String name, Font fallback) {
        for (String dir : new String[] { "fonts/", "./", "" }) {
            File f = new File(dir + name);
            if (f.exists()) {
                try {
                    return Font.createFont(Font.TRUETYPE_FONT, f);
                } catch (Exception ignored) {
                }
            }
        }
        return fallback;
    }

    static Font px(float size, double tracking) {
        return styled(PIXEL, size, tracking);
    }

    static Font vt(float size, double tracking) {
        return styled(VT, size, tracking);
    }

    static Font styled(Font base, float size, double tracking) {
        Map<java.awt.font.TextAttribute, Object> m = new HashMap<>();
        m.put(TextAttribute.TRACKING, tracking);
        return base.deriveFont(size).deriveFont(m);
    }

    static Color c(int rgb, double a) {
        return new Color((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255,
                (int) Math.round(Math.max(0, Math.min(1, a)) * 255));
    }

    // ------------------------------------------------------------------ sound
    static class Sound {
        volatile boolean muted = false;
        static final float SR = 44100f;
        final ExecutorService ex = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        void tone(String type, double f0, double f1, boolean exp, double g0, double dur) {
            if (muted)
                return;
            ex.submit(() -> {
                try {
                    int n = (int) (SR * dur);
                    byte[] buf = new byte[n * 2];
                    double phase = 0;
                    for (int i = 0; i < n; i++) {
                        double t = (double) i / n;
                        double f = exp ? f0 * Math.pow(f1 / f0, t) : f0 + (f1 - f0) * t;
                        phase += f / SR;
                        phase -= Math.floor(phase);
                        double s;
                        switch (type) {
                            case "sine":
                                s = Math.sin(2 * Math.PI * phase);
                                break;
                            case "square":
                                s = phase < 0.5 ? 1 : -1;
                                break;
                            case "sawtooth":
                                s = 2 * phase - 1;
                                break;
                            default:
                                s = phase < 0.5 ? 4 * phase - 1 : 3 - 4 * phase; // triangle
                        }
                        double gain = g0 + (0.01 - g0) * t;
                        short v = (short) (s * gain * 32767);
                        buf[2 * i] = (byte) v;
                        buf[2 * i + 1] = (byte) (v >> 8);
                    }
                    Clip clip = AudioSystem.getClip();
                    clip.open(new AudioFormat(SR, 16, 1, true, false), buf, 0, buf.length);
                    clip.addLineListener(e -> {
                        if (e.getType() == LineEvent.Type.STOP)
                            clip.close();
                    });
                    clip.start();
                } catch (Exception ignored) {
                }
            });
        }

        void playWaka() {
            tone("triangle", 400, 200, true, 0.15, 0.08);
        }

        void playEatGhost() {
            tone("square", 300, 800, true, 0.2, 0.2);
        }

        void playPowerPellet() {
            tone("sine", 150, 300, false, 0.2, 0.15);
        }

        void playDeath() {
            tone("sawtooth", 500, 50, false, 0.25, 0.6);
        }
    }

    final Sound sound = new Sound();

    // ------------------------------------------------------------------ game state
    int score = 0, lives = 3, frightenTimer = 0, displayedScore = 0;
    int highScore;
    String hsDisplay;
    boolean gameOver = false, gamePaused = false, gameStarted = false;
    int[][] currentMap = copyMap();
    final Preferences prefs = Preferences.userNodeForPackage(PacmanGame.class);

    // Pac-Man
    double pX = 18 * TILE + TILE / 2.0, pY = 9 * TILE + TILE / 2.0;
    final double pRadius = TILE / 2.0 - 2, pSpeed = 2;
    int pDirX = 0, pDirY = 0, pNextX = 0, pNextY = 0;
    double mouthAngle = 0.2, mouthSpeed = 0.03;

    void pacReset() {
        pX = 18 * TILE + TILE / 2.0;
        pY = 9 * TILE + TILE / 2.0;
        pDirX = 0;
        pDirY = 0;
        pNextX = 0;
        pNextY = 0;
    }

    static int[][] copyMap() {
        int[][] m = new int[ROWS][];
        for (int i = 0; i < ROWS; i++)
            m[i] = MAP[i].clone();
        return m;
    }

    boolean isWallCollision(double x, double y) {
        return isWallCollision(x, y, pRadius - 2);
    }

    boolean isWallCollision(double x, double y, double radius) {

        // Slightly smaller collision box prevents Pac-Man from
        // getting caught on wall corners.
        double r = radius;

        double[][] pts = {
                { x - r, y - r },
                { x + r, y - r },
                { x - r, y + r },
                { x + r, y + r }
        };

        for (double[] pt : pts) {

            int gx = (int) Math.floor(pt[0] / TILE);
            int gy = (int) Math.floor(pt[1] / TILE);

            if (gy < 0 || gy >= ROWS || gx < 0 || gx >= COLS)
                return true;

            if (currentMap[gy][gx] == 1)
                return true;
        }

        return false;
    }

    double tileCenterX(double x) {
        int tile = (int) Math.floor(x / TILE);
        return tile * TILE + TILE / 2.0;
    }

    double tileCenterY(double y) {
        int tile = (int) Math.floor(y / TILE);
        return tile * TILE + TILE / 2.0;
    }

    boolean nearTileCenter(double value) {
        double center = Math.floor(value / TILE) * TILE + TILE / 2.0;
        return Math.abs(value - center) <= pSpeed + 0.25;
    }

    boolean canMoveFromCenter(int gx, int gy, int dx, int dy) {

        int nx = gx + dx;
        int ny = gy + dy;

        if (nx < 0 || nx >= COLS || ny < 0 || ny >= ROWS)
            return false;

        return currentMap[ny][nx] != 1;
    }

    // Ghosts
    class Ghost {

        final int color;
        final int initialX, initialY;

        double x, y;

        double speed = 1.6;

        int dirX = 0;
        int dirY = -1;

        boolean frightened = false;

        Ghost(int color, int gx, int gy) {

            this.color = color;

            initialX = gx;
            initialY = gy;

            x = gx * TILE + TILE / 2.0;
            y = gy * TILE + TILE / 2.0;
        }

        void reset() {

            x = initialX * TILE + TILE / 2.0;
            y = initialY * TILE + TILE / 2.0;

            dirX = 0;
            dirY = -1;

            frightened = false;
        }

        boolean ghostCanMove(int gx, int gy, int dx, int dy) {

            int nx = gx + dx;
            int ny = gy + dy;

            if (nx < 0 || nx >= COLS ||
                    ny < 0 || ny >= ROWS)
                return false;

            return currentMap[ny][nx] != 1;
        }

        void chooseDirection(int gx, int gy) {

            List<int[]> possible = new ArrayList<>();

            int[][] dirs = {
                    { 0, -1 },
                    { 0, 1 },
                    { -1, 0 },
                    { 1, 0 }
            };

            /*
             * First try directions without immediately reversing.
             */
            for (int[] d : dirs) {

                if (d[0] == -dirX &&
                        d[1] == -dirY)
                    continue;

                if (ghostCanMove(gx, gy, d[0], d[1])) {

                    possible.add(d);
                }
            }

            /*
             * Dead end:
             * allow reverse direction.
             */
            if (possible.isEmpty()) {

                for (int[] d : dirs) {

                    if (ghostCanMove(gx, gy, d[0], d[1])) {

                        possible.add(d);
                    }
                }
            }

            if (!possible.isEmpty()) {

                int[] choice = possible.get(
                        (int) (Math.random() * possible.size()));

                dirX = choice[0];
                dirY = choice[1];
            }
        }

        void update() {

            /*
             * IMPORTANT FIX
             *
             * Your old code required:
             *
             * x % TILE == 10
             *
             * But speed = 1.6.
             *
             * Therefore the ghost could move past 10 without
             * ever becoming exactly 10.
             *
             * Now we detect when it is CLOSE to the tile center
             * and snap the ghost to the center.
             */

            double centerX = Math.floor(x / TILE) * TILE + TILE / 2.0;

            double centerY = Math.floor(y / TILE) * TILE + TILE / 2.0;

            boolean nearX = Math.abs(x - centerX) <= speed + 0.2;

            boolean nearY = Math.abs(y - centerY) <= speed + 0.2;

            if (nearX && nearY) {

                /*
                 * Snap perfectly to tile center.
                 */
                x = centerX;
                y = centerY;

                int gx = (int) Math.floor(x / TILE);
                int gy = (int) Math.floor(y / TILE);

                chooseDirection(gx, gy);
            }

            double nextX = x + dirX * speed;
            double nextY = y + dirY * speed;

            /*
             * Ghost has its OWN collision radius now.
             *
             * Your previous code used Pac-Man's radius when
             * checking the ghost.
             */
            double ghostRadius = TILE / 2.0 - 3;

            if (!isWallCollision(
                    nextX,
                    nextY,
                    ghostRadius)) {

                x = nextX;
                y = nextY;

            } else {

                /*
                 * Emergency recovery.
                 *
                 * If a ghost reaches a wall because of floating
                 * point movement, snap him to the current tile
                 * and choose another direction.
                 */

                int gx = (int) Math.floor(x / TILE);
                int gy = (int) Math.floor(y / TILE);

                x = gx * TILE + TILE / 2.0;
                y = gy * TILE + TILE / 2.0;

                chooseDirection(gx, gy);
            }
        }

        void draw(Graphics2D g) {

            Color fill;

            if (frightened) {

                fill = (frightenTimer < 100 &&
                        Math.floor(frightenTimer / 10.0) % 2 == 0)

                                ? Color.WHITE
                                : new Color(0x0000FF);

            } else {

                fill = new Color(color);
            }

            double radius = TILE / 2.0 - 2;

            Path2D p = new Path2D.Double();

            p.append(

                    new Arc2D.Double(
                            x - radius,
                            y - 2 - radius,
                            radius * 2,
                            radius * 2,
                            180,
                            -180,
                            Arc2D.OPEN),

                    false);

            p.lineTo(
                    x + radius,
                    y + radius);

            int feet = 3;

            double fw = (radius * 2) / feet;

            for (int i = 0; i < feet; i++) {

                p.lineTo(

                        x + radius -
                                (i * fw) -
                                (fw / 2),

                        y + radius -
                                (i % 2 == 0 ? 3 : 0));
            }

            p.lineTo(
                    x - radius,
                    y + radius);

            p.closePath();

            g.setColor(fill);

            g.fill(p);

            if (!frightened) {

                g.setColor(Color.WHITE);

                g.fill(

                        new Ellipse2D.Double(
                                x - 4 - 3,
                                y - 4 - 3,
                                6,
                                6));

                g.fill(

                        new Ellipse2D.Double(
                                x + 4 - 3,
                                y - 4 - 3,
                                6,
                                6));

                g.setColor(
                        new Color(0x0000FF));

                g.fill(

                        new Ellipse2D.Double(

                                x - 4 +
                                        dirX * 1.5 -
                                        1.5,

                                y - 4 +
                                        dirY * 1.5 -
                                        1.5,

                                3,
                                3));

                g.fill(

                        new Ellipse2D.Double(

                                x + 4 +
                                        dirX * 1.5 -
                                        1.5,

                                y - 4 +
                                        dirY * 1.5 -
                                        1.5,

                                3,
                                3));

            } else {

                g.setColor(
                        new Color(0xFFCC00));

                g.fill(

                        new Rectangle2D.Double(
                                x - 4,
                                y - 4,
                                2,
                                2));

                g.fill(

                        new Rectangle2D.Double(
                                x + 2,
                                y - 4,
                                2,
                                2));
            }
        }
    }

    List<Ghost> ghosts = new ArrayList<>();

    // ------------------------------------------------------------------ game logic
    void updateGame() {

        if (gameOver ||
                gamePaused ||
                !gameStarted)
            return;

        // ---------------------------------------------------------
        // Frightened timer
        // ---------------------------------------------------------

        if (frightenTimer > 0) {

            frightenTimer--;

            if (frightenTimer == 0) {

                for (Ghost gh : ghosts)
                    gh.frightened = false;
            }
        }

        // =========================================================
        // PAC-MAN MOVEMENT
        // =========================================================

        /*
         * Find the exact center of Pac-Man's current tile.
         */

        double centerX = Math.floor(pX / TILE) * TILE + TILE / 2.0;

        double centerY = Math.floor(pY / TILE) * TILE + TILE / 2.0;

        boolean nearX = Math.abs(pX - centerX) <= pSpeed + 0.25;

        boolean nearY = Math.abs(pY - centerY) <= pSpeed + 0.25;

        /*
         * Direction change is allowed only near an intersection.
         *
         * This prevents Pac-Man from clipping the corner of a
         * wall when the player presses another direction early.
         */

        if ((pNextX != 0 || pNextY != 0)
                && nearX && nearY) {

            int currentGX = (int) Math.floor(centerX / TILE);

            int currentGY = (int) Math.floor(centerY / TILE);

            if (canMoveFromCenter(
                    currentGX,
                    currentGY,
                    pNextX,
                    pNextY)) {

                /*
                 * Snap Pac-Man perfectly to the corridor center
                 * before turning.
                 */

                pX = centerX;
                pY = centerY;

                pDirX = pNextX;
                pDirY = pNextY;
            }
        }

        /*
         * Keep Pac-Man centered while moving through corridors.
         *
         * Vertical movement:
         * X must remain at the center of the column.
         */

        if (pDirY != 0) {

            double targetX = Math.floor(pX / TILE) * TILE +
                    TILE / 2.0;

            double diff = targetX - pX;

            if (Math.abs(diff) <= pSpeed)

                pX = targetX;

            else

                pX += Math.signum(diff) *
                        Math.min(pSpeed, Math.abs(diff));
        }

        /*
         * Horizontal movement:
         * Y must remain at the center of the row.
         */

        if (pDirX != 0) {

            double targetY = Math.floor(pY / TILE) * TILE +
                    TILE / 2.0;

            double diff = targetY - pY;

            if (Math.abs(diff) <= pSpeed)

                pY = targetY;

            else

                pY += Math.signum(diff) *
                        Math.min(pSpeed, Math.abs(diff));
        }

        // ---------------------------------------------------------
        // Move Pac-Man
        // ---------------------------------------------------------

        double nx = pX + pDirX * pSpeed;

        double ny = pY + pDirY * pSpeed;

        if (!isWallCollision(nx, ny)) {

            pX = nx;
            pY = ny;

        } else {

            /*
             * Stop cleanly instead of repeatedly pushing Pac-Man
             * into the wall.
             */

            pDirX = 0;
            pDirY = 0;
        }

        // =========================================================
        // DOT / POWER PELLET
        // =========================================================

        int gx = (int) Math.floor(pX / TILE);

        int gy = (int) Math.floor(pY / TILE);

        if (gy >= 0 &&
                gy < ROWS &&
                gx >= 0 &&
                gx < COLS) {

            if (currentMap[gy][gx] == 0) {

                currentMap[gy][gx] = 3;

                score += 10;

                sound.playWaka();

            } else if (currentMap[gy][gx] == 2) {

                currentMap[gy][gx] = 3;

                score += 50;

                frightenTimer = 300;

                for (Ghost gh : ghosts)

                    gh.frightened = true;

                sound.playPowerPellet();
            }
        }

        // =========================================================
        // HIGH SCORE
        // =========================================================

        displayedScore = score;

        if (score > highScore) {

            highScore = score;

            prefs.putInt(
                    "pacman_high_score",
                    highScore);

            hsDisplay = String.format(
                    "%06d",
                    highScore);
        }

        // =========================================================
        // GHOSTS
        // =========================================================

        for (Ghost ghost : ghosts) {

            ghost.update();

            double dist = Math.hypot(
                    pX - ghost.x,
                    pY - ghost.y);

            if (dist < TILE - 4) {

                // --------------------------------------------------
                // Eat frightened ghost
                // --------------------------------------------------

                if (ghost.frightened) {

                    sound.playEatGhost();

                    score += 200;

                    ghost.reset();

                } else {

                    // --------------------------------------------------
                    // Pac-Man dies
                    // --------------------------------------------------

                    sound.playDeath();

                    lives--;

                    if (lives <= 0) {

                        gameOver = true;

                        showOverlay(
                                "GAME OVER",
                                "FINAL SCORE: " + score);

                    } else {

                        pacReset();

                        for (Ghost gh : ghosts)

                            gh.reset();
                    }
                }
            }
        }

        // =========================================================
        // VICTORY CHECK
        // =========================================================

        boolean dotsLeft = false;

        for (int r = 0; r < ROWS && !dotsLeft; r++) {

            for (int cc = 0; cc < COLS; cc++) {

                if (currentMap[r][cc] == 0 ||
                        currentMap[r][cc] == 2) {

                    dotsLeft = true;

                    break;
                }
            }
        }

        if (!dotsLeft) {

            gameOver = true;

            showOverlay(
                    "VICTORY!",
                    "YOU CLEARED THE MAZE!");
        }
    }

    void startNewGame() {
        currentMap = copyMap();
        score = 0;
        displayedScore = 0;
        lives = 3;
        gameOver = false;
        gamePaused = false;
        gameStarted = true;
        pacReset();
        ghosts = new ArrayList<>();
        ghosts.add(new Ghost(0xFF0000, 17, 5));
        ghosts.add(new Ghost(0xFFB8FF, 18, 5));
        ghosts.add(new Ghost(0x00FFFF, 19, 5));
        ghosts.add(new Ghost(0xFFB852, 20, 5));
        hideOverlay();
    }

    // overlay state
    boolean overlayVisible = false;
    String overlayTitle = "READY!", overlaySub = "PRESS ANY KEY TO START";

    void showOverlay(String t, String s) {
        overlayTitle = t;
        overlaySub = s;
        overlayVisible = true;
    }

    void hideOverlay() {
        overlayVisible = false;
    }

    // ------------------------------------------------------------------ UI state
    boolean screenGame = false;
    String modal = null; // "how", "about", "exit"
    boolean muteTouched = false;

    static class Btn {
        final String id;
        final Rectangle2D r;
        final boolean arcade;

        Btn(String id, Rectangle2D r, boolean arcade) {
            this.id = id;
            this.r = r;
            this.arcade = arcade;
        }
    }

    List<Btn> btns = new ArrayList<>(), btnsNew = new ArrayList<>();
    final Map<String, Float> scl = new HashMap<>();
    String hoverId = null, pressedId = null;
    boolean dry = false, drawingModal = false;
    BufferedImage scan;

    // ------------------------------------------------------------------
    // construction
    public PacmanGame() {
        setPreferredSize(new Dimension(W, H));
        setBackground(new Color(BODY));
        setFocusable(true);

        highScore = prefs.getInt("pacman_high_score", 0);
        hsDisplay = String.format("%06d", highScore);
        buildScanlines();

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hover(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                hover(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressedId = hit(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                String h = hit(e.getPoint());
                if (h != null && h.equals(pressedId))
                    click(h);
                pressedId = null;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverId = null;
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() != KeyEvent.KEY_PRESSED)
                return false;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    pNextX = 0;
                    pNextY = -1;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    pNextX = 0;
                    pNextY = 1;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    pNextX = -1;
                    pNextY = 0;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    pNextX = 1;
                    pNextY = 0;
                    break;
                case KeyEvent.VK_P:
                    gamePaused = !gamePaused;
                    if (gamePaused)
                        showOverlay("PAUSED", "PRESS P TO RESUME");
                    else
                        hideOverlay();
                    break;
                default:
            }
            return false;
        });

        new javax.swing.Timer(16, e -> tick()).start();
    }

    String hit(Point p) {
        for (int i = btns.size() - 1; i >= 0; i--)
            if (btns.get(i).r.contains(p))
                return btns.get(i).id;
        return null;
    }

    void hover(MouseEvent e) {
        hoverId = hit(e.getPoint());
        setCursor(hoverId != null ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
    }

    void click(String id) {
        switch (id) {
            case "start":
                screenGame = true;
                startNewGame();
                break;
            case "how":
                modal = "how";
                break;
            case "about":
                modal = "about";
                break;
            case "exit":
                modal = "exit";
                break;
            case "closeHow":
            case "closeAbout":
            case "restartExit":
                modal = null;
                break;
            case "mute":
                sound.muted = !sound.muted;
                muteTouched = true;
                break;
            case "back":
                screenGame = false;
                gameStarted = false;
                break;
            case "pause":
                gamePaused = !gamePaused;
                if (gamePaused)
                    showOverlay("PAUSED", "PRESS PAUSE TO RESUME");
                else
                    hideOverlay();
                break;
            case "overlayBtn":
                if (gameOver)
                    startNewGame();
                else {
                    gamePaused = false;
                    hideOverlay();
                }
                break;
            default:
        }
    }

    void tick() {
        for (Btn b : btns) {
            if (!b.arcade)
                continue;
            float cur = scl.getOrDefault(b.id, 1f);
            float target = b.id.equals(pressedId) ? 0.97f : (b.id.equals(hoverId) ? 1.03f : 1f);
            scl.put(b.id, cur + (target - cur) * 0.25f);
        }
        updateGame();
        repaint();
    }

    // ------------------------------------------------------------------ CRT
    // scanlines
    void buildScanlines() {
        scan = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D s = scan.createGraphics();
        for (int x = 0; x < W; x++) {
            double p = (x % 6) / 6.0, r, gr, b, a;
            if (p < 0.5) {
                double t = p / 0.5;
                r = 255 * (1 - t);
                gr = 255 * t;
                b = 0;
                a = 0.03 + (0.01 - 0.03) * t;
            } else {
                double t = (p - 0.5) / 0.5;
                r = 0;
                gr = 255 * (1 - t);
                b = 255 * t;
                a = 0.01 + 0.02 * t;
            }
            s.setColor(new Color((int) r, (int) gr, (int) b, (int) Math.round(a * 255)));
            s.fillRect(x, 0, 1, H);
        }
        s.setColor(new Color(0, 0, 0, 64));
        for (int y = 2; y < H; y += 4)
            s.fillRect(0, y, W, 2);
        s.dispose();
    }

    // ------------------------------------------------------------------ drawing
    // helpers
    double tw(String s, Font f) {
        return new TextLayout(s.isEmpty() ? " " : s, f, FRC).getAdvance();
    }

    /** align: 0 left, 1 center, 2 right. cy = vertical centre of the text line. */
    void drawT(Graphics2D g, String s, double x, double cy, Font f, Color col, int align) {
        if (dry)
            return;
        TextLayout tl = new TextLayout(s.isEmpty() ? " " : s, f, FRC);
        double w = tl.getAdvance();
        double lx = align == 0 ? x : align == 1 ? x - w / 2 : x - w;
        double by = cy + (tl.getAscent() - tl.getDescent()) / 2;
        g.setColor(col);
        tl.draw(g, (float) lx, (float) by);
    }

    void glowT(Graphics2D g, String s, double x, double cy, Font f, Color col, int glow, int align) {
        if (dry)
            return;
        TextLayout tl = new TextLayout(s, f, FRC);
        double w = tl.getAdvance();
        double lx = align == 1 ? x - w / 2 : x;
        double by = cy + (tl.getAscent() - tl.getDescent()) / 2;
        Shape o = tl.getOutline(AffineTransform.getTranslateInstance(lx, by));
        for (int sw = 20; sw >= 4; sw -= 4) {
            g.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(c(glow, 0.10));
            g.draw(o);
        }
        g.setColor(col);
        tl.draw(g, (float) lx, (float) by);
    }

    List<String> wrap(String s, Font f, double maxW) {
        List<String> out = new ArrayList<>();
        String line = "";
        for (String w : s.split(" ")) {
            String t = line.isEmpty() ? w : line + " " + w;
            if (line.isEmpty() || tw(t, f) <= maxW)
                line = t;
            else {
                out.add(line);
                line = w;
            }
        }
        if (!line.isEmpty())
            out.add(line);
        return out;
    }

    static RoundRectangle2D rr(double x, double y, double w, double h, double arc) {
        return new RoundRectangle2D.Double(x, y, w, h, arc, arc);
    }

    void fillRR(Graphics2D g, double x, double y, double w, double h, double arc, Color col) {
        if (dry)
            return;
        g.setColor(col);
        g.fill(rr(x, y, w, h, arc));
    }

    void strokeRR(Graphics2D g, double x, double y, double w, double h, double arc, double bw, Color col) {
        if (dry)
            return;
        g.setColor(col);
        g.setStroke(new BasicStroke((float) bw));
        g.draw(rr(x + bw / 2, y + bw / 2, w - bw, h - bw, Math.max(0, arc - bw)));
    }

    void outerGlow(Graphics2D g, RoundRectangle2D r, int rgb, double a, int blur) {
        if (dry)
            return;
        g.setStroke(new BasicStroke(2f));
        for (int d = blur; d >= 1; d--) {
            double t = 1 - (double) d / blur;
            g.setColor(c(rgb, a * t * t * 0.5));
            g.draw(new RoundRectangle2D.Double(r.getX() - d, r.getY() - d, r.getWidth() + 2 * d, r.getHeight() + 2 * d,
                    r.getArcWidth() + 2 * d, r.getArcHeight() + 2 * d));
        }
    }

    void innerGlow(Graphics2D g, RoundRectangle2D r, int rgb, double a, int blur) {
        if (dry)
            return;
        Shape oc = g.getClip();
        g.clip(r);
        g.setStroke(new BasicStroke(2f));
        for (int d = 0; d < blur; d++) {
            double t = 1 - (double) d / blur;
            g.setColor(c(rgb, a * t * t * 0.5));
            g.draw(new RoundRectangle2D.Double(r.getX() + d, r.getY() + d, r.getWidth() - 2 * d, r.getHeight() - 2 * d,
                    Math.max(0, r.getArcWidth() - 2 * d), Math.max(0, r.getArcHeight() - 2 * d)));
        }
        g.setClip(oc);
    }

    // Buttons ------------------------------------------------------------
    AffineTransform btnStart(Graphics2D g, String id, double x, double y, double w, double h, boolean arcade) {
        if (!dry && (modal == null || drawingModal))
            btnsNew.add(new Btn(id, new Rectangle2D.Double(x, y, w, h), arcade));
        AffineTransform old = g.getTransform();
        if (arcade) {
            float s = scl.getOrDefault(id, 1f);
            g.translate(x + w / 2, y + h / 2);
            g.scale(s, s);
            g.translate(-(x + w / 2), -(y + h / 2));
        }
        return old;
    }

    boolean isHover(String id) {
        return id.equals(hoverId);
    }

    /**
     * The .btn-arcade style: hover -> pink background, white text, pink glow,
     * scale.
     */
    void arcade(Graphics2D g, String id, double x, double y, double w, double h, Color bg, Color border, Color fg,
            String label, Font f, String icon) {
        AffineTransform old = btnStart(g, id, x, y, w, h, true);
        if (!dry) {
            boolean hv = isHover(id);
            RoundRectangle2D r = rr(x, y, w, h, 8);
            if (hv)
                outerGlow(g, r, 0xff0080, 1.0, 20);
            g.setColor(hv ? new Color(0xff0080) : bg);
            g.fill(r);
            strokeRR(g, x, y, w, h, 8, 2, border);
            Color tc = hv ? Color.WHITE : fg;
            if (icon == null) {
                drawT(g, label, x + w / 2, y + h / 2, f, tc, 1);
            } else {
                drawT(g, label, x + 16, y + h / 2, f, tc, 0);
                icon(g, icon, x + w - 16 - 6, y + h / 2, 12, tc);
            }
        }
        g.setTransform(old);
    }

    // Icons (simple vector stand-ins for the FontAwesome glyphs) -------------
    void icon(Graphics2D g, String kind, double cx, double cy, double s, Color col) {
        if (dry)
            return;
        g.setColor(col);
        g.setStroke(new BasicStroke((float) Math.max(1.2, s / 9), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        switch (kind) {
            case "play": {
                Path2D p = new Path2D.Double();
                p.moveTo(cx - s * 0.3, cy - s * 0.45);
                p.lineTo(cx + s * 0.45, cy);
                p.lineTo(cx - s * 0.3, cy + s * 0.45);
                p.closePath();
                g.fill(p);
                break;
            }
            case "gamepad": {
                g.draw(rr(cx - s * 0.65, cy - s * 0.32, s * 1.3, s * 0.64, s * 0.4));
                g.draw(new Line2D.Double(cx - s * 0.38, cy, cx - s * 0.12, cy));
                g.draw(new Line2D.Double(cx - s * 0.25, cy - s * 0.13, cx - s * 0.25, cy + s * 0.13));
                g.fill(new Ellipse2D.Double(cx + s * 0.12, cy - s * 0.1, s * 0.14, s * 0.14));
                g.fill(new Ellipse2D.Double(cx + s * 0.32, cy - s * 0.02, s * 0.14, s * 0.14));
                break;
            }
            case "users": {
                g.fill(new Ellipse2D.Double(cx - s * 0.45, cy - s * 0.45, s * 0.4, s * 0.4));
                g.fill(new Ellipse2D.Double(cx + s * 0.05, cy - s * 0.45, s * 0.4, s * 0.4));
                g.fill(new Arc2D.Double(cx - s * 0.6, cy + s * 0.0, s * 0.7, s * 0.7, 0, 180, Arc2D.PIE));
                g.fill(new Arc2D.Double(cx - s * 0.1, cy + s * 0.0, s * 0.7, s * 0.7, 0, 180, Arc2D.PIE));
                break;
            }
            case "power": {
                g.draw(new Arc2D.Double(cx - s * 0.5, cy - s * 0.45, s, s, 120, 300, Arc2D.OPEN));
                g.draw(new Line2D.Double(cx, cy - s * 0.6, cx, cy));
                break;
            }
            case "vol":
            case "volx": {
                Path2D p = new Path2D.Double();
                p.moveTo(cx - s * 0.6, cy - s * 0.18);
                p.lineTo(cx - s * 0.3, cy - s * 0.18);
                p.lineTo(cx + s * 0.05, cy - s * 0.5);
                p.lineTo(cx + s * 0.05, cy + s * 0.5);
                p.lineTo(cx - s * 0.3, cy + s * 0.18);
                p.lineTo(cx - s * 0.6, cy + s * 0.18);
                p.closePath();
                g.fill(p);
                if (kind.equals("vol")) {
                    g.draw(new Arc2D.Double(cx - s * 0.2, cy - s * 0.25, s * 0.5, s * 0.5, -50, 100, Arc2D.OPEN));
                    g.draw(new Arc2D.Double(cx - s * 0.3, cy - s * 0.45, s * 0.9, s * 0.9, -50, 100, Arc2D.OPEN));
                } else {
                    g.draw(new Line2D.Double(cx + s * 0.22, cy - s * 0.22, cx + s * 0.6, cy + s * 0.22));
                    g.draw(new Line2D.Double(cx + s * 0.6, cy - s * 0.22, cx + s * 0.22, cy + s * 0.22));
                }
                break;
            }
            default:
        }
    }

    // ------------------------------------------------------------------ painting
    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g.setColor(new Color(BODY));
        g.fillRect(0, 0, W, H);

        btnsNew = new ArrayList<>();
        dry = false;
        drawingModal = false;

        drawContainer(g);
        if (modal != null)
            drawModalLayer(g);

        g.drawImage(scan, 0, 0, null);
        btns = btnsNew;
        g.dispose();
    }

    void drawContainer(Graphics2D g) {
        RoundRectangle2D box = rr(16, 16, 896, 740, 24);
        outerGlow(g, box, PURPLE600, 0.3, 50);
        g.setColor(c(SLATE950, 1));
        g.fill(box);
        g.setColor(c(PURPLE600, 1));
        g.setStroke(new BasicStroke(4));
        g.draw(rr(18, 18, 892, 736, 20));

        drawHeader(g);
        if (!screenGame)
            drawMenu(g);
        else
            drawGameScreen(g);

        // footer
        g.setColor(c(PURPLE900, 0.4));
        g.fillRect(44, 703, 840, 1);
        drawT(g, "\u00A9 2026 CLASSIC ARCADE \u2022 GOOGLE PAC-MAN STYLE MAZE", W / 2.0, 720, px(12, 0), c(SLATE500, 1),
                1);
    }

    void drawHeader(Graphics2D g) {
        Font hp = px(14, 0);
        drawT(g, "HIGH SCORE:", 44, 58, hp, c(YELLOW400, 1), 0);
        drawT(g, hsDisplay, 44 + tw("HIGH SCORE:", hp) + 8, 58, hp, Color.WHITE, 0);

        Font v = vt(18, 0.1);
        String t = "ARCADE MACHINE V2.5";
        drawT(g, t, 884, 58, v, c(PURPLE400, 1), 2);
        double ix = 884 - tw(t, v) - 16 - 20;
        AffineTransform old = btnStart(g, "mute", ix, 48, 20, 20, false);
        Color col;
        if (!muteTouched)
            col = isHover("mute") ? c(YELLOW400, 1) : c(GRAY400, 1);
        else
            col = sound.muted ? c(RED500, 1) : c(GRAY400, 1);
        icon(g, sound.muted ? "volx" : "vol", ix + 10, 58, 20, col);
        g.setTransform(old);

        g.setColor(c(PURPLE900, 0.5));
        g.fillRect(44, 80, 840, 2);
    }

    void drawMenu(Graphics2D g) {
        glowT(g, "PAC-MAN", W / 2.0, 154, px(72, 0.05), c(YELLOW400, 1), 0xff0055, 1);
        drawT(g, "GOOGLE DOODLE EDITION", W / 2.0, 212, vt(20, 0.1), c(CYAN400, 1), 1);

        // menu box
        RoundRectangle2D box = rr(240, 266, 448, 324, 16);
        outerGlow(g, box, 0x0096ff, 0.6, 15);
        g.setColor(c(SLATE900, 0.8));
        g.fill(box);
        innerGlow(g, box, 0x0096ff, 0.4, 15);
        strokeRR(g, 240, 266, 448, 324, 16, 2, c(PURPLE500, 1));

        String[][] items = { { "start", "> START GAME", "play" }, { "how", "> HOW TO PLAY", "gamepad" },
                { "about", "> ABOUT US", "users" }, { "exit", "> EXIT", "power" } };
        Font f = px(14, 0.05);
        for (int i = 0; i < items.length; i++) {
            arcade(g, items[i][0], 266, 292 + i * 72, 396, 56, c(PURPLE950, 0.6), c(PURPLE400, 1), c(PURPLE200, 1),
                    items[i][1], f, items[i][2]);
        }

        drawT(g, "USE ARROWS / WASD TO MOVE PAC-MAN", W / 2.0, 636, vt(18, 0), c(GRAY400, 1), 1);
        drawT(g, "PRESS [P] TO PAUSE GAME", W / 2.0, 668, vt(18, 0), c(YELLOW500, 1), 1);
    }

    void drawGameScreen(Graphics2D g) {
        double gy = 205;
        Font f14 = px(14, 0);
        // top bar
        drawT(g, "SCORE:", 52, gy + 10, f14, c(RED500, 1), 0);
        drawT(g, String.valueOf(displayedScore), 52 + tw("SCORE:", f14) + 8, gy + 10, f14, Color.WHITE, 0);
        double lx = 876;
        for (int i = 0; i < Math.max(0, lives); i++) {
            lx -= 16;
            g.setColor(c(YELLOW400, 1));
            g.fill(new Ellipse2D.Double(lx, gy + 2, 16, 16));
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1f));
            g.draw(new Ellipse2D.Double(lx + 0.5, gy + 2.5, 15, 15));
            lx -= 4;
        }
        String ll = "LIVES:";
        drawT(g, ll, lx - 4 + 4 - tw(ll, f14) - (lives > 0 ? 4 : 0), gy + 10, f14, c(YELLOW400, 1), 0);

        // canvas wrapper
        double wx = 44, wy = gy + 28, ww = 840, wh = 304;
        RoundRectangle2D wrap = rr(wx, wy, ww, wh, 16);
        outerGlow(g, wrap, 0x0066ff, 0.6, 20);
        g.setColor(Color.BLACK);
        g.fill(wrap);
        strokeRR(g, wx, wy, ww, wh, 16, 4, c(BLUE600, 1));

        // canvas (760 x 280)
        Graphics2D cg = (Graphics2D) g.create();
        cg.translate(wx + 4 + 4 + 36 + 0, wy + 12);
        cg.translate(0, 0);
        cg.clipRect(0, 0, 760, 280);
        cg.setColor(Color.BLACK);
        cg.fillRect(0, 0, 760, 280);
        renderMap(cg);
        drawPacman(cg);
        for (Ghost gh : ghosts)
            gh.draw(cg);
        cg.dispose();

        // overlay
        if (overlayVisible) {
            Shape oc = g.getClip();
            g.clip(rr(wx + 4, wy + 4, ww - 8, wh - 8, 8));
            g.setColor(c(0, 0.8));
            g.fillRect((int) wx, (int) wy, (int) ww, (int) wh);
            double top = wy + 4 + (wh - 8 - 142) / 2;
            glowT(g, overlayTitle, W / 2.0, top + 20, px(36, 0), c(YELLOW400, 1), 0xff0055, 1);
            drawT(g, overlaySub, W / 2.0, top + 40 + 16 + 10, px(14, 0), c(CYAN300, 1), 1);
            Font bf = px(12, 0);
            double bw = tw("CONTINUE", bf) + 48 + 4;
            arcade(g, "overlayBtn", W / 2.0 - bw / 2, top + 40 + 16 + 20 + 24, bw, 42, c(PURPLE900, 1), c(PURPLE400, 1),
                    Color.WHITE, "CONTINUE", bf, null);
            g.setClip(oc);
        }

        // controls footer row
        double by = wy + wh + 12;
        Font f12 = px(12, 0);
        double mw = tw("< MENU", f12) + 24 + 2;
        smallBtn(g, "back", 52, by, mw, 30, "< MENU", f12);
        double pw = tw("PAUSE", f12) + 24 + 2;
        smallBtn(g, "pause", 876 - pw, by, pw, 30, "PAUSE", f12);
        drawT(g, "PAC-MAN MAZE: GOOGLE DOODLE THEME", W / 2.0, by + 15, vt(18, 0), c(SLATE400, 1), 1);
    }

    void smallBtn(Graphics2D g, String id, double x, double y, double w, double h, String label, Font f) {
        AffineTransform old = btnStart(g, id, x, y, w, h, false);
        boolean hv = isHover(id);
        fillRR(g, x, y, w, h, 8, c(SLATE800, 1));
        strokeRR(g, x, y, w, h, 8, 1, hv ? c(YELLOW400, 1) : c(SLATE600, 1));
        drawT(g, label, x + w / 2, y + h / 2, f, hv ? c(YELLOW400, 1) : c(0xd1d5db, 1), 1);
        g.setTransform(old);
    }

    // Map / entities ---------------------------------------------------------
    void renderMap(Graphics2D g) {
        for (int r = 0; r < ROWS; r++) {
            for (int cc = 0; cc < COLS; cc++) {
                int tile = currentMap[r][cc];
                double x = cc * TILE, y = r * TILE;
                if (tile == 1) {
                    g.setColor(new Color(0x1919A6));
                    g.setStroke(new BasicStroke(3f));
                    g.draw(new Rectangle2D.Double(x + 2, y + 2, TILE - 4, TILE - 4));
                    g.setColor(new Color(0x0000FF));
                    g.setStroke(new BasicStroke(1f));
                    g.draw(new Rectangle2D.Double(x + 4, y + 4, TILE - 8, TILE - 8));
                } else if (tile == 0) {
                    g.setColor(new Color(0xffb8ae));
                    g.fill(new Ellipse2D.Double(x + TILE / 2.0 - 2.5, y + TILE / 2.0 - 2.5, 5, 5));
                } else if (tile == 2) {
                    if ((System.currentTimeMillis() / 200) % 2 == 0) {
                        g.setColor(new Color(0xffb8ae));
                        g.fill(new Ellipse2D.Double(x + TILE / 2.0 - 6, y + TILE / 2.0 - 6, 12, 12));
                    }
                }
            }
        }
    }

    void drawPacman(Graphics2D g) {
        double rotation = 0;
        if (pDirX == 1)
            rotation = 0;
        if (pDirX == -1)
            rotation = Math.PI;
        if (pDirY == 1)
            rotation = Math.PI / 2;
        if (pDirY == -1)
            rotation = -Math.PI / 2;

        mouthAngle += mouthSpeed;
        if (mouthAngle > 0.45 || mouthAngle < 0.05)
            mouthSpeed = -mouthSpeed;

        // canvas angles run clockwise, Java2D arcs run counter-clockwise -> negate
        double start = -Math.toDegrees(rotation + mouthAngle);
        double extent = -Math.toDegrees(Math.PI * 2 - 2 * mouthAngle);
        g.setColor(new Color(0xFFFF00));
        g.fill(new Arc2D.Double(pX - pRadius, pY - pRadius, pRadius * 2, pRadius * 2, start, extent, Arc2D.PIE));
    }

    // ------------------------------------------------------------------ modals
    interface Body {
        double run(Graphics2D g, double x, double y, double w);
    }

    void drawModalLayer(Graphics2D g) {
        switch (modal) {
            case "how":
                drawModal(g, 576, PINK500, 0xff0080, 0.85, this::howBody);
                break;
            case "about":
                drawModal(g, 672, CYAN500, 0x0096ff, 0.85, this::aboutBody);
                break;
            case "exit":
                drawModal(g, 448, RED500, -1, 0.90, this::exitBody);
                break;
            default:
        }
    }

    void drawModal(Graphics2D g, double cw, int border, int glow, double back, Body body) {
        double inner = cw - 4 - 48;
        dry = true;
        double ch = body.run(g, 0, 0, inner);
        dry = false;
        double cardH = ch + 48 + 4;
        double x0 = (W - cw) / 2, y0 = (H - cardH) / 2;

        g.setColor(c(0, back));
        g.fillRect(0, 0, W, H);

        RoundRectangle2D card = rr(x0, y0, cw, cardH, 16);
        if (glow >= 0)
            outerGlow(g, card, glow, 0.6, 15);
        g.setColor(c(SLATE900, 1));
        g.fill(card);
        if (glow >= 0)
            innerGlow(g, card, glow, 0.4, 15);
        strokeRR(g, x0, y0, cw, cardH, 16, 2, c(border, 1));

        drawingModal = true;
        body.run(g, x0 + 26, y0 + 26, inner);
        drawingModal = false;
    }

    double modalBtn(Graphics2D g, String id, double x, double y, double w, String label, int bg, int border, int fg) {
        arcade(g, id, x, y, w, 42, c(bg, 1), c(border, 1), c(fg, 1), label, px(12, 0), null);
        return 42;
    }

    double howBody(Graphics2D g, double x, double y, double w) {
        double cy = y;
        Font h3 = px(12, 0), body = vt(20, 0);
        double lh = 32.5;

        drawT(g, "HOW TO PLAY", x + w / 2, cy + 16, px(24, 0), c(PINK500, 1), 1);
        cy += 32 + 12;
        if (!dry) {
            g.setColor(c(PINK900, 0.5));
            g.fillRect((int) x, (int) cy, (int) w, 1);
        }
        cy += 1 + 16;

        // objective
        drawT(g, "OBJECTIVE:", x, cy + 8, h3, c(YELLOW400, 1), 0);
        cy += 16 + 4;
        for (String l : wrap(
                "Guide Pac-Man through the maze to eat all the yellow dots while avoiding the four colorful ghosts.",
                body, w)) {
            drawT(g, l, x, cy + lh / 2, body, c(SLATE200, 1), 0);
            cy += lh;
        }
        cy += 16;

        // controls
        drawT(g, "KEYBOARD CONTROLS:", x, cy + 8, h3, c(YELLOW400, 1), 0);
        cy += 16 + 4;
        String[][] keys = { { "UP / W:", " Move Up" }, { "DOWN / S:", " Move Down" }, { "LEFT / A:", " Move Left" },
                { "RIGHT / D:", " Move Right" }, { "P KEY:", " Pause Game" } };
        for (int i = 0; i < keys.length; i++) {
            if (!dry) {
                g.setColor(c(SLATE300, 1));
                g.fill(new Ellipse2D.Double(x + 4, cy + lh / 2 - 2.5, 5, 5));
            }
            drawT(g, keys[i][0], x + 20, cy + lh / 2, body, c(CYAN400, 1), 0);
            drawT(g, keys[i][1], x + 20 + tw(keys[i][0], body), cy + lh / 2, body, c(SLATE300, 1), 0);
            cy += lh + (i < keys.length - 1 ? 4 : 0);
        }
        cy += 16;

        // power pellets
        drawT(g, "POWER PELLETS & GHOSTS:", x, cy + 8, h3, c(YELLOW400, 1), 0);
        cy += 16 + 4;
        for (String l : wrap(
                "Eat the larger flashing Power Pellets to turn ghosts vulnerable (blue) for a short period. Eat blue ghosts for bonus points!",
                body, w)) {
            drawT(g, l, x, cy + lh / 2, body, c(SLATE200, 1), 0);
            cy += lh;
        }
        cy += 16 + 8;

        cy += modalBtn(g, "closeHow", x, cy, w, "PRESS ENTER OR CLICK TO RETURN", PINK950, PINK500, PINK200);
        return cy - y;
    }

    BufferedImage loadImage(String path) {
        try {
            return javax.imageio.ImageIO.read(new File(path));
        } catch (Exception e) {
            System.out.println("Image not found: " + path);
            return null;
        }
    }

    void drawCircularImage(Graphics2D g, BufferedImage img,
            double cx, double cy, double size,
            int borderColor, int glowColor) {

        if (dry)
            return;

        double x = cx - size / 2;
        double y = cy - size / 2;

        Shape oldClip = g.getClip();

        Ellipse2D circle = new Ellipse2D.Double(
                x, y, size, size);

        // Make image circular
        g.clip(circle);

        // Background
        g.setColor(c(SLATE800, 1));
        g.fill(circle);

        if (img != null) {

            double imageRatio = (double) img.getWidth() / img.getHeight();

            double drawW;
            double drawH;

            if (imageRatio > 1.0) {
                drawH = size;
                drawW = size * imageRatio;
            } else {
                drawW = size;
                drawH = size / imageRatio;
            }

            double drawX = cx - drawW / 2;
            double drawY = cy - drawH / 2;

            g.drawImage(
                    img,
                    (int) drawX,
                    (int) drawY,
                    (int) drawW,
                    (int) drawH,
                    null);
        }

        g.setClip(oldClip);

        // Only circular border, NO GLOW
        g.setColor(c(borderColor, 1));
        g.setStroke(new BasicStroke(2f));

        g.draw(new Ellipse2D.Double(
                x + 1,
                y + 1,
                size - 2,
                size - 2));
    }

    double aboutBody(Graphics2D g, double x, double y, double w) {
        double cy = y;
        drawT(g, "DEVELOPERS", x + w / 2, cy + 16, px(24, 0), c(CYAN400, 1), 1);
        cy += 32 + 12;
        if (!dry) {
            g.setColor(c(CYAN900, 0.5));
            g.fillRect((int) x, (int) cy, (int) w, 1);
        }
        cy += 1 + 16;

        drawT(g, "ABOUT US", x + w / 2, cy + 8, px(12, 0), c(YELLOW400, 1), 1);
        cy += 16 + 8 + 16;

        cy += 8; // grid my-2
        String[] names = { "MD UDOY HOSSAIN JOY", "IMTIYAZ ALI", "JUNAED AHMED" };
        String[] roles = { "LEAD DEVELOPER", "GAME ARCHITECT", "UI/UX DESIGNER" };
        String[] imagePaths = {
                "images/udoy.jpg",
                "images/imtiyaz.jpg",
                "images/junaed.jpg"
        };

        BufferedImage[] developerImages = {
                loadImage(imagePaths[0]),
                loadImage(imagePaths[1]),
                loadImage(imagePaths[2])
        };

        int[] accent = { CYAN400, PINK400, GREEN400 };
        int[] badgeBg = { CYAN950, PINK950, GREEN950 };
        int[] badgeBorder = { CYAN800, PINK800, GREEN800 };
        int[] glowC = { 0x00dcff, 0xff0080, 0x00ff80 };

        double colW = (w - 32) / 3;
        Font nf = px(11, 0), rf = vt(10, 0);
        int maxLines = 1;
        List<List<String>> nameLines = new ArrayList<>();
        for (String n : names) {
            List<String> ls = wrap(n, nf, colW - 34);
            nameLines.add(ls);
            maxLines = Math.max(maxLines, ls.size());
        }
        double cardH = 2 + 32 + 80 + 12 + maxLines * 16.5 + 8 + 22;

        for (int i = 0; i < 3; i++) {
            double cx0 = x + i * (colW + 16);
            fillRR(g, cx0, cy, colW, cardH, 16, c(SLATE950, 1));
            strokeRR(g, cx0, cy, colW, cardH, 16, 1, c(CYAN500, 0.5));
            double mx = cx0 + colW / 2, ay = cy + 1 + 16 + 40;
            drawCircularImage(
                    g,
                    developerImages[i],
                    mx,
                    ay,
                    80,
                    accent[i],
                    glowC[i]);

            double ny = cy + 1 + 16 + 80 + 12;
            for (String l : nameLines.get(i)) {
                drawT(g, l, mx, ny + 8.25, nf, c(YELLOW300, 1), 1);
                ny += 16.5;
            }
            double bw = tw(roles[i], rf) + 16 + 2;
            double bx = mx - bw / 2, bY = ny + 8;
            fillRR(g, bx, bY, bw, 22, 8, c(badgeBg[i], 0.6));
            strokeRR(g, bx, bY, bw, 22, 8, 1, c(badgeBorder[i], 1));
            drawT(g, roles[i], mx, bY + 11, rf, c(accent[i], 1), 1);
        }
        cy += cardH + 8 + 16;

        cy += 4;
        Font ff = vt(18, 0);
        for (String l : wrap("2-2 GAME PROJECT \u2022 PAC-MAN RETRO RECREATION", ff, w)) {
            drawT(g, l, x + w / 2, cy + 14, ff, c(SLATE400, 1), 1);
            cy += 28;
        }
        cy += 4 + 16 + 8;

        cy += modalBtn(g, "closeAbout", x, cy, w, "PRESS ENTER OR CLICK TO RETURN", CYAN950, CYAN500, CYAN200);
        return cy - y;
    }

    double exitBody(Graphics2D g, double x, double y, double w) {
        double cy = y;
        drawT(g, "GAME TERMINATED", x + w / 2, cy + 16, px(24, 0), c(RED500, 1), 1);
        cy += 32 + 16;

        Font f = vt(24, 0);
        for (String l : wrap("THANKS FOR PLAYING PAC-MAN!", f, w)) {
            drawT(g, l, x + w / 2, cy + 16, f, c(SLATE300, 1), 1);
            cy += 32;
        }
        cy += 16;

        double t = (System.currentTimeMillis() % 2000) / 2000.0;
        double alpha = 0.75 + 0.25 * Math.cos(2 * Math.PI * t);
        icon(g, "power", x + w / 2, cy + 16 + 24, 48, c(RED500, alpha));
        cy += 16 + 48 + 16 + 16;

        cy += modalBtn(g, "restartExit", x, cy, w, "RESTART MACHINE", RED950, RED500, RED200);
        return cy - y;
    }

    // ------------------------------------------------------------------ main
    public static void main(String[] args) {
        PIXEL = loadFont("PressStart2P-Regular.ttf", new Font(Font.MONOSPACED, Font.BOLD, 12));
        VT = loadFont("VT323-Regular.ttf", new Font(Font.MONOSPACED, Font.PLAIN, 12));

        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Pac-Man Arcade Edition");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new PacmanGame());
            f.setResizable(true);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
