package GUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import java.io.FileWriter;

import Information.Tag;
import Information.Tag.Side;

import BoardComponents.CheckersBoard;

import SpeechRecognizer.SpeechRecognizerMain;

public class CheckersGameGUI extends GameGUI {
    public CheckersGameGUI(MainGUI main, SpeechRecognizerMain speech, String playerOne, String playerTwo, int colorSet) {
        super(main, speech, playerOne, playerTwo, colorSet);
    }

    public CheckersGameGUI(MainGUI main, String[] pieces, SpeechRecognizerMain speech, String playerOne, String playerTwo, int colorSet)
    {
        super(main, pieces, speech, playerOne, playerTwo, colorSet);
    }

    protected void initializeGameGUI() {
        createFrame();
        addButtons();
        this.boardGUI = new CheckersBoard(this, colorSet);
        createBoardGUIFrame();
        this.boardGUI.updateBoardGUI();
        setSize();
        this.gameGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    protected void initializeGameGUI(String[] pieces) {
        createFrame();
        addButtons();
        this.boardGUI = new CheckersBoard(this, pieces);
        createBoardGUIFrame();
        this.boardGUI.updateBoardGUI();
        setSize();
        this.gameGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    protected void createFrame() {
        gameGUI = new JFrame("Voice Controlled Checkers");
        gameGUI.setIconImage(new ImageIcon(Tag.RED_CHECKERS_LOGO).getImage());
        this.gameGUI.setLayout(new BorderLayout(0, 0));
        this.gameGUI.getContentPane().setBackground(Tag.ColorChoice[colorSet][6]);
    }

    protected void saveItemActionPerformed(ActionEvent e) {
        if (!boardGUI.canSave())
        {
            if (boardGUI.getTurn() == Side.OVER)
                speechOutput.replaceRange("Can not save a finished game", 0, speechOutput.getText().length());
            else
                speechOutput.replaceRange("Please finish the current move before saving", 0, speechOutput.getText().length());
        }
        else
        {
            try {
                FileWriter writer = new FileWriter("./savedgames/Checkers.txt", false);
                writer.write(playerOneName + " " + playerTwoName + " " + boardGUI.asString());
                writer.close();
                boardGUI.setSaved();
                speechOutput.replaceRange("Saved", 0, speechOutput.getText().length());
            }
            catch (Exception error) {
                error.getStackTrace();
            }
        }
    }

    public void updateCurrentTurn(Side side)
    {
        String replace = "Current turn: ";
        if (side == Side.BLACK)
            replace += playerOneName;
        else //red
            replace += playerTwoName;
        currentTurn.replaceRange(replace, 0, currentTurn.getText().length());
    }

    public void updateGameOver(Side side, String winCondition) {
        speechOutput.replaceRange(winCondition, 0, speechOutput.getText().length());
        String playerName = getTurnPlayerName(side);
            currentTurn.replaceRange("Winner: " + playerName, 0, currentTurn.getText().length());
    }
}