package Pieces;

import java.util.List;
import java.util.ArrayList;

import BoardComponents.Position;
import Information.Tag.Side;

public class Bishop extends ChessPiece {
    public Bishop(Side side, Position start, String imageFileName) { super(side, start, imageFileName); }

    @Override
    public List<Position> getLegalMoves(Position[][] gameBoard) {
        List<Position> bishopLegalMoves = new ArrayList<Position>();
        bishopLegalMoves.addAll(getLegalDiagonalPositions(gameBoard, this.getPosition()));
        return bishopLegalMoves;
    }

    @Override
    public String name() {
        return "(B)";
    }
}