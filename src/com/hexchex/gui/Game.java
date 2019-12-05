package com.hexchex.gui;

import com.hexchex.HexChex;
import com.hexchex.engine.board.Board;
import com.hexchex.engine.board.Cell;
import com.hexchex.engine.pieces.Piece;
import com.hexchex.engine.pieces.Team;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

import static javax.swing.SwingUtilities.*;

public class Game implements Serializable, Cloneable {

    private static final long serialVersionUID = 12345L;

    public static Game getInstance() {
        return instance;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private static Game instance;
    private static final File saveDirectory = new File("src/saves");
    private  JFrame gameFrame;
    private Point gameFrameCenter;
    private HexPanel hexPanel;
    private GameMessagePanel messagePanel;
    private JLabel currentMessageDisplay;

    private boolean gameEnded = false;
    private Team winningTeam;
    private String currentMessage = "";
    private Hexagon sourceHex, destinationHex;
    private Piece pieceToMove;

    public HexChex getHexChex() {
        return hexChex;
    }

    private HexChex hexChex;
    private Board board;
    private Team team1, team2;
    private String gameName;

    private Dimension gameFrameDimension;
    private Dimension calculateGameFrameDimension() {
        return new Dimension(board.getWidth() * 100,
                board.getHeight() * 100 + 125);
    }

    private void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Game(HexChex hexChex) {

        instance = this;

        this.hexChex = hexChex;

        board = hexChex.getBoard();
        team1 = hexChex.getTeam1();
        team2 = hexChex.getTeam2();
        gameName = hexChex.getGameName();

        gameFrame = new JFrame("HexChex");

        if (gameName != null) {
            gameFrame.setTitle("HexChex - " + gameName);
        }

        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrameDimension = calculateGameFrameDimension();
        gameFrame.setSize(gameFrameDimension);
        BorderLayout gameFrameLayout = new BorderLayout();
        gameFrame.setLayout(gameFrameLayout);
        gameFrameCenter = new Point(gameFrame.getSize().width / 3, gameFrame.getSize().height / 3);
        gameFrame.setLayout(new BorderLayout());
        gameFrame.setResizable(false);

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

        gameFrame.pack();

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


    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        newGame.addActionListener(e -> new NewGameFrame());

        saveGame.addActionListener(e -> new SaveGameFrame());

        loadGame.addActionListener(e -> new LoadGameFrame());
        if (saveDirectory.listFiles() == null) {
            loadGame.setEnabled(false);
        }

        final JMenuItem deleteSaves = new JMenuItem("Delete saved games");
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

        private HexPanel() {
            createHexes();
        }

        ArrayList<Hexagon> hexList = new ArrayList<>();

        private void createHexes() {

            int hexRadius = 50;
            hexList.clear();

            for(int row = 0; row < board.getBoard().length; row++) {
                for(int col = 0; col < board.getBoard()[row].length; col++) {

                    if (col % 2 == 0) {
                        if (row % 2 == 0) {

                            Point hexCenter = new Point(col * 80 + (2 * hexRadius), row * 46 + (2 * hexRadius));
                            Hexagon hex = new Hexagon(hexCenter, hexRadius, board.getBoard()[row][col]);
                            hex.setDefaultColor(Color.DARK_GRAY);
                            hex.setToDefaultColor();

                            hexList.add(hex);

                        }

                    } else if (row % 2 != 0) {

                        Point hexCenter = new Point(col * 80 + (2 * hexRadius), row * 46 + (2 * hexRadius));
                        Hexagon hex = new Hexagon(hexCenter, hexRadius, board.getBoard()[row][col]);
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
           paintComponent((Graphics2D)g);
        }

        private void paintComponent(Graphics2D g) {
            super.paintComponent(g);

            int pieceRadius = 60;
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
                    Color tmpC = g.getColor();
                    Stroke tmpS = g.getStroke();


                    Color color = hex.getCell().getPiece().getTeam().getColor();
                    g.setColor(color);
                    g.fillOval(hex.getCenter().x - (pieceRadius / 2), hex.getCenter().y - (pieceRadius / 2), pieceRadius, pieceRadius);

                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.BLACK);
                    g.drawOval(hex.getCenter().x - (pieceRadius / 2), hex.getCenter().y - (pieceRadius / 2), pieceRadius, pieceRadius);

                    g.setColor(tmpC);
                    g.setStroke(tmpS);
                }

            }
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

                                    if (hex.getCell().getPiece().getTeam() != hexChex.getCurrentMove()) {
                                        System.out.println(hexChex.getCurrentMove().getName() + "'s move.");
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
                                        winningTeam = hexChex.getCurrentMove();
                                        System.out.println(winningTeam.getName() + " wins!\n");
                                        gameEnded = true;
                                        new EndScreenFrame();
                                    }
                                    //messagePanel.pieceMoved(sourceHex.getCell(), destinationHex.getCell());

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

                        break;

                    }
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

            hexPanel.setCursor(new Cursor(12));

        }
    }

