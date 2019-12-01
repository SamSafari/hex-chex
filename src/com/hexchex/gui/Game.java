package com.hexchex.gui;

import com.hexchex.HexChex;
import com.hexchex.engine.board.Board;
import com.hexchex.engine.board.Cell;
import com.hexchex.engine.pieces.Piece;
import com.hexchex.engine.pieces.Team;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;

import static javax.swing.SwingUtilities.*;

public class Game implements Serializable {

    private final JFrame gameFrame;
    private final HexPanel hexPanel;
    private GameMessagePanel messagePanel;
    private JLabel currentMessageDisplay;

    private String currentMessage = "xD";
    private Team currentMove;
    private Hexagon sourceHex, destinationHex;
    private Piece pieceToMove;
    private final Board board;
    private final Team team1, team2;

    public Game(HexChex hexChex) {
        board = hexChex.getBoard();
        team1 = hexChex.getTeam1();
        team2 = hexChex.getTeam2();
        currentMove = team1;

        gameFrame = new JFrame("HexChex");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(new Dimension(board.BOARD_WIDTH() * 100,
                board.BOARD_HEIGHT() * 100 + 100));
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
                    sourceHex = null;
                    destinationHex = null;
                    pieceToMove = null;
                    System.out.println("Cancelled");
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

    private MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException e) {
            System.err.println("formatter is bad: " + e.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    private JMenu createEditMenu() {

        final JMenu editMenu = new JMenu("Edit");

        final JMenuItem resizeBoard = new JMenuItem("Resize board");

        resizeBoard.addActionListener(e -> {

            final int[] newWidth = new int[1];
            final int[] newHeight = new int[1];
            final boolean[] dimensionsValid = {false, false};


            final JFrame resizePopout = new JFrame("Resize board");
            final JPanel resizeInput = new JPanel();

            resizePopout.setLayout(new GridBagLayout());
            resizePopout.setSize(250, 250);
            resizePopout.add(resizeInput);

            JTextField widthField = new JTextField("width");
            widthField.setToolTipText("width");

            JTextField heightField = new JTextField("height");
            heightField.setToolTipText("height");

            JButton createBoardButton = new JButton("Create board");
            createBoardButton.setEnabled(false);

            widthField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    if (widthField.getText().equals("")) {
                        widthField.setText("width");
                    }
                    try {
                        newWidth[0] = Integer.parseInt(widthField.getText());
                        dimensionsValid[0] = true;
                        if (dimensionsValid[0] && dimensionsValid[1]) {
                            createBoardButton.setEnabled(true);
                        }
                        if (newWidth[0] < 5 || newWidth[0] > 100) {
                            widthField.setText("");
                        }
                    } catch(NumberFormatException nfe) {
                        widthField.setText("");
                    }
                }
            });
            widthField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (widthField.getText().equals("width")) {
                        widthField.setText("");
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (widthField.getText().equals("")) {
                        widthField.setText("width");
                    }
                }
            });

