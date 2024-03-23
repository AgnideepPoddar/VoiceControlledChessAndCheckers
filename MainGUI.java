package GUI;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Information.Tag;
import BoardComponents.Position;
import SpeechRecognizer.SpeechRecognizerMain;

public class MainGUI implements Runnable {
    private SpeechRecognizerMain speech = new SpeechRecognizerMain();
    private static final int COLUMN_SPACE = 10;
    private int colorSet;

    private JFrame mainGUI;
    private JPanel gameTitlePanel;
    private JPanel playerPanel;
    private JPanel blackPlayerPanel;
    private JPanel whitePlayerPanel;
    private JPanel buttons;
    private JTextField blackPlayerTextField;
    private JTextField whitePlayerTextField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MainGUI());
    }
    
    public void run() {
        assignColorSet();
        initializeMainMenu();
        mainGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainGUI.setVisible(true);
    }

    private void assignColorSet() {
        try {
            File saveFile = new File("./savedgames/Settings.txt");
            Scanner myReader = new Scanner(saveFile);
            String savedColorSet = myReader.nextLine();
            colorSet = Integer.valueOf(savedColorSet);
            myReader.close();
        
      } catch (FileNotFoundException error) {
        System.out.println("No settings found");
        error.printStackTrace();
      }
    }

    private void initializeMainMenu() {
        createFrame();
        addGameTitle();
        addPlayerFields();
        addButtons();
    }

    /***
     * 
     */
    private void createFrame() {
        mainGUI = new JFrame(Tag.TITLE);
        mainGUI.setIconImage(new ImageIcon(Tag.LAZY_ICON).getImage());
        mainGUI.setSize(Tag.IMAGE_WIDTH * 8, Tag.IMAGE_HEIGHT * 8);
        mainGUI.setResizable(false);
        mainGUI.setBackground(Tag.ColorChoice[1][6]);
        mainGUI.setLocationRelativeTo(null);
    }

    /***
     * creates and formats text at the top of the frame
     */
    private void addGameTitle() {
        gameTitlePanel = new JPanel();
        gameTitlePanel.setLayout(new GridLayout(3, 1, 0, 0));
        JLabel top = new JLabel("Voice Controlled Chess", JLabel.CENTER);
        JLabel bottom = new JLabel("and Checkers", JLabel.CENTER);
        top.setHorizontalAlignment(JTextField.CENTER);
        bottom.setHorizontalAlignment(JTextField.CENTER);
        top.setFont(new Font("Monospaced", Font.BOLD, 35));
        top.setForeground(Tag.ColorChoice[1][9]);
        top.setBackground(Tag.ColorChoice[1][6]);
        bottom.setFont(new Font("Monospaced", Font.BOLD, 35));
        bottom.setForeground(Tag.ColorChoice[1][9]);
        bottom.setBackground(Tag.ColorChoice[1][6]);
        gameTitlePanel.setBackground(Tag.ColorChoice[1][6]);
        gameTitlePanel.add(top);
        gameTitlePanel.add(bottom);
        JLabel bottomSpacer = new JLabel();
        bottomSpacer.setBackground(Tag.ColorChoice[1][6]);
        gameTitlePanel.add(bottomSpacer);
        mainGUI.add(gameTitlePanel, BorderLayout.NORTH);
        gameTitlePanel.setPreferredSize(new Dimension(600, 200));
    }

    /***
     * initializes player fields, middle of the frame, includes play and load buttons, chess and checkers logos, and playertextfields
     */
    private void addPlayerFields() {
        JLabel whiteChessIcon = new JLabel(new ImageIcon((Tag.WHITE_KING)));
        JLabel blackChessIcon = new JLabel(new ImageIcon((Tag.BLACK_KING)));
        JLabel blackCheckersIcon = new JLabel(new ImageIcon((Tag.BLACK_CHECKERS_LOGO)));
        JLabel redCheckersIcon = new JLabel(new ImageIcon((Tag.RED_CHECKERS_LOGO)));
        JButton playChess = new JButton("Play");
        JButton playCheckers = new JButton("Play");
        JButton loadChess = new JButton("Load");
        JButton loadCheckers = new JButton("Load");
        playChess.addActionListener(e -> playChessItemActionPerformed(e));
        playCheckers.addActionListener(e -> playCheckersItemActionPerformed(e));
        loadChess.addActionListener(e -> loadChessItemActionPerformed(e));
        loadCheckers.addActionListener(e -> loadCheckersItemActionPerformed(e));
        // create new panel for player one
        whitePlayerPanel = new JPanel();
        whitePlayerPanel.add(playChess);
        whitePlayerPanel.add(whiteChessIcon);
        whitePlayerPanel.setBackground(Tag.ColorChoice[1][6]);
        // create new panel for player two
        blackPlayerPanel = new JPanel();
        blackPlayerPanel.add(loadChess);
        blackPlayerPanel.add(blackChessIcon);
        blackPlayerPanel.setBackground(Tag.ColorChoice[1][6]);
        addPlayerTextField();
        whitePlayerPanel.add(blackCheckersIcon);
        blackPlayerPanel.add(redCheckersIcon);
        whitePlayerPanel.add(playCheckers);
        blackPlayerPanel.add(loadCheckers);
        //create panel that holds both player panels
        playerPanel = new JPanel();
        playerPanel.setBackground(Tag.ColorChoice[1][6]);
        //third, empty panel to leave more space between player panel and buttons
        JPanel buttonSpacer = new JPanel();
        buttonSpacer.setBackground(Tag.ColorChoice[1][6]);
        playerPanel.setLayout(new GridLayout(3, 1, 0, 0));
        playerPanel.add(whitePlayerPanel);
        playerPanel.add(blackPlayerPanel);
        playerPanel.add(buttonSpacer);
        //add panel holding both to frame
        mainGUI.add(playerPanel, BorderLayout.CENTER);
    }

    /***
     * adds player text boxes where players can enter their names
     */
    private void addPlayerTextField() {
        blackPlayerTextField = new JTextField();
        whitePlayerTextField = new JTextField();
        blackPlayerPanel.add(blackPlayerTextField);
        whitePlayerPanel.add(whitePlayerTextField);
        blackPlayerTextField.setToolTipText("Enter Player 2 Name Here");
        whitePlayerTextField.setToolTipText("Enter Player 1 Name Here");
        blackPlayerTextField.setColumns(COLUMN_SPACE);
        whitePlayerTextField.setColumns(COLUMN_SPACE);
    }

    /***
     * adds settings, help, and quit buttons at the bottom of the screen
     */
    private void addButtons() {
        buttons = new JPanel();
        buttons.setBackground(Tag.ColorChoice[1][6]);
        buttons.setLayout(new GridLayout(1, 3, 45, 10));
        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setBackground(Tag.ColorChoice[1][6]);
        buttonWrapper.setPreferredSize(new Dimension(600, 120));
        final JButton settings = new JButton("Settings");
        final JButton help = new JButton("Help");
        final JButton quit = new JButton("Quit");
        settings.addActionListener(e -> settingsItemActionPerformed(e));
        help.addActionListener(e -> helpItemActionPerformed(e));
        quit.addActionListener(e -> quitItemActionPerformed(e));
        buttons.add(settings);
        buttons.add(help);
        buttons.add(quit);
        buttonWrapper.add(buttons);
        mainGUI.add(buttonWrapper, BorderLayout.SOUTH);
    }

    /***
     * called by GameGUI if user returns to main menu, used to redisplay MainGUI
     */
    public void mainMenu()
    {
        mainGUI.setVisible(true);
    }

    private void playChessItemActionPerformed(ActionEvent e) {
        String playerOne, playerTwo;
        if (whitePlayerTextField.getText().length() == 0)
            playerOne = "white";
        else
            playerOne = whitePlayerTextField.getText();
        if (blackPlayerTextField.getText().length() == 0)
            playerTwo = "black";
        else
            playerTwo = blackPlayerTextField.getText();
        new ChessGameGUI(this, speech, playerOne, playerTwo, colorSet);
        mainGUI.setVisible(false);
    }

    private void playCheckersItemActionPerformed(ActionEvent e) {
        String playerOne, playerTwo;
        if (whitePlayerTextField.getText().length() == 0)
            playerOne = "black";
        else
            playerOne = whitePlayerTextField.getText();
        if (blackPlayerTextField.getText().length() == 0)
            playerTwo = "red";
        else
            playerTwo = blackPlayerTextField.getText();
        new CheckersGameGUI(this, speech, playerOne,  playerTwo, colorSet);
        mainGUI.setVisible(false);
    }

    private void loadChessItemActionPerformed(ActionEvent e) {
        try {
                File saveFile = new File("./savedgames/Chess.txt");
                Scanner myReader = new Scanner(saveFile);
                String savedGame = myReader.nextLine();
                String[] pieces = savedGame.split(" "); //list of words separated by spaces
                myReader.close();
                new ChessGameGUI(this, pieces, speech, pieces[0], pieces[1], Integer.valueOf(pieces[2]));
                mainGUI.setVisible(false);
            
          } catch (FileNotFoundException error) {
            System.out.println("No save found");
            error.printStackTrace();
          }
    }

    private void loadCheckersItemActionPerformed(ActionEvent e) {
        try {
                File saveFile = new File("./savedgames/Checkers.txt");
                Scanner myReader = new Scanner(saveFile);
                String savedGame = myReader.nextLine();
                String[] pieces = savedGame.split(" "); //list of words separated by spaces
                myReader.close();
                new CheckersGameGUI(this, pieces, speech, pieces[0], pieces[1], Integer.valueOf(pieces[2]));
                mainGUI.setVisible(false);
            
          } catch (FileNotFoundException error) {
            System.out.println("No save found");
            error.printStackTrace();
          }
    }

    private void helpItemActionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(mainGUI,
        "Enter player names or let them default to their respective colors\n" +
        "Press play to start a new game or load to load a previous save\n" +
        "The left play and load buttons will launch chess and the right will launch checkers\n" +
        "Left click or press the speak button and say the name of the square to select\n" +
        "Because the speech recognizer can mishear you, please say one square at a time\n" +
        "For example, say 'alpha two' to select and then 'alpha four' to move that piece\n" +
        "Right click or press the speak button and say 'clear' to unselect a piece\n" +
        "Press play to start a new game or load to load a previously saved game",
        "Help Menu", JOptionPane.INFORMATION_MESSAGE);
    }

    private void quitItemActionPerformed(java.awt.event.ActionEvent e) {
        int quit = JOptionPane.showConfirmDialog(mainGUI, "Are you sure you want to quit?", "Quit", JOptionPane.OK_CANCEL_OPTION);
        if(quit == JOptionPane.OK_OPTION) 
        {
            mainGUI.dispose();
            exit();
        }
    }

    /***
     * called by quit buttons in main and game GUI, public so that gameGUI can access it, otherwise gameGUI would be disposed and mainGUI would just be invisible, leaving the program running
     */
    public void exit() {
        speech.stopSpeechRecognizerThread();
        mainGUI.dispatchEvent(new WindowEvent(mainGUI, WindowEvent.WINDOW_CLOSING));
    }

    /***
     * this method creates a new JFrame that allows the user to pick which color set they want for the board, writes preferred color to Settings.txt or automatically closes if the user clicks out of it
     * @param e - default actionevent
     */
    private void settingsItemActionPerformed(ActionEvent e) {
        new Settings(); //no need to do anything further such as save reference to or dispose settings instance, settings automatically disposes itself when the user either applies or clicks on another window
    }
    public class Settings extends JFrame {
        //radio buttons saved as class members so that apply item action can see which radio button is selected
        JRadioButton colorSetOne;
        JRadioButton colorSetTwo;
        JRadioButton colorSetThree;
        JRadioButton colorSetFour;
        public Settings() {
            this.setIconImage(new ImageIcon(Tag.SETTINGS_LOGO).getImage());
            this.setSize(300, 525);
            this.setLocationRelativeTo(mainGUI);
            createInstructions();
            createDemoDisplays();
            createApplyButton();
            this.setResizable(false);
            this.setVisible(true);
            this.addWindowFocusListener( new WindowFocusListener() {
                //implemented because settings window would otherwise stay open when you do anything else with the main menu, including starting a game
                @Override
                public void windowLostFocus(WindowEvent event) {
                    closeWindow();
                }
                //has to be overwritten for windowFocusListener, not used so left empty
                @Override
                public void windowGainedFocus(WindowEvent event) { }
            });
        }

        /***
         * disposes this, separated method so that windowLostFocus can call it as this in windowLostFocus is a WindowFocusListener, not settings
         */
        public void closeWindow() { this.dispose(); };

        /***
         * creates instructions text at the top of the frame
         */
        private void createInstructions() {
            JPanel instructions = new JPanel();
            instructions.setLayout(new GridLayout(2, 1, 0, 0));
            //create across two JLabels so that text fits within frame
            JLabel top = new JLabel("Please select your preferred", JLabel.CENTER);
            JLabel bottom = new JLabel("color and press apply", JLabel.CENTER);
            top.setHorizontalAlignment(JTextField.CENTER);
            bottom.setHorizontalAlignment(JTextField.CENTER);
            top.setBackground(Tag.ColorChoice[1][6]);
            top.setForeground(Tag.ColorChoice[1][9]);
            top.setFont(new Font("Monospaced", Font.BOLD, 14));
            bottom.setBackground(Tag.ColorChoice[1][6]);
            bottom.setForeground(Tag.ColorChoice[1][9]);
            bottom.setFont(new Font("Monospaced", Font.BOLD, 14));
            instructions.add(top);
            instructions.add(bottom);
            instructions.setBackground(Tag.ColorChoice[1][6]);
            instructions.setPreferredSize(new Dimension(300, 50));
            this.add(instructions, BorderLayout.NORTH);
        }

        /***
         * creates radio buttons and previews of board colors in the middle of frame
         */
        private void createDemoDisplays() {
            JPanel mainPanel = new JPanel();
            mainPanel.setBackground(Tag.ColorChoice[1][6]);
            mainPanel.setPreferredSize(new Dimension(300, 375));
            mainPanel.setLayout(null);
            colorSetOne = new JRadioButton();
            colorSetOne.setBackground(Tag.ColorChoice[1][6]);
            colorSetTwo = new JRadioButton();
            colorSetTwo.setBackground(Tag.ColorChoice[1][6]);
            colorSetThree = new JRadioButton();
            colorSetThree.setBackground(Tag.ColorChoice[1][6]);
            colorSetFour = new JRadioButton();
            colorSetFour.setBackground(Tag.ColorChoice[1][6]);
            ButtonGroup bg = new ButtonGroup();
            bg.add(colorSetOne);
            bg.add(colorSetTwo);
            bg.add(colorSetThree);
            bg.add(colorSetFour);
            mainPanel.add(colorSetOne);
            mainPanel.add(colorSetTwo);
            mainPanel.add(colorSetThree);
            mainPanel.add(colorSetFour);
            if (colorSet == 0)
                colorSetOne.setSelected(true);
            else if (colorSet == 1)
                colorSetTwo.setSelected(true);
            else if (colorSet == 2)
                colorSetThree.setSelected(true);
            else //colorSetFour
                colorSetFour.setSelected(true);
            colorSetOne.setBounds(100, 50, 50, 20);
            colorSetTwo.setBounds(100, 125, 50, 20);
            colorSetThree.setBounds(100, 200, 50, 20);
            colorSetFour.setBounds(100, 275, 50, 20);
            //loop through and display create displayBoard for each color set
            for (int i = 0; i < Tag.ColorChoice.length; i++)
            {
                JPanel displayBoard = new JPanel();
                displayBoard.setLayout(new GridLayout(2, 2, 0, 0));
                displayBoard.add(new Position(0, 6, false, 10, i));
                displayBoard.add(new Position(1, 6, true, 10, i));
                displayBoard.add(new Position(0, 7, true, 10, i));
                displayBoard.add(new Position(1, 7, false, 10, i));
                displayBoard.setPreferredSize(new Dimension(50, 50));
                displayBoard.setBorder(BorderFactory.createLineBorder(Tag.ColorChoice[i][7]));
                mainPanel.add(displayBoard);
                displayBoard.setBounds(30, 35 + (i * 75), 50,50); //shifts each down to corresponding buttons
            }
            this.add(mainPanel);
        }

        /***
         * creates apply button at the bottom of the board and assigns it an action listener
         */
        private void createApplyButton() {
            JButton apply = new JButton("Apply");
            apply.setBackground(Tag.ColorChoice[1][7]);
            apply.addActionListener(e -> applyItemActionPerformed(e));
            JPanel buttonWrapper = new JPanel();
            buttonWrapper.setPreferredSize(new Dimension(300, 50));
            buttonWrapper.setBackground(Tag.ColorChoice[1][6]);
            buttonWrapper.add(apply);
            this.add(buttonWrapper, BorderLayout.SOUTH);
        }
        private void applyItemActionPerformed(ActionEvent e) {
            if (colorSetOne.isSelected())
                colorSet = 0;
            else if (colorSetTwo.isSelected())
                colorSet = 1;
            else if (colorSetThree.isSelected())
                colorSet = 2;
            else 
                colorSet = 3;
            try {
                FileWriter writer = new FileWriter("./savedgames/Settings.txt", false);
                writer.write(String.valueOf(colorSet));
                writer.close();
            }
            catch (Exception error) {
                error.getStackTrace();
            }
            this.dispose(); //close once user is done selecting and colorSet has been updated
        }
    }
}