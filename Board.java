package BoardComponents;

import java.util.List;
import java.util.HashMap;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import GUI.GameGUI;
import Information.Tag;
import Information.Tag.Side;
import Pieces.Piece;

public abstract class Board extends JPanel implements MouseListener {
    private static final Dimension FRA_DIMENSION = new Dimension((Tag.IMAGE_WIDTH + 10) * Tag.SIZE_MAX, (Tag.IMAGE_HEIGHT + 10) * Tag.SIZE_MAX);

    protected boolean saved;
    protected int colorSet;
    protected Side turn;
    protected GameGUI gameGUI;
    protected Position[][] gameBoard;
    protected Piece selectedPiece;
    protected HashMap<String, Integer> letters;
    protected HashMap<String, Integer> numbers;
    public List<Position> selectedMovablePositions;
    
    /***
     * this is the basic constructor, creates a brand new board and initializes the board display
     * @param gui - GameGUI that created this board, stored so that board can output text on GameGUI
     * @param colorSet - color of the board, colorSet is index of 2D array of Colors that board will use, stored in Tag.Java
     */
    public Board(GameGUI gui, int colorSet) {
        this.setGameGUI(gui);
        this.setGameBoard(new Position[Tag.SIZE_MAX][Tag.SIZE_MAX]);
        this.setLayout(new GridLayout(Tag.SIZE_MAX, Tag.SIZE_MAX, 0, 0));
        this.colorSet = colorSet;
        this.addMouseListener(this);
        this.createNewBoardPositions();
        this.initializePiecesToBoard();
        this.setPanelDimensions(FRA_DIMENSION);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.saved = true;
        this.initializeWordMaps();
    }

    /***
     * this constructor initializes a board from the copy of another board as a string
     * does not do anything with a gui because it is used to test check in the background
     * @param pieces - stores both player names, board color, current turn, all piece names and current locations, and en passant piece
     */
    public Board(String[] pieces)
    {
        this.colorSet = Integer.valueOf(pieces[2]); //0 and 1 are player/side names
        if (pieces[3].equals("white"))
            this.setTurn(Side.WHITE);
        else if (pieces[3].equals("red"))
            this.setTurn(Side.RED);
        else //black
            this.setTurn(Side.BLACK);
        this.setGameBoard(new Position[Tag.SIZE_MAX][Tag.SIZE_MAX]);
        this.setLayout(new GridLayout(Tag.SIZE_MAX, Tag.SIZE_MAX, 0, 0));
        this.createNewBoardPositions();
        this.initializePiecesToBoard(pieces);
    }

    /***
     * this constructor initializes a board from a previous save and creates the display, calls Board(pieces) to handle board creation and then does the additional UI stuff after
     * @param gui - GameGUI that created this board, stored so that board can output text on GameGUI
     * @param pieces - stores both player names, board color, current turn, all piece names and current locations, and en passant piece
     */
    public Board(GameGUI gui, String[] pieces)
    {
        this(pieces); //this constructor intializes board but does not create the display
        this.setGameGUI(gui);
        this.addMouseListener(this);
        this.setPanelDimensions(FRA_DIMENSION);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.saved = true;
        this.initializeWordMaps();
    }

    /***
     * fills gameboard and this panel with all 64 positions
     */
    protected void createNewBoardPositions() {
        for(int i = 0; i < Tag.SIZE_MAX; i++) {
            for(int j = 0; j < Tag.SIZE_MAX; j++){
                if(((i % 2) == 0 && (j % 2) == 0) || ((i % 2) == 1 && (j % 2) == 1)) {
                    this.gameBoard[i][j] = new Position(j, i, true, 20, colorSet);
                    this.add(gameBoard[i][j]);
                } else {
                    this.gameBoard[i][j] = new Position(j, i, false, 20, colorSet);
                    this.add(gameBoard[i][j]);
                }
            }
        }
    }

    /***
     * creates all pieces and assigns them to corresponding positions on the board
     */
    protected abstract void initializePiecesToBoard();

    /***
     * initializes all pieces from copied board onto new board in their respective positions
     * @param pieces - stores both player names, board color, current turn, all piece names and current locations
     */
    protected abstract void initializePiecesToBoard(String[] pieces);
    
    /***
     * initializes and fills maps for letters and numbers, used by speechCalled method
     */
    protected void initializeWordMaps() {
        letters = new HashMap<String, Integer>();
        letters.put("alpha", 0);
        letters.put("bravo", 1);
        letters.put("charlie", 2);
        letters.put("delta", 3);
        letters.put("echo", 4);
        letters.put("foxtrot", 5);
        letters.put("golf", 6);
        letters.put("hotel", 7);
        //top row on UI is marked as 8 but that is 0th row in 2D array gameBoard, bottom row is 1 on UI and 7 in gameBoard, map accordingly
        numbers = new HashMap<String, Integer>();
        numbers.put("one", 7);
        numbers.put("two", 6);
        numbers.put("three", 5);
        numbers.put("four", 4);
        numbers.put("five", 3);
        numbers.put("six", 2);
        numbers.put("seven", 1);
        numbers.put("eight", 0);
    }

    /***
     * calls all relevant JPanel methods to set the size of the board
     * @param size - dimension that panel should be set to
     */
    private void setPanelDimensions(Dimension size) {
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.setSize(size);
    }

    // setter
    public void setGameBoard(Position[][] board) { this.gameBoard = board; }
    public void setGameGUI(GameGUI gui) { this.gameGUI = gui; }
    public void setTurn(Side side) { this.turn = side; }
    public void setSaved() { this.saved = true;}
    public void setSelectedPiece(Piece selected) { this.selectedPiece = selected; }
    public void setSelectedMovablePositions(Piece piece) { this.selectedMovablePositions = piece.getLegalMoves(this.gameBoard); }

