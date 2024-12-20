package TicTacToeAI.connectfour;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ConnectFour extends JPanel {
  private static final long serialVersionUID = 1L;

  public static final String TITLE = "Connect Four";
  public static final Color COLOR_BG = Color.WHITE;
  public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
  public static final Color COLOR_SNOWY = new Color(239, 105, 80);
  public static final Color COLOR_STORMY = new Color(64, 154, 225);
  public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

  private Board board;
  private State currentState;
  private Seed currentPlayer;
  private JLabel statusBar;
  private Seed userPlayer;

  public ConnectFour() {
    super.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        int row = mouseY / Cell.SIZE;
        int col = mouseX / Cell.SIZE;

        if (currentState == State.PLAYING) {
          if (col >= 0 && col < Board.COLS) {
            for (int rowI = Board.ROWS - 1; rowI >= 0; rowI--) {
              if (board.cells[rowI][col].content == Seed.NO_SEED) {
                board.cells[rowI][col].content = currentPlayer;
                if (hasWon(currentPlayer, rowI, col)) {
                  currentState = (currentPlayer == Seed.SNOWY) ? State.SNOWY_WON : State.STORMY_WON;
                  showEndGameDialog(currentState);
                } else if (isDraw()) {
                  currentState = State.DRAW;
                  showEndGameDialog(currentState);
                } else {
                  currentPlayer = (currentPlayer == Seed.SNOWY) ? Seed.STORMY : Seed.SNOWY;
                  if (currentPlayer == Seed.SNOWY) {
                    SoundEffect.SNOWY.play();
                  } else {
                    SoundEffect.STORMY.play();
                  }
                }
                break;
              }
            }
          }
        } else {
          newGame();
        }
        repaint();
      }
    });

    statusBar = new JLabel();
    statusBar.setFont(FONT_STATUS);
    statusBar.setBackground(COLOR_BG_STATUS);
    statusBar.setOpaque(true);
    statusBar.setPreferredSize(new Dimension(300, 30));
    statusBar.setHorizontalAlignment(JLabel.LEFT);
    statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

    super.setLayout(new BorderLayout());
    super.add(statusBar, BorderLayout.PAGE_END);
    super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
    super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

    initGame();
  }

  public void initGame() {
    board = new Board();
    showPlayerSelectionDialog();
    newGame();
  }

  public void newGame() {
    for (int row = 0; row < Board.ROWS; ++row) {
      for (int col = 0; col < Board.COLS; ++col) {
        board.cells[row][col].content = Seed.NO_SEED;
      }
    }
    currentPlayer = Seed.SNOWY;
    currentState = State.PLAYING;
    statusBar.setText("Snowy's Turn");
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    setBackground(COLOR_BG);
    board.paint(g);

    if (currentState == State.PLAYING) {
      statusBar.setForeground(Color.BLACK);
      statusBar.setText((currentPlayer == Seed.SNOWY) ? "Snowy's Turn" : "Stormy's Turn");
    } else if (currentState == State.DRAW) {
      statusBar.setForeground(Color.RED);
      statusBar.setText("It's a Draw! Click to play again.");
    } else if (currentState == State.SNOWY_WON) {
      statusBar.setForeground(Color.RED);
      statusBar.setText("Snowy Won! Click to play again.");
    } else if (currentState == State.STORMY_WON) {
      statusBar.setForeground(Color.RED);
      statusBar.setText("Stormy Won! Click to play again.");
    }
  }

  public boolean hasWon(Seed theSeed, int rowSelected, int colSelected) {
    return checkDirection(theSeed, rowSelected, colSelected, 1, 0) || // Horizontal
        checkDirection(theSeed, rowSelected, colSelected, 0, 1) || // Vertical
        checkDirection(theSeed, rowSelected, colSelected, 1, 1) || // Diagonal (right-down)
        checkDirection(theSeed, rowSelected, colSelected, 1, -1); // Diagonal (left-down)
  }

  private boolean checkDirection(Seed theSeed, int row, int col, int rowDir, int colDir) {
    int count = 0;
    for (int i = -3; i <= 3; i++) {
      int newRow = row + i * rowDir;
      int newCol = col + i * colDir;
      if (newRow >= 0 && newRow < Board.ROWS && newCol >= 0 && newCol < Board.COLS
          && board.cells[newRow][newCol].content == theSeed) {
        count++;
        if (count == 4) {
          return true;
        }
      } else {
        count = 0;
      }
    }
    return false;
  }

  public boolean isDraw() {
    for (int row = 0; row < Board.ROWS; ++row) {
      for (int col = 0; col < Board.COLS; ++col) {
        if (board.cells[row][col].content == Seed.NO_SEED) {
          return false;
        }
      }
    }
    return true;
  }

  private void showEndGameDialog(State state) {
    String message = "";
    if (state == State.SNOWY_WON) {
      message = "Snowy Won! Play again?";
      if (userPlayer == Seed.SNOWY) {
        SoundEffect.WIN.play();
      } else {
        SoundEffect.LOSE.play();
      }
    } else if (state == State.STORMY_WON) {
      message = "Stormy Won! Play again?";
      if (userPlayer == Seed.STORMY) {
        SoundEffect.WIN.play();
      } else {
        SoundEffect.LOSE.play();
      }
    } else if (state == State.DRAW) {
      message = "It's a Draw! Play again?";
      SoundEffect.DIE.play();
    }

    int response = JOptionPane.showOptionDialog(this, message, "Game Over",
        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
        new String[] { "Play Again", "Exit" }, "Play Again");

    if (response == JOptionPane.YES_OPTION) {
      newGame();
      repaint();
    } else if (response == JOptionPane.NO_OPTION) {
      System.exit(0);
    }
  }

  private void showPlayerSelectionDialog() {
    String message = "Choose your player:";
    ImageIcon SNOWYIcon = new ImageIcon(
        new ImageIcon(getClass().getClassLoader().getResource("TicTacToeAI/images/cat1.gif")).getImage()
            .getScaledInstance(50, 50, Image.SCALE_SMOOTH));
    ImageIcon STORMYIcon = new ImageIcon(
        new ImageIcon(getClass().getClassLoader().getResource("TicTacToeAI/images/cat2.gif")).getImage()
            .getScaledInstance(50, 50, Image.SCALE_SMOOTH));

    Object[] options = {
        "SNOWY (Snowy)", SNOWYIcon,
        "STORMY (Stormy Cat)", STORMYIcon
    };

    int response = JOptionPane.showOptionDialog(this, message, "Player Selection",
        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
        options, options[0]);

    if (response == 0) {
      userPlayer = Seed.SNOWY;
    } else if (response == 2) {
      userPlayer = Seed.STORMY;
    }
  }

  public void play() {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame(TITLE);
        ConnectFour gamePanel = new ConnectFour();
        frame.setContentPane(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Add menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenuItem resetMenuItem = new JMenuItem("Reset");
        resetMenuItem.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            gamePanel.newGame();
            gamePanel.repaint();
          }
        });
        menuBar.add(resetMenuItem);
        frame.setJMenuBar(menuBar);
      }
    });
  }
}