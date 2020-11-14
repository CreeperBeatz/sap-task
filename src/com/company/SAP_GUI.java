package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SAP_GUI extends JFrame{

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SAP_GUI gui;
        FileManipulator fileManipulator;

        try {
            fileManipulator = new FileManipulator();
            gui = new SAP_GUI(fileManipulator);
            gui.setVisible(true);
            gui.setTextField_directory(fileManipulator.getCurrentDirectory());
            fileManipulator.loadFile();
            gui.setPrintTextArea(fileManipulator.getFileText());

        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO write a good method to close file, since currently, if an exception is thrown closefile isnt executed
    }

    private JButton switchButton;
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
    private FileManipulator fileManipulator;

    public SAP_GUI(FileManipulator fileManipulator){
        this.fileManipulator = fileManipulator;

        add(rootPanel);
        setTitle("SAP text editor - alpha 0.1");
        setSize(700,400);

        //Setting up closing algorithm to make sure all Streams are closed
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setClosingAlgorithm(this, fileManipulator);

        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (validateFields()) { //returns true if fields are valid
                        switch (checkFieldStatus()) {
                            case 0:
                                fileManipulator.switchLines(
                                        Integer.parseInt(textField_line_switch_line1.getText()),
                                        Integer.parseInt(textField_line_switch_line2.getText())
                                );
                                break;
                            case 1:
                                fileManipulator.switchWords(
                                        Integer.parseInt(textField_word_switch_line1.getText()),
                                        Integer.parseInt(textField_word_switch_word1.getText()),
                                        Integer.parseInt(textField_word_switch_line2.getText()),
                                        Integer.parseInt(textField_word_switch_word2.getText())
                                );
                                break;
                            case 2:
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Firstly, switchLines will be executed\nthen switchWords.",
                                        "Input for both methods detected!",
                                        JOptionPane.INFORMATION_MESSAGE
                                );

                                fileManipulator.switchLines(
                                        Integer.parseInt(textField_line_switch_line1.getText()),
                                        Integer.parseInt(textField_line_switch_line2.getText())
                                );

                                fileManipulator.switchWords(
                                        Integer.parseInt(textField_word_switch_line1.getText()),
                                        Integer.parseInt(textField_word_switch_word1.getText()),
                                        Integer.parseInt(textField_word_switch_line2.getText()),
                                        Integer.parseInt(textField_word_switch_word2.getText())
                                );

                                break;
                            default:
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Please enter full information for the method/methods",
                                        "Input error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                                break;
                        }
                        //updating the text in the end of the method
                        printTextArea.setText(fileManipulator.getFileText());

                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        setDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileManipulator.setDirectory(textField_directory.getText())) {
                    try {
                        fileManipulator.loadFile();
                        printTextArea.setText(fileManipulator.getFileText());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        JOptionPane.showMessageDialog(
                                null,
                                "Couldn't save changes to the file",
                                "Saving error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }

                }
            }
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
                try {
                    fileManipulator.saveChangesToFile();
                } catch (IOException ioException) {
                    ioException.printStackTrace();

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
     * Checks the testedDtring with REGEX and .equals() to determine whether it is a digit or blank
     * @param testedString given String for testing
     * @return true if it is blank or number, false if it is everything else
     */
    private boolean checkIfNumOrBlank(String testedString) {
        final String REGEX = "^\\d+$"; //Checks if string is digits only

        if(!Pattern.matches(REGEX, testedString)){
            if(!testedString.equals("")) {
                return false;
            }
            else {
                return true;
            }
        }
        return true;
    }

    //Getters and setters
    public void setPrintTextArea(String printTextArea) {
        this.printTextArea.setText(printTextArea);
    }

    public String getTextField_word_switch_line2() {
        return textField_word_switch_line2.getText();
    }

    public void setTextField_word_switch_line2(String textField_word_switch_line2) {
        this.textField_word_switch_line2.setText(textField_word_switch_line2);
    }

    public String getTextField_word_switch__word2() {
        return textField_word_switch_word2.getText();
    }

    public void setTextField_word_switch__word2(String textField_word_switch_word2) {
        this.textField_word_switch_word2.setText(textField_word_switch_word2);
    }

    public String getTextField_word_switch_line1() {
        return textField_word_switch_line1.getText();
    }

    public void setTextField_word_switch_line1(String textField_word_switch_line1) {
        this.textField_word_switch_line1.setText(textField_word_switch_line1);
    }

    public String getTextField_word_switch_word1() {
        return textField_word_switch_word1.getText();
    }

    public void setTextField_word_switch_word1(String textField_word_switch_word1) {
        this.textField_word_switch_word1.setText(textField_word_switch_word1);
    }

    public String getTextField_line_switch_line1() {
        return textField_line_switch_line1.getText();
    }

    public void setTextField_line_switch_line1(String textField_line_switch_line1) {
        this.textField_line_switch_line1.setText(textField_line_switch_line1);
    }

    public String getTextField_line_switch_line2() {
        return textField_line_switch_line2.getText();
    }

    public void setTextField_line_switch_line2(String textField_line_switch_line2) {
        this.textField_line_switch_line2.setText(textField_line_switch_line2);
    }

    public String getTextField_directory() {
        return textField_directory.getText();
    }

    public void setTextField_directory(String textField_directory) {
        this.textField_directory.setText(textField_directory);
    }
}
