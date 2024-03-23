package BoardComponents;

import java.util.List;
import java.util.ArrayList;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

import GUI.GameGUI;

import Information.Tag;
import Information.Tag.Side;

import Pieces.Bishop;
import Pieces.King;
import Pieces.Knight;
import Pieces.Pawn;
import Pieces.Piece;
import Pieces.Queen;
import Pieces.Rook;

public class ChessBoard extends Board {
    private Promotion promo;
    private Piece enPassantPawn;
    private Piece promotionPiece;
    private Piece wKing; //store kings for check and checkmate, assigned during initialize methods
    private Piece bKing;

    public ChessBoard(GameGUI gui, int colorSet) {
        super(gui, colorSet);
        setTurn(Side.WHITE);
        enPassantPawn = null;
        promotionPiece = null;
    }

    public ChessBoard(String[] pieces) {
        super(pieces);
    }

    public ChessBoard(GameGUI gui, String[] pieces) {
        super(gui, pieces);
    }

    /***
     * swaps current turn, updates gameGUI, and tests if other player was just moved into check
     */
    public void nextTurn() 
    { 
        gameGUI.clearSpeechOutput();
        if (turn == Side.OVER)
            return;
        else
        {
            turn = (this.turn == Side.BLACK) ? Side.WHITE : Side.BLACK;
            gameGUI.updateCurrentTurn(turn);
        }
        if (enPassantPawn != null && enPassantPawn.getSide() == this.turn) //en Passant is valid for only one move
            clearEnPassant(); //en passant for white pawn no longer valid once black moves
        updateBoardGUI();
    }

    protected void selectPiece(Piece piece)
    {
        selectedPiece = piece;
        setSelectedMovablePositions(selectedPiece);
        selectedPiece.getPosition().setSelect(true);
        highlightLegalPositions(selectedMovablePositions);
    }

    protected void deselectPiece() {
        if(selectedPiece != null) {
            selectedPiece.getPosition().setSelect(false);
            dehighlightlegalPositions(selectedMovablePositions);
            selectedPiece = null;
        }
    }

    /***
     * called at the end of nex turn, updates boardGUI and tests for check/checkmate or stalemate
     */
    public void updateBoardGUI() {
        gameGUI.updateCurrentTurn(this.turn);
        if( !checkHighlight()) //makes call to checkHighlight and updates board accordingly, if king is not in check test for stalemate
            stalemateHighlight();
    }

    /***
     * prevents saving after game is done or if promotion selection still needs to be made
     * @return - returns true if game is on going and there is no promotion piece to select, false otherwise
     */
    public boolean canSave() { return this.turn != Side.OVER && promo == null; }

    /***
     * stores relevant board information as a string, called by save button and by moveLegal to create copy of board
     * @return - board color, current turn, all piece names, current locations, and position of en passant pawn
     */
    public String asString()
    {
        String save = "";
        save += String.valueOf(colorSet);
        if (turn == Side.WHITE)
            save += " white";
        else
            save += " black";
        for (int y = 0; y < Tag.SIZE_MAX; y++)
        {
            for (int x = 0; x < Tag.SIZE_MAX; x++)
            {
                if (!gameBoard[y][x].isFree())
                {
                    save += " " + gameBoard[y][x].getPiece().name();
                    if (gameBoard[y][x].getPiece().getSide() == Side.WHITE)
                        save += "w";
                    else
                        save += "b";
                    save += String.valueOf(y) + String.valueOf(x);
                    if (gameBoard[y][x].getPiece().getMoved())
                        save += "t";
                    else
                        save += "f";
                }
            }
        }
        if (enPassantPawn == null) //when copying board, last index is checked separate from all other indicies so something must be there even if there is not en passant pawn
            save += " null";
        else
            save += " " + String.valueOf(enPassantPawn.getPosition().getPosY()) + String.valueOf(enPassantPawn.getPosition().getPosX()); //en passant pawn itself was already counted in for loop above, simply add position of it separately to identify it
        return save;
    }
    
