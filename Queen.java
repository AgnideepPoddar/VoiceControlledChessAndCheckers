package Pieces;

import java.util.List;
import java.util.ArrayList;

import BoardComponents.Position;
import Information.Tag.Side;

public class Queen extends ChessPiece {
    public Queen(Side side, Position start, String imageFileName) { 
        super(side, start, imageFileName);
    }

    @Override
    public List<Position> getLegalMoves(Position[][] gameBoard) {
        List<Position> queenLegalMoves = new ArrayList<Position>();
        queenLegalMoves.addAll(getLegalLinearPositions(gameBoard, this.getPosition()));
        queenLegalMoves.addAll(getLegalDiagonalPositions(gameBoard, this.getPosition()));
        return queenLegalMoves;
    }
    
    @Override
    public String name() { 
        return "(Q)"; 
    }
}