    /***
     * swaps current turn, updates gameGUI
     */
    protected abstract void nextTurn();

    // getter
    public Side getTurn() { return this.turn; }
    public boolean getSaved() { return this.saved; }
    public GameGUI getGameGUI() { return this.gameGUI; }
    public Position[][] getGameBoard() { return this.gameBoard; }
    public Piece getSelectedPiece() { return this.selectedPiece; }
    public List<Position> getMovablePositions() { return this.selectedMovablePositions; }

    /***
     * checks if board can currently be saved
     * @return - returns true if board can be saved, false if it can not
     */
    public abstract boolean canSave();

    /***
     * stores relevant board information as a string, called by save button
     * @return - board color, current turn, all piece names and current locations
     */
    public abstract String asString();


    public abstract void updateBoardGUI();

    /***
     * highlights all moves the selected piece can potentially make, highlighted positions are moves the piece can potentially make, ignoring whether player is moving themself into check
     * @param positions - all positions the selected piece can potentially make, from piece.getLegalMoves()
     */
    protected void highlightLegalPositions(List<Position> positions) {
        for(int i = 0; i < positions.size(); i++)
            positions.get(i).setHighLight(true);
        repaint();
    }

    /***
     * unhighlights potential moves, called when player unselects piece or makes move
     * @param positions - all positions the selected piece can potentially make, from piece.getLegalMoves()
     */
    protected void dehighlightlegalPositions(List<Position> positions) {
        for(int i = 0; i < positions.size(); i++)
            positions.get(i).setHighLight(false);
        repaint();
    }

    

    /***
     * this method sets the selected piece, responsible for highlighting selected piece's position and legal moves
     * @param piece - piece that was selected
     */
    protected abstract void selectPiece(Piece piece);

    /***
     * this method unselects the piece and unhighlights the respective positions
     */
    protected abstract void deselectPiece();

    //mouseClicked and speechCalled convert click or speech to piece selection and then call attemptMove()
    @Override
    public void mouseClicked(MouseEvent e) {
        gameGUI.clearSpeechOutput(); //dont leave output up if user decides to use mouse instead   
        Position clickedPosition = (Position) this.getComponentAt(new Point(e.getX(), e.getY()));
        if(e.getButton() == MouseEvent.BUTTON1 && selectedPiece == null) 
        {
            if(!clickedPosition.isFree() && clickedPosition.getPiece().getSide() == turn)
                selectPiece(clickedPosition.getPiece());
            else
            {
                if (clickedPosition.isFree()) //no piece
                    gameGUI.updateInvalidMove("No piece to select");
                else //piece is wrong side
                    gameGUI.updateInvalidMove("Piece is wrong side");
                deselectPiece();
            }
        } 
        else if (e.getButton() == MouseEvent.BUTTON1 && selectedPiece != null) 
            attemptMove(clickedPosition);
        else
            deselectPiece();
        repaint();
    }

    /*** 
     * this method is called from speechrecognizermain after speech button is pressed
     * @param speechReceived - what speech recognizer heard
     */
    public void speechCalled(String speechReceived)
    {
        gameGUI.updateSpeechOutput(speechReceived);
    	if (speechReceived.equals("clear")) //say clear to unselect piece, clear because sphinx 4 cant understand unselect
    	{
    		deselectPiece();
    		return;
    	}
    	else if (speechReceived.equals("<unk>")) //<unk> means recognizer did not understand speech, will tell user using boardGUI
            return;
    	String[] coordinates = speechReceived.split(" ");
        if (coordinates.length == 1)
        {
        	//rarely passes in single word that is not unk, should only be passing in two words, prevents out of bounds error by accessing coordinates[1] below
        	System.out.println("I did not understand what you said");
    		return;
        }

        //squares are labelled as x y (alpha one) but gameBoard is [y][x] so access as one alpha, top row of gameBoard (row 0) is row 8 on GUI and bottom row (row 7) is 1 on GUI so subtract yCoords index from 7 to find corresponding position
        Position spokenPosition = gameBoard[numbers.get(coordinates[1])][letters.get(coordinates[0])];

        if(selectedPiece == null) 
        {
            if(!spokenPosition.isFree() && spokenPosition.getPiece().getSide() == turn)
                selectPiece(spokenPosition.getPiece());
            else
            {
                if (spokenPosition.isFree())
                    gameGUI.updateInvalidMove("No piece to select");
                else
                    gameGUI.updateInvalidMove("Piece is wrong side");
                deselectPiece();
            }
        } 
        else if (selectedPiece != null)
            attemptMove(spokenPosition);
        else
            deselectPiece();
        repaint();
    }

    /***
     * this method moves the selected piece to the chosen location (as long as the move is legal) and handles special cases
     * @param chosen - position the selected piece is moving to
     */
    protected abstract void attemptMove(Position chosen);

    /***
     * helper method for attemptMove, if move is legal this method actually makes the move and unhighlights selected and legalMoves squares
     * @param chosen
     */
    protected abstract void moveAndUnhighlight(Position chosen);

    /***
     * called by gameGUI when either main menu or quit button is pressed, overridden in chessboard so that it can close promotion window if either of these buttons are pressed
     */
    public void dispose() { }

    /***
     * used for debugging, mostly for check/checkmate tests since I can't otherwise see those boards, should not be called in finished project
     */
    public void terminalPrint()
    {
        for (int y = 0; y < 8; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                if (!gameBoard[y][x].isFree())
                    System.out.print(gameBoard[y][x].getPiece().name() + "  ");
                else
                    System.out.print("null ");
            }
            System.out.println();
        }
    }

    /**
     * since the board implements MouseListner, 
     * the following methods have to be overridden. 
     * currently left empty as they are not needed
     */
    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

}