    /***
     * called after move is made, looks if last move placed the other player in check
     * @return - returns true if king is in check or checkmate, false otherwise, returns boolean so that stalemate is not tested if king is already in check
     */
    public boolean checkHighlight()
    {
        //since move is already made, turn has been swapped (if black just moved, turn has already been assigned to white)
        boolean check = false;
        if (turn == Side.WHITE)
        {
            List<Piece> pieces = canBeTaken(Side.BLACK, wKing.getPosition());
            if (pieces.size() != 0) //at least one piece placing king in check
            {
                if (checkmate(Side.WHITE, pieces))
                {
                    wKing.getPosition().setCheckmate(true);
                    turn = Side.OVER;
                    gameGUI.updateGameOver(Side.BLACK, "Checkmate");
                }
                else //check, not checkmate
                {
                    wKing.getPosition().setCheck(true);
                    gameGUI.updateTurnStatus(" (in check)");
                }
                check = true;
            }
        }
        else //black
        {
            List<Piece> pieces = canBeTaken(Side.WHITE, bKing.getPosition());
            if (pieces.size() != 0)
            {
                if (checkmate(Side.BLACK, pieces))
                {
                    bKing.getPosition().setCheckmate(true);
                    turn = Side.OVER;
                    gameGUI.updateGameOver(Side.WHITE, "Checkmate");
                }
                else
                {
                    bKing.getPosition().setCheck(true);
                    gameGUI.updateTurnStatus(" (in check)");
                }
                check = true;
            }
        }
        repaint();
        return check; //stored as boolean so that repaint can be called before return
    }

    /***
     * called after making sure no one is in check, looks for at least one legal move, if no legal moves game is in stalemate
     */
    protected void stalemateHighlight() {
        boolean stalemate = true; //default to true, set to false once at least one legal move is found
        for (int y = 0; y < 8 && stalemate; y++)
        {
            for (int x = 0; x < 8 && stalemate; x++)
            {
                if (!gameBoard[y][x].isFree() && gameBoard[y][x].getPiece().getSide() == this.getTurn())
                {
                    for (Position dest : gameBoard[y][x].getPiece().getLegalMoves(gameBoard))
                    {
                        if (moveLegal(gameBoard[y][x].getPiece(), dest)) //if there is at least one legal move for any piece on this side, it is not stalemate
                        {
                            stalemate = false;
                            break; //break for in loop, y and x end when stalemate is false
                        }
                    }
                }
            }
        }
        if (stalemate) {
            gameGUI.updateGameOver(this.turn, "Stalemate");
            setTurn(Side.OVER);
        }
    }

    /***
     * default initialization method to fill all pieces into their starting positions, called by new board (copy/loaded board uses other method)
     */
    protected void initializePiecesToBoard() {
        // generate rook
        gameBoard[0][0].setPiece(new Rook(Side.BLACK, gameBoard[0][0], Tag.BLACK_ROOK));
        gameBoard[0][7].setPiece(new Rook(Side.BLACK, gameBoard[0][7], Tag.BLACK_ROOK));
        gameBoard[7][0].setPiece(new Rook(Side.WHITE, gameBoard[7][0], Tag.WHITE_ROOK));
        gameBoard[7][7].setPiece(new Rook(Side.WHITE, gameBoard[7][7], Tag.WHITE_ROOK));
        // generate knight
        gameBoard[0][1].setPiece(new Knight(Side.BLACK, gameBoard[0][1], Tag.BLACK_KNIGHT));
        gameBoard[0][6].setPiece(new Knight(Side.BLACK, gameBoard[0][6], Tag.BLACK_KNIGHT));
        gameBoard[7][1].setPiece(new Knight(Side.WHITE, gameBoard[7][1], Tag.WHITE_KNIGHT));
        gameBoard[7][6].setPiece(new Knight(Side.WHITE, gameBoard[7][6], Tag.WHITE_KNIGHT));
        // generate bishop
        gameBoard[0][2].setPiece(new Bishop(Side.BLACK, gameBoard[0][2], Tag.BLACK_BISHOP));
        gameBoard[0][5].setPiece(new Bishop(Side.BLACK, gameBoard[0][5], Tag.BLACK_BISHOP));
        gameBoard[7][2].setPiece(new Bishop(Side.WHITE, gameBoard[7][2], Tag.WHITE_BISHOP));
        gameBoard[7][5].setPiece(new Bishop(Side.WHITE, gameBoard[7][5], Tag.WHITE_BISHOP));
        // generate queen
        gameBoard[0][3].setPiece(new Queen(Side.BLACK, gameBoard[0][3], Tag.BLACK_QUEEN));
        gameBoard[7][3].setPiece(new Queen(Side.WHITE, gameBoard[7][3], Tag.WHITE_QUEEN));
        // generate king
        gameBoard[0][4].setPiece(new King(Side.BLACK, gameBoard[0][4], Tag.BLACK_KING));
        bKing = gameBoard[0][4].getPiece();
        gameBoard[7][4].setPiece(new King(Side.WHITE, gameBoard[7][4], Tag.WHITE_KING));
        wKing = gameBoard[7][4].getPiece();
        // generate Pawn
        for(int i = 0; i < 8; i++) {
            gameBoard[1][i].setPiece(new Pawn(Side.BLACK, gameBoard[1][i], Tag.BLACK_PAWN));
            gameBoard[6][i].setPiece(new Pawn(Side.WHITE, gameBoard[6][i], Tag.WHITE_PAWN));
        }
    }

