package com.company;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.regex.Pattern;

public class SAP_GUI extends JFrame{

    /**
     * Main method. Initializes and configures the GUI and FileManipulator.
     * All other events are based on ActionListeners
     * @param args Default args
     * @throws ClassNotFoundException UIManager can't find System look and feel class
     * @throws UnsupportedLookAndFeelException Can't apply System look and feel to the java GUI
     * @throws InstantiationException Can't
     * @throws IllegalAccessException Can't access System look and feel class
     */
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SAP_GUI gui;
        FileManipulator fileManipulator;

        try {
            fileManipulator = new FileManipulator();
            gui = new SAP_GUI(fileManipulator);
            gui.setVisible(true);
            gui.setTextField_directory(fileManipulator.getCurrentDirectory()); //Setting the default directory to be displayed
            fileManipulator.loadFile(); //loading the default txt
            gui.setPrintTextArea(fileManipulator.getFileText()); //Printing the file text to the Text box in the GUI

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JButton switchWordsButton;
    private JTextArea printTextArea;
    private JTextField textField_word_switch_line2;
    private JTextField textField_word_switch_word2;
    private JTextField textField_word_switch_line1;
    private JTextField textField_word_switch_word1;
    private JTextField textField_line_switch_line1;
    private JTextField textField_line_switch_line2;
    private JPanel rootPanel;
    private JTextField textField_directory;
    private JButton setDirectoryButton;
    private JButton clearFieldsButton;
    private JButton switchLinesButton;

    /**
     * Constructor, initializes the GUI
     * Sets up the action listeners
     * Sets up closing operation
     * @param fileManipulator Main logic of the program
     */
    public SAP_GUI(FileManipulator fileManipulator){

        add(rootPanel);
        setTitle("SAP text editor - 1.1");
        setSize(700,400);

        //Setting up closing algorithm to make sure all Streams are closed
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setClosingAlgorithm(this, fileManipulator);

        /*
          Logic of the Switch words button.
          Validates the fields via the validateFields(checks for integers),
          then with checkFieldStatus to see whether it's full information
          if yes, pass that information to the fileManipulator
         */
        switchWordsButton.addActionListener(e -> {
            if (validateFields()) { //returns true if fields are valid (with integers)
                if (checkFieldStatus() == 1 || checkFieldStatus() == 2) { //returns an int depending on the case
                    //Switching the words
                    fileManipulator.switchWords(
                            Integer.parseInt(textField_word_switch_line1.getText()) ,
                            Integer.parseInt(textField_word_switch_word1.getText()) ,
                            Integer.parseInt(textField_word_switch_line2.getText()) ,
                            Integer.parseInt(textField_word_switch_word2.getText())
                    );
                    //updating the text in the end of the method
                    printTextArea.setText(fileManipulator.getFileText());
                } else { // case 3: no full information
                    JOptionPane.showMessageDialog(
                            null ,
                            "Please enter full information for the method/methods" ,
                            "Input error" ,
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });


        /*
          If the file exists, save the changes to it
          then get the directory from user input text box and validate it
          if valid, set it to this.directory and load the new file
          loadFile method will ask the user if they want to create a file, in case it was missing
          and if they want to fill it with test information
         */
        setDirectoryButton.addActionListener(e -> {
            if(fileManipulator.getFile().exists()) {
                try {
                    fileManipulator.saveChangesToFile();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    JOptionPane.showMessageDialog(
                            null ,
                            "Couldn't save changes to the file" ,
                            "Saving error" ,
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
            //setDirectory tests if the input is valid and returns True/False
            if(fileManipulator.setDirectory(textField_directory.getText())) {
                try {
                    fileManipulator.loadFile();
                    printTextArea.setText(fileManipulator.getFileText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        });


        /*
          Logic of the Switch lines button.
          Validates the fields via the validateFields(checks for integers),
          then with checkFieldStatus to see whether it's full information
          if yes, pass that information to the fileManipulator
         */
        switchLinesButton.addActionListener(e -> {
            if (validateFields()) { //returns true if fields are valid (with integers)
                if (checkFieldStatus() == 0 || checkFieldStatus() == 2) {
                    //Switching the lines
                    fileManipulator.switchLines(
                            Integer.parseInt(textField_line_switch_line1.getText()) ,
                            Integer.parseInt(textField_line_switch_line2.getText())
                    );
                    //updating the text in the end of the method
                    printTextArea.setText(fileManipulator.getFileText());
                }
                else {
                    JOptionPane.showMessageDialog(
                            null ,
                            "Please enter full information for the method/methods" ,
                            "Input error" ,
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        //Sets all the text boxes, except the directory one, to ""
        clearFieldsButton.addActionListener(e -> {
            textField_line_switch_line1.setText("");
            textField_line_switch_line2.setText("");
            textField_word_switch_line1.setText("");
            textField_word_switch_line2.setText("");
            textField_word_switch_word1.setText("");
            textField_word_switch_word2.setText("");
        });
    }

    /**
     * this was made, because in main, I don't have access to sap.gui(this) inside windowClosing event
     * @param gui gets a SAP_GUI
     */
    private void setClosingAlgorithm(SAP_GUI gui, FileManipulator fileManipulator) {
        gui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(fileManipulator.getFile().exists()) {
                    try {
                        fileManipulator.saveChangesToFile();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        JOptionPane.showMessageDialog(
                                null ,
                                "Couldn't save changes to the file" ,
                                "Saving error" ,
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
                gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
    }

    /**
     * Checks fields that require an integer for switch functions
     * Changes to empty field all that aren't an integer
     * @return true if all values are valid
     */
    private boolean validateFields() {

        boolean isValid = true;

        if(!checkIfNumOrBlank(textField_line_switch_line1.getText())){
            isValid = false;
            textField_line_switch_line1.setText("");
        }

        if(!checkIfNumOrBlank(textField_line_switch_line2.getText())){
            isValid = false;
            textField_line_switch_line2.setText("");
        }

        if(!checkIfNumOrBlank(textField_word_switch_line1.getText())){
            isValid = false;
            textField_word_switch_line1.setText("");
        }

        if(!checkIfNumOrBlank(textField_word_switch_line2.getText())){
            isValid = false;
            textField_word_switch_line2.setText("");
        }

        if(!checkIfNumOrBlank(textField_word_switch_word1.getText())){
            isValid = false;
            textField_word_switch_word1.setText("");
        }

        if(!checkIfNumOrBlank(textField_word_switch_word2.getText())) {
                textField_word_switch_word2.setText("");
                isValid = false;
        }

        if(isValid) {
            return true;
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Please enter positive numbers only!",
                    "Number format exception",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    /**
     * @return 0 if there is only input in switchLines
     *         1 if there is only input in switchWords
     *         2 if there is input both in switchLines AND switchWords,
     *         3 if there is no full input
     */
    private int checkFieldStatus() {
        //Check if there are numbers ONLY on switchLine
        if(!textField_line_switch_line1.getText().equals("") && !textField_line_switch_line2.getText().equals("")) {
            if( textField_word_switch_line1.getText().equals("")
                && textField_word_switch_line2.getText().equals("")
                && textField_word_switch_word1.getText().equals("")
                && textField_word_switch_word2.getText().equals("")
            ) {
                return 0;
            }
        }

        //Check if there are numbers ONLY on switchWord
        if( !textField_word_switch_line1.getText().equals("")
                && !textField_word_switch_line2.getText().equals("")
                && !textField_word_switch_word1.getText().equals("")
                && !textField_word_switch_word2.getText().equals("")
        ) {
            if(textField_line_switch_line1.getText().equals("") && textField_line_switch_line2.getText().equals("")) {
                return 1;
            }
        }

        //Check if there are numbers on BOTH switchWord and switchLines
        if( !textField_word_switch_line1.getText().equals("")
                && !textField_word_switch_line2.getText().equals("")
                && !textField_word_switch_word1.getText().equals("")
                && !textField_word_switch_word2.getText().equals("")
                && !textField_line_switch_line1.getText().equals("")
                && !textField_line_switch_line2.getText().equals("")
        ) {
            return 2;
        }

        //else return 3 for empty/unrecognized input
        return 3;
    }

    /**
     * Checks the testedString with REGEX and .equals() to determine whether it is a digit or blank
     * @param testedString given String for testing
     * @return true if it is blank or number, false if it is everything else
     */
    private boolean checkIfNumOrBlank(String testedString) {
        final String REGEX = "^\\d+$"; //Checks if string is digits only

        if(!Pattern.matches(REGEX, testedString)){
            return testedString.equals("");
        }
        return true;
    }

    //Getters and setters
    public void setPrintTextArea(String printTextArea) {
        this.printTextArea.setText(printTextArea);
    }

    public void setTextField_directory(String textField_directory) {
        this.textField_directory.setText(textField_directory);
    }
}
