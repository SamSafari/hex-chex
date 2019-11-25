package com.hexchex.gui;

import com.hexchex.engine.board.Board;
import com.hexchex.engine.board.Cell;
import com.hexchex.engine.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Game {

    private final JFrame gameFrame;
    private final HexPanel hexPanel;

    private Hexagon sourceHex, destinationHex;
    private Piece pieceToMove;
    private final Board board;

    public Game(Board board) {
        this.board = board;
        this.gameFrame = new JFrame("HexChex");
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameFrame.setSize(new Dimension(this.board.BOARD_WIDTH()*100,
                                            this.board.BOARD_HEIGHT()*100));

        final JMenuBar gameMenuBar = createGameMenuBar();
        this.gameFrame.setJMenuBar(gameMenuBar);

        this.hexPanel = new HexPanel();
        HexSelector selector = new HexSelector(hexPanel);
        hexPanel.addMouseListener(selector);

        this.gameFrame.add(this.hexPanel);

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

        final JMenuItem saveGame = new JMenuItem("Save game");
        saveGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("open!");
            }
        });

        fileMenu.add(saveGame);

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
            this.setBackground(Color.LIGHT_GRAY);
            int radius = 50;

            for (int row = 0; row < board.BOARD_HEIGHT(); row++) {
                for (int col = 0; col < board.BOARD_WIDTH(); col++) {

                    if (col % 2 == 0) {
                        if (row == 0) {

                        } else {

                            if (row % 2 == 0) {
                                g.setColor(Color.blue);
                            } else {
                                g.setColor(new Color(0, 50, 100));
                            }
                            Point hexCenter = new Point(col * 80 + radius, row * 90 - 45 + radius);
                            Hexagon hex = new Hexagon(hexCenter, radius, board.getBoard()[row][col]);

                            g.fillPolygon(hex);
                            hexList.add(hex);

                            if(hex.getCell().isOccupied()) {
                                Color color = hex.getCell().getPiece().getTeam().getColor();
                                g.setColor(color);
                                g.fillOval(hexCenter.x-(radius/2), hexCenter.y-(radius/2), radius, radius);
                            }

                        }

                    } else if (row == board.BOARD_HEIGHT() - 1) {

                    } else {
                        if (row % 2 == 0) {
                            g.setColor(Color.ORANGE);
                        } else {
                            g.setColor(new Color(200, 150, 0));
                        }
                        Point hexCenter = new Point(col * 80 + radius, row * 90 + radius);
                        Hexagon hex = new Hexagon(hexCenter, radius, board.getBoard()[row][col]);

                        g.fillPolygon(hex);
                        hexList.add(hex);

                        if(hex.getCell().isOccupied()) {
                            Color color = hex.getCell().getPiece().getTeam().getColor();
                            g.setColor(color);
                            g.fillOval(hexCenter.x-(radius/2), hexCenter.y-(radius/2), radius, radius);
                        }

                    }
                }
            }



        }

    }

    class HexSelector implements MouseListener {

        HexPanel hexPanel;

        public HexSelector(HexPanel hexPanel) {
            this.hexPanel = hexPanel;
        }

        /**
         * Moves the piece on a selected Hexagon (LClick 1) to another selected Hexagon (LClick 2),
         * or cancels all selections (RClick). Will update the graphical representation of the board when complete.
         * @param e User mouse click input
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            Point p = e.getPoint();
            ArrayList<Hexagon> hexes = hexPanel.hexList;
            for(int i = 0; i < hexes.size(); i++) {
                if(hexes.get(i).contains(p)) {

                    if(isRightMouseButton(e)) {

                        sourceHex = null;
                        destinationHex = null;
                        pieceToMove = null;
                        System.out.println("Canceled");

                    } else if(isLeftMouseButton(e)) {

                        if(sourceHex == null) {
                            sourceHex = hexes.get(i);

                            if(hexes.get(i).getCell().isOccupied()) {
                                pieceToMove = sourceHex.getCell().getPiece();
                                System.out.println("Source selected");
                            } else {
                                sourceHex = null;
                                System.out.println("No piece on cell");
                            }

                        } else {
                            destinationHex = hexes.get(i);
                            destinationHex.setColor(Color.GREEN);
                            System.out.println("Destination selected");

                            if(pieceToMove.validateMove(sourceHex.getCell(), destinationHex.getCell())) {

                                pieceToMove.executeMove(sourceHex.getCell(), destinationHex.getCell());

                                sourceHex.setCell(new Cell.EmptyCell(sourceHex.getCell()));
                                destinationHex.setCell(new Cell.OccupiedCell(destinationHex.getCell(), pieceToMove));

                                sourceHex = null;
                                destinationHex = null;

                                System.out.println(board);
                                System.out.println("Move successful");

                            }


                        }
                    }

                    hexPanel.repaint();
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


}


