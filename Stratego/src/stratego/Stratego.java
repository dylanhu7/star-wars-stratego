/**
 * @file Stratego.java
 * @author dylan.hu
 * @assignment Game Project
 * @date 4/6/2018
 * @description Game controller class for Stratego. Implements all graphical
 * elements and controls game logic.
 */
package stratego;

import javax.swing.*;

import objectdraw.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class Stratego extends WindowController {

    //position and dimension constants
    private static final int INITIAL_WINDOW_WIDTH = 1250;
    private static final int WINDOW_WIDTH = 985;
    private static final int WINDOW_HEIGHT = 1050;
    public static final int BOARD_X_OFFSET = 50;
    public static final int BOARD_Y_OFFSET = 80;
    private static final int LOGO_WIDTH = 242;
    private static final int LOGO_HEIGHT = 75;
    private static final int LOGO_X = (WINDOW_WIDTH / 2) - (LOGO_WIDTH / 2);
    private static final int LOGO_Y = 6;
    private static final int READY_BUTTON_WIDTH = 500;
    private static final int READY_BUTTON_HEIGHT = 200;
    private static final int READY_BUTTON_X = WINDOW_WIDTH / 2 - READY_BUTTON_WIDTH / 2;
    private static final int READY_BUTTON_Y = WINDOW_HEIGHT / 2 - 50;

    private BoardTile[][] board;
    private final int ROWS = 10;
    private final int COLUMNS = 10;

    private JTextArea helperText;

    private Rectangle doneButton;
    private Text doneButtonText;
    private Rectangle randomizeButton;
    private Text randomizeButtonText;
    private FilledRect dividerBackground;
    private Rectangle readyButton;
    private Text readyButtonText;
    private Rectangle exitButton;
    private Text exitButtonText;

    private Text PLEASE_SWITCH_TO;
    private Text THE_OTHER_PLAYER;

    //game result messages
    private Text REBELS_WIN;
    private Text EMPIRE_WINS;
    private Text GAME_TIED;

    private int[] ranks = {0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4,
        4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 8, 8, 9, 10, 11, 11, 11, 11, 11, 11};
    private Image[] rebelImages = new Image[12];
    private Image[] empireImages = new Image[12];
    private ArrayList<Unit> rebels = new ArrayList<Unit>();
    private ArrayList<Unit> empire = new ArrayList<Unit>();
    private ArrayList<UnitButton> rebelButtons = new ArrayList<UnitButton>();
    private ArrayList<UnitButton> empireButtons = new ArrayList<UnitButton>();

    //control what stage the game is in
    private boolean rebelPlacementStage;
    private boolean empirePlacementStage;
    private boolean rebelMovementStage;
    private boolean empireMovementStage;

    private boolean dividerShown;

    //stores the game result in an int
    private int gameResult;
    private static final int GAME_RESULT_NO_WIN = 0;
    private static final int GAME_RESULT_BLUE_WIN = 1;
    private static final int GAME_RESULT_RED_WIN = 2;
    private static final int GAME_RESULT_TIE = 3;

    //stores whose turn it is
    private boolean blueTurn;

    //Timers for combat
    private Timer removeTimer;
    private Timer moveTimer;
    private Timer switchTimer;
    //Timer for win message flashing
    private Timer winMessageTimer;

    public Stratego() {
        //initialize board and stage booleans
        board = new BoardTile[ROWS][COLUMNS];
        rebelPlacementStage = true;
        empirePlacementStage = false;
        rebelMovementStage = false;
        empireMovementStage = false;
        blueTurn = true;
        dividerShown = false;
        gameResult = GAME_RESULT_NO_WIN;
        startController();
    }

    public void begin() {
        resize(INITIAL_WINDOW_WIDTH, WINDOW_HEIGHT);

        //stratego logo
        ImageIcon strategoLogoIcon = new ImageIcon
                ("src/images/stratego-logo.png");
        Image strategoLogoImage = strategoLogoIcon.getImage();
        VisibleImage strategoLogo = new VisibleImage(strategoLogoImage,
                LOGO_X, LOGO_Y, LOGO_WIDTH, LOGO_HEIGHT, canvas);

        createBoard();
        createUnitsAndButtons();
        placeButtons(rebelButtons);

        //helper text
        Panel help = new Panel();
        help.setBackground(Color.white);
        help.setLocation(950,75);
        help.setSize(230, 250);
        helperText = new JTextArea("Drag units to the bottom four rows to"
                + "manually place them or press AUTO to randomly place them "
                + "all. Drag placed units to change placement. Press DONE to"
                + " start the other player's placement.");
        helperText.setLocation(0, 0);
        helperText.setSize(230, 250);
        helperText.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        helperText.setEditable(false);
        helperText.setLineWrap(true);
        helperText.setWrapStyleWord(true);
        helperText.setBackground(Color.white);
        help.add(helperText);
        ((Container) canvas).add(help);

        //done button
        doneButton = new Rectangle(960, 880, 200, 75, canvas);
        doneButton.setFillColor(Color.green);
        doneButtonText = new Text("DONE", 985, 885, canvas);
        doneButtonText.setFontSize(52);

        //AUTO button
        randomizeButton = new Rectangle(960, 790, 200, 75, canvas);
        randomizeButton.setFillColor(Color.magenta);
        randomizeButtonText = new Text("AUTO", 990, 795, canvas);
        randomizeButtonText.setFontSize(52);

        //exit button
        exitButton = new Rectangle(55, 20, 50, 30, canvas);
        exitButton.setFillColor(Color.red);
        exitButtonText = new Text("EXIT", 58, 21, canvas);
        exitButtonText.setFontSize(20);

        //game result messages
        REBELS_WIN = new Text("REBELS WIN", 180, 450, canvas);
        REBELS_WIN.setFontSize(100);
        REBELS_WIN.setColor(Unit.unitBlue);
        REBELS_WIN.hide();
        EMPIRE_WINS = new Text("EMPIRE WINS", 150, 450, canvas);
        EMPIRE_WINS.setFontSize(100);
        EMPIRE_WINS.setColor(Unit.unitRed);
        EMPIRE_WINS.hide();
        GAME_TIED = new Text("GAME TIED", 215, 450, canvas);
        GAME_TIED.setFontSize(100);
        GAME_TIED.setColor(Color.black);
        GAME_TIED.hide();

        //divider
        dividerBackground = new FilledRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
                canvas);
        dividerBackground.setColor(Color.white);
        readyButton = new Rectangle(READY_BUTTON_X, READY_BUTTON_Y,
                READY_BUTTON_WIDTH, READY_BUTTON_HEIGHT, canvas);
        readyButton.setFillColor(Color.green);
        readyButtonText = new Text("READY", READY_BUTTON_X + 82,
                READY_BUTTON_Y + 38, canvas);
        readyButtonText.setFontSize(100);
        PLEASE_SWITCH_TO = new Text("PLEASE SWITCH TO", 242, 300, canvas);
        PLEASE_SWITCH_TO.setFontSize(54);
        THE_OTHER_PLAYER = new Text("THE OTHER PLAYER", 237, 375, canvas);
        THE_OTHER_PLAYER.setFontSize(54);
        hideDivider();
    }

    //store the offsets between the cursor location and the Unit X and Y so that
    //dragging does not place the cursor at the top left of the Unit.
    private double dragXOffset;
    private double dragYOffset;
    private int initialRow;
    private int initialColumn;

    @Override
    public void onMousePress(Location point) {
        //prevents things from being selected when the game is over
        if (gameResult == GAME_RESULT_NO_WIN && dividerShown == false) {
            if (rebelPlacementStage) {
                selectUnit(rebels, rebelButtons, point);
            } else if (empirePlacementStage) {
                selectUnit(empire, empireButtons, point);
            } else if (rebelMovementStage) {
                selectUnit(rebels, rebelButtons, point);
            } else if (empireMovementStage) {
                selectUnit(empire, empireButtons, point);
            }
        }
    }

    @Override
    public void onMouseDrag(Location point) {
        if (gameResult == GAME_RESULT_NO_WIN) {
            if (rebelPlacementStage) {
                dragUnit(rebels, point);
            } else if (empirePlacementStage) {
                dragUnit(empire, point);
            } else if (rebelMovementStage) {
                dragUnit(rebels, point);
            } else if (empireMovementStage) {
                dragUnit(empire, point);
            }
        }
    }

    @Override
    public void onMouseRelease(Location point) {
        if (gameResult == GAME_RESULT_NO_WIN) {
            if (rebelPlacementStage) {
                //places the Unit in the tile that the cursor is in
                placeUnit(rebels, rebelButtons, point);
            } else if (empirePlacementStage) {
                placeUnit(empire, empireButtons, point);
            } else if (rebelMovementStage) {
                moveUnit(rebels, point);
            } else if (empireMovementStage) {
                moveUnit(empire, point);
            }
        }
    }

    @Override
    public void onMouseClick(Location point) {
        if (gameResult == GAME_RESULT_NO_WIN) {
            //first done button press switches to empire placement stage
            if (doneButton.contains(point)) {
                if (rebelPlacementStage && allUnitsPlaced(rebels)) {
                    rebelPlacementStage = false;
                    empirePlacementStage = true;
                    helperText.setText("Drag units to the bottom four rows to"
                            + " manually place them or press AUTO to randomly"
                            + " place them all. Drag placed units to change"
                            + " placement. Press DONE to start the game.");
                    for (UnitButton button : rebelButtons) {
                        button.removeFromCanvas();
                    }
                    placeButtons(empireButtons);
                    switchTurn();
                    switchSides(rebels);
                } //second done button press switches to rebel movement stage
                else if (empirePlacementStage && allUnitsPlaced(empire)) {
                    //removes buttons and text
                    doneButton.removeFromCanvas();
                    doneButtonText.removeFromCanvas();
                    randomizeButton.removeFromCanvas();
                    randomizeButtonText.removeFromCanvas();
                    helperText.setVisible(false);
                    for (UnitButton button : empireButtons) {
                        button.removeFromCanvas();
                    }
                    empirePlacementStage = false;
                    switchPlayer();
                    rebelMovementStage = true;
                    empireMovementStage = false;
                    resize(WINDOW_WIDTH, WINDOW_HEIGHT);
                }
            } else if (randomizeButton.contains(point)) {
                if (rebelPlacementStage) {
                    randomPlace(rebels);
                } else if (empirePlacementStage) {
                    randomPlace(empire);
                }
            } else if (readyButton.contains(point)) {
                hideDivider();
            }
        }
        if (exitButton.contains(point)) {
            System.exit(0);
        }
    }

    //switches perspective of player
    public void switchSides(ArrayList<Unit> units) {
        if (gameResult == GAME_RESULT_NO_WIN) {
            for (Unit unit : units) {
                int originalRow = (int) unit.getRow();
                int originalColumn = (int) unit.getColumn();
                int destinationRow = (ROWS - 1) - originalRow;
                int destinationColumn = (COLUMNS - 1) - originalColumn;
                if (board[destinationRow][destinationColumn].isFilled()) {
                    unit.moveToTile(destinationRow, destinationColumn);
                } else {
                    unit.moveToTile(destinationRow, destinationColumn);
                    board[destinationRow][destinationColumn].fill();
                    board[originalRow][originalColumn].empty();
                }
                if (unit.isBlue()) {
                    if (!blueTurn) {
                        unit.conceal();
                    } else {
                        unit.reveal();
                    }
                } else {
                    if (blueTurn) {
                        unit.conceal();
                    } else {
                        unit.reveal();
                    }
                }
            }
        }
    }

    //inverts turn and stage booleans
    private void switchTurn() {
        blueTurn = !blueTurn;
        if (rebelMovementStage || empireMovementStage) {
            rebelMovementStage = !rebelMovementStage;
            empireMovementStage = !empireMovementStage;
        }
    }

    //randomly places Units for quick placement
    private void randomPlace(ArrayList<Unit> units) {
        if (rebelPlacementStage || empirePlacementStage) {
            //makes an Integer ArrayList of ints 0 to 39
            ArrayList<Integer> list = new ArrayList<Integer>(40);
            for (int i = 0; i < units.size(); i++) {
                list.add(i);
            }

            Random generator = new Random();
            //places Units randomly in bottom 4 rows
            for (int r = 6; r < 10; r++) {
                for (int c = 0; c < 10; c++) {
                    if (list.size() > 0) {
                        int index = generator.nextInt(list.size());
                        units.get(list.get(index)).moveToTile(r, c);
                        units.get(list.get(index)).markAsPlaced();
                        board[r][c].fill();
                        list.remove(index);
                    }
                }
            }
        }
    }

    //selects a Unit so that it can be dragged, placed, moved, etc.
    private void selectUnit(ArrayList<Unit> units, ArrayList<UnitButton> 
            buttons, Location point) {
        //selects a Unit depending on the UnitButton pressed
        for (int i = 0; i < buttons.size(); i++) {
            for (Unit unit : units) {
                if (buttons.get(i).contains(point)) {
                    if (unit.getRank() == i && buttons.get(i).isSelectable()
                            && !unit.isPlaced()) {
                        unit.select();
                        dragXOffset = buttons.get(i).getX() - point.getX();
                        dragYOffset = buttons.get(i).getY() - point.getY();
                        return;
                    }
                } //selects a Unit already placed on the board
                else if (unit.contains(point)) {
                    //if it's a movement stage
                    if (rebelMovementStage || empireMovementStage) {
                        //restricts flag and bomb from being selected
                        if (!(unit.getRank() == 0 || unit.getRank() == 11)) {
                            selectUnit(unit, point);
                            return;
                        }
                    } //if it's a placement stage
                    else if (rebelPlacementStage || empirePlacementStage) {
                        selectUnit(unit, point);
                        return;
                    }
                }
            }
        }
    }

    //overloaded helper method that selects a Unit and logs necessary
    //information
    private void selectUnit(Unit unit, Location point) {
        unit.select();
        initialRow = (int) unit.getRow();
        initialColumn = (int) unit.getColumn();
        dragXOffset = unit.getX() - point.getX();
        dragYOffset = unit.getY() - point.getY();
    }

    private void dragUnit(ArrayList<Unit> units, Location point) {
        //moves the selected Unit with the cursor
        for (Unit unit : units) {
            if (unit.isSelected()) {
                unit.sendToFront();
                unit.moveTo(point.getX() + dragXOffset,
                        point.getY() + dragYOffset);
            }
        }
    }

    //places Units when in placement stage
    private void placeUnit(ArrayList<Unit> units, ArrayList<UnitButton> buttons,
            Location point) {
        Unit unit = findSelectedUnit(units);
        if (unit != null) {
            for (int r = ROWS - 1; r > ROWS / 2; r--) {
                for (int c = 0; c < board[0].length; c++) {
                    if (board[r][c].contains(point)) {
                        //if the Unit is already placed
                        if (unit.isPlaced()) {
                            //if the destination tile is already filled, switch
                            if (board[r][c].isFilled()) {
                                for (Unit otherUnit : units) {
                                    if (r == otherUnit.getRow()
                                            && c == otherUnit.getColumn()) {
                                        unit.moveToTile(r, c);
                                        otherUnit.moveToTile(initialRow,
                                                initialColumn);
                                        unit.deselect();
                                        return;
                                    }
                                }
                            }
                            //normal placement
                            else {
                                unit.moveToTile(r, c);
                                board[r][c].fill();
                                board[initialRow][initialColumn].empty();
                                unit.deselect();
                                return;
                            }
                        }
                        //if the Unit is not already placed (comes from a
                        //UnitButton)
                        else {
                            if (!board[r][c].isFilled()) {
                                unit.moveToTile(r, c);
                                unit.markAsPlaced();
                                board[r][c].fill();
                                buttons.get(unit.getRank()).decrementQuantity();
                                unit.deselect();
                                return;
                            }
                        }
                    }
                    //if mouse not released on bottom four rows
                    else {
                        if (unit.isPlaced()) {
                            returnUnit(unit);
                        }
                        else {
                            unit.moveTo(-100, -100);
                            unit.deselect();
                        }
                    }
                }
            }
        }
    }

    //moves a Unit to a BoardTile and manages combat
    private void moveUnit(ArrayList<Unit> units, Location point) {
        Point destination = getDestinationPoint(units, point);
        Unit unit = findSelectedUnit(units);
        if (destination != null && unit != null) {
            int destinationRow = (int) destination.getX();
            int destinationColumn = (int) destination.getY();
            if (isValidMove(unit, destination)) {
                //if trying to move into empty tile, allow it
                if (!board[destinationRow][destinationColumn].isFilled()) {
                    unit.moveToTile(destinationRow, destinationColumn);
                    board[destinationRow][destinationColumn].fill();
                    board[initialRow][initialColumn].empty();
                    unit.deselect();
                    switchPlayer();
                } //if there is another unit occupying destination
                else {
                    returnUnit(unit);
                    Unit opponent = getOpponent(destination);
                    //if there is an opponent
                    if (opponent != null) {
                        Unit winner = fight(unit, opponent);
                        //if there is a winner
                        if (winner != null) {
                            Unit loser = unit;
                            if (winner == unit) {
                                loser = opponent;
                            }
                            opponent.reveal();
                            //needed to make a final variable to use in
                            //removeTimer
                            final Unit loser1 = loser;
                            //waits for one second before removing loser
                            removeTimer = new Timer(1000, new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                    removeTimer.stop();
                                    removeUnit(loser1);
                                    moveTimer.start();
                                }
                            });
                            //waits for one second before moving winner to
                            //destination
                            moveTimer = new Timer(1000, new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                    moveTimer.stop();
                                    //if winner was attacker, moves to
                                    //destination if winner was defender, moves
                                    //to own tile (essentially does nothing)
                                    winner.moveToTile(destinationRow,
                                            destinationColumn);
                                    board[destinationRow]
                                            [destinationColumn].fill();
                                    board[initialRow][initialColumn].empty();
                                    switchTimer.start();
                                }
                            });
                            //waits half a second before switching player
                            switchTimer = new Timer(500, new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                    switchTimer.stop();
                                    switchPlayer();
                                }
                            });
                            removeTimer.start();
                        } //if there is a tie
                        else {
                            opponent.reveal();
                            //waits one second before removing both
                            removeTimer = new Timer(1000, new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                    removeTimer.stop();
                                    //remove both Units
                                    removeUnit(unit);
                                    removeUnit(opponent);
                                    switchTimer.start();
                                }
                            });
                            //waits for half a second before switching player
                            switchTimer = new Timer(500, new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                    switchTimer.stop();
                                    unit.deselect();
                                    switchPlayer();
                                }
                            });
                            removeTimer.start();
                        }
                    }
                }
            } //if the unit is not capable of making that move
            else {
                returnUnit(unit);
            }
        }
    }

    //inverts turn and stage booleans and changes perspective and shows divider
    private void switchPlayer() {
        if (gameResult == GAME_RESULT_NO_WIN) {
            switchTurn();
            switchSides(rebels);
            switchSides(empire);
        }
        showDivider();
    }

    //returns a Unit to its original place
    //used when a move is not valid
    private void returnUnit(Unit unit) {
        unit.moveToTile(initialRow, initialColumn);
        unit.deselect();
    }

    //checks if a move is valid for a Unit
    //if an enemy occupies the destination tile, it is considered not valid
    private boolean isValidMove(Unit unit, Point destination) {
        boolean isValid = false;
        int destinationRow = (int) destination.getX();
        int destinationColumn = (int) destination.getY();
        int rowDiff = (int) Math.abs(destination.getX() - initialRow);
        int columnDiff = (int) Math.abs(destination.getY() - initialColumn);
        //if destination is a BoardTile
        if (destinationRow >= 0 && destinationRow < 10 && destinationColumn >= 0
                && destinationColumn < 10) {
            if (!board[destinationRow][destinationColumn].isLake()) {
                //if scout, it can move vertically or horizontally to any tile
                //when there are no units or lake tiles in between
                if (unit.getRank() == 2) {
                    if (((rowDiff > 0) && (columnDiff == 0)) ||
                            (rowDiff == 0 && (columnDiff > 0))) {
                        if (rowDiff > 0) {
                            int start = initialRow;
                            int finish = destinationRow;
                            if (finish < start) {
                                start = destinationRow;
                                finish = initialRow;
                            }
                            //if there are units or lake tiles between initial
                            //tile and destination tile, return false
                            for (int r = start + 1; r < finish; r++) {
                                if (board[r][initialColumn].isFilled() ||
                                        board[r][initialColumn].isLake()) {
                                    return false;
                                }
                            }
                        }
                        if (columnDiff > 0) {
                            int start = initialColumn;
                            int finish = destinationColumn;
                            if (finish < start) {
                                start = destinationColumn;
                                finish = initialColumn;
                            }
                            //if there are units or lake tiles between initial
                            //tile and destination tile, return false
                            for (int c = start + 1; c < finish; c++) {
                                if (board[initialRow][c].isFilled() ||
                                        board[initialRow][c].isLake()) {
                                    return false;
                                }
                            }
                        }
                        isValid = true;
                    }
                } //if other rank
                else {
                    if ((rowDiff == 1 && columnDiff == 0)
                            || (rowDiff == 0 && columnDiff == 1)) {
                        isValid = true;
                    }
                }
            }
        }
        return isValid;
    }

    //checks if a team has no Units left or if the flag was captured
    private void checkForWin() {
        //if rebel flag is gone
        if (rebels.get(0).getRank() != 0) {
            gameResult = GAME_RESULT_RED_WIN;
            return;
        } //if empire flag is gone
        else if (empire.get(0).getRank() != 0) {
            gameResult = GAME_RESULT_BLUE_WIN;
            return;
        }

        boolean rebelsEliminated = true;
        for (Unit rebel : rebels) {
            //if any rebel is not a flag or a bomb
            if (rebel.getRank() != 0 && rebel.getRank() != 11) {
                rebelsEliminated = false;
                break;
            }
        }
        boolean empireEliminated = true;
        for (Unit imperial : empire) {
            //if any imperial is not a flag or a bomb
            if (imperial.getRank() != 0 && imperial.getRank() != 11) {
                empireEliminated = false;
                break;
            }
        }

        if (rebelsEliminated && empireEliminated) {
            gameResult = GAME_RESULT_TIE;
        }
        else if (rebelsEliminated) {
            gameResult = GAME_RESULT_RED_WIN;
        }
        else if (empireEliminated) {
            gameResult = GAME_RESULT_BLUE_WIN;
        }
    }

    //helps in finding which BoardTile was pressed or released on
    private Point getDestinationPoint(ArrayList<Unit> units, Location point) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (board[r][c].contains(point)) {
                    return new Point(r, c);
                }
            }
        }
        return null;
    }

    //helps in finding which Unit is currently selected
    private Unit findSelectedUnit(ArrayList<Unit> units) {
        for (Unit unit : units) {
            if (unit.isSelected()) {
                return unit;
            }
        }
        return null;
    }

    //removes a Unit from its team ArrayList and from the canvas
    private void removeUnit(Unit unit) {
        board[(int) unit.getRow()][(int) unit.getColumn()].empty();
        unit.removeFromCanvas();
        if (rebels.contains(unit)) {
            rebels.remove(unit);
        }
        else if (empire.contains(unit)) {
            empire.remove(unit);
        }
        //will check for win here since win can only happen when a unit is lost
        checkForWin();
    }

    //finds the Unit at a BoardSlot given a Point
    private Unit getOpponent(Point destination) {
        Unit opponent = null;
        int destinationRow = (int) destination.getX();
        int destinationColumn = (int) destination.getY();
        ArrayList<Unit> units = rebels;
        if (blueTurn) {
            units = empire;
        }
        for (Unit unit : units) {
            //finds a Unit with same row and column as the destination tile
            if (unit.getRow() == destinationRow
                    && unit.getColumn() == destinationColumn) {
                opponent = unit;
                break;
            }
        }
        return opponent;
    }

    //takes two Units and determines which one wins
    private Unit fight(Unit attacking, Unit defending) {
        Unit winner = null;
        //spy vs. marshal
        if (attacking.getRank() == 1 && defending.getRank() == 10) {
            winner = attacking;
        } //miner vs. bomb
        else if (attacking.getRank() == 3 && defending.getRank() == 11) {
            winner = attacking;
        } //if a tie, there is no winner
        else if (attacking.getRank() == defending.getRank()) {
            winner = null;
        } //normal combat
        else {
            if (attacking.getRank() > defending.getRank()) {
                winner = attacking;
            }
            else {
                winner = defending;
            }
        }
        return winner;
    }

    //checks if all BoardTiles are filled for placement stage
    private boolean allUnitsPlaced(ArrayList<Unit> units) {
        for (Unit unit : units) {
            if (!unit.isPlaced()) {
                return false;
            }
        }
        return true;
    }

    //helper method that creates the board
    private void createBoard() {
        ImageIcon grassIcon = new ImageIcon("src/images/grass.jpg");
        Image grassImage = grassIcon.getImage();
        ImageIcon waterIcon = new ImageIcon("src/images/water.jpg");
        Image waterImage = waterIcon.getImage();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                //creates grass tiles
                if (r <= 3 || r >= 6 || c == 0 || c == 1 || c == 4
                        || c == 5 || c == 8 || c == 9) {
                    board[r][c] = new BoardTile(grassImage, false,
                            BoardTile.TILE_DIM * c + BOARD_X_OFFSET,
                            BoardTile.TILE_DIM * r + BOARD_Y_OFFSET, canvas);
                } //creates lake tiles
                else {
                    board[r][c] = new BoardTile(waterImage, true,
                            BoardTile.TILE_DIM * c + BOARD_X_OFFSET,
                            BoardTile.TILE_DIM * r + BOARD_Y_OFFSET, canvas);
                }
            }
        }
    }

    //helper method that creates the Units and UnitButtons for both teams
    private void createUnitsAndButtons() {
        //creates empire Images and UnitButtons
        for (int i = 0; i < empireImages.length; i++) {
            ImageIcon empireIcon = new ImageIcon("src/images/stratego_empire_"
                    + i + ".png");
            empireImages[i] = empireIcon.getImage();
            empireButtons.add(new UnitButton(empireImages[i], false, i,
                    canvas));
        }
        //creates empire Units
        for (int i = 0; i < ranks.length; i++) {
            empire.add(new Unit(empireImages[ranks[i]], false, ranks[i],
                    canvas));
        }
        //creates rebel Images and UnitButtons
        for (int i = 0; i < rebelImages.length; i++) {
            ImageIcon rebelIcon = new ImageIcon("src/images/stratego_rebels_"
                    + i + ".png");
            rebelImages[i] = rebelIcon.getImage();
            rebelButtons.add(new UnitButton(rebelImages[i], true, i, canvas));
        }
        //creates rebel Units
        for (int i = 0; i < ranks.length; i++) {
            //adds new Units to the rebels ArrayList using the ranks array to
            //determine which image in rebelImages is used
            rebels.add(new Unit(rebelImages[ranks[i]], true, ranks[i], canvas));
        }
    }

    //places UnitButtons in space to the right for placement stage
    private void placeButtons(ArrayList<UnitButton> buttons) {
        //moves UnitButtons into space on the right
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 3; c++) {
                buttons.get(r * 3 + c).moveToTile(3 + (r * 1.2), c + 10);
            }
        }
    }

    private void showDivider() {
        if (gameResult == GAME_RESULT_NO_WIN) {
            dividerBackground.sendToFront();
            readyButton.sendToFront();
            readyButtonText.sendToFront();
            PLEASE_SWITCH_TO.sendToFront();
            THE_OTHER_PLAYER.sendToFront();
            dividerBackground.show();
            readyButton.show();
            readyButtonText.show();
            PLEASE_SWITCH_TO.show();
            THE_OTHER_PLAYER.show();

            dividerShown = true;
        } //if there is a win or tie
        else {
            winMessageTimer = new Timer(200, new ActionListener() {
                int flashCount = 0;

                public void actionPerformed(ActionEvent evt) {
                    //repeatedly hides and shows appropriate game result message
                    if (gameResult == GAME_RESULT_BLUE_WIN) {
                        REBELS_WIN.sendToFront();
                        if (REBELS_WIN.isHidden()) {
                            REBELS_WIN.show();
                        }
                        else {
                            REBELS_WIN.hide();
                        }
                    }
                    else if (gameResult == GAME_RESULT_RED_WIN) {
                        EMPIRE_WINS.sendToFront();
                        if (EMPIRE_WINS.isHidden()) {
                            EMPIRE_WINS.show();
                        } else {
                            EMPIRE_WINS.hide();
                        }
                    }
                    else {
                        GAME_TIED.sendToFront();
                        if (GAME_TIED.isHidden()) {
                            GAME_TIED.show();
                        } else {
                            GAME_TIED.hide();
                        }
                    }
                    flashCount++;
                    //flashes 10 times at 400ms between each flash
                    if (flashCount == 20) {
                        winMessageTimer.stop();
                    }
                }
            });
            winMessageTimer.start();
        }
    }

    private void hideDivider() {
        dividerBackground.hide();
        readyButton.hide();
        readyButtonText.hide();
        PLEASE_SWITCH_TO.hide();
        THE_OTHER_PLAYER.hide();
        dividerShown = false;
    }

    public static void main(String[] args) {
        new Stratego();
    }

}
