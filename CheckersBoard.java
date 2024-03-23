package BoardComponents;

import GUI.GameGUI;

import Information.Tag;
import Information.Tag.Side;

import Pieces.Piece;
import Pieces.CheckersPawn;
import Pieces.CheckersKing;

public class CheckersBoard extends Board {
    private boolean availableAttacks; //if player can attack, they must attack
    private boolean attackMade;
    public CheckersBoard(GameGUI gui, int colorSet) {
        super(gui, colorSet);
        setTurn(Side.BLACK);
        availableAttacks = false;
        attackMade = false;
    }

    public CheckersBoard(GameGUI gui, String[] pieces) {
        super(gui, pieces);
    }

    public void updateBoardGUI() {
        gameGUI.updateCurrentTurn(this.turn);
    }

    /***
     * called by checkers piece if they ever find an attack
     */
    public void setAvailableAttacks() { availableAttacks = true; }

    /***
     * called by pieces when looking for legal moves
     * @return - true if current player can attack, false otherwise
     */
    public boolean getAvailableAttacks() { return this.availableAttacks; }

    /***
     * swaps current turn, updates gameGUI
     */
    public void nextTurn() 
    { 
        gameGUI.clearSpeechOutput();
        if (turn == Side.OVER)
            return;
        else
        {
            attackMade = false;
            turn = (this.turn == Side.BLACK) ? Side.RED : Side.BLACK;
            gameGUI.updateCurrentTurn(turn);
            availableAttacks = false;
            //find friendly and enemy moves and pieces left after each move, identifies if game is over and what win condition was
            int availableFriendlyMoves = 0;
            int friendlyPieces = 0;
            //setAvailableAttack is called in getLegalMoves if there is an attack, both friendlyPieces and availableFriendlyMoves >= 1 if availableAttacks is true so loop can end once attack is found
            //game is over if there are zero available moves left for either side, tracking pieces just identifies if win condition was blocking remaining pieces or taking all pieces
            for (int y = 0; y < Tag.SIZE_MAX && !availableAttacks; y++)
            {
                for (int x = 0; x < Tag.SIZE_MAX && !availableAttacks; x++)
                {
                    if (!gameBoard[y][x].isFree() && gameBoard[y][x].getPiece().getSide() == turn)
                    {
                        friendlyPieces++;
                        availableFriendlyMoves += gameBoard[y][x].getPiece().getLegalMoves(gameBoard).size();
                    }
                }
            }
            if (availableFriendlyMoves == 0) //this player loses
            {
                if (friendlyPieces == 0) //no remaining pieces
                    gameGUI.updateGameOver(this.turn == Side.BLACK ? Side.RED : Side.BLACK, "No Remaining Pieces"); //current player has no moves left, therefor other player wins
                else //remaining pieces were blocked in
                    gameGUI.updateGameOver(this.turn == Side.BLACK ? Side.RED : Side.BLACK, "No Remaining Moves"); //current player has no moves left, therefor other player wins
                turn = Side.OVER;
            }
            else if (availableAttacks)
                gameGUI.updateTurnStatus(" (must attack)");
            //check if other player moved themself into spot with no moves left on previous move? depends on whether this player can move and unblock any of other players moves or whether this players blocking pieces are also blocked in/can only take
        }
    }

    protected void selectPiece(Piece piece)
    {
        if (attackMade && piece != selectedPiece)
            deselectPiece();
        else
        {
            selectedPiece = piece;
            setSelectedMovablePositions(selectedPiece);
            selectedPiece.getPosition().setSelect(true);
            highlightLegalPositions(selectedMovablePositions);
        }
    }

    protected void deselectPiece() {
        if (attackMade && (selectedPiece != null && selectedPiece.getLegalMoves(gameBoard).size() > 0)) //can not unselect after attack is made if there are more attacks, player must continue;
            return;
        if(selectedPiece != null) {
            selectedPiece.getPosition().setSelect(false);
            dehighlightlegalPositions(selectedMovablePositions);
            selectedPiece = null;
        }
        if (attackMade) //if, not else if or else because piece should be cleared if it is not null and deselect should only change turns after piece is done making moves
            nextTurn();
    }

    /***
     * checks if program can save, prevents saving after game is over and halfway through attack
     */
    public boolean canSave() { return (this.turn != Side.OVER && !attackMade); }

