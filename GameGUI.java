package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.BorderFactory;
import javax.swing.AbstractAction;

import BoardComponents.Board;

import Information.Tag;
import Information.Tag.Side;

import SpeechRecognizer.SpeechRecognizerMain;

public abstract class GameGUI {
    protected static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW; //used for key bindings
    protected int colorSet;
    protected String playerOneName;
    protected String playerTwoName;
    protected JTextArea speechOutput;
    protected JTextArea currentTurn;
    protected JFrame gameGUI;
    protected toggleDisplay speechToggle;
    protected Board boardGUI;
    protected MainGUI main;
    protected SpeechRecognizerMain speech;

    public GameGUI(MainGUI main, SpeechRecognizerMain speech, String playerOne, String playerTwo, int colorSet) { 
        this.main = main;
        this.speech = speech;
        this.colorSet = colorSet;
        this.playerOneName = playerOne;
        this.playerTwoName = playerTwo;
        initializeGameGUI();
        speech.updateGame(boardGUI);
        addKeyBindings();
    }

    public GameGUI(MainGUI main, String[] pieces, SpeechRecognizerMain speech, String playerOne, String playerTwo, int colorSet)
    {
        this.main = main;
        this.speech = speech;
        this.colorSet = colorSet;
        this.playerOneName = playerOne;
        this.playerTwoName = playerTwo;
        initializeGameGUI(pieces);
        speech.updateGame(boardGUI);
        addKeyBindings();
    }
    
    /***
     * creates new game with GUI from scratch
     */
    protected abstract void initializeGameGUI();

    /***
     * creates game from previous save
     */
    protected abstract void initializeGameGUI(String[] pieces);

    /***
     * creates JFrame for this GameGUI
     */
    protected abstract void createFrame();

    protected void createBoardGUIFrame() {
        int borderPanelSize = 30; //width of panels around board
        JPanel boardPanel = new JPanel(new BorderLayout(0, 0));
        //create panels to create "frame" around board
        JPanel top = new JPanel();
        JPanel left = new JPanel();
        JPanel right = new JPanel();
        JPanel bottom = new JPanel();
        top.setBackground(Tag.ColorChoice[colorSet][6]);
        left.setBackground(Tag.ColorChoice[colorSet][6]);
        right.setBackground(Tag.ColorChoice[colorSet][6]);
        bottom.setBackground(Tag.ColorChoice[colorSet][6]);
        //preferred size will keep borderPanelSize as width or length and change the other to match boardGUI size
        top.setPreferredSize(new Dimension(borderPanelSize, borderPanelSize));
        left.setPreferredSize(new Dimension(borderPanelSize, borderPanelSize));
        right.setPreferredSize(new Dimension(borderPanelSize, borderPanelSize));
        bottom.setPreferredSize(new Dimension(borderPanelSize, borderPanelSize + 4));

        //add text output on bottom and top
        this.currentTurn = new JTextArea("");
        this.currentTurn.setEnabled(false); //prevents user from interacting with/changing text in JTextArea
        currentTurn.setFont(new Font("Monospaced", Font.BOLD, 20));
        currentTurn.setBackground(Tag.ColorChoice[colorSet][6]);
        currentTurn.setDisabledTextColor(Tag.ColorChoice[colorSet][9]); //disabling jtextarea also changes color to default gray so a new command is needed (not setForeground)
        JPanel bottomCenter = new JPanel();
        bottomCenter.setBackground(Tag.ColorChoice[colorSet][6]);
        bottomCenter.add(currentTurn);

        //add panels from left to right
        this.speechToggle = new toggleDisplay(true);
        JPanel bottomRight = new toggleDisplay(false);
        bottom.setLayout(new GridBagLayout());
        GridBagConstraints manager = new GridBagConstraints();
        manager.gridx = 0;
        manager.gridy = 0;
        manager.ipadx = 10;
        manager.weightx = 0.28f;
        manager.anchor = GridBagConstraints.WEST;
        bottom.add(speechToggle, manager);
        
        manager.gridx = 2;
        manager.gridy = 0;
        manager.ipadx = 10;
        manager.weightx = 0.28f;
        manager.anchor = GridBagConstraints.EAST;
        bottom.add(bottomRight, manager);
        
        manager.gridx = 1;
        manager.gridy = 0;
        manager.weightx = 0.44f;
        bottomCenter.setMinimumSize(new Dimension(440, borderPanelSize));
        manager.anchor = GridBagConstraints.CENTER;
        bottom.add(bottomCenter, manager);

        //create speech output at the top of the screen
        this.speechOutput = new JTextArea();
        this.speechOutput.setEnabled(false);
        speechOutput.setFont(new Font("Monospaced", Font.BOLD, 20));
        speechOutput.setBackground(Tag.ColorChoice[colorSet][6]);
        speechOutput.setDisabledTextColor(Tag.ColorChoice[colorSet][9]);
        top.add(speechOutput, BorderLayout.NORTH);

        //add all panels and board in corresponding spots
        boardPanel.add(top, BorderLayout.NORTH);
        boardPanel.add(left, BorderLayout.WEST);
        boardPanel.add(right, BorderLayout.EAST);
        boardPanel.add(bottom, BorderLayout.SOUTH);
        boardPanel.add(boardGUI, BorderLayout.CENTER);
        this.gameGUI.add(boardPanel, BorderLayout.CENTER);
    }
    
