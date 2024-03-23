package Pieces;

import java.util.List;
import java.util.ArrayList;

import Information.Tag.Side;

import BoardComponents.Position;
import BoardComponents.CheckersBoard;

public class CheckersKing extends CheckersPiece {
    public CheckersKing(Side side, Position start, String imageFileName, CheckersBoard checkersBoard) {
        super(side, start, imageFileName, checkersBoard);
    }

    @Override
    public List<Position> getLegalMoves(Position[][] gameBoard) {
        if (board.getAvailableAttacks())
            return getLegalAttacks(gameBoard);
        else
        {
            List<Position> legalMoves = new ArrayList<Position>();
            legalMoves.addAll(checkForward(gameBoard, -1));
            legalMoves.addAll(checkForward(gameBoard, 1));
            return legalMoves;
        }
    }

    public List<Position> getLegalAttacks(Position[][] gameBoard) {
        List<Position> legalAttacks = new ArrayList<Position>();
        legalAttacks.addAll(checkAttackForward(gameBoard, -1));
        legalAttacks.addAll(checkAttackForward(gameBoard, 1));
        return legalAttacks;
    }

    @Override
    public String name() {
        return "(K)";
    }
}
