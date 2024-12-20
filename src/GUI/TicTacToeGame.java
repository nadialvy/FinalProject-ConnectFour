package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TicTacToeGame extends JFrame {
  // Constants for game configuration
  public static final int ROWS = 3;
  public static final int COLS = 3;
  public static final int CELL_SIZE = 120;
  public static final int BOARD_WIDTH = CELL_SIZE * COLS;
  public static final int BOARD_HEIGHT = CELL_SIZE * ROWS;
  public static final int GRID_WIDTH = 10;
  public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
  public static final int CELL_PADDING = CELL_SIZE / 5;
  public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
  public static final int SYMBOL_STROKE_WIDTH = 8;

  // Color constants
  public static final Color COLOR_BG = Color.WHITE;
  public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
  public static final Color COLOR_GRID = Color.LIGHT_GRAY;
  public static final Color COLOR_CROSS = new Color(211, 45, 65);
  public static final Color COLOR_NOUGHT = new Color(76, 181, 245);
  public static final Font FONT_STATUS = new Font("StormyCR A Extended", Font.PLAIN, 14);

  // Game state variables
  private State currentState;
  private Seed currentPlayer;
  private Seed[][] board;

  // UI Components
  private GamePanel gamePanel;
  private JLabel statusBar;

  public TicTacToeGame() {
    // Initialize the game objects
    initGame();

    // Set up game panel
    gamePanel = new GamePanel();
    gamePanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

    // Add mouse listener to game panel
    gamePanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        int row = mouseY / CELL_SIZE;
        int col = mouseX / CELL_SIZE;

        if (currentState == State.PLAYING) {
          if (row >= 0 && row < ROWS && col >= 0
              && col < COLS && board[row][col] == Seed.NO_SEED) {
            currentState = stepGame(currentPlayer, row, col);
            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
          }
        } else {
          newGame();
        }
        repaint();
      }
    });

    // Setup status bar
    statusBar = new JLabel("       ");
    statusBar.setFont(FONT_STATUS);
    statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
    statusBar.setOpaque(true);
    statusBar.setBackground(COLOR_BG_STATUS);

    // Set up content pane
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(gamePanel, BorderLayout.CENTER);
    cp.add(statusBar, BorderLayout.PAGE_END);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setTitle("Tic Tac Toe");
    setVisible(true);

    newGame();
  }

  public void initGame() {
    board = new Seed[ROWS][COLS];
  }

  public void newGame() {
    for (int row = 0; row < ROWS; ++row) {
      for (int col = 0; col < COLS; ++col) {
        board[row][col] = Seed.NO_SEED;
      }
    }
    currentPlayer = Seed.CROSS;
    currentState = State.PLAYING;
  }

  public State stepGame(Seed player, int selectedRow, int selectedCol) {
    board[selectedRow][selectedCol] = player;

    // Check win conditions
    if (checkWin(player, selectedRow, selectedCol)) {
      return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
    } else {
      // Check for draw
      for (int row = 0; row < ROWS; ++row) {
        for (int col = 0; col < COLS; ++col) {
          if (board[row][col] == Seed.NO_SEED) {
            return State.PLAYING;
          }
        }
      }
      return State.DRAW;
    }
  }

  private boolean checkWin(Seed player, int selectedRow, int selectedCol) {
    // Check row
    if (board[selectedRow][0] == player
        && board[selectedRow][1] == player
        && board[selectedRow][2] == player) {
      return true;
    }

    // Check column
    if (board[0][selectedCol] == player
        && board[1][selectedCol] == player
        && board[2][selectedCol] == player) {
      return true;
    }

    // Check diagonals
    if (selectedRow == selectedCol
        && board[0][0] == player
        && board[1][1] == player
        && board[2][2] == player) {
      return true;
    }

    if (selectedRow + selectedCol == 2
        && board[0][2] == player
        && board[1][1] == player
        && board[2][0] == player) {
      return true;
    }

    return false;
  }

  // Inner class for game panel
  class GamePanel extends JPanel {
    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      setBackground(COLOR_BG);

      // Draw grid lines
      g.setColor(COLOR_GRID);
      for (int row = 1; row < ROWS; ++row) {
        g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF,
            BOARD_WIDTH - 1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
      }
      for (int col = 1; col < COLS; ++col) {
        g.fillRoundRect(CELL_SIZE * col - GRID_WIDTH_HALF, 0,
            GRID_WIDTH, BOARD_HEIGHT - 1, GRID_WIDTH, GRID_WIDTH);
      }

      // Draw Seeds
      Graphics2D g2d = (Graphics2D) g;
      g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH,
          BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

      for (int row = 0; row < ROWS; ++row) {
        for (int col = 0; col < COLS; ++col) {
          int x1 = col * CELL_SIZE + CELL_PADDING;
          int y1 = row * CELL_SIZE + CELL_PADDING;
          if (board[row][col] == Seed.CROSS) {
            g2d.setColor(COLOR_CROSS);
            int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
            int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
            g2d.drawLine(x1, y1, x2, y2);
            g2d.drawLine(x2, y1, x1, y2);
          } else if (board[row][col] == Seed.NOUGHT) {
            g2d.setColor(COLOR_NOUGHT);
            g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
          }
        }
      }

      // Update status bar
      updateStatusBar();
    }
  }

  private void updateStatusBar() {
    if (currentState == State.PLAYING) {
      statusBar.setForeground(Color.BLACK);
      statusBar.setText((currentPlayer == Seed.CROSS) ? "Snowy's Turn" : "Choho's Turn");
    } else if (currentState == State.DRAW) {
      statusBar.setForeground(Color.RED);
      statusBar.setText("It's a Draw! Click to play again");
    } else if (currentState == State.CROSS_WON) {
      statusBar.setForeground(Color.RED);
      statusBar.setText("Snowy Won! Click to play again");
    } else if (currentState == State.NOUGHT_WON) {
      statusBar.setForeground(Color.RED);
      statusBar.setText("Stormy Won! Click to play again");
    }
  }
}