    protected void setSize() {
        this.gameGUI.setSize(gameGUI.getPreferredSize());
        this.gameGUI.setMinimumSize(gameGUI.getPreferredSize());
        this.gameGUI.setLocationRelativeTo(null);
        this.gameGUI.setVisible(true);
        this.gameGUI.setResizable(false);
    }

    public class toggleDisplay extends JPanel {
        private boolean on;
        private JPanel toggleLight;
        private JTextArea topToggleText;
        private JTextArea bottomToggleText;
        /***
         * creates toggle display for speech recognizer, bottom left corner is visible so colors are distinct, bottom right corner blends in with board and acts as spacer in gridbaglayout
         * @param visible - true if visible (bottom left), false if blending in (bottom right)
         */
        public toggleDisplay(boolean visible) {
            createToggleDisplay(visible);
            on = false;
        }
        /***
         * creates various components of toggleDisplay, sets them to distinct colors or same as board background depending on visible
         * @param visible - true if board should stand out, false if board should blend in with background
         */
        public void createToggleDisplay(boolean visible) {
            Color boardBackground = Tag.ColorChoice[colorSet][6];
            this.setBackground(boardBackground);
            this.setLayout(new FlowLayout(FlowLayout.LEFT));
            //red or green light showing if toggle speech is on
            this.toggleLight = new JPanel();
            toggleLight.setPreferredSize(new Dimension(20, 20));
            toggleLight.setBackground(visible ? Tag.ColorChoice[1][5] : boardBackground);
            toggleLight.setBorder(BorderFactory.createLineBorder(visible ? Tag.ColorChoice[1][6] : boardBackground));
            this.add(toggleLight);
            //text next to toggleLight, split in two rows for space
            this.topToggleText = new JTextArea("Press 'T' to");
            topToggleText.setEnabled(false);
            topToggleText.setFont(new Font("Monospaced", Font.PLAIN, 10));
            this.bottomToggleText  = new JTextArea("toggle speech on ");
            bottomToggleText.setEnabled(false);
            bottomToggleText.setFont(new Font("Monospaced", Font.PLAIN, 10));
            //JPanel with gridlayout to order top and bottom text
            JPanel toggleTextWrapper = new JPanel();
            toggleTextWrapper.setLayout(new GridLayout(2, 1, 0, 0));
            toggleTextWrapper.setBackground(boardBackground);
            topToggleText.setBackground(boardBackground);
            topToggleText.setDisabledTextColor(visible ? Tag.ColorChoice[colorSet][9] : boardBackground);
            bottomToggleText.setBackground(boardBackground);
            bottomToggleText.setDisabledTextColor(visible ? Tag.ColorChoice[colorSet][9] : boardBackground);
            toggleTextWrapper.add(topToggleText);
            toggleTextWrapper.add(bottomToggleText);
            this.add(toggleTextWrapper);
        }
        /***
         * toggles switch and then updates light and text
         */
        public void toggleSwitch() {
            speech.toggleIgnoreSpeechRecognitionResults();
            on = !on;
            updateLight();
            updateText();
        }
        public void turnOffToggle() {
            on = false;
            speech.disableToggle();
            updateLight();
            updateText();
        }
        private void updateLight() {
            toggleLight.setBackground(on ? Color.GREEN : Tag.ColorChoice[1][5]);
        }
        private void updateText() {
            String updatedText = "toggle speech " + ((on) ? "off" : "on "); //space after on string to keep spacing consistent when toggleSwitch is called
            bottomToggleText.replaceRange(updatedText, 0, bottomToggleText.getText().length());
            if (!on)
                clearSpeechOutput(); //no need to leave speech output up if speech recognizer is disabled
        }
    }

    protected void addButtons() {
        JPanel buttons = new JPanel();
        buttons.setBackground(Tag.ColorChoice[colorSet][6]);
        buttons.setLayout(new GridLayout(1, 4, 10, 10));

        final JButton speak = new JButton("Speak");
        final JButton save = new JButton ("Save");
        final JButton mainMenu = new JButton("Main Menu");
        final JButton quite = new JButton("Quit");
        
        speak.setBackground(Tag.ColorChoice[colorSet][7]);
        save.setBackground(Tag.ColorChoice[colorSet][7]);
        quite.setBackground(Tag.ColorChoice[colorSet][7]);
        mainMenu.setBackground(Tag.ColorChoice[colorSet][7]);
        
        speak.addActionListener((e) -> speakItemActionPerformed(e));
        save.addActionListener((e) -> saveItemActionPerformed(e));
        quite.addActionListener((e) -> quitItemActionPerformed(e));
        mainMenu.addActionListener((e) ->  mainMenuItemActionPerformed(e));
        
        buttons.add(speak);
        buttons.add(save);
        buttons.add(mainMenu);
        buttons.add(quite);
        gameGUI.add(buttons, BorderLayout.BEFORE_FIRST_LINE);
    }

