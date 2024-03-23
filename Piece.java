package Pieces;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import BoardComponents.Position;
import Information.Tag;
import Information.Tag.Side;

public abstract class Piece {
    private boolean alive;
    private Side side;
    private Position position;
    private BufferedImage image;

    public Piece(Side side, Position start, String imageFileName) {
        setAlive();
        setSide(side);
        setPosition(start);
        setImage(imageFileName);
    }

    // setters
    public void setAlive() { this.alive = true; }
    public void setDead() { this.alive = false; }
    public void setSide(Side side) { this.side = side; }
    public void setPosition(Position position) { this.position = position; }
    
    public void setImage(String imageFileName) { 
        if (this.image == null)
            try { this.image = ImageIO.read(new File(imageFileName)); //try catch because different java versions require different paths to find and render image
            } 
            catch (IOException e) { 
                try { 
                    this.image = ImageIO.read(new File("VoiceControlChess\\" + imageFileName));
                } catch (IOException a) { a.printStackTrace(); }
        }
    }

    // getters
    public Side getSide() { return this.side; }
    public boolean isAlive() { return this.alive == true; }
    public boolean isDead() { return this.alive == false; }
    public Position getPosition() { return this.position; }
    public Image getImage() { return this.image; }
    public abstract void draw(Graphics g);

    /***
     * tries to move this piece to given position
     * @param desPosition - position this piece is moving to
     * @return - true if piece was successfully moved, false otherwise
     */
    public boolean move(Position desPosition) {
        boolean canMove = true;
        Piece desPiece = desPosition.getPiece();

        if(desPiece != null) {
            if(desPiece.getSide() == this.side) {
                canMove = false;
            } else {
                desPiece = null;
                //desPosition.removePiece();
                desPosition.setPiece(this.position.removePiece());
            }
        } else {
            desPosition.setPiece(this.position.removePiece());
        }
        if (canMove)
            this.setMoved();
        return canMove;
    }

    /***
     * ensures that value is within range of board (0 to 7)
     * @param value - value being checking
     * @return - returns true if value is within board bounds, false otherwise
     */
    public boolean positionInBounds(int value) {
        return (value >= Tag.SIZE_MIN && value < Tag.SIZE_MAX);
    }

    /**
     * abstract methods to return all legal moves from current position of piece
     * @param gameBoard - board that pieces are on
     * @return - all legal moves from current postion on board
     */
    public abstract List<Position> getLegalMoves(Position[][] gameBoard);

    public String name() { 
        return "(_)";
    }

    //will be overridden in king, rook, and pawn
    public boolean getMoved() {
        return true;
    }

    public void setMoved() {

    }
}