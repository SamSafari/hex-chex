package com.hexchex.gui;

import com.hexchex.HexChex;
import com.hexchex.engine.board.Board;
import com.hexchex.engine.board.Cell;
import com.hexchex.engine.pieces.Piece;
import com.hexchex.engine.pieces.Team;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

import static javax.swing.SwingUtilities.*;

/**
 * Creates the graphical representation of a HexChex Object and handles the game logic
 */
public class Game implements Serializable, Cloneable {

    private static final long serialVersionUID = 12345L;

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private static Game instance;
    private static final File saveDirectory = new File("src/saves");
    private static final File cacheDirectory = new File("src/cache");
    private JFrame gameFrame;
    private Point gameFrameCenter;
    private HexPanel hexPanel;
    private int hexRadius;

    private boolean gameEnded = false;
    private Team winningTeam;
    private Hexagon sourceHex, destinationHex;
    private Piece pieceToMove;

    private HexChex getHexChex() {
        return hexChex;
    }

    private HexChex hexChex;
    private Board board;
    private Team team1, team2;

    private Dimension gameFrameDimension;

    private Dimension calculateGameFrameDimension() {
        return new Dimension(board.getWidth() * ((int)Math.round(hexRadius * 1.7)) + (2 * hexRadius),
                board.getHeight() * ((int)Math.round(hexRadius * 2.2)) + (2 * hexRadius));
    }

    private int calculateHexRadius() {
        return hexRadius = 300 / (int) Math.round(Math.sqrt(board.getWidth() * board.getHeight()));
    }

    /**
     * Creates a temporary save of the current Game by cloning and serializing it to a file in cacheDirectory
     */
    private void cacheGame() {
        try {
            Game save = (Game) instance.clone();

            FileOutputStream fileOut = new FileOutputStream(cacheDirectory.getPath() + "\\temp");
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

            objOut.writeObject(save);
            objOut.close();

            System.out.println("Current game has been cached!");

        } catch(IOException | CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Replaces the current Game with the one stored in cacheDirectory
     */
    private void loadCachedGame() {
        try {
            FileInputStream fileIn = new FileInputStream(cacheDirectory.getPath() + "\\temp");
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            Game loaded = (Game) objIn.readObject();

            gameFrame.dispose();

            new Game(loaded.getHexChex());

            File tempSave = new File(cacheDirectory.getPath() + "\\temp");
            tempSave.delete();

            System.out.println("Cached game restored!");

        } catch(IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public Game(HexChex hexChex) {

        instance = this;

        this.hexChex = hexChex;

        board = hexChex.getBoard();
        team1 = hexChex.getTeam1();
        team2 = hexChex.getTeam2();
        hexChex.setHexRadius(calculateHexRadius());

        gameFrame = new JFrame("HexChex");

        if (hexChex.getGameName() != null) {
            gameFrame.setTitle("HexChex - " + hexChex.getGameName());
        }

        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrameDimension = calculateGameFrameDimension();
        gameFrame.setSize(gameFrameDimension);
        BorderLayout gameFrameLayout = new BorderLayout();
        gameFrame.setLayout(gameFrameLayout);
        gameFrameCenter = new Point(gameFrame.getSize().width / 4, gameFrame.getSize().height / 4);
        gameFrame.setResizable(false);

        final JMenuBar gameMenuBar = createGameMenuBar();
        gameFrame.setJMenuBar(gameMenuBar);

        hexPanel = new HexPanel();
        HexSelector selector = new HexSelector(hexPanel);

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

                        System.out.println("Cancelled\n");

                        hexPanel.repaint();
                    }
                }
            }
        });