    /***
     * initializes all pieces from copied board onto new board in their respective positions
     * @param pieces - stores both player names, board color, current turn, all piece names and current locations, and position of en passant pawn
     */
    protected void initializePiecesToBoard(String[] pieces)
    {
        for (int i = 4; i < pieces.length - 1; i++) //0 and 1 are player names, 2 is turn, last spot is en passant
        {
            String current = pieces[i];
            int y = current.charAt(4) - '0';
            int x = current.charAt(5) - '0';
            if (current.charAt(3) == 'b') //black
            {
                if (current.charAt(1) == 'K') //king
                {
                    gameBoard[y][x].setPiece(new King(Side.BLACK, gameBoard[y][x], Tag.BLACK_KING));
                    this.bKing = gameBoard[y][x].getPiece();
                    if (current.charAt(6) == 't')
                        gameBoard[y][x].getPiece().setMoved();
                }
                else if (current.charAt(1) == 'Q')
                    gameBoard[y][x].setPiece(new Queen(Side.BLACK, gameBoard[y][x], Tag.BLACK_QUEEN));
                else if (current.charAt(1) == 'P') //pawn
                {
                    gameBoard[y][x].setPiece(new Pawn(Side.BLACK, gameBoard[y][x], Tag.BLACK_PAWN));
                    if (current.charAt(6) == 't')
                        gameBoard[y][x].getPiece().setMoved();
                }
                else if (current.charAt(1) == 'N') //knight
                    gameBoard[y][x].setPiece(new Knight(Side.BLACK, gameBoard[y][x], Tag.BLACK_KNIGHT));
                else if (current.charAt(1) == 'R') //rook
                {
                    gameBoard[y][x].setPiece(new Rook(Side.BLACK, gameBoard[y][x], Tag.BLACK_ROOK));
                    if (current.charAt(6) == 't')
                        gameBoard[y][x].getPiece().setMoved();
                }
                else //bishop
                    gameBoard[y][x].setPiece(new Bishop(Side.BLACK, gameBoard[y][x], Tag.BLACK_BISHOP));
            }
            else //white
            {
                if (current.charAt(1) == 'K') //king
                {
                    gameBoard[y][x].setPiece(new King(Side.WHITE, gameBoard[y][x], Tag.WHITE_KING));
                    this.wKing = gameBoard[y][x].getPiece();
                    if (current.charAt(6) == 't')
                        gameBoard[y][x].getPiece().setMoved();
                }
                else if (current.charAt(1) == 'Q') //queen
                    gameBoard[y][x].setPiece(new Queen(Side.WHITE, gameBoard[y][x], Tag.WHITE_QUEEN));
                else if (current.charAt(1) == 'P') //pawn
                {
                    gameBoard[y][x].setPiece(new Pawn(Side.WHITE, gameBoard[y][x], Tag.WHITE_PAWN));
                    if (current.charAt(6) == 't')
                        gameBoard[y][x].getPiece().setMoved();
                }
                else if (current.charAt(1) == 'N') //knight
                    gameBoard[y][x].setPiece(new Knight(Side.WHITE, gameBoard[y][x], Tag.WHITE_KNIGHT));
                else if (current.charAt(1) == 'R') //rook
                {
                    gameBoard[y][x].setPiece(new Rook(Side.WHITE, gameBoard[y][x], Tag.WHITE_ROOK));
                    if (current.charAt(6) == 't')
                        gameBoard[y][x].getPiece().setMoved();
                }
                else //bishop
                    gameBoard[y][x].setPiece(new Bishop(Side.WHITE, gameBoard[y][x], Tag.WHITE_BISHOP));
            }
        }
        //en passant is marked simply by just position
        String enPassant = pieces[pieces.length - 1];
        if (enPassant.equals("null"))
            enPassantPawn = null;
        else
        {
            int y = enPassant.charAt(0) - '0';
            int x = enPassant.charAt(1) - '0';
            enPassantPawn = gameBoard[y][x].getPiece();
            if (y == 3)
                gameBoard[2][x].setEnPassant(true);
            else //y == 4
                gameBoard[5][x].setEnPassant(true);
        }
        //terminalPrint();
    }

