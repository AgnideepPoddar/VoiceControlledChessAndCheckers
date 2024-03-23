package Pieces;

import java.util.List;
import java.util.ArrayList;

import BoardComponents.Position;
import Information.Tag.*;

public class King extends ChessPiece {
    private boolean moved;

    public King(Side side, Position start, String imageFileName) {
        super(side, start, imageFileName);
        this.moved = false;
    }

    @Override
    public List<Position> getLegalMoves(Position[][] gameBoard) {
        List<Position> kingLegalMoves = new ArrayList<Position>();
        int[][] directions = {{1, 1}, {1, 0}, {1, -1}, {0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}}; //all 8 directions
        for (int[] shift : directions)
        {
            int x = this.getPosition().getPosX() + shift[0];
            int y = this.getPosition().getPosY() + shift[1];
            if ((positionInBounds(x) && positionInBounds(y)) && basicLegalPosition(gameBoard, y, x))
                kingLegalMoves.add(gameBoard[y][x]);
        }
        //check for castling
        if (!this.getMoved())
        {
            int y = this.getPosition().getPosY();
            if (legalCastling(gameBoard, y, 2))
                kingLegalMoves.add(gameBoard[y][2]);
            if (legalCastling(gameBoard, y, 6))
                kingLegalMoves.add(gameBoard[y][6]);
        }
        return kingLegalMoves;
    }

    @Override
    public String name() {
        return "(K)";
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
