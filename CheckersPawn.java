package Pieces;

import java.util.List;
import java.util.ArrayList;

import Information.Tag.Side;

import BoardComponents.Position;
import BoardComponents.CheckersBoard;

public class CheckersPawn extends CheckersPiece {
    private int forward;

    public CheckersPawn(Side side, Position start, String imageFileName, CheckersBoard checkersBoard) {
        super(side, start, imageFileName, checkersBoard);
        forward = (side == Side.BLACK) ? -1 : 1; //black starts at bottom of board so y is decreasing when black moves forward
    }

    @Override
    public List<Position> getLegalMoves(Position[][] gameBoard) {
        if (board.getAvailableAttacks())
            return getLegalAttacks(gameBoard);
        else
        {
            List<Position> legalMoves = new ArrayList<Position>();
            legalMoves.addAll(checkForward(gameBoard, forward));
            return legalMoves;
        }
    }

    public List<Position> getLegalAttacks(Position[][] gameBoard) {
        List<Position> legalAttacks = new ArrayList<Position>();
        legalAttacks.addAll(checkAttackForward(gameBoard, forward));
        return legalAttacks;
    }

    @Override
    public String name() {
        return "(P)";
    }
}