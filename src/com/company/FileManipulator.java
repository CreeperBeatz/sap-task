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

    //Maximum chars that can be read with the buffered reader
    final int READ_AHEAD_LIMIT = 1024;

    File file;//current file

    BufferedReader fileReader; //Reader for the file

    String currentDirectory; //Always verified due to setDirectory() validation

    public FileManipulator() throws IOException{

        //load default directory
        this.currentDirectory = System.getProperty("user.dir");
        this.currentDirectory += "\\test.txt";
    }

/*
 * Sets file to open current directory
 * Sets the scanner to this file
 * If file doesn't exist, asks the user if they want to create it
 * If yes, creates a new file at current directory
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

        System.out.println(userChoice);
            //if yes, create one
            if(userChoice == 0) { //if user wants to create file
                file.createNewFile();

            }
            else {
                return;
            }
        }

        //After we are sure a file exists in this directory, we init the fileReader
        this.fileReader = new BufferedReader(new FileReader(this.file));
        this.fileReader.mark(READ_AHEAD_LIMIT);
    }

    //TODO Optimize switchLines()
    //Currently, a bit unoptimized
    public void switchLines(int line1, int line2) throws ArrayIndexOutOfBoundsException, IOException {

        //making sure line2 is the higher number
        if(line1 > line2) {
            line1 += line2;
            line2 = line1 - line2;
            line1 -= line2;
        }

        if(line1 <=0) {
            throw new ArrayIndexOutOfBoundsException("Line num lower than 0!");
        }

        // Temporary storage of the file contents
        ArrayList<String> fileContents = new ArrayList<>();
        fileReader.reset();

        String temp;
        //Write to the array, while there is content
        while((temp = fileReader.readLine()) != null) {
            fileContents.add(temp);
        }

        //Validation if the line exists
        if(fileContents.size() < line2) {
            throw new ArrayIndexOutOfBoundsException("Line doesn't exist!");
        }

        //Swapping the lines
        Collections.swap(fileContents, line1, line2);

        //Writing to the file
        writeToFile(fileContents);
    }

    public void switchWords(int line1, int line1Position, int line2, int line2Position) throws ArrayIndexOutOfBoundsException, IOException{
        //So arrays are synchronized
        line1--;
        line2--;

        ArrayList<String> fileContent = new ArrayList<>();
        fileReader.reset();

        String temp;
        //Write to the array, while there is content
        while((temp = fileReader.readLine()) != null) {
            fileContent.add(temp);
        }

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

        writeToFile(fileContent);
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

    //Will open a writer, write to the file and close the writer
    private void writeToFile(ArrayList<String> listToPrint) throws IOException{
        StringBuilder tempList = new StringBuilder();
        for (String s : listToPrint) {
            tempList.append(s);
            tempList.append('\n');
        }

        FileWriter fileWriter = new FileWriter(this.file);
        fileWriter.append(tempList);
        fileWriter.close();
    }

    //@Return String currentDirectory. If user hasn't set up a directory, a default one will be loaded in the constructor
    public String getCurrentDirectory(){
        return this.currentDirectory;
    }

    //Will return TRUE, only if there is nothing in the file, if there are white spaces, will return FALSE
    public boolean isFileEmpty() {
        return (file.length() == 0);
    }

    public void closeFile() throws IOException{
        fileReader.close();
    }

    public String getFileText() throws IOException{
        fileReader.reset();
        StringBuilder finalText = new StringBuilder();

        String temp;

        while((temp = fileReader.readLine()) != null) {
            finalText.append(temp);
            finalText.append('\n');
        }

        return finalText.toString();
    }

    //TODO change oracles "feature" with something actually non brain dead
    //Shitty way to reset the scanner nextLine to the first line. Change in the future
    private Scanner resetFileScanner(Scanner oldScanner) throws IOException{ //throws IOException if scanner cant close properly
        oldScanner.close();
        return new Scanner(this.file, StandardCharsets.UTF_8.name());
    }

    //Can only be called on initialized FileManipulator
    private void setDirectory() {

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