    private class GameMessagePanel extends JPanel {

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

    private class EndScreenFrame extends JFrame {

        private EndScreenFrame() {
            setSize(250,100);
            setLocation(gameFrameCenter);
            add(new EndScreenPanel());
            setVisible(true);
        }

        private class EndScreenPanel extends JPanel {

            private EndScreenPanel() {

                JLabel winnerLabel = new JLabel(winningTeam.getName() + " wins!");
                JButton rematchButton = new JButton("Rematch");
                JButton newGameButton = new JButton("New game");

                add(winnerLabel);
                add(rematchButton);
                add(newGameButton);

                rematchButton.addActionListener(e -> {
                    gameFrame.dispose();
                    new Game(new HexChex(new Board(board.getWidth(), board.getHeight()), team1, team2));
                });

                newGameButton.addActionListener(e -> new ResizeBoardFrame());
            }
        }

    }

    private class ResizeBoardFrame extends JFrame {

        private ResizeBoardFrame() {
            setTitle("Resize board");
            setLayout(new GridBagLayout());
            setLocation(gameFrameCenter);
            add(new ResizeInputPanel());
            pack();
            setVisible(true);
        }

        class ResizeInputPanel extends JPanel {

            static final int DIM_MIN = 5;
            static final int DIM_MAX = 16;
            final int WIDTH_INIT = board.getWidth();
            final int HEIGHT_INIT = board.getHeight();
            final File tempSaveDirectory = new File("src/cache");

            JSlider widthSlider = new JSlider(JSlider.HORIZONTAL, DIM_MIN, DIM_MAX, WIDTH_INIT);
            JSlider heightSlider = new JSlider(JSlider.VERTICAL, DIM_MIN, DIM_MAX, HEIGHT_INIT);
            JButton confirmButton = new JButton("Confirm");
            JButton cancelButton = new JButton("Cancel");

            private void createTempSave() {
                try {
                    Game save = (Game) instance.clone();

                    FileOutputStream fileOut = new FileOutputStream(tempSaveDirectory.getPath() + "\\temp");
                    ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

                    objOut.writeObject(save);
                    objOut.close();

                    System.out.println("Current game has been cached!");

                } catch(IOException | CloneNotSupportedException ex) {
                    ex.printStackTrace();
                }
            }

            private ResizeInputPanel() {

                add(widthSlider);
                add(heightSlider);
                add(confirmButton);
                add(cancelButton);

                createTempSave();

                widthSlider.addChangeListener(e -> {
                    board.setWidth(widthSlider.getValue());
                    board.setupDefaultBoard(team1, team2);
                    hexPanel.createHexes();
                    hexPanel.repaint();
                    hexChex.setCurrentMove(team1);

                    gameFrameDimension = calculateGameFrameDimension();
                    gameFrame.setSize(calculateGameFrameDimension());
                });

                heightSlider.addChangeListener(e -> {
                    board.setHeight(heightSlider.getValue());
                    board.setupDefaultBoard(team1, team2);
                    hexPanel.createHexes();
                    hexPanel.repaint();
                    hexChex.setCurrentMove(team1);

                    gameFrameDimension = calculateGameFrameDimension();
                    gameFrame.setSize(calculateGameFrameDimension());
                });

                confirmButton.addActionListener(e -> dispose());

                cancelButton.addActionListener(e -> {
                    try {
                        FileInputStream fileIn = new FileInputStream(tempSaveDirectory.getPath() + "\\temp");
                        ObjectInputStream objIn = new ObjectInputStream(fileIn);
                        Game loaded = (Game) objIn.readObject();

                        gameFrame.dispose();

                        new Game(loaded.getHexChex());

                        File tempSave = new File(tempSaveDirectory.getPath() + "\\temp");
                        tempSave.delete();

                        dispose();
                        System.out.println("Cached game restored!");

                    } catch(IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                });

            }
        }
    }

    private class NewGameFrame extends JFrame {

