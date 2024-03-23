package Pieces;

import java.util.List;
import java.util.ArrayList;

import BoardComponents.Position;
import Information.Tag.Side;

public class Knight extends ChessPiece {
    public Knight(Side side, Position start, String imageFileName) { super(side, start, imageFileName); }

    @Override
    public List<Position> getLegalMoves(Position[][] gameBoard) {
        List<Position> knightLegalMoves = new ArrayList<Position>();
        int[][] moves = {{1, 2}, {1, -2}, {2, 1}, {2, -1}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}};
        for (int[] shift : moves)
        {
            //int x and y are based on original position and shifted in directions a knight can move
            int x = this.getPosition().getPosX() + shift[0];
            int y = this.getPosition().getPosY() + shift[1];
            if (positionInBounds(x) && positionInBounds(y))
            {
                if (basicLegalPosition(gameBoard, y, x))
                    knightLegalMoves.add(gameBoard[y][x]);
            }
        }
        return knightLegalMoves;
    }

    @Override
    public String name() {
        return "(N)";
    }
}
