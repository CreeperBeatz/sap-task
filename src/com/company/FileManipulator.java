package com.company;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileManipulator {

    File file;//current file
    String currentDirectory; //Always verified due to setDirectory() validation
    ArrayList<String> fileContent; //File content stored here, manipulated here, until

    public FileManipulator(){
        //load default directory
        this.currentDirectory = System.getProperty("user.dir");
        this.currentDirectory += "\\test.txt";
    }

/*
 * initializes file to open current directory
 * If file doesn't exist, asks the user if they want to create it
 * If yes, creates a new file at current directory
 * initializes the fileReader to this file and marks the beginning
 */
    public void loadFile() throws IOException{
        this.file = new File(this.currentDirectory);

        //if there isn't a txt file, ask user if he wants to create one
        if(!file.exists()) {
            int userChoice = JOptionPane.showConfirmDialog(
                    null,
                    "Do you want to create a new file at that location?",
                    "No file found!",
                    JOptionPane.YES_NO_OPTION
            );

            //if yes, create one
            if(userChoice == 0) { //if user wants to create file
                if(file.createNewFile()) {
                    //TODO statement that file was succesfully created
                }
                else {
                    //TODO Statement that file couldnt be created
                }
            }
            else {
                return;
            }
        }

        updateFileContentsArray();
    }

    /**
     *
     * @param line1
     * @param line2
     * @throws ArrayIndexOutOfBoundsException
     * @throws IOException
     */
    public void switchLines(int line1, int line2) throws ArrayIndexOutOfBoundsException, IOException {

        //Making the input synchronized with the array lines
        line1--;
        line2--;

        //making sure line2 is the higher number
        if(line1 > line2) {
            line1 += line2;
            line2 = line1 - line2;
            line1 -= line2;
        }

        if(line1 <0) {
            throw new ArrayIndexOutOfBoundsException("Line num lower than 1!");
        }

        //Validation if the line exists
        if(fileContent.size() < line2) {
            throw new ArrayIndexOutOfBoundsException("Line doesn't exist!");
        }

        //Swapping the lines
        Collections.swap(fileContent, line1, line2);
    }

    /**
     *
     * @param line1
     * @param line1Position
     * @param line2
     * @param line2Position
     * @throws ArrayIndexOutOfBoundsException
     * @throws IOException
     */
    public void switchWords(int line1, int line1Position, int line2, int line2Position) throws ArrayIndexOutOfBoundsException, IOException{
        //So arrays are synchronized
        line1--;
        line2--;

        //Validation
        if(fileContent.size() <= line2) { throw new ArrayIndexOutOfBoundsException("File doesn't have needed  lines!");}

        // stores value for the beginning and end of the words for the selected string
        int word1IndexArray[] = getTokenStartEndIndex(fileContent.get(line1), line1Position);
        int word2IndexArray[] = getTokenStartEndIndex(fileContent.get(line2), line2Position);

        //Temp variable to store the word from line1 before we switch it with the word from line2
        String word1String = fileContent.get(line1).substring(word1IndexArray[0], word1IndexArray[1]);

        fileContent.set(line1, (fileContent.get(line1).substring(0, word1IndexArray[0]) //beginning of the line up until word1
                + fileContent.get(line2).substring(word2IndexArray[0], word2IndexArray[1]) //placing word2 in the place of word1
                + fileContent.get(line1).substring(word1IndexArray[1]))); //placing the other part of the line

        fileContent.set(line2, (fileContent.get(line2).substring(0, word2IndexArray[0]) //beginning of the line up until word2
                + word1String //placing word1 in the place of word2
                + fileContent.get(line2).substring(word2IndexArray[1]))); //placing the other part of the line
    }

    private int[] getTokenStartEndIndex(String testedString, int wordPosition) throws ArrayIndexOutOfBoundsException{
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(testedString);

        //re-rolling the matcher.find method until we find the needed positionIndex
        for(int currentPosition = 1; currentPosition <= wordPosition; currentPosition++) {
            if(!matcher.find()) {
                throw new ArrayIndexOutOfBoundsException("Word out of bounds!");
            }
            if(currentPosition == wordPosition) {
                return new int[] {matcher.start(), matcher.end()};
            }
        }
        return null; //keeping this, because compiler isn't happy about not having a return statement. It's unreachable pratically
    }

    private void updateFileContentsArray() throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(this.file));

        this.fileContent = new ArrayList<>();
        String temp;
        while((temp = fileReader.readLine()) !=null ) {
            this.fileContent.add(temp);
        }

        fileReader.close();
    }

    //Will open a writer, write to the file and close the writer
    public void saveChangesToFile() throws IOException{
        StringBuilder tempList = new StringBuilder();
        for (String s : fileContent) {
            tempList.append(s);
            tempList.append('\n');
        }

        FileWriter fileWriter = new FileWriter(this.file);
        fileWriter.append(tempList);
        fileWriter.close();
    }

    /**
     * @return String currentDirectory. If user hasn't set up a directory, a default one will be loaded in the constructor
     */
    public String getCurrentDirectory(){
        return this.currentDirectory;
    }

    //Will return TRUE, only if there is nothing in the file, if there are white spaces, will return FALSE
    public boolean isFileEmpty() {
        return (file.length() == 0);
    }

    /**
     * Will NOT read from file!
     * @return arraylist fileContents in the form of a String
     * @throws IOException
     */
    public String getFileText() throws IOException{
        StringBuilder finalText = new StringBuilder();

        for(int currentLine = 0; currentLine < fileContent.size(); currentLine++) {
            finalText.append(fileContent.get(currentLine));
            finalText.append('\n');
        }

        return finalText.toString();
    }

    /**
     * reads a line from an INITIALIZED FILE and adds the number of chars to a integer counter
     * finally, closes the reader
     * @return number of chars in the file
     * @throws FileNotFoundException if there is no file initialized
     * @throws IOException if Buffered reader couldn't be closed properly
     */
    private int getCharCount() throws FileNotFoundException, IOException{
        BufferedReader tempReader = new BufferedReader(new FileReader(this.file));
        int charCount = 0;
        String data;
        while((data = tempReader.readLine())!= null) {
            charCount += data.length();
        }
        tempReader.close();
        return charCount;
    }

    /**
     * Checks if userInput is true to the reg expr.
     * @return true if directory is set correctly, false if not set
     */
    public boolean setDirectory(String userInput) {

        //used to see if directory ends with txt
        final String REGEX = ".*\\.txt$";

        if(Pattern.matches(REGEX, userInput)) {
            this.currentDirectory = userInput;
            return true;
        }
        else {
            JOptionPane.showMessageDialog(
                    null,
                    "Directory doesn't end in .txt!\nPlease type in a valid directory!",
                    "Directory error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

    }

    //Gets and validates user input for directory
    public void setDirectoryByConsole() {

        //Used for getting directory and confirming it
        String userInput = "null";

        //used when checking if file ends in txt in order to know whether to ask user if they want to change directory
         boolean fileEndsInTxt = true;

        //used to see if directory ends with txt
        final String REGEX = ".*\\.txt$";

        while(true) {

            //if directory doesnt end with .txt, skip asking user if they want to change directory
            if(fileEndsInTxt) {
            System.out.println("Current directory: " + currentDirectory);
            System.out.println("Is that right?");
            System.out.println("(no) - change directory      (yes) - save current directory");
            //userInput = scanner.nextLine();
            }
            else {
                fileEndsInTxt = false; // we reset the value for the next iteration
            }

            //testing user input
            if(userInput.equals("no")){
                System.out.println("Please enter new directory: ");
                //this.currentDirectory = scanner.nextLine();
            }
            else if(userInput.equals("yes")) {
                //testing if string ends with .txt, else we kindly ask the user to change to .txt directory
                if(Pattern.matches(REGEX, this.currentDirectory)) {
                    System.out.println("Path set to: " + this.currentDirectory);
                    return;
                }
                else {
                    System.out.println("Please change the directory to a file ending in .txt");
                    userInput = "no"; //we do this to go directly in mode for changing value
                    fileEndsInTxt = false;
                }
            }
            else {
                System.out.println("Input not recognized");
            }
        }

    }

}