    protected void addKeyBindings() {
        this.boardGUI.getInputMap(IFW).put(KeyStroke.getKeyStroke("T"), "toggle");
        this.boardGUI.getActionMap().put("toggle", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                speechToggle.toggleSwitch();
            }
        });
        this.boardGUI.getInputMap(IFW).put(KeyStroke.getKeyStroke(" "), "speak");
        this.boardGUI.getActionMap().put("speak", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                speakItemActionPerformed(e);
            }
        });
    }

    protected void speakItemActionPerformed(ActionEvent e) {
        try
        {
            Thread.sleep(400); //without delay, mic registers mouse click as command
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        speech.stopIgnoreSpeechRecognitionResults();
    }

    protected abstract void saveItemActionPerformed(ActionEvent e);
    
    protected void mainMenuItemActionPerformed(ActionEvent e) {
        String message = "Are you sure you want to return to the main menu?";
        if (!boardGUI.getSaved() && boardGUI.getTurn() != Side.OVER)
            message += "\nThis game has not been saved.";
        int quit = JOptionPane.showConfirmDialog(gameGUI, message, "Main Menu", JOptionPane.OK_CANCEL_OPTION);
        if(quit == JOptionPane.OK_OPTION) {
            boardGUI.dispose();
            gameGUI.dispose();
            speechToggle.turnOffToggle(); //make sure voice recognizer is reset to toggle off before another board is created
            main.mainMenu();
        }
    }
    
    protected void quitItemActionPerformed(ActionEvent e) {
        String message = "Are you sure you want to quit?";
        if (!boardGUI.getSaved() && boardGUI.getTurn() != Side.OVER)
            message += "\nThis game has not been saved.";
        int quit = JOptionPane.showConfirmDialog(gameGUI, message, "Quit", JOptionPane.OK_CANCEL_OPTION);
        if(quit == JOptionPane.OK_OPTION) 
        {
            boardGUI.dispose();
            gameGUI.dispose();
            main.exit();
        }
    }

    /***
     * used to get corresponding name for side, board only needs to know player name when creating promotion so it calls this method instead of taking both names as additional constructor parameters and storing them
     * @param side - side that board needs name for
     * @return - name that corresponds to side
     */
    public String getTurnPlayerName(Side side)
    {
        return (side == Side.WHITE ? playerOneName : playerTwoName);
    }

    /***
     * called by board, displays what speech recognizer heard
     * @param speech
     */
    public void updateSpeechOutput(String speech)
    {
        if (boardGUI.getTurn() != Side.PAUSE && boardGUI.getTurn() != Side.OVER) //don't update speech output during pause (promotion) or after game is over
        {
            String replace;
            if (speech.equals("<unk>"))
                replace = "Sorry, I did not understand what you said, please try again";
            else
                replace = "I heard: " + speech;
            speechOutput.replaceRange(replace, 0, speechOutput.getText().length());
        }
    }

    /***
     * this updates the top of the board and adds the reason that a given move is invalid
     * @param invalid - reason that move is invalid, such as not in piece's moveset or attacking friendly piece
     */
    public void updateInvalidMove(String invalid)
    {
        if (boardGUI.getTurn() != Side.PAUSE && boardGUI.getTurn() != Side.OVER) //don't update speech output during pause (promotion) or after game is over
        {
            if (speechOutput.getText().length() == 0)
                speechOutput.append(invalid);
            else
                speechOutput.append(" (" + invalid + ")"); 
        }
    }

    /***
     * called after move is made and there is no need to display previous speech output
     */
    public void clearSpeechOutput()
    {
        if (boardGUI.getTurn() != Side.OVER) //leave win condition in speech output box even if player clears/right clicks
        {
            if (speechOutput.getText().length() != 0)
                speechOutput.replaceRange("", 0, speechOutput.getText().length());
        }
    }

    /***
     * updates current turn displayed at the bottom of the screen
     * @param side - current turn
     */
    public abstract void updateCurrentTurn(Side side);

    /***
     * updates special status for turns
     */
    public void updateTurnStatus(String status) {
        currentTurn.append(status);
        if (currentTurn.getText().length() > 35) //prevent text from expanding beyond textbox range
            currentTurn.replaceRange(getTurnPlayerName(boardGUI.getTurn()) + status, 0, currentTurn.getText().length()); //player name + status, same as normal without "Current turn: " in front
    }

    /***
     * call when game has ended and the GUI will display winner
     * @param side - winning side
     */
    public abstract void updateGameOver(Side side, String winCondition);
}