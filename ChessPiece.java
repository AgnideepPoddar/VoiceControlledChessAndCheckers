package Pieces;

import java.util.List;
import java.util.ArrayList;

import java.awt.Graphics;

import Information.Tag;
import Information.Tag.Side;

import BoardComponents.Position;

public abstract class ChessPiece extends Piece {
    public ChessPiece(Side side, Position start, String imageFileName) {
        super(side, start, imageFileName);
    }

    public void draw(Graphics g) { g.drawImage(this.getImage(), 4, 0, null); };

    /***
     * checks if position is either open or occupied by enemy piece, called by king and knight
     * @param gameBoard - gameBoard that pieces are on
     * @param y - y index being checked
     * @param x - x index being checked
     * @return - returns true if this piece can move to gameBoard[y][x], false otherwise
     */
    public boolean basicLegalPosition(Position[][] gameBoard, int y, int x) {
        return (gameBoard[y][x].isFree() || gameBoard[y][x].getPiece().getSide() != this.getSide());
    }

    /***
     * checks if position is occupied by enemy piece, used by pawn to check diagonals as they can only move diagonally by attacking
     * @param gameBoard - gameBoard that pieces are on
     * @param y - y index being checked
     * @param x - x index being checked
     * @return - returns true if pawn can attack gameBoard[y][x], false otherwise
     */
    public boolean complexLegalPostion(Position[][] gameBoard, int y, int x) {
        return (!gameBoard[y][x].isFree() && gameBoard[y][x].getPiece().getSide() != this.getSide());
    }

    //spot can be taken by en passant, called by pawn
    public boolean legalEnPassant(Position[][] gameBoard, int y, int x)
    {
        return gameBoard[y][x].getEnPassant();
    }

    //called by king, checks if it can castle and move to specific x index (either 2 or 6, left or right)
    /***
     * called by king, checks if king can castle and move to specific index (either x == 2 or 6, left or right)
     * @param gameBoard - gameBoard that pieces are on
     * @param y - y position of king
     * @param x - x position king is attempting to castle to
     * @return - return true if king can move to gameBoard[y][x] by castling, false otherwise
     */
    public boolean legalCastling(Position[][] gameBoard, int y, int x)
    {
        if (!this.getMoved())
        {
            if (x == 2)
            {
                if (gameBoard[y][3].isFree() && gameBoard[y][2].isFree() && gameBoard[y][1].isFree() && (!gameBoard[y][0].isFree() && gameBoard[y][0].getPiece().getSide() == this.getSide() && !gameBoard[y][0].getPiece().getMoved()))
                    return true;
                else
                    return false;
            }
            else //x == 6
            {
                if (gameBoard[y][5].isFree() && gameBoard[y][6].isFree() && (!gameBoard[y][7].isFree() && gameBoard[y][7].getPiece().getSide() == this.getSide() && !gameBoard[y][7].getPiece().getMoved()))
                    return true;
                else
                    return false;
            }
        }
        return false;
    }

    /***
     * this method gets all legal position linear from start position
     * @param gameBoard - board to check
     * @param start - starting position to get legal moves from
     * @return all legal positions north, south, east, and west from start
     */
    public List<Position> getLegalLinearPositions(Position[][] gameBoard, Position start) {
        List<Position> linearPositions = new ArrayList<Position>();
        int[][] lines = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}}; //left, right, up, down
        for (int[] shift : lines)
        {
            //similar to can be taken logic in board, start with shifted values to avoid checking start position
            int x = start.getPosX() + shift[0];
            int y = start.getPosY() + shift[1];
            while (x > -1 && x < Tag.SIZE_MAX && y > -1 && y < Tag.SIZE_MAX) //check all bounds since x or y could be increasing or decreasing
            {
                if (gameBoard[y][x].isFree()) //empty square
                    linearPositions.add(gameBoard[y][x]);
                else //taken
                {
                    if (gameBoard[y][x].getPiece().getSide() != this.getSide())
                        linearPositions.add(gameBoard[y][x]);
                    break; //can not move past piece, can either take enemy piece or not take friendly piece, break either way
                }
                x += shift[0];
                y += shift[1];
            }
        }
        return linearPositions;
    }

   /***
     * Method that gets all legal diagonal positions from start position
     * @param gameBoard - board to check
     * @param start - starting position to get legal moves from
     * @return all legal positions north east, north west, south east, and south west from start
     */
    public List<Position> getLegalDiagonalPositions(Position[][] gameBoard, Position start) {
        List<Position> diagonalPositions = new ArrayList<Position>();
        int[][] diagonals = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}; //top left, bottom left, bottom right, top right
        for (int[] shift : diagonals)
        {
            //similar to can be taken logic in board, start with shifted values to avoid checking start position
            int x = start.getPosX() + shift[0];
            int y = start.getPosY() + shift[1];
            while (x > -1 && x < Tag.SIZE_MAX && y > -1 && y < Tag.SIZE_MAX)
            {
                if (gameBoard[y][x].isFree())
                    diagonalPositions.add(gameBoard[y][x]);
                else
                {
                    if (gameBoard[y][x].getPiece().getSide() != this.getSide())
                        diagonalPositions.add(gameBoard[y][x]);
                    break;
                }
                x += shift[0];
                y += shift[1];
            }
        }
        return diagonalPositions;
    }
}
