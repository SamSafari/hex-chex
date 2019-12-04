package com.hexchex.gui;

import com.hexchex.HexChex;
import com.hexchex.engine.board.Board;
import com.hexchex.engine.board.Cell;
import com.hexchex.engine.pieces.Piece;
import com.hexchex.engine.pieces.Team;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import static javax.swing.SwingUtilities.*;

public class Game implements Serializable {

    public static Game getInstance() {
        return instance;
    }



    public static Game instance;
    private final JFrame gameFrame;
    private Point gameFrameCenterpoint;
    public HexPanel hexPanel;
    private GameMessagePanel messagePanel;
    private JLabel currentMessageDisplay;

    private boolean gameEnded = false;
    private Team winningTeam;
    private String currentMessage = "";
    private Team currentMove;
    private Hexagon sourceHex, destinationHex;
    private Piece pieceToMove;
    private final Board board;
    private final Team team1, team2;

    public Game(HexChex hexChex) {

        instance = this;

        board = hexChex.getBoard();
        team1 = hexChex.getTeam1();
        team2 = hexChex.getTeam2();
        currentMove = team1;

        gameFrame = new JFrame("HexChex");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(new Dimension(board.getWidth() * 100,
                board.getHeight() * 100 + 100));
        gameFrameCenterpoint = new Point(gameFrame.getSize().width / 3, gameFrame.getSize().height / 3);
        gameFrame.setLayout(new BorderLayout());

        final JMenuBar gameMenuBar = createGameMenuBar();
        gameFrame.setJMenuBar(gameMenuBar);

        hexPanel = new HexPanel();
        HexSelector selector = new HexSelector(hexPanel);
        GameMessagePanel messagePanel = new GameMessagePanel();
        currentMessageDisplay  = new JLabel();
        messagePanel.add(currentMessageDisplay);

        hexPanel.addMouseListener(selector);
        hexPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (isRightMouseButton(e)) {
                    if (sourceHex != null) {
                        sourceHex.setToDefaultColor();
                        sourceHex = null;
                        destinationHex = null;
                        pieceToMove = null;
                        System.out.println("Cancelled");
                        hexPanel.repaint();
                    }
                }
            }

        });

        gameFrame.add(messagePanel, BorderLayout.PAGE_START);
        messagePanel.setSize(gameFrame.getSize().width, 100);

        gameFrame.add(hexPanel, BorderLayout.CENTER);
        hexPanel.setPreferredSize(gameFrame.getSize());

        gameFrame.setVisible(true);
    }

    private JMenuBar createGameMenuBar() {
        final JMenuBar gameMenuBar = new JMenuBar();
        gameMenuBar.add(createFileMenu());
        gameMenuBar.add(createEditMenu());
        return gameMenuBar;
    }

    private JMenu createEditMenu() {

        final JMenu editMenu = new JMenu("Edit");

        final JMenuItem editTeams = new JMenuItem("Edit teams");
        editTeams.addActionListener(e -> {
            EditTeamFrame editTeamFrame = new EditTeamFrame();
        });

        final JMenuItem resizeBoard = new JMenuItem("Resize board");
        resizeBoard.addActionListener(e -> {
                ResizeBoardFrame resizeBoardFrame = new ResizeBoardFrame();
        });

        editMenu.add(editTeams);
        editMenu.add(resizeBoard);

        return editMenu;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem newGame = new JMenuItem("New game");
        newGame.addActionListener(e -> {
            NewGameFrame newGameFrame = new NewGameFrame();
        });

        final JMenuItem saveGame = new JMenuItem("Save game");
        saveGame.addActionListener(e -> {
            try {
                FileOutputStream fileOut = new FileOutputStream("C:\\Users\\Samer\\IdeaProjects\\CheckersGame\\src\\com\\hexchex\\saves.ser");
                ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
                objOut.writeObject(new HexChex(board, team1, team2));
                System.out.println("Game saved");
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        });

        final JMenuItem loadGame = new JMenuItem("Load game");
        saveGame.addActionListener(e -> {
            try {
                FileInputStream fileIn = new FileInputStream("C:\\Users\\Samer\\IdeaProjects\\CheckersGame\\src\\com\\hexchex\\saves.ser");
                ObjectInputStream objIn = new ObjectInputStream(fileIn);
                HexChex loadedHexChex = (HexChex) objIn.readObject();
                gameFrame.dispose();
                Game loadedGame = new Game(loadedHexChex);
                System.out.println("Game loaded");
            } catch(IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        final JMenuItem deleteSaves = new JMenuItem("Delete saved games");
        saveGame.addActionListener(e -> {
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter("C:\\Users\\Samer\\IdeaProjects\\CheckersGame\\src\\com\\hexchex\\saves.ser");
                    writer.print("");
                    writer.close();
                } catch(FileNotFoundException ex) {
                    ex.printStackTrace();
                }

        });

        fileMenu.add(newGame);
        fileMenu.add(saveGame);
        fileMenu.add(loadGame);
        fileMenu.add(deleteSaves);

        return fileMenu;
    }

    public class HexPanel extends JPanel {

        private HexPanel() {
            createHexes();
        }

        ArrayList<Hexagon> hexList = new ArrayList<>();

        private void createHexes() {

            int radius = 50;
            hexList.clear();

            for(int row = 0; row < board.getBoard().length; row++) {
                for(int col = 0; col < board.getBoard()[row].length; col++) {

                    if (col % 2 == 0) {
                        if (row % 2 == 0) {

                            Point hexCenter = new Point(col * 80 + (2 * radius), row * 45 + (2 * radius));
                            Hexagon hex = new Hexagon(hexCenter, radius, board.getBoard()[row][col]);
                            hex.setDefaultColor(Color.DARK_GRAY);
                            hex.setToDefaultColor();

                            hexList.add(hex);

                        }

                    } else if (row % 2 != 0) {

                        Point hexCenter = new Point(col * 80 + (2 * radius), row * 45 + (2 * radius));
                        Hexagon hex = new Hexagon(hexCenter, radius, board.getBoard()[row][col]);
                        hex.setDefaultColor(Color.DARK_GRAY);
                        hex.setToDefaultColor();

                        hexList.add(hex);

                    }

                }
            }
        }


        /**
         * Generates a graphical representation of the board with properly tessellated Hexagons, each with its own
         * Cell object. This method does not need to be actively invoked.
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            int radius = 50;
            setBackground(Color.LIGHT_GRAY);
            Color boardColor = Color.DARK_GRAY;

           /* g.setColor(new Color(200, 150, 100));
            g.setColor(new Color(100, 70, 30));
            g.setColor(new Color(150, 100, 50));
            g.setColor(new Color(75, 50, 30));*/

           for (Hexagon hex : hexList) {
               g.setColor(hex.getColor());
               g.fillPolygon(hex);
               g.setColor(boardColor);

               if (hex.getCell().isOccupied()) {
                   Color temp = g.getColor();

                   Color color = hex.getCell().getPiece().getTeam().getColor();
                   g.setColor(color);
                   g.fillOval(hex.getCenter().x - (radius / 2), hex.getCenter().y - (radius / 2), radius, radius);

                   g.setColor(temp);
               }

           }

        }

    }

    private class HexSelector extends MouseAdapter implements MouseMotionListener {

        HexPanel hexPanel;

        HexSelector(HexPanel hexPanel) {
            this.hexPanel = hexPanel;
        }

        /**
         * Moves the piece on a selected Hexagon (LClick 1) to another selected Hexagon (LClick 2),
         * or cancels all selections (RClick). Will update the graphical representation of the board when complete.
         *
         * @param e User mouse click input
         */
        @Override
        public void mouseClicked(MouseEvent e) {

            if (!gameEnded) {

                Point p = e.getPoint();

                for(Hexagon hex : hexPanel.hexList) {
                    if (hex.contains(p)) {

                        if (isMiddleMouseButton(e)) {
                            System.out.println("(" + hex.getCell().col() + ", " + hex.getCell().row() + ")");
                        }

                        if (isLeftMouseButton(e)) {

                            if (sourceHex == null) {

                                sourceHex = hex;

                                if (hex.getCell().isOccupied()) {

                                    if (hex.getCell().getPiece().getTeam() != currentMove) {
                                        System.out.println(currentMove.getName() + "'s move.");
                                        sourceHex = null;
                                    } else {
                                        pieceToMove = sourceHex.getCell().getPiece();
                                        System.out.println("Source selected\n");

                                        sourceHex.setToSelectedColor();
                                        hexPanel.repaint();
                                    }

                                } else {
                                    sourceHex.setToDefaultColor();
                                    hexPanel.repaint();

                                    sourceHex = null;

                                    System.out.println("No piece on cell\n");
                                }

                            } else if (hex != sourceHex) {

                                destinationHex = hex;

                                hexPanel.repaint();

                                System.out.print("\nDestination selected");

                                if (pieceToMove.move(sourceHex.getCell(), destinationHex.getCell())) {

                                    sourceHex.setToDefaultColor();

                                    sourceHex.setCell(new Cell.EmptyCell(sourceHex.getCell()));
                                    destinationHex.setCell(new Cell.OccupiedCell(destinationHex.getCell(), pieceToMove));

                                    System.out.print(" (Legal)\n");
                                    System.out.println(board);
                                    System.out.print("Move successful\n");

                                    if (team1.getNumPieces() == 0 || team2.getNumPieces() == 0) {
                                        winningTeam = currentMove;
                                        System.out.println(winningTeam.getName() + " wins!\n");
                                        gameEnded = true;
                                        EndScreenFrame endScreenFrame = new EndScreenFrame();
                                    }
                                    //messagePanel.pieceMoved(sourceHex.getCell(), destinationHex.getCell());

                                    if (currentMove == team1) {
                                        currentMove = team2;
                                    } else {
                                        currentMove = team1;
                                    }

                                    sourceHex = null;
                                    destinationHex = null;
                                    pieceToMove = null;

                                } else {

                                    sourceHex = null;
                                    destinationHex = null;
                                    pieceToMove = null;

                                    System.out.print(" (Illegal)\n");
                                }
                            }

                        }

                        break;

                    }
                }
            }
        }
    }

    class GameMessagePanel extends JPanel {

        void pieceMoved(Cell startCell, Cell endCell) {
            currentMessage = endCell.getPiece().getTeam().getName() + " moved from ("
                            + startCell.col() + ", " + startCell.row() + ") to ("
                            + endCell.col() + ", " + endCell.row() + ").";
            currentMessageDisplay.setText(currentMessage);
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(Color.RED);
            g.drawString(currentMessage, 100, 0);
        }

    }

    class EndScreenFrame extends JFrame {

        private EndScreenFrame() {
            setSize(250,100);
            setLocation(gameFrameCenterpoint);
            add(new EndScreenPanel());
            setVisible(true);
        }

        class EndScreenPanel extends JPanel {

            JLabel winnerLabel = new JLabel(winningTeam.getName() + " wins!");
            JButton rematchButton = new JButton("Rematch");
            JButton newGameButton = new JButton("New game");

            private EndScreenPanel() {
                add(winnerLabel);
                add(rematchButton);
                add(newGameButton);

                rematchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        gameFrame.dispose();
                        Game rematch = new Game(new HexChex(new Board(board.getWidth(), board.getHeight()), team1, team2));
                    }
                });

                newGameButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ResizeBoardFrame resizeBoardFrame = new ResizeBoardFrame();
                    }
                });
            }
        }

    }

    class ResizeBoardFrame extends JFrame {

        private ResizeBoardFrame() {
            setTitle("Resize board");
            setLayout(new GridBagLayout());
            setLocation(gameFrameCenterpoint);
            setSize(500, 500);
            add(new ResizeInputPanel());
            setVisible(true);
        }

        class ResizeInputPanel extends JPanel {

            static final int DIM_MIN = 5;
            static final int DIM_MAX = 50;
            final int WIDTH_INIT = board.getWidth();
            final int HEIGHT_INIT = board.getHeight();

            JSlider widthSlider = new JSlider(JSlider.HORIZONTAL, DIM_MIN, DIM_MAX, WIDTH_INIT);
            JSlider heightSlider = new JSlider(JSlider.VERTICAL, DIM_MIN, DIM_MAX, HEIGHT_INIT);
            JButton confirmButton = new JButton("Confirm");
            JButton cancelButton = new JButton("Cancel");

            Game tempGame;


            private ResizeInputPanel() {

                add(widthSlider);
                add(heightSlider);
                add(confirmButton);
                add(cancelButton);

                widthSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        board.setWidth(widthSlider.getValue());
                        board.setupDefaultBoard(team1, team2);
                        hexPanel = new HexPanel();
                        hexPanel.repaint();
                    }
                });

                heightSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        board.setHeight(heightSlider.getValue());
                        board.setupDefaultBoard(team1, team2);
                        hexPanel = new HexPanel();
                        hexPanel.repaint();
                    }
                });

                confirmButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Game resized = new Game(new HexChex(new Board(board.getWidth(), board.getHeight()), team1, team2));
                        gameFrame.dispose();
                        dispose();
                    }
                });

                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Game reverted = new Game(new HexChex(new Board(WIDTH_INIT, HEIGHT_INIT), team1, team2));
                        gameFrame.dispose();
                        dispose();
                    }
                });

            }
        }
    }

    private class NewGameFrame extends JFrame {

        private NewGameFrame() {
            setTitle("New game");
            setLayout(new GridBagLayout());
            setLocation(gameFrameCenterpoint);
            setSize(500, 250);
            add(new NewGamePanel());
            setVisible(true);
        }

        private class NewGamePanel extends JPanel {

            JButton setBoardSizeButton = new JButton("Set board size");
            JButton editTeamButton = new JButton("Edit teams");
            JButton startGameButton = new JButton("Start game");

            private NewGamePanel() {

                add(setBoardSizeButton);
                add(editTeamButton);
                add(startGameButton);

                setBoardSizeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ResizeBoardFrame resizeBoardFrame = new ResizeBoardFrame();
                    }
                });

                editTeamButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        EditTeamFrame editTeamFrame = new EditTeamFrame();
                    }
                });

                startGameButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });

            }

        }

    }

    class EditTeamFrame extends JFrame {

        private EditTeamFrame() {
            setTitle("Edit teams");
            setLayout(new GridBagLayout());
            setLocation(gameFrameCenterpoint);
            setSize(1750, 500);

            EditTeamPanel team1Panel = new EditTeamPanel(team1);

            EditTeamPanel team2Panel = new EditTeamPanel(team2);

            JButton confirmButton = new JButton("Confirm");

            add(team1Panel);
            add(team2Panel);
            add(confirmButton);

            confirmButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    team1.setName(team1Panel.teamNameField.getText());
                    team2.setName(team2Panel.teamNameField.getText());
                    dispose();
                }
            });

            setVisible(true);
        }

        class EditTeamPanel extends JPanel {

            private Team team;

            JColorChooser teamColorChooser;
            JLabel teamNameLabel = new JLabel("Team name");
            JTextField teamNameField;


            void setTeam(Team team) {
                this.team = team;
            }

            private EditTeamPanel(Team team) {

                this.team = team;

                setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

                teamNameField = new JTextField(team.getName());
                teamColorChooser = new JColorChooser(team.getColor());

                add(teamColorChooser);
                add(teamNameLabel);
                add(teamNameField);

                teamColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        team.setColor(teamColorChooser.getColor());
                        hexPanel.repaint();
                    }
                });

                teamNameField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        team.setName(teamNameField.getText());
                    }
                });

            }

        }

    }

}