    /***
     * this method is called when en passant is no longer legal (turn has passed), clears en passant piece and position
     */
    private void clearEnPassant()
    {
        if (enPassantPawn != null)
        {
            int y = (enPassantPawn.getSide() == Side.WHITE) ? 5 : 2;
            int x = enPassantPawn.getPosition().getPosX();
            gameBoard[y][x].setEnPassant(false);
            enPassantPawn = null;
        }
    }

    /***
     * this method assigns the en passant piece on the board and opens the position behind it to be attacked via en passant
     * @param piece - the pawn that can be taken via en passant
     */
    private void setEnPassant(Piece piece)
    {
        clearEnPassant();
        int y = (piece.getSide() == Side.WHITE) ? 5 : 2;
        int x = piece.getPosition().getPosX();
        gameBoard[y][x].setEnPassant(true);
        enPassantPawn = piece;
    }

    /***
     * this method is called to close promotion pop up window and assign promotion variables to null
     */
    private void clearPromotion()
    {
        if (promo != null)
            promo.closePromotion();
        promo = null;
        promotionPiece = null;
    }

    /***
     * this method moves the corresponding rook when king makes castling move
     * @param chosen - the destination that the king is moving to, identifies which rook to move
     */
    private void castle(Position chosen)
    {
        int y = chosen.getPosY(); //get y coord from destination
        if (chosen.getPosX() == 2) //is king going left
        {
            Piece rook = gameBoard[y][0].removePiece();
            gameBoard[y][3].setPiece(rook);
        }
        else if (chosen.getPosX() == 6) //is king going right, will always be x = 2 or 6 but using else if instead of else to prevent unlikely bugs
        {
            Piece rook = gameBoard[y][7].removePiece();
            gameBoard[y][5].setPiece(rook);
        }
        repaint();
    }

    /***
     * this method promotes a pawn to another piece of the player's choosing
     * @param name - name of piece that pawn is promoting to such as (Q) for queen
     */
    public void promote(String name)
    {
        if (promotionPiece != null)
        {
            Side side = promotionPiece.getSide();
            Position temp = promotionPiece.getPosition();
            temp.removePiece();
            clearPromotion();
            if (name.equals("(Q)"))
            {
                if (side == Side.BLACK)
                    temp.setPiece(new Queen(side, temp, Tag.BLACK_QUEEN));
                else
                    temp.setPiece(new Queen(side, temp, Tag.WHITE_QUEEN));
            }
            else if (name.equals("(R)"))
            {
                if (side == Side.BLACK)
                    temp.setPiece(new Rook(side, temp, Tag.BLACK_ROOK));
                else
                    temp.setPiece(new Rook(side, temp, Tag.WHITE_ROOK));
            }
            else if (name.equals("(B)"))
            {
                if (side == Side.BLACK)
                    temp.setPiece(new Bishop(side, temp, Tag.BLACK_BISHOP));
                else
                    temp.setPiece(new Bishop(side, temp, Tag.WHITE_BISHOP));
            }
            else
            {
                if (side == Side.BLACK)
                    temp.setPiece(new Knight(side, temp, Tag.BLACK_KNIGHT));
                else
                    temp.setPiece(new Knight(side, temp, Tag.WHITE_KNIGHT));
            }
            turn = (side == Side.WHITE) ? Side.BLACK : Side.WHITE;
            repaint();
            updateBoardGUI();
        }
        deselectPiece();
    }

    //creates new JFrame with similar implementation to board, shows piece choices for player to promote a pawn to
    protected class Promotion extends JPanel implements MouseListener {
        JFrame frame;
        Position[][] promotionPositions;
        public Promotion() {
            frame = new JFrame();
            setLayout(new GridLayout(1, 4, 0, 0));
            promotionPositions = new Position[1][4];
            for (int i = 0; i < 4; i++)
            {
                promotionPositions[0][i] = new Position(i, 0, false, 0, colorSet);
                this.add(promotionPositions[0][i]);
            }
            initializePiecesForPromotion(promotionPiece.getSide());
            this.addMouseListener(this);
            frame = new JFrame("Promotion");
            frame.setIconImage(new ImageIcon(Tag.BLACK_QUEEN).getImage());
            JPanel panel = new JPanel();
            panel.setBackground(Tag.ColorChoice[colorSet][0]);
            JLabel instructions = new JLabel(gameGUI.getTurnPlayerName(promotionPiece.getSide()) + ", please select a piece your pawn to promote to");
            instructions.setForeground(Tag.ColorChoice[colorSet][9]);
            panel.add(instructions);
            frame.add(panel, BorderLayout.NORTH);
            frame.add(this, BorderLayout.CENTER);
            frame.setSize(400, 150);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            //have to handle promotion jframe getting closed, if it is closed without selecting piece the game is permanently paused
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    promote("(Q)"); //default to queen if window is closed
                    closePromotion();
                }
            });
            frame.setVisible(true);
        }

        /***
         * initializes four piece choices onto promotion display
         * @param side - side the pieces are for
         */
        private void initializePiecesForPromotion(Side side)
        {
            promotionPositions[0][0].setPiece(new Queen(side, promotionPositions[0][0], (side == Side.WHITE) ? Tag.WHITE_QUEEN : Tag.BLACK_QUEEN));
            promotionPositions[0][1].setPiece(new Rook(side, promotionPositions[0][1], (side == Side.WHITE) ? Tag.WHITE_ROOK : Tag.BLACK_ROOK));
            promotionPositions[0][2].setPiece(new Knight(side, promotionPositions[0][2], (side == Side.WHITE) ? Tag.WHITE_KNIGHT : Tag.BLACK_KNIGHT));
            promotionPositions[0][3].setPiece(new Bishop(side, promotionPositions[0][3], (side == Side.WHITE) ? Tag.WHITE_BISHOP : Tag.BLACK_BISHOP));
        }

        @Override
        public void mouseClicked(MouseEvent e) {        
            Position clickedPosition = (Position) this.getComponentAt(new Point(e.getX(), e.getY()));
            if (clickedPosition.getPiece() != null)
            {
                promote(clickedPosition.getPiece().name());
                closePromotion();
            }
        }
        
        /***
         * public method to dispose frame, can be called by save or main menu buttons to close window
         */
        public void closePromotion()
        {
            frame.dispose();
        }

        /**
         * since the promotion implements MouseListner, 
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

    /***
     * this is a helper method for mouseClicked and speechCalled, tries to selected piece to chosen position and handles special rules (en passant, castling, promotion)
     * @param chosen - position that the selected piece (stored as class variable) will move
     */
    protected void attemptMove(Position chosen) {
        if(chosen.isFree() || chosen.getPiece().getSide() != turn) //moving to free position or one occupied by other side
        {
            if(selectedMovablePositions.contains(chosen)) //move is allowed in piece's moveset
            {
                if (moveLegal(selectedPiece, chosen)) //move does not place yourself into check
                {
                    checkSpecialCases(selectedPiece, chosen);
                    moveAndUnhighlight(chosen); //move after special rules have been checked

                    //after moving, check for promotion, nextTurn() cannot be called in promotion case because promotion pauses game, nextTurn() would reassign turn and allow players to move before promotion selection is made
                    if (selectedPiece.name().equals("(P)") && (selectedPiece.getPosition().getPosY() == 7 || selectedPiece.getPosition().getPosY() == 0))
                    {
                        promotionPiece = selectedPiece;
                        deselectPiece();
                        turn = Side.PAUSE; //manually pause until promotion is done
                        gameGUI.clearSpeechOutput(); //no need for speech output when promoting
                        promo = new Promotion();
                    }
                    else
                    {
                        deselectPiece();
                        nextTurn();
                    }
                }
                else //could not move, either tried to move yourself out of check or already in check and move did not escape it
                {
                    if (bKing.getPosition().isCheck() || wKing.getPosition().isCheck()) //other players last turn placed them in check, square is already red under king
                        gameGUI.updateInvalidMove("Must escape check");
                    else //moved yourself into check
                        gameGUI.updateInvalidMove("Can not move yourself into check");
                }
            }
            else //not in movable positions but either free or taken by enemy piece, selected piece can not move to that position due to its movement limitations (like rook along diagonal or pawn 3 sqaures forward)
                gameGUI.updateInvalidMove("Invalid move for piece");
        }
        else //chosen is not free, occupied by same sided piece
            gameGUI.updateInvalidMove("Can not attack own piece");
    }

    /***
     * this method is responsible for checking for castling and en passant
     * @param moving - piece to move, does not rely on selectedPiece because check test copy boards do not have selected pieces
     * @param chosen - position piece is moving to
     */
    protected void checkSpecialCases(Piece piece, Position chosen)
    {
        if (piece.name().equals("(P)")) //selected is pawn, check for en passant
        {
            if (Math.abs(piece.getPosition().getPosY() - chosen.getPosY()) == 2) //moving forward two, sets up en passant
                setEnPassant(piece);
            else if (gameBoard[chosen.getPosY()][chosen.getPosX()].getEnPassant()) //attacking en passant pawn
            {
                Position enPassantedPawn = enPassantPawn.getPosition(); //save position so that it can removed after en passant is cleared
                clearEnPassant();
                enPassantedPawn.removePiece();
            }
        }
        else if (piece.name().equals("(K)")) //piece is king, may be castling
        {
            if (Math.abs(piece.getPosition().getPosX() - chosen.getPosX()) == 2) //moving two squares, move respective rook
                castle(chosen);
        }
    }
    /***
     * called by attemptMove if the move is actually legal, makes the move and unhighlights the respective positions
     * @param chosen - the position that the selected piece is being moved to
     */
    protected void moveAndUnhighlight(Position chosen)
    {
        selectedPiece.getPosition().setSelect(false);
        wKing.getPosition().setCheck(false);
        bKing.getPosition().setCheck(false);
        selectedPiece.move(chosen);
        saved = false;
    }

    /***
     * this method will create a copy of the board, make the move being attempted on that board, and then ensure the player did not move themself into check, if this move is legal, it will then be made on the actual board in the attemptMove() method
     * @param piece - piece that is being moved, takes piece as argument instead of selectedPiece so that it can be used to test checkmate (can king legally move to this square or can piece legally take chosen position)
     * @param chosen - position that selected is being moved to
     * @return - true if move is legal (player did not move themself into check), false if it is illegal
     */
    public boolean moveLegal(Piece piece, Position chosen)
    {
        //using ints of positions as positions themselves are tied to this board and tester is a copy with separate positions
        int selectedY = piece.getPosition().getPosY();
        int selectedX = piece.getPosition().getPosX();
        int chosenY = chosen.getPosY();
        int chosenX = chosen.getPosX();
        String boardCopy = "white black " + this.asString(); //added colors in front as place holders for names, similar to how BoardGUI adds player names, makes indicies in boardCopy array consistent with that of saved game
        ChessBoard tester = new ChessBoard(boardCopy.split(" "));
        //seperation with if else and print statement rather than just return testCheck() is helpful for debug so I'm leaving it but commmenting print out
        if (!tester.testCheck(selectedY, selectedX, chosenY, chosenX))
        {
            //System.out.println("Legal move");
            return true;
        }
        else
        {
            //System.out.println("Illegal move");
            return false;
        }
    }

    /***
     * this method is used for check/checkmate detection, tests is a given position can be taken by a given side, called with kings position and enemy side to see if king is in check, can be called on checking pieces or in positions in between checking piece and king to try to take or block respectively
     * @param side - side attempting to take position
     * @param initial - position for side to take, position is either empty or occupied by opposite side of side passed in
     * @return
     */
    public List<Piece> canBeTaken(Side side, Position initial)
    {
        List<Piece> pieces = new ArrayList<Piece>();
        //check along all lines, add piece to list if it can take
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        for (int[] line : directions)
        {
            //shift y and x from the start to avoid comparing initial position
            int y = initial.getPosY() + line[0];
            int x = initial.getPosX() + line[1];
            boolean oneShift = true; //true on first loop, within range of king and pawn
            while (y < 8 && y > -1 && x < 8 && x > -1)
            {
                if (gameBoard[y][x].isFree()) //square is empty
                {
                    //keep incrementing until a piece is found or out of bounds
                    y += line[0];
                    x += line[1];
                    oneShift = false;
                }
                else //square is taken
                {
                    Piece occupyingPiece = gameBoard[y][x].getPiece();
                    String name = occupyingPiece.name();
                    if (occupyingPiece.getSide() == side) //piece is side I'm looking for
                    {
                        if (line[0] != 0 && line[1] != 0) //both are shifting, diagonal line
                        {
                            if (name.equals("(B)") || name.equals("(Q)") || (name.equals("(K)") && oneShift))
                                pieces.add(occupyingPiece);
                            else if (name.equals("(P)") && oneShift) //pawn and within pawn range
                            {
                                //make sure pawn can move in that direction, separate checks because pawn can only move diagonally when attacking unlike pieces above
                                //white pawns start at y = 6, can only move in decreasing y direction
                                //black pawns start at y = 1, can only move in increasing y direction
                                if ((occupyingPiece.getSide() == Side.WHITE && initial.getPosY() < y) || (occupyingPiece.getSide() == Side.BLACK && initial.getPosY() > y))
                                {
                                    if (!initial.isFree() || initial.getEnPassant()) //occupyingPiece can take initial because it has a piece or because it is empty but can be taken with en passant
                                        pieces.add(occupyingPiece);
                                }
                            }
                        }
                        else //only x or y shifting, vertical or horizontal line
                        {
                            if (name.equals("(R)") || name.equals("(Q)") || (name.equals("(K)") && oneShift))
                                pieces.add(occupyingPiece);
                            else if (name.equals("(P)") && line[1] == 0 && initial.isFree()) //pawn can only move forward along y axis to open squares
                            {
                                //ensure that pawn can move in that y direction
                                if ((occupyingPiece.getSide() == Side.WHITE && initial.getPosY() < y) || (occupyingPiece.getSide() == Side.BLACK && initial.getPosY() > y))
                                {
                                    if (oneShift || (Math.abs(y - initial.getPosY()) == 2 && !occupyingPiece.getMoved())) //moving forward one square, or has not moved yet amd initial is 2 squares in front of pawn
                                        pieces.add(occupyingPiece);
                                }
                            }
                        }
                    }
                    break; //piece will block initial from other pieces further along that line, regardless of type or side of piece
                }
            }
        }
        //check all potential knight locations
        int[][] knights = {{1, 2}, {1, -2}, {2, 1}, {2, -1}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}};
        for (int[] shift : knights)
        {
            //shift to each potential knight location
            int y = initial.getPosY() + shift[1];
            int x = initial.getPosX() + shift[0];
            if (y > -1 && y < 8 && x > -1 && x < 8) //check board bounds
            {
                if (!gameBoard[y][x].isFree()) //check if piece is there
                {
                    Piece potential = gameBoard[y][x].getPiece();
                    if (potential.getSide() == side && potential.name().equals("(N)")) //right side and piece type
                        pieces.add(potential);
                }
            }
        }
        return pieces;
    }

    /***
     * this method is called by moveLegal after moveLegal creates a copy of the board, moves piece at selected indicies to chosen indicies and looks if player moved themself into check, takes ints instead of position or piece because position and piece are specific to actual board and therefor not present on copy board within which this method is called
     * @param selectedY - y coordinate of selected piece
     * @param selectedX - x coordinate of selected piece
     * @param chosenY - y coordinate of chosen piece
     * @param chosenX - x coordinate of chosen piece
     * @return - returns true if player is now in check, false if not
     */
    public boolean testCheck(int selectedY, int selectedX, int chosenY, int chosenX)
    {
        checkSpecialCases(gameBoard[selectedY][selectedX].getPiece(), gameBoard[chosenY][chosenX]);
        gameBoard[selectedY][selectedX].getPiece().move(gameBoard[chosenY][chosenX]);
        //terminalPrint();
        if (turn == Side.WHITE) //white moved, turn has not yet been reassigned, make sure white did not move themself into check
            return (canBeTaken(Side.BLACK, wKing.getPosition()).size() != 0); //zero if nothing can attack king
        else //black
            return (canBeTaken(Side.WHITE, bKing.getPosition()).size() != 0);
    }

    /***
     * this method is called after turn is made and player is moved into check, tests if there is any way to escape check
     * @param side - side in check
     * @param pieces - pieces placing the king in check
     * @return - true if player is in checkmate, false if there is a way out of check
     */
    public boolean checkmate(Side side, List<Piece> pieces)
    {
        //start by trying to move king
        Piece king;
        if (side == Side.WHITE)
            king = wKing;
        else
            king = bKing;
        List<Position> kingMoves = king.getLegalMoves(this.gameBoard); //all possible king moves
        for (int i = 0; i < kingMoves.size(); i++)
        {
            if (moveLegal(king, kingMoves.get(i))) //if there is a single move that is legal, it is not checkmate
                return false;
        }
        //can't move king, try taking
        if (pieces.size() > 1) //can't take or block more than one piece per turn, if moving king does not work and there are multiple pieces checking, it is checkmate
            return true;
        Piece checkingPiece = pieces.get(0); //there is only one piece checking king (if statement would return if size > 1) so .get(0) is only checking piece
        List<Piece> takeCheckingPiece = canBeTaken(side, checkingPiece.getPosition()); //what friendly pieces can take checking piece
        for (int i = 0; i < takeCheckingPiece.size(); i++)
        {
            if (moveLegal(takeCheckingPiece.get(i), checkingPiece.getPosition())) //if there is a single way to take the checking piece legally, it is not checkmate
                return false;
        }
        //cannot move king or take checking piece, try blocking piece
        if (checkingPiece.name().equals("(N)")) //cannot block knight
            return true;
        int xShift = 0; //default to 0, assign as positive or negative one if x or y changes
        int yShift = 0;
        if (king.getPosition().getPosX() > checkingPiece.getPosition().getPosX())
            xShift = -1;
        else if (king.getPosition().getPosX() < checkingPiece.getPosition().getPosX())
            xShift = 1;
        if (king.getPosition().getPosY() > checkingPiece.getPosition().getPosY())
            yShift = -1;
        else if (king.getPosition().getPosY() < checkingPiece.getPosition().getPosY())
            yShift = 1;
        int y = king.getPosition().getPosY() + yShift;
        int x = king.getPosition().getPosX() + xShift;
        //start with values shifted along line between king and piece, loop while [y][x] != checkingPiece position because that was already checked when attempting to take checkingPiece
        //look at every square between king and checkingPiece (exclusive), || not && because x == getPosX on vertical and y == getPosY on horizontal lines
        while (y != checkingPiece.getPosition().getPosY() || x != checkingPiece.getPosition().getPosX())
        {
            List<Piece> blockable = canBeTaken(side, gameBoard[y][x]);
            for (int i = 0; i < blockable.size(); i++)
            {
                if (moveLegal(blockable.get(i), gameBoard[y][x])) //if there is a single way to block checking piece legally, it is not checkmate
                    return false;
            }
            y += yShift;
            x += xShift;
        }
        return true;
    }

    @Override
    public void dispose() { clearPromotion(); }
}
