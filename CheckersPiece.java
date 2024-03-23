package Pieces;

import java.util.List;
import java.util.ArrayList;

import java.awt.Graphics;

import Information.Tag.Side;

import BoardComponents.Position;
import BoardComponents.CheckersBoard;

public abstract class CheckersPiece extends Piece {
    protected CheckersBoard board; //checkersboard needs to know if there are any attacks as player must attack if it is an option, store board and call setAvailableAttacks if legal attack is found
    public CheckersPiece(Side side, Position start, String imageFileName, CheckersBoard checkersBoard) {
        super(side, start, imageFileName);
        this.board = checkersBoard;
    }

    public void draw(Graphics g) { g.drawImage(this.getImage(), 2, -1, null); };

    public List<Position> checkForward(Position[][] gameBoard, int direction) {
        List<Position> legalPositions = new ArrayList<>();
        int y = this.getPosition().getPosY();
        int x = this.getPosition().getPosX();
        if (positionInBounds(y + direction) && positionInBounds(x + 1))
        {
            if (gameBoard[y + direction][x + 1].isFree())
                legalPositions.add(gameBoard[y + direction][x + 1]);
            else if (gameBoard[y + direction][x + 1].getPiece().getSide() != this.getSide()) //diagonal is occupied, only other option is attack if piece is same side
            {
                if (positionInBounds(y + (2 * direction)) && positionInBounds(x + 2) && gameBoard[y + (2 * direction)][x + 2].isFree()) //two squares diagonally is in bounds and free
                {
                    legalPositions.add(gameBoard[y + (2 * direction)][x + 2]);
                    board.setAvailableAttacks();
                }
            }
        }
        if (positionInBounds(y + direction) && positionInBounds(x - 1))
        {
            if (gameBoard[y + direction][x - 1].isFree())
                legalPositions.add(gameBoard[y + direction][x - 1]);
            else if (gameBoard[y + direction][x - 1].getPiece().getSide() != this.getSide()) //diagonal is occupied, only other option is attack if piece is same side
            {
                if (positionInBounds(y + (2 * direction)) && positionInBounds(x - 2) && gameBoard[y + (2 * direction)][x - 2].isFree()) //two squares diagonally is in bounds and free
                {
                    legalPositions.add(gameBoard[y + (2 * direction)][x - 2]);
                    board.setAvailableAttacks();
                }
            }
        }
        return legalPositions;
    }
    public List<Position> checkAttackForward(Position[][] gameBoard, int direction) {
        List<Position> legalAttacks = new ArrayList<>();
        int y = this.getPosition().getPosY();
        int x = this.getPosition().getPosX();
        if ((positionInBounds(y + direction) && positionInBounds(x + 1)) && !gameBoard[y + direction][x + 1].isFree() && gameBoard[y + direction][x + 1].getPiece().getSide() != this.getSide()) //forward right one square is in bounds, occupied by enemy piece
        {
            if (positionInBounds(y + (2 * direction)) && positionInBounds(x + 2) && gameBoard[y + (2 * direction)][x + 2].isFree()) //forward right 2 squares is in bounds, free
                legalAttacks.add(gameBoard[y + (2 * direction)][x + 2]);
        }
        if ((positionInBounds(y + direction) && positionInBounds(x - 1)) && !gameBoard[y + direction][x - 1].isFree() && gameBoard[y + direction][x - 1].getPiece().getSide() != this.getSide()) //forward left one square is in bounds, occupied by enemy piece
        {
            if (positionInBounds(y + (2 * direction)) && positionInBounds(x - 2) && gameBoard[y + (2 * direction)][x - 2].isFree()) //forward left 2 squares is in bounds, free
                legalAttacks.add(gameBoard[y + (2 * direction)][x - 2]);
        }
        return legalAttacks;
    }
}
