import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class MapGenerator {
    private int[][] map;
    private int brickWidth;
    private int brickHeight;

    public MapGenerator(int rows, int cols) {
        map = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                map[i][j] = 1;
            }
        }
        brickWidth = 540 / cols;
        brickHeight = 150 / rows;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    g.setColor(new Color(0XFF8787)); // Brick color
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);

                    g.setStroke(new BasicStroke(4));
                    g.setColor(Color.BLACK);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }

    public int[][] getMap() {
        return map;
    }

    public int getBrickWidth() {
        return brickWidth;
    }

    public int getBrickHeight() {
        return brickHeight;
    }
}

class GamePlay extends JPanel implements KeyListener, ActionListener {
    private boolean play = true;
    private int score = 0;
    private int totalBricks = 21;
    private Timer timer;
    private int playerX = 310;
    private int ballposX = 120;
    private int ballposY = 350;
    private int ballXdir = -1;
    private int ballYdir = -2;
    private MapGenerator map;

    public GamePlay() {
        map = new MapGenerator(3, 7);
        initPanel();
    }

    private void initPanel() {
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(8, this);
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        drawBackground(g);
        map.draw((Graphics2D) g);
        drawBorders(g);
        drawPaddle(g);
        drawBall(g);
        drawScore(g);
        checkGameOver(g);
        g.dispose();
    }

    private void drawBackground(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(1, 1, 692, 592);
    }

    private void drawBorders(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);
    }

    private void drawPaddle(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(playerX, 550, 100, 12);
    }

    private void drawBall(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(ballposX, ballposY, 20, 20);
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("MV Boli", Font.BOLD, 25));
        g.drawString("Score: " + score, 520, 30);
    }

    private void checkGameOver(Graphics g) {
        if (totalBricks <= 0) {
            displayWinMessage(g);
        } else if (ballposY > 570) {
            displayGameOverMessage(g);
        }
    }

    private void displayWinMessage(Graphics g) {
        play = false;
        stopBall();
        g.setColor(new Color(0XFF6464));
        g.setFont(new Font("MV Boli", Font.BOLD, 30));
        g.drawString("You Won, Score: " + score, 190, 300);
        g.setFont(new Font("MV Boli", Font.BOLD, 20));
        g.drawString("Press Enter to Restart.", 230, 350);
    }

    private void displayGameOverMessage(Graphics g) {
        play = false;
        stopBall();
        g.setColor(Color.BLACK);
        g.setFont(new Font("MV Boli", Font.BOLD, 30));
        g.drawString("Game Over, Score: " + score, 190, 300);
        g.setFont(new Font("MV Boli", Font.BOLD, 20));
        g.drawString("Press Enter to Restart", 230, 350);
    }

    private void stopBall() {
        ballXdir = 0;
        ballYdir = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            ballPaddleCollision();
            ballBrickCollision();
            moveBall();
            repaint();
        }
    }

    private void ballPaddleCollision() {
        if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
            ballYdir = -ballYdir;
        }
    }

    private void ballBrickCollision() {
        for (int i = 0; i < map.getMap().length; i++) {
            for (int j = 0; j < map.getMap()[0].length; j++) {
                if (map.getMap()[i][j] > 0) {
                    int brickX = j * map.getBrickWidth() + 80;
                    int brickY = i * map.getBrickHeight() + 50;

                    Rectangle brickRect = new Rectangle(brickX, brickY, map.getBrickWidth(), map.getBrickHeight());
                    Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);

                    if (ballRect.intersects(brickRect)) {
                        map.setBrickValue(0, i, j);
                        totalBricks--;
                        score += 5;

                        if (ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width) {
                            ballXdir = -ballXdir;
                        } else {
                            ballYdir = -ballYdir;
                        }
                    }
                }
            }
        }
    }

    private void moveBall() {
        ballposX += ballXdir;
        ballposY += ballYdir;

        if (ballposX < 0 || ballposX > 670) {
            ballXdir = -ballXdir;
        }
        if (ballposY < 0) {
            ballYdir = -ballYdir;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            moveRight();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            moveLeft();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER && !play) {
            restartGame();
        }
    }

    private void moveRight() {
        if (playerX < 600) {
            playerX += 50;
            play = true;
        }
    }

    private void moveLeft() {
        if (playerX > 10) {
            playerX -= 50;
            play = true;
        }
    }

    private void restartGame() {
        play = true;
        ballposX = 120;
        ballposY = 350;
        ballXdir = -1;
        ballYdir = -2;
        score = 0;
        totalBricks = 21;
        map = new MapGenerator(3, 7);
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        GamePlay gamePlay = new GamePlay();

        frame.setBounds(10, 10, 700, 600);
        frame.setTitle("Brick Breaker");
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePlay);
    }
}