            heightField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    if (heightField.getText().equals("")) {
                        heightField.setText("height");
                    }
                    try {
                        newHeight[0] = Integer.parseInt(heightField.getText());
                        dimensionsValid[1] = true;
                        if (dimensionsValid[0] && dimensionsValid[1]) {
                            createBoardButton.setEnabled(true);
                        }
                        if (newHeight[0] < 5 || newHeight[0] > 100) {
                            heightField.setText("");
                        }
                    } catch(NumberFormatException nfe) {
                        heightField.setText("");
                    }
                }
            });
            heightField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (heightField.getText().equals("height")) {
                        heightField.setText("");
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (heightField.getText().equals("")) {
                        heightField.setText("height");
                    }
                }
            });

            createBoardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    Board newBoard = new Board(newWidth[0], newHeight[0]);
                    JFrame confirmResizeWindow = new JFrame("Confirm resize");
                    confirmResizeWindow.setSize(500, 500);
                    confirmResizeWindow.setLayout(new GridBagLayout());

                    JButton resizeButton = new JButton("Resize");
                    resizeButton.setBorderPainted(true);

                    JButton cancelButton = new JButton("Cancel");
                    JLabel resizeWarning = new JLabel("Resizing the board will cause all current game progress \n" +
                            "to be lost. Continue?");

                    GridBagConstraints c = new GridBagConstraints();
                    c.fill = GridBagConstraints.PAGE_START;
                    c.weightx = 0.5;
                    c.gridx = 0;
                    c.gridy = 0;
                    confirmResizeWindow.add(resizeWarning, c);

                    c.fill = GridBagConstraints.LINE_START;
                    c.weightx = 0.5;
                    c.gridx = 0;
                    c.gridy = 1;
                    confirmResizeWindow.add(resizeButton, c);

                    c.fill = GridBagConstraints.LINE_END;
                    c.weightx = 0.5;
                    c.gridx = 1;
                    c.gridy = 1;
                    confirmResizeWindow.add(cancelButton, c);


                    resizeButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Game newGame = new Game(new HexChex(newBoard, team1, team2));
                            confirmResizeWindow.dispose();
                            resizePopout.dispose();
                            gameFrame.dispose();
                        }
                    });

                    cancelButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            confirmResizeWindow.dispose();
                        }
                    });

                    confirmResizeWindow.setVisible(true);

                }
            });

            resizeInput.add(widthField);
            resizeInput.add(heightField);
            resizeInput.add(createBoardButton);

            resizePopout.setVisible(true);

        });

        editMenu.add(resizeBoard);

        return editMenu;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

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

        fileMenu.add(saveGame);
        fileMenu.add(loadGame);
        fileMenu.add(deleteSaves);

        return fileMenu;
    }


    private class HexPanel extends JPanel {

        ArrayList<Hexagon> hexList = new ArrayList<>();

        /**
         * Generates a graphical representation of the board with properly tessellated Hexagons, each with its own
         * Cell object. This method does not need to be actively invoked.
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            int radius = 50;
            setBackground(Color.LIGHT_GRAY);

           /* g.setColor(new Color(200, 150, 100));
            g.setColor(new Color(100, 70, 30));
            g.setColor(new Color(150, 100, 50));
            g.setColor(new Color(75, 50, 30));*/

            Color boardColor = new Color(75, 75, 75);
            g.setColor(boardColor);

            for(int row = 0; row < board.getBoard().length; row++) {
                for(int col = 0; col < board.getBoard()[row].length; col++) {

                    if (col % 2 == 0) {
                        if (row % 2 == 0) {

                            Point hexCenter = new Point(col * 80 + (2 * radius), row * 45 + (2 * radius));
                            Hexagon hex = new Hexagon(hexCenter, radius, board.getBoard()[row][col]);

                            g.fillPolygon(hex);
                            hexList.add(hex);


                            if (hex.getCell().isOccupied()) {

                                Color temp = g.getColor();

                                Color color = hex.getCell().getPiece().getTeam().getColor();
                                g.setColor(color);
                                g.fillOval(hexCenter.x - (radius / 2), hexCenter.y - (radius / 2), radius, radius);

                                g.setColor(temp);

                            }
                        }

                    } else if (row % 2 != 0) {

                        Point hexCenter = new Point(col * 80 + (2 * radius), row * 45 + (2 * radius));
                        Hexagon hex = new Hexagon(hexCenter, radius, board.getBoard()[row][col]);

                        g.fillPolygon(hex);
                        hexList.add(hex);

                        if (hex.getCell().isOccupied()) {
                            Color temp = g.getColor();

                            Color color = hex.getCell().getPiece().getTeam().getColor();
                            g.setColor(color);
                            g.fillOval(hexCenter.x - (radius / 2), hexCenter.y - (radius / 2), radius, radius);

                            g.setColor(temp);
                        }
                    }

                }
            }

        } //end paintComponent()

    }

    private class HexSelector extends MouseAdapter {

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

            Point p = e.getPoint();
            ArrayList<Hexagon> hexes = hexPanel.hexList;
            for(Hexagon hex : hexes) {

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
                                    System.out.println("Source selected");
                                }

                            } else {
                                sourceHex = null;
                                System.out.println("No piece on cell");
                            }

                        } else {

                            destinationHex = hex;

                            hexPanel.repaint();
                            System.out.print("\nDestination selected");

                            if (pieceToMove.move(sourceHex.getCell(), destinationHex.getCell())) {

                                sourceHex.setCell(new Cell.EmptyCell(sourceHex.getCell()));
                                destinationHex.setCell(new Cell.OccupiedCell(destinationHex.getCell(), pieceToMove));

                                System.out.print(" (Legal)\n");
                                System.out.println(board);
                                System.out.print("Move successful\n");
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

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

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


}


