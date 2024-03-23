package Pieces;

import java.util.List;
import java.util.ArrayList;

import BoardComponents.Position;
import Information.Tag.Side;

public class Rook extends ChessPiece {
    private boolean moved;
    
    public Rook(Side side, Position start, String imageFileName) {
        super(side, start, imageFileName);
        this.moved = false;
    }

    @Override
    public List<Position> getLegalMoves(Position[][] gameBoard) {
        List<Position> rookLegalMoves = new ArrayList<Position>();
        rookLegalMoves.addAll(getLegalLinearPositions(gameBoard, this.getPosition()));
        return rookLegalMoves;
    }

    @Override
    public String name() { 
        return "(R)"; 
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