package Information;

import java.awt.Color;

public class Tag {
    // renamed Color enum to Side to make it less confusing with 2D color array
    public static enum Side { BLACK, WHITE, RED, PAUSE, OVER }
    public static enum Status { ACTIVE, CHECK, CHECKMATE, STAKEMATE, SURRENDER }

    // choices for board/general UI colors, each index corresponds to specific spot in UI
    //in order: brown, gray, green layouts
    //dark square, light square, dark highlighted square, light highlighted square, selected color, check/checkmate color, frame color, button color, text on squares, text output on frame of board
    public static Color[][] ColorChoice = {
        {new Color(89, 32, 9), new Color(193, 142, 107), new Color(128, 56, 26), new Color(235, 189, 158), new Color(250, 147, 44), new Color(196, 0, 33), new Color(43, 29, 19), new Color(249, 184, 141), Color.GREEN, Color.WHITE},
        {new Color(96, 96, 96), new Color(160, 160, 160), new Color(64, 64, 64), new Color(224, 224, 224), new Color(250, 147, 44), new Color(196, 0, 33), new Color(32, 32, 32), new Color(175, 175, 175), new Color(34, 27, 252), Color.WHITE},
        {new Color(60, 60, 60), new Color(180, 30, 30), new Color(64, 64, 64), new Color(222, 22, 22), new Color(250, 147, 44), new Color(196, 0, 33), new Color(32, 32, 32), new Color(175, 175, 175), Color.ORANGE, Color.WHITE},
        {new Color(8, 82, 11), new Color(160, 160, 160), new Color(19, 125, 22), new Color(224, 224, 224), new Color(250, 147, 44), new Color(196, 0, 33), new Color(1, 46, 3), new Color(211, 236, 212), Color.ORANGE, Color.WHITE}};

        //{new Color(130, 176, 130), new Color(209, 230, 216), new Color(91, 146, 91), new Color(234, 240, 236), new Color(250, 147, 44), new Color(196, 0, 33), new Color(8, 82, 11), new Color(211, 236, 212), new Color(102, 40, 14), Color.WHITE}};
    // image size
    public static final int IMAGE_WIDTH = 75;
    public static final int IMAGE_HEIGHT = 75;
    
    // lazy chess icon
    public static final String LAZY_ICON = "assets\\black_king.png";
    public static final String SETTINGS_LOGO = "assets\\settings_logo.png";
    public static final String TITLE = "Voice Controlled Chess and Checkers";

    // white piece images
    public static final String WHITE_KING = "assets\\white_king.png";
    public static final String WHITE_QUEEN = "assets\\white_queen.png";
    public static final String WHITE_KNIGHT = "assets\\white_knight.png";
    public static final String WHITE_ROOK = "assets\\white_rook.png";
    public static final String WHITE_BISHOP = "assets\\white_bishop.png";
    public static final String WHITE_PAWN = "assets\\white_pawn.png";

    // black piece images
    public static final String BLACK_KING = "assets\\black_king.png";
    public static final String BLACK_QUEEN = "assets\\black_queen.png";
    public static final String BLACK_KNIGHT = "assets\\black_knight.png";
    public static final String BLACK_ROOK = "assets\\black_rook.png";
    public static final String BLACK_BISHOP = "assets\\black_bishop.png";
    public static final String BLACK_PAWN = "assets\\black_pawn.png";

    // black checkers pieces
    public static final String BLACK_CHECKERS_PAWN = "assets\\black_checkers_pawn.png";
    public static final String BLACK_CHECKERS_KING = "assets\\black_checkers_king.png";

    // red checkers pieces
    public static final String RED_CHECKERS_PAWN = "assets\\red_checkers_pawn.png";
    public static final String RED_CHECKERS_KING = "assets\\red_checkers_king.png";

    // checkers logos
    public static final String RED_CHECKERS_LOGO = "assets\\red_checkers_logo.png";
    public static final String BLACK_CHECKERS_LOGO = "assets\\black_checkers_logo.png";

    // const for board size
    public static final int SIZE_MAX = 8;
    public static final int SIZE_MIN = 0;
}