    /***
     * stores relevant board information as a string, called by save button and by moveLegal to create copy of board
     * @return - board color, current turn, all piece names, current locations
     */
    public String asString()
    {
        String save = "";
        save += String.valueOf(colorSet);
        if (turn == Side.RED)
            save += " red";
        else
            save += " black";
        for (int y = 0; y < Tag.SIZE_MAX; y++)
        {
            for (int x = 0; x < Tag.SIZE_MAX; x++)
            {
                if (!gameBoard[y][x].isFree())
                {
                    save += " " + gameBoard[y][x].getPiece().name();
                    if (gameBoard[y][x].getPiece().getSide() == Side.RED)
                        save += "r";
                    else
                        save += "b";
                    save += String.valueOf(y) + String.valueOf(x);
                }
            }
        }
        if (availableAttacks) //so that program knows when loading game whether there is an attack available (to prevent regular moves)
            save += " true";
        else
            save += " false";
        return save;
    }

    protected void initializePiecesToBoard() {
        //row 0
        gameBoard[0][1].setPiece(new CheckersPawn(Side.RED, gameBoard[0][1], Tag.RED_CHECKERS_PAWN, this));
        gameBoard[0][3].setPiece(new CheckersPawn(Side.RED, gameBoard[0][3], Tag.RED_CHECKERS_PAWN, this));
        gameBoard[0][5].setPiece(new CheckersPawn(Side.RED, gameBoard[0][5], Tag.RED_CHECKERS_PAWN, this));
        gameBoard[0][7].setPiece(new CheckersPawn(Side.RED, gameBoard[0][7], Tag.RED_CHECKERS_PAWN, this));
        //row 1
        gameBoard[1][0].setPiece(new CheckersPawn(Side.RED, gameBoard[1][0], Tag.RED_CHECKERS_PAWN, this));
        gameBoard[1][2].setPiece(new CheckersPawn(Side.RED, gameBoard[1][2], Tag.RED_CHECKERS_PAWN, this));
        gameBoard[1][4].setPiece(new CheckersPawn(Side.RED, gameBoard[1][4], Tag.RED_CHECKERS_PAWN, this));
        gameBoard[1][6].setPiece(new CheckersPawn(Side.RED, gameBoard[1][6], Tag.RED_CHECKERS_PAWN, this));
        //row 2
        gameBoard[2][1].setPiece(new CheckersPawn(Side.RED, gameBoard[2][1], Tag.RED_CHECKERS_PAWN, this));
        gameBoard[2][3].setPiece(new CheckersPawn(Side.RED, gameBoard[2][3], Tag.RED_CHECKERS_PAWN, this));
        gameBoard[2][5].setPiece(new CheckersPawn(Side.RED, gameBoard[2][5], Tag.RED_CHECKERS_PAWN, this));
        gameBoard[2][7].setPiece(new CheckersPawn(Side.RED, gameBoard[2][7], Tag.RED_CHECKERS_PAWN, this));

        //row 5
        gameBoard[5][0].setPiece(new CheckersPawn(Side.BLACK, gameBoard[5][0], Tag.BLACK_CHECKERS_PAWN, this));
        gameBoard[5][2].setPiece(new CheckersPawn(Side.BLACK, gameBoard[5][2], Tag.BLACK_CHECKERS_PAWN, this));
        gameBoard[5][4].setPiece(new CheckersPawn(Side.BLACK, gameBoard[5][4], Tag.BLACK_CHECKERS_PAWN, this));
        gameBoard[5][6].setPiece(new CheckersPawn(Side.BLACK, gameBoard[5][6], Tag.BLACK_CHECKERS_PAWN, this));
        //row 6
        gameBoard[6][1].setPiece(new CheckersPawn(Side.BLACK, gameBoard[6][1], Tag.BLACK_CHECKERS_PAWN, this));
        gameBoard[6][3].setPiece(new CheckersPawn(Side.BLACK, gameBoard[6][3], Tag.BLACK_CHECKERS_PAWN, this));
        gameBoard[6][5].setPiece(new CheckersPawn(Side.BLACK, gameBoard[6][5], Tag.BLACK_CHECKERS_PAWN, this));
        gameBoard[6][7].setPiece(new CheckersPawn(Side.BLACK, gameBoard[6][7], Tag.BLACK_CHECKERS_PAWN, this));
        //row 7
        gameBoard[7][0].setPiece(new CheckersPawn(Side.BLACK, gameBoard[7][0], Tag.BLACK_CHECKERS_PAWN, this));
        gameBoard[7][2].setPiece(new CheckersPawn(Side.BLACK, gameBoard[7][2], Tag.BLACK_CHECKERS_PAWN, this));
        gameBoard[7][4].setPiece(new CheckersPawn(Side.BLACK, gameBoard[7][4], Tag.BLACK_CHECKERS_PAWN, this));
        gameBoard[7][6].setPiece(new CheckersPawn(Side.BLACK, gameBoard[7][6], Tag.BLACK_CHECKERS_PAWN, this));
    }