        gameFrame.add(hexPanel);

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
            new EditTeamFrame();
        });

        final JMenuItem resizeBoard = new JMenuItem("Resize board");
        resizeBoard.addActionListener(e -> {
            ActionListener continueResize = e1 -> new ResizeBoardFrame();
            new ConfirmPrompt("WARNING: Resizing the board will cause all current game progress to be lost!", "Continue",
                    continueResize, "Resize warning");
        });

        editMenu.add(editTeams);
        editMenu.add(resizeBoard);

        return editMenu;
    }

    private final JMenuItem loadGame = new JMenuItem("Load game");
    private final JMenuItem newGame = new JMenuItem("New game");
    private final JMenuItem saveGame = new JMenuItem("Save game");
    private final JMenuItem deleteSaves = new JMenuItem("Delete saved games");

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        newGame.addActionListener(e -> new NewGameFrame());

        saveGame.addActionListener(e -> new SaveGameFrame());

        if (Objects.requireNonNull(saveDirectory.listFiles()).length > 0) {
            loadGame.setEnabled(true);
            deleteSaves.setEnabled(true);
        } else {
            loadGame.setEnabled(false);
            deleteSaves.setEnabled(false);
        }

        loadGame.addActionListener(e -> new LoadGameFrame());

        deleteSaves.addActionListener(e -> {

            ActionListener deleteAllSaves = e1 -> {

                int filesDeleted = 0;

                for(File file : Objects.requireNonNull(saveDirectory.listFiles())) {
                    file.delete();
                    filesDeleted++;
                }

                if (filesDeleted == 0) {
                    new NotificationPrompt("No save files to delete!", "No save files found");
                } else {
                    new NotificationPrompt(filesDeleted + " save files have been successfully deleted!", "Save files deleted");
                }

                loadGame.setEnabled(false);
                deleteSaves.setEnabled(false);

            };

            new ConfirmPrompt("Are you sure you want to delete all saved games?", deleteAllSaves, "Delete saves");

        });

        fileMenu.add(newGame);
        fileMenu.add(saveGame);
        fileMenu.add(loadGame);
        fileMenu.add(deleteSaves);

        return fileMenu;
    }

    public class HexPanel extends JPanel {

        Color boardColor = Color.DARK_GRAY;
        Color backgroundColor = Color.LIGHT_GRAY;

        private HexPanel() {
            createHexes();
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        ArrayList<Hexagon> hexList = new ArrayList<>();

        /**
         * Populates hexList with Hexagon objects with proper color, locations and radii to tessellate,
         * and assigns them with the appropriate Cell object they represent. Will old values of hexList every time
         * it's called.
         */
        private void createHexes() {

            hexList.clear();

            for(int row = 0; row < board.getBoard().length; row++) {
                for(int col = 0; col < board.getBoard()[row].length; col++) {

                    if (col % 2 == 0) {
                        if (row % 2 == 0) {

                            Point hexCenter = new Point(col * ((int)Math.round(hexRadius*1.6)) + (2 * hexRadius),
                                    row * ((int)Math.round(hexRadius*.92)) + (2 * hexRadius));
                            Hexagon hex = new Hexagon(hexCenter, hexRadius, board.getBoard()[row][col]);
                            hex.setDefaultColor(boardColor);
                            hex.setToDefaultColor();

                            hexList.add(hex);

                        }

                    } else if (row % 2 != 0) {

                        Point hexCenter = new Point(col * ((int)Math.round(hexRadius*1.6)) + (2 * hexRadius),
                                row * ((int)Math.round(hexRadius*.92)) + (2 * hexRadius));
                        Hexagon hex = new Hexagon(hexCenter, hexRadius, board.getBoard()[row][col]);
                        hex.setDefaultColor(boardColor);
                        hex.setToDefaultColor();

                        hexList.add(hex);

                    }
                }
            }
        }

        /**
         * Generates a graphical representation of the board with properly tessellated Hexagons, each with its own
         * Cell object. If a hexagon's cell is occupied, it will generate a graphical representation of a piece with
         * its appropriate team color. This method does not need to be actively invoked. Uses Graphics2D to allow for
         * Strokes to be applied to components.
         */
        @Override
        public void paintComponent(Graphics g) {
            paintComponent((Graphics2D) g);
        }

        private void paintComponent(Graphics2D g) {
            super.paintComponent(g);

            int pieceRadius = hexRadius + 10;
            setBackground(backgroundColor);

            for(Hexagon hex : hexList) {
                g.setColor(hex.getColor());
                g.fillPolygon(hex);
                g.setColor(boardColor);

                if (hex.getCell().isOccupied()) {
                    Color tmpC = g.getColor();
                    Stroke tmpS = g.getStroke();
                    Font tmpF = g.getFont();

                    Color color = hex.getCell().getPiece().getTeam().getColor();
                    g.setColor(color);
                    g.fillOval(hex.getCenter().x - (pieceRadius / 2), hex.getCenter().y - (pieceRadius / 2), pieceRadius, pieceRadius);

                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.BLACK);
                    g.drawOval(hex.getCenter().x - (pieceRadius / 2), hex.getCenter().y - (pieceRadius / 2), pieceRadius, pieceRadius);

                    String abbr = hex.getCell().getPiece().getTeam().getAbbr().toString();
                    
                    double luminance = getLuminance(color);
                    double threshold = getLuminance(new Color(55, 55, 55));

                    if (luminance < threshold) {
                        g.setColor(Color.WHITE);
                    }

                    int abbrAlignment = (hex.getCenter().x - g.getFontMetrics().stringWidth(abbr)/2);
                    g.drawString(abbr, abbrAlignment, hex.getCenter().y + pieceRadius/8);

                    g.setColor(tmpC);
                    g.setStroke(tmpS);
                    g.setFont(tmpF);
                }
            }
        }

        private double getLuminance(Color color) {
            return 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
        }
    }

    private class HexSelector extends MouseAdapter {

        HexPanel hexPanel;

        HexSelector(HexPanel hexPanel) {
            this.hexPanel = hexPanel;
        }

        /**
         * Moves the piece on a selected Hexagon (LClick 1) to another selected Hexagon (LClick 2),
         * or cancels all selections (RClick). Will update the graphical representation of the board when complete.
         * @param e User mouse click input
         */
        @Override
        public void mouseClicked(MouseEvent e) {

            if (!gameEnded) {

                Point p = e.getPoint();

                for(Hexagon hex : hexPanel.hexList) {
                    if (hex.contains(p)) {

                        if (isMiddleMouseButton(e)) {
                            System.out.println("(" + hex.getCell().col() + ", " + hex.getCell().row() + ")\n");
                        }

                        if (isLeftMouseButton(e)) {

                            if (sourceHex == null) {

                                sourceHex = hex;

                                if (hex.getCell().isOccupied()) {

                                    if (hex.getCell().getPiece().getTeam() != hexChex.getCurrentMove()) {
                                        System.out.println(hexChex.getCurrentMove().getName() + "'s move.\n");
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

                            } else if (hex == sourceHex) {
                                System.out.println("Source deselected\n");

                                sourceHex.setToDefaultColor();
                                sourceHex = null;
                                hexPanel.repaint();
                            } else {

                                if (hex.getCell().isOccupied() && hex.getCell().getPiece().getTeam() ==
                                        sourceHex.getCell().getPiece().getTeam()) {
                                    sourceHex.setToDefaultColor();
                                    sourceHex = hex;
                                    sourceHex.setToSelectedColor();
                                    hexPanel.repaint();
                                } else {

                                    destinationHex = hex;

                                    hexPanel.repaint();

                                    System.out.print("\nDestination selected");

                                    if (pieceToMove.move(destinationHex.getCell())) {

                                        sourceHex.setToDefaultColor();

                                        sourceHex.setCell(new Cell.EmptyCell(sourceHex.getCell()));
                                        destinationHex.setCell(new Cell.OccupiedCell(destinationHex.getCell(), pieceToMove));

                                        System.out.print(" (Legal)\n");
                                        System.out.println(board);
                                        System.out.println("Move successful\n");

                                        if (team1.getNumActivePieces() == 0 || team2.getNumActivePieces() == 0) {
                                            winningTeam = hexChex.getCurrentMove();
                                            System.out.println(winningTeam.getName() + " wins!\n");
                                            gameEnded = true;
                                            new EndScreenFrame();
                                        }

                                        if (hexChex.getCurrentMove() == team1) {
                                            hexChex.setCurrentMove(team2);
                                        } else {
                                            hexChex.setCurrentMove(team1);
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
                        }

                        break;

                    }
                }
            }
        }
    }

    private class EndScreenFrame extends JFrame {

        private EndScreenFrame() {

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gameFrame.setEnabled(true);
                }
            });

            setTitle(winningTeam.getName() + " wins!");
            setLocation(gameFrameCenter);
            add(new EndScreenPanel());
            pack();
            setVisible(true);
        }

        private class EndScreenPanel extends JPanel {

            private EndScreenPanel() {

                JLabel winnerLabel = new JLabel(winningTeam.getName() + " wins!");
                JButton rematchButton = new JButton("Rematch");
                JButton newGameButton = new JButton("New game");

                add(winnerLabel);
                add(rematchButton);
                add(rematchButton);
                add(newGameButton);

                rematchButton.addActionListener(e -> {
                    gameFrame.dispose();
                    EndScreenFrame.super.dispose();
                    new Game(new HexChex(new Board(board.getWidth(), board.getHeight()), team1, team2));
                });

                newGameButton.addActionListener(e -> {
                    new NewGameFrame();
                    EndScreenFrame.super.dispose();
                });

                gameFrame.setEnabled(false);
            }
        }
    }

    private class ResizeBoardFrame extends JFrame {

        private ResizeBoardFrame() {

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gameFrame.setEnabled(true);
                }
            });

            setTitle("Resize board");
            setLayout(new GridBagLayout());
            setLocation(gameFrameCenter);
            add(new ResizeInputPanel());
            pack();
            setVisible(true);

            gameFrame.setEnabled(false);
        }

        class ResizeInputPanel extends JPanel {

            static final int DIM_MIN = 5;
            static final int DIM_MAX = 15;

            final int WIDTH_INIT = board.getWidth();
            final int HEIGHT_INIT = board.getHeight();

            JSlider widthSlider = new JSlider(JSlider.HORIZONTAL, DIM_MIN, DIM_MAX, WIDTH_INIT);
            JSlider heightSlider = new JSlider(JSlider.VERTICAL, DIM_MIN, DIM_MAX, HEIGHT_INIT);
            JButton confirmButton = new JButton("Confirm");
            JButton cancelButton = new JButton("Cancel");

            private ResizeInputPanel() {

                add(widthSlider);
                add(heightSlider);
                add(confirmButton);
                add(cancelButton);

                widthSlider.setPaintTicks(true);
                widthSlider.setPaintLabels(true);
                heightSlider.setPaintTicks(true);
                heightSlider.setPaintLabels(true);

                widthSlider.createStandardLabels(1);
                heightSlider.createStandardLabels(1);

                widthSlider.setMajorTickSpacing(5);
                widthSlider.setMinorTickSpacing(1);
                heightSlider.setMajorTickSpacing(5);
                heightSlider.setMinorTickSpacing(1);

                cacheGame();

                widthSlider.addChangeListener(e -> {

                    board.setWidth(widthSlider.getValue());

                    hexChex.setHexRadius(calculateHexRadius());

                    board.setupDefaultBoard(team1, team2);
                    hexPanel.createHexes();
                    hexPanel.repaint();
                    hexChex.setCurrentMove(team1);

                    gameFrameDimension = calculateGameFrameDimension();
                    gameFrame.setSize(gameFrameDimension);
                });

                heightSlider.addChangeListener(e -> {

                    board.setHeight(heightSlider.getValue());

                    hexChex.setHexRadius(calculateHexRadius());

                    board.setupDefaultBoard(team1, team2);
                    hexPanel.createHexes();
                    hexPanel.repaint();
                    hexChex.setCurrentMove(team1);

                    gameFrameDimension = calculateGameFrameDimension();
                    gameFrame.setSize(gameFrameDimension);
                });

                confirmButton.addActionListener(e -> {
                    gameFrame.setEnabled(true);
                    dispose();
                });

                cancelButton.addActionListener(e -> {
                    ResizeBoardFrame.super.dispose();
                    loadCachedGame();
                });

            }
        }
    }

    private class NewGameFrame extends JFrame {

        private NewGameFrame() {

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gameFrame.setEnabled(true);
                }
            });

            setTitle("New game");
            setLayout(new GridBagLayout());
            setLocation(gameFrameCenter);
            add(new NewGamePanel());
            pack();
            setVisible(true);
        }

        private class NewGamePanel extends JPanel {

            JButton setBoardSizeButton = new JButton("Set board size");
            JButton editTeamButton = new JButton("Edit teams");
            JButton startGameButton = new JButton("Start game");
            JButton newDefaultGameButton = new JButton("New default game");

            private NewGamePanel() {

                add(setBoardSizeButton);
                add(editTeamButton);
                add(startGameButton);
                add(newDefaultGameButton);

                setBoardSizeButton.addActionListener(e -> {
                    new ResizeBoardFrame();
                });

                editTeamButton.addActionListener(e -> {
                    new EditTeamFrame();
                });

                startGameButton.addActionListener(e -> {
                    NewGameFrame.super.dispose();
                    gameFrame.dispose();
                    new Game(new HexChex(new Board(board.getWidth(), board.getHeight()), team1, team2));
                });

                newDefaultGameButton.addActionListener(e -> {
                    dispose();
                    gameFrame.dispose();
                    new Game(new HexChex());
                });

            }

        }

    }

    private class EditTeamFrame extends JFrame {

        private EditTeamFrame() {

            cacheGame();

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gameFrame.setEnabled(true);
                }
            });

            setTitle("Edit teams");

            JPanel verticalHolderPanel = new JPanel();
            JPanel horizontalHolderPanel = new JPanel();

            verticalHolderPanel.setLayout(new BoxLayout(verticalHolderPanel, BoxLayout.Y_AXIS));

            setLocation(gameFrameCenter);

            EditTeamPanel team1Panel = new EditTeamPanel(team1);

            EditTeamPanel team2Panel = new EditTeamPanel(team2);

            JButton confirmButton = new JButton("Confirm");
            JButton cancelButton = new JButton("Cancel");

            add(verticalHolderPanel);

            verticalHolderPanel.add(team1Panel);
            verticalHolderPanel.add(team2Panel);
            verticalHolderPanel.add(horizontalHolderPanel);

            horizontalHolderPanel.add(confirmButton);
            horizontalHolderPanel.add(cancelButton);

            confirmButton.addActionListener(e -> {
                team1.setName(team1Panel.teamNameField.getText());
                team2.setName(team2Panel.teamNameField.getText());
                gameFrame.setEnabled(true);
                hexPanel.repaint();
                dispose();
            });

            cancelButton.addActionListener(e -> {
                loadCachedGame();
                dispose();
            });

            setVisible(true);
            pack();
            gameFrame.setEnabled(false);
        }

        class EditTeamPanel extends JPanel {

            JColorChooser teamColorChooser;
            JLabel teamNameLabel = new JLabel("Team name");
            JTextField teamNameField;

            private EditTeamPanel(Team team)  {

                setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

                teamNameField = new JTextField(team.getName());
                teamColorChooser = new JColorChooser(team.getColor());

                AbstractColorChooserPanel[] panels = teamColorChooser.getChooserPanels();
                for (AbstractColorChooserPanel panel : panels) {
                    if (!panel.getDisplayName().equals("HSV")) {
                        teamColorChooser.removeChooserPanel(panel);
                    }
                }

                teamColorChooser.setPreviewPanel(new JPanel());

                add(teamColorChooser);
                add(teamNameLabel);
                add(teamNameField);

                teamColorChooser.getSelectionModel().addChangeListener(e -> {
                    team.setColor(teamColorChooser.getColor());
                    hexPanel.repaint();
                });

            }

        }

    }

    private class SaveGameFrame extends JFrame {

        private SaveGameFrame() {

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gameFrame.setEnabled(true);
                }
            });

            setTitle("Save game");
            setLayout(new GridBagLayout());
            setLocation(gameFrameCenter);

            add(new SaveGamePanel());
            pack();

            setVisible(true);
            gameFrame.setEnabled(false);
        }


        private class SaveGamePanel extends JPanel {

            private void createSave(String name) {
                try {
                    Game save = (Game) instance.clone();

                    FileOutputStream fileOut = new FileOutputStream
                            (saveDirectory.getPath() + "\\" + name);

                    ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
                    objOut.writeObject(save);
                    objOut.close();

                    loadGame.setEnabled(true);
                    deleteSaves.setEnabled(true);


                    System.out.println("Game saved");
                    gameFrame.setEnabled(true);

                } catch(IOException | CloneNotSupportedException ex) {
                    ex.printStackTrace();
                }
            }

            private SaveGamePanel() {

                JTextField saveNameField = new JTextField();
                saveNameField.setText("Game(" + Objects.requireNonNull(saveDirectory.listFiles()).length + ")");
                JButton saveButton = new JButton("Save game");
                JButton cancelButton = new JButton("Cancel");

                add(saveNameField);
                saveNameField.setColumns(10);

                add(saveButton);
                add(cancelButton);

                saveButton.addActionListener(e -> {

                    File[] existingSaves = saveDirectory.listFiles();
                    final boolean[] saveComplete = {false};

                    if (existingSaves != null) {
                        for(File file : existingSaves) {
                            if (file.getName().equalsIgnoreCase(saveNameField.getText())) {

                                System.out.println("A save with that name already exists!");

                                ActionListener overwrite = e1 -> {
                                    file.delete();
                                    createSave(saveNameField.getText());

                                    System.out.println("Game saved");
                                    System.out.println(saveNameField.getText() + " has been overwritten!");

                                    saveComplete[0] = true;
                                    SaveGameFrame.super.dispose();

                                };

                                new ConfirmPrompt("A save with that name already exists. Overwrite?", overwrite, "Overwrite save");

                            }
                        }
                    }

                    if (!saveComplete[0]) {
                        createSave(saveNameField.getText());
                        SaveGameFrame.super.dispose();
                    }

                    gameFrame.setEnabled(true);

                });

                cancelButton.addActionListener(e -> {
                    gameFrame.setEnabled(true);
                    SaveGameFrame.super.dispose();
                });

            }

        }

    }

    private class LoadGameFrame extends JFrame {

        private LoadGameFrame() {

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gameFrame.setEnabled(true);
                }
            });

            setTitle("Load game");
            setLayout(new GridBagLayout());
            setLocation(gameFrameCenter);

            add(new LoadGamePanel());
            pack();
            setVisible(true);
            gameFrame.setEnabled(false);
        }

        private class LoadGamePanel extends JPanel {

            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Saves");
            DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
            String newGameName;

            FileInputStream gameToLoad;
            JButton loadButton = new JButton("Load game");
            JButton cancelButton = new JButton("Cancel");

            private LoadGamePanel() {

                for(File file : Objects.requireNonNull(saveDirectory.listFiles())) {
                    DefaultMutableTreeNode saveNode = new DefaultMutableTreeNode(file.getName());
                    rootNode.add(saveNode);
                }

                JTree gameSaves = new JTree(treeModel);

                gameSaves.setRootVisible(false);

                add(gameSaves);
                add(loadButton);
                loadButton.setEnabled(false);
                add(cancelButton);

                gameSaves.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

                gameSaves.addTreeSelectionListener(e -> {

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) gameSaves.getLastSelectedPathComponent();
                    File nodeFile = new File(saveDirectory + "\\" + node.getUserObject());
                    newGameName = (String) node.getUserObject();

                    try {
                        gameToLoad = new FileInputStream(nodeFile.getPath());
                        loadButton.setEnabled(true);
                    } catch(FileNotFoundException ex) {
                        ex.printStackTrace();
                    }

                });


                loadButton.addActionListener(e -> {
                    try {
                        ObjectInputStream objIn = new ObjectInputStream(gameToLoad);
                        Game loaded = (Game) objIn.readObject();

                        gameFrame.dispose();
                        LoadGameFrame.super.dispose();

                        loaded.getHexChex().setGameName(newGameName);
                        new Game(loaded.getHexChex());

                        System.out.println("Game loaded");
                    } catch(IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                });

                cancelButton.addActionListener(e -> {
                    gameFrame.setEnabled(true);
                    LoadGameFrame.super.dispose();
                });

            }

        }

    }

    private class ConfirmPrompt extends JFrame {

        JLabel messageLabel = new JLabel();
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        private ConfirmPrompt(String message, ActionListener confirmTask, String title) {

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gameFrame.setEnabled(true);
                }
            });

            setTitle(title);
            setLocation(gameFrameCenter);
            setSize(350, 100);

            ConfirmPromptPanel confirmPanel = new ConfirmPromptPanel(message, confirmTask);

            add(confirmPanel);
            this.pack();

            setVisible(true);
            gameFrame.setEnabled(false);
        }

        private ConfirmPrompt(String message, String confirmText, ActionListener confirmTask, String title) {
            this(message, confirmTask, title);
            confirmButton.setText(confirmText);
        }

        private class ConfirmPromptPanel extends JPanel {

            private ConfirmPromptPanel(String message, ActionListener confirmTask) {
                messageLabel.setText(message);

                add(messageLabel);
                add(confirmButton);
                add(cancelButton);

                confirmButton.addActionListener(confirmTask);
                confirmButton.addActionListener(e -> {
                    gameFrame.setEnabled(true);
                    ConfirmPrompt.super.dispose();
                });
                cancelButton.addActionListener(e -> {
                    gameFrame.setEnabled(true);
                    ConfirmPrompt.super.dispose();
                });

            }
        }
    }

    private class NotificationPrompt extends JFrame {

        private NotificationPrompt(String message) {

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gameFrame.setEnabled(true);
                }
            });

            setLocation(gameFrameCenter);
            setSize(350, 100);

            NotificationPanel notificationPanel = new NotificationPanel(message);

            add(notificationPanel);
            this.pack();

            setVisible(true);
        }

        private NotificationPrompt(String message, String title) {
            this(message);
            setTitle(title);
        }

        class NotificationPanel extends JPanel {

            private NotificationPanel(String message) {
                JLabel messageLabel = new JLabel(message);
                JButton okButton = new JButton("OK");

                add(messageLabel);
                add(okButton);

                okButton.addActionListener(e -> {
                    gameFrame.setEnabled(true);
                    NotificationPrompt.super.dispose();
                });
            }

        }
    }

}




