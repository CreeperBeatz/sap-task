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

    //TODO replace with bufferedReader
    Scanner fileScanner; //scan the file

    String currentDirectory; //Always verified due to setDirectory() validation
    //Scanner scanner; // read user input

    SAP_GUI gui; //Needed in order for the logic display

    public FileManipulator(SAP_GUI gui) throws IOException{
        this.gui = gui;

        //final directory also stored in current directory
        this.currentDirectory = System.getProperty("user.dir");
        this.currentDirectory += "\\test.txt";

        //Display the default directory on the GUI
        gui.setTextField_directory(this.currentDirectory);



        //this.fileWriter = new FileWriter(currentDirectory);

    }

    public void loadFile(SAP_GUI gui) throws IOException{
        this.file = new File(this.currentDirectory);
        this.fileScanner = new Scanner(this.file, StandardCharsets.UTF_8.name());

        //if there isn't a txt file, ask user if he wants to create one
        if(!file.exists()) {
            while(true) {


                System.out.println("File not found. Do you want to create this file? (yes/no)");
                //TODO get input from form

                //if yes, create one
                if(true) { //if user wants to create file
                    file.createNewFile();
                    System.out.println("New empty file created");
                    break;
                }
            }
        }
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
        //needed in order to use nextLine() method
        fileScanner = resetFileScanner(fileScanner);

        //Write to the array, while there is content
        while(fileScanner.hasNextLine()) {
            fileContents.add(fileScanner.nextLine());
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
        fileScanner = resetFileScanner(fileScanner);

        //TODO replace with fileReader
        while(fileScanner.hasNextLine()) {
            fileContent.add(fileScanner.nextLine());
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
    private void writeToFile(String content) throws IOException{
        FileWriter fileWriter = new FileWriter(this.file);
        fileWriter.append(content);
        fileWriter.close();
    }

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

    //Will return TRUE, only if there is nothing in the file, if there are white spaces, will return FALSE
    public boolean isFileEmpty() {
        return (file.length() == 0);
    }

    public void closeFile(){
        fileScanner.close();
    }

    public void printByLine() throws IOException{
        fileScanner = resetFileScanner(fileScanner);
        System.out.println("-----------------------------------");
        while(fileScanner.hasNextLine()) {
            System.out.println(fileScanner.nextLine());
        }
        System.out.println("-----------------------------------");
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