        private NewGameFrame() {
            setTitle("New game");
            setLayout(new GridBagLayout());
            setLocation(gameFrameCenter);
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

                setBoardSizeButton.addActionListener(e -> {
                    new ResizeBoardFrame();
                });

                editTeamButton.addActionListener(e -> new EditTeamFrame());

                startGameButton.addActionListener(e -> dispose());

            }

        }

    }

    private class EditTeamFrame extends JFrame {

        private EditTeamFrame() {
            setTitle("Edit teams");
            setLayout(new GridBagLayout());
            setLocation(gameFrameCenter);
            setSize(1750, 500);

            EditTeamPanel team1Panel = new EditTeamPanel(team1);

            EditTeamPanel team2Panel = new EditTeamPanel(team2);

            JButton confirmButton = new JButton("Confirm");

            add(team1Panel);
            add(team2Panel);
            add(confirmButton);

            confirmButton.addActionListener(e -> {
                team1.setName(team1Panel.teamNameField.getText());
                team2.setName(team2Panel.teamNameField.getText());
                dispose();
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

    private class SaveGameFrame extends JFrame {

        private SaveGameFrame() {
            setTitle("Save game");
            setLayout(new GridBagLayout());
            setLocation(gameFrameCenter);
            setSize(350, 100);

            add(new SaveGamePanel());

            setVisible(true);
        }


        private class SaveGamePanel extends JPanel {

            private void createSave(String name) {
                try {
                    Game save = (Game) instance.clone();

                    FileOutputStream fileOut = new FileOutputStream(saveDirectory.getPath() + "\\" + name);

                    ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
                    objOut.writeObject(save);
                    objOut.close();

                    loadGame.setEnabled(true);

                    System.out.println("Game saved");

                } catch(IOException | CloneNotSupportedException ex) {
                    ex.printStackTrace();
                }
            }

            private SaveGamePanel() {

                JTextField saveNameField = new JTextField();
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
                        for (File file : existingSaves) {
                            if (file.getName().equalsIgnoreCase(saveNameField.getText())) {

                                System.out.println("A save with that name already exists!");

                                ActionListener overwrite = e1 -> {
                                    file.delete();
                                    createSave(saveNameField.getText());

                                    System.out.println("Game saved!");
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

                });

                cancelButton.addActionListener(e -> SaveGameFrame.super.dispose());

            }

        }

    }

    private class LoadGameFrame extends JFrame {

        private LoadGameFrame() {
            setTitle("Load game");
            setLayout(new GridBagLayout());
            setLocation(gameFrameCenter);
            setSize(350, 250);

            add(new LoadGamePanel());

            setVisible(true);
        }

        private class LoadGamePanel extends JPanel {

            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Saves");
            DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
            String newGameName;

            FileInputStream gameToLoad;
            JButton loadButton = new JButton("Load game");
            JButton cancelButton = new JButton("Cancel");

            private LoadGamePanel() {


                for (File file : Objects.requireNonNull(saveDirectory.listFiles())) {
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
                        gameToLoad =  new FileInputStream(nodeFile.getPath());
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

                cancelButton.addActionListener(e -> LoadGameFrame.super.dispose());

            }


        }

    }

    private class ConfirmPrompt extends JFrame {

        JLabel messageLabel = new JLabel();
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        private ConfirmPrompt(String message, ActionListener confirmTask, String title) {
            setTitle(title);
            setLocation(gameFrameCenter);
            setSize(350, 100);

            ComfirmPromptPanel confirmPanel = new ComfirmPromptPanel(message, confirmTask);

            add(confirmPanel);
            this.pack();

            setVisible(true);
        }

        private ConfirmPrompt(String message, String confirmText, ActionListener confirmTask, String title) {
            this(message, confirmTask, title);
            confirmButton.setText(confirmText);
        }

        private class ComfirmPromptPanel extends JPanel {

            private ComfirmPromptPanel(String message, ActionListener confirmTask) {
                messageLabel.setText(message);

                add(messageLabel);
                add(confirmButton);
                add(cancelButton);

                confirmButton.addActionListener(confirmTask);
                confirmButton.addActionListener(e -> ConfirmPrompt.super.dispose());
                cancelButton.addActionListener(e -> ConfirmPrompt.super.dispose());
            }

        }
    }

    class NotificationPrompt extends JFrame {

        private NotificationPrompt(String message) {
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

                okButton.addActionListener(e -> NotificationPrompt.super.dispose());
            }

        }
    }

}