    protected void initializePiecesToBoard(String[] pieces) {
        for (int i = 4; i < pieces.length - 1; i++) //0 and 1 are player names, 2 is turn, last spot is en passant
        {
            String current = pieces[i];
            int y = current.charAt(4) - '0';
            int x = current.charAt(5) - '0';
            if (current.charAt(3) == 'b') //black
            {
                if (current.charAt(1) == 'K')
                    gameBoard[y][x].setPiece(new CheckersKing(Side.BLACK, gameBoard[y][x], Tag.BLACK_CHECKERS_KING, this));
                else
                    gameBoard[y][x].setPiece(new CheckersPawn(Side.BLACK, gameBoard[y][x], Tag.BLACK_CHECKERS_PAWN, this));
            }
            else //red
            {
                if (current.charAt(1) == 'K')
                    gameBoard[y][x].setPiece(new CheckersKing(Side.RED, gameBoard[y][x], Tag.RED_CHECKERS_KING, this));
                else
                    gameBoard[y][x].setPiece(new CheckersPawn(Side.RED, gameBoard[y][x], Tag.RED_CHECKERS_PAWN, this));
            }
        }
        if (pieces[pieces.length - 1].equals("true"))
            availableAttacks = true;
        else
            availableAttacks = false;
    }

    public void attemptMove(Position chosen) {
        if (selectedMovablePositions.contains(chosen))
        {
            if (Math.abs(chosen.getPosY() - selectedPiece.getPosition().getPosY()) > 1) //attacking
            {
                attackPiece(chosen);
                moveAndUnhighlight(chosen);
                attackMade = true;
                if (selectedPiece.getLegalMoves(gameBoard).size() > 0) //if attackmade is true, getLegalMoves will only return attacks
                {
                    selectPiece(chosen.getPiece()); //reselect piece to rehighlight
                    gameGUI.updateInvalidMove("Please choose your next attack");
                }
                else
                    deselectPiece();
            }
            else //moving one square
            {
                if (availableAttacks)
                    gameGUI.updateInvalidMove("You must attack");
                else
                {
                    moveAndUnhighlight(chosen);
                    deselectPiece();
                    nextTurn();
                }
            }
        }
        else
        {
            if (!chosen.isFree())
                gameGUI.updateInvalidMove("Can not move onto occupied square");
            else
                gameGUI.updateInvalidMove("Invalid move for piece"); //position is empty but is out of pieces range or not in correct direction
        }
    }

    protected void moveAndUnhighlight(Position chosen) {
        selectedPiece.getPosition().setSelect(false);
        dehighlightlegalPositions(selectedMovablePositions);
        selectedPiece.move(chosen);
        if (selectedPiece.name().equals("(P)") && (chosen.getPosY() == 7 && selectedPiece.getSide() == Side.RED) || (chosen.getPosY() == 0 && selectedPiece.getSide() == Side.BLACK)) //pawn has reached far side
            promote(chosen);
        saved = false;
    }

    /***
     * this method removes whichever piece the selected piece (class variable) jumps over while moving to chosen
     * @param chosen - position selected piece is moving to
     */
    private void attackPiece(Position chosen) {
        int yShift, xShift;
        if (selectedPiece.getPosition().getPosY() > chosen.getPosY())
            yShift = 1;
        else
            yShift = -1;
        if (selectedPiece.getPosition().getPosX() > chosen.getPosX())
            xShift = 1;
        else
            xShift = -1;
        gameBoard[chosen.getPosY() + yShift][chosen.getPosX() + xShift].removePiece();
    }

    /***
     * this method promotes the piece at the chosen position after it moves
     * @param chosen - position pawn moved to, at opposite end of the board for that pawn
     */
    private void promote(Position chosen) {
        if (chosen.getPiece().getSide() == Side.RED)
        {
            chosen.removePiece();
            chosen.setPiece(new CheckersKing(Side.RED, chosen, Tag.RED_CHECKERS_KING, this));
        }
        else
        {
            chosen.removePiece();
            chosen.setPiece(new CheckersKing(Side.BLACK, chosen, Tag.BLACK_CHECKERS_KING, this));
        }
    }
}
