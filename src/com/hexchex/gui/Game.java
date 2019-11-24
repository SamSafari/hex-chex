package com.hexchex.gui;

import com.hexchex.engine.board.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Game {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(100, 100);
    private final static Dimension CELL_PANEL_DIMENSION = new Dimension(10, 5);
    private final Board board;

    public Game(Board board) {
        this.board = board;
        this.gameFrame = new JFrame("HexChex");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);

        final JMenuBar gameMenuBar = createGameMenuBar();
        
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setJMenuBar(gameMenuBar);

        this.gameFrame.setVisible(true);
    }

    private JMenuBar createGameMenuBar() {
        final JMenuBar gameMenuBar = new JMenuBar();
        gameMenuBar.add(createFileMenu());
        gameMenuBar.add(createEditMenu());
        return gameMenuBar;
    }

    private JMenu createEditMenu() {
        final JMenu editMenu = new JMenu("Edit");

        final JMenuItem resizeBoard = new JMenuItem("Resize board");
        resizeBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("open!!");
            }
        });

        editMenu.add(resizeBoard);

        return editMenu;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("open!");
            }
        });

        fileMenu.add(openPGN);

        return fileMenu;
    }

    private class BoardPanel extends JPanel {

        JPanel[][] boardCells;

        private JPanel[][] generateBoardCells() {

            boardCells = new JPanel[board.BOARD_HEIGHT()][board.BOARD_WIDTH()];

            for (int row = 0; row < board.getBoard().length; row++) {
                for (int col = 0; col < board.getBoard()[row].length; col++) {

                    if (col % 2 == 0) {
                        if (row == 0) {
                            JPanel emptyPanel = new JPanel();
                            boardCells[row][col] = emptyPanel;
                            add(emptyPanel);
                        } else {
                            final CellPanel cellPanel = new CellPanel(this);
                            boardCells[row][col] = cellPanel;
                            add(cellPanel);
                        }

                    } else if (row == board.BOARD_HEIGHT() - 1) {
                        JPanel emptyPanel = new JPanel();
                        boardCells[row][col] = emptyPanel;
                        add(emptyPanel);
                    } else {
                        final CellPanel cellPanel = new CellPanel(this);
                        boardCells[row][col] = cellPanel;
                        add(cellPanel);
                    }
                }
            }
            return boardCells;
        }

        BoardPanel() {
            super(new GridLayout(board.BOARD_HEIGHT(), board.BOARD_WIDTH()));
            boardCells = generateBoardCells();
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }


    }


    private class CellPanel extends JPanel {

        protected ImageIcon createImageIcon(String path) {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                return new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(80, 40, Image.SCALE_DEFAULT));
            } else {
                System.err.println("Couldn't find file: " + path);
                return null;
            }
        }

        ImageIcon icon = createImageIcon("hexagon.png");
        JLabel label = new JLabel(icon, JLabel.CENTER);

        CellPanel(final BoardPanel boardPanel) {
            super(new GridBagLayout());
            System.out.println(new java.io.File("hexagon.png").exists());
            setPreferredSize(CELL_PANEL_DIMENSION);
            add(label);
            assignCellColor();
            validate();
        }

        private void assignCellColor() {
            setBackground(Color.gray);
        }

    }

}

