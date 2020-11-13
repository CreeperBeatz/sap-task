package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Scanner;

public class SAP_GUI extends JFrame{

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        SAP_GUI gui = new SAP_GUI();
        gui.setVisible(true);

        FileManipulator fileManipulator;

        //Main cycle -> program goes back here when user wants a new file
        while (true) {
            try {
                fileManipulator = new FileManipulator(gui);
                //fileManipulator.printByLine();
                JOptionPane.showMessageDialog(null,"Do you want to create a file at that location?","No txt file found!", JOptionPane.YES_NO_OPTION);
                //fileManipulator.switchLines(1,3);
                //fileManipulator.printByLine();
                //fileManipulator.switchWords(1 , 3 , 2 , 4);
                //fileManipulator.printByLine();
                break;
            } catch (IOException e) {
                System.out.println("Please provide a valid Directory!");
                //e.printStackTrace();
            }
            //TODO rewrite this exception in the inner cycle
            //TODO write a good method to close file, since currently, if an exception is trown closefile isnt executed
        }
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

    public SAP_GUI(){
        add(rootPanel);
        setTitle("SAP text editor - alpha 0.1");
        setSize(700,400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("test?");
            }
        });

        setDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //If file already opened
                //  check if new directory is valid (regex)
                //      close old object
                //      return to constructor
                //
            }
        });
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
