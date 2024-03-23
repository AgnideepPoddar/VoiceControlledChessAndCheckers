package Pieces;

import java.util.List;
import java.util.ArrayList;

import BoardComponents.Position;
import Information.Tag.Side;

public class Pawn extends ChessPiece {
    private int up;
    private boolean moved;
    
    public Pawn(Side side, Position start, String imageFileName) {
        super(side, start, imageFileName);
        moved = false;
        if(this.getSide() == Side.BLACK) this.up = 1; //black moves down board (in increasing y direction)
        else this.up = -1;
    }

    @Override
    public List<Position> getLegalMoves(Position[][] gameBoard) {
        List<Position> pawnLegalMoves = new ArrayList<Position>();
        int startX = this.getPosition().getPosX();
        int startY = this.getPosition().getPosY();
        //check forward squares, make sure they are empty
        if (gameBoard[startY + this.up][startX].isFree()) //this.up is plus or minus one for black or white respectively, look one square forward, can only move not attack forward so make sure square is free
            pawnLegalMoves.add(gameBoard[startY + this.up][startX]);
        if (!getMoved() && (gameBoard[startY + this.up][startX].isFree() && gameBoard[startY + (2 * this.up)][startX].isFree())) //check 2 spaces forward for unmoved pawn, make sure both squares in front are empty
            pawnLegalMoves.add(gameBoard[startY + (2 * this.up)][startX]);
        //check forward diagonals, make sure they are taken by enemy piece, y is always in bounds because when pawn reaches end it promotes to another piece but x +/- 1 can be outside the board so make sure x is in bounds
        if (positionInBounds(startX + 1) && (complexLegalPostion(gameBoard, startY + this.up, startX + 1) || legalEnPassant(gameBoard, startY + this.up, startX + 1))) //forward and right one
            pawnLegalMoves.add(gameBoard[startY + this.up][startX + 1]);
        if (positionInBounds(startX - 1) && (complexLegalPostion(gameBoard, startY + this.up, startX - 1) || legalEnPassant(gameBoard, startY + this.up, startX - 1))) //forward and left one
            pawnLegalMoves.add(gameBoard[startY + this.up][startX - 1]);
        return pawnLegalMoves;
    }

    @Override
    public String name() {
        return "(P)";
    }

    @Override
    public boolean getMoved() {
        return this.moved;
    }

    @Override
    public void setMoved() {
        this.moved = true;
    }
}