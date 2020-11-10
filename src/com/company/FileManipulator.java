package com.company;

import javax.sound.sampled.Line;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileManipulator {
    File file;//current file
    Scanner fileScanner; //scan the file

    String currentDirectory; //Always verified due to setDirectory() validation
    Scanner scanner; // read user input

    //FileWriter fileWriter; //write to the file in changeLines and changeWord

    //File manipulator will ask the user for directory
    public FileManipulator(Scanner scanner) throws IOException{
        this.scanner = scanner;
        setDirectory();
        this.file = new File(this.currentDirectory);

        //if there isn't a txt file, ask user if he wants to create one
        if(!file.exists()) {
            while(true) {
                System.out.println("File not found. Do you want to create this file? (yes/no)");
                String temp = scanner.nextLine();

                //if yes, create one
                if(temp.equals("yes")) {
                    file.createNewFile();
                    System.out.println("New empty file created");
                    break;
                }
                else if (temp.equals("no")) { //else throw exception to go in the main loop
                    throw new IOException();
                }
                else {
                    System.out.println("Input not recognised");
                }
            }
        }

        this.fileScanner = new Scanner(this.file, StandardCharsets.UTF_8.name());
        //this.fileWriter = new FileWriter(currentDirectory);

    }

    //TODO Optimize switchLines()
    //Currently, a bit unoptimized
    public void switchLines(int line1, int line2) throws LineOutOfBoundsException, IOException {

        //making sure line2 is the higher number
        if(line1 > line2) {
            line1 += line2;
            line2 = line1 - line2;
            line1 -= line2;
        }

        if(line1 <=0) {
            throw new LineOutOfBoundsException();
        }

        // Temporary storage of the file contents
        ArrayList<String> fileContents = new ArrayList<String>();
        //needed in order to use nextLine() method
        fileScanner = resetFileScanner(fileScanner);

        //Write to the array, while there is content
        while(fileScanner.hasNextLine()) {
            fileContents.add(fileScanner.nextLine());
        }

        //Validation if the line exists
        if(fileContents.size() < line2) {
            throw new LineOutOfBoundsException();
        }

        //Swapping the lines
        Collections.swap(fileContents, line1, line2);

        //Writing to the file
        writeToFile(fileContents);

        //OLD VERSION OF THE CODE
        /*
        //temporary storage of the strings
        String line1String = "null";
        String line2String = "null";

        //TODO change scanner with something else
        //We reset the scanner in order to use .nextLine() properly
        fileScanner = resetFileScanner(fileScanner);

        //We go through the file searching for line 1 and line 2
        //  if line exists, write it to a temp string
        //  else throw out of bounds exception
        for(int currentLine = 1; currentLine <=line2; currentLine++) {
            if(fileScanner.hasNextLine()) {
                if(currentLine == line1) {
                    line1String = fileScanner.nextLine();
                }
                else if(currentLine == line2) {
                    line2String = fileScanner.nextLine();
                }
                else {
                    fileScanner.nextLine();
                }
            }
            else {
                throw new LineOutOfBoundsException();
            }
        }

        //TODO change scanner with something else
        //We reset the scanner from previous uses
        fileScanner = resetFileScanner(fileScanner);
        //We temporarily save file contents in the dynamic string in order to modify them;
        StringBuilder fileContent = new StringBuilder();

        //We use while, not for, since we don't know the length of the file
        int currentLine = 1;
        while(fileScanner.hasNextLine()){
            //if the line is equal to the one we want to switch, append from the lineString
            if(currentLine == line1) {
                fileContent.append(line2String + System.lineSeparator());
                fileScanner.nextLine(); //we make the scanner skip a line
            }
            else if (currentLine == line2) {
                fileContent.append(line1String + System.lineSeparator());
                fileScanner.nextLine();
            }
            else { // else append from the already existing file
                fileContent.append(fileScanner.nextLine() + System.lineSeparator());
            }
            currentLine++;
        }

        writeToFile(fileContent.toString());
        */
    }

    public void switchWords(int line1, int line1Position, int line2, int line2Position) throws ArrayIndexOutOfBoundsException, IOException{
        //So arrays are synchronized
        line1--;
        line2--;

        ArrayList<String> fileContent = new ArrayList<String>();
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

        //We go through the string, searching for the index
        for(int currentPosition = 1; currentPosition <= wordPosition; currentPosition++) {
            if(!matcher.find()) {
                throw new ArrayIndexOutOfBoundsException("Word out of bounds!");
            }
            if(currentPosition == wordPosition) {
                return new int[] {matcher.start(), matcher.end()};
            }
        }
        return null; //keeping this, because compiler isnt happy about not having a return statement. It's unreachable pratically
    }

    /*
    //Pretty unoptimized method, but it works
    private String getTokenByIndex(int lineNum, int tokenIndex) throws IOException, LineOutOfBoundsException, WordIndexOutOfBoundsException{
        //needed to use the scanner
        fileScanner = resetFileScanner(fileScanner);

        //Validation
        if(lineNum <=0) { throw new LineOutOfBoundsException(); }
        if (tokenIndex <= 0) { throw new WordIndexOutOfBoundsException(); }

        int currentLine = 1;
        //Setting the scanner to the correct line
        while(currentLine < lineNum) {
            if(fileScanner.hasNextLine()) {
                fileScanner.nextLine();
            }
            else throw new LineOutOfBoundsException();
        }

        //Setting the scanner to the correct token index
        int currentTokenIndex = 1;
        while(currentTokenIndex < tokenIndex) {
            if(fileScanner.hasNext()) {
                fileScanner.next();
            }
            else {
                throw new WordIndexOutOfBoundsException();
            }
        }

        return fileScanner.next();
    }
    */

    //Will open a writer, write to the file and close the writer
    private void writeToFile(String content) throws IOException{
        FileWriter fileWriter = new FileWriter(this.file);
        fileWriter.append(content);
        fileWriter.close();
    }

    private void writeToFile(ArrayList<String> listToPrint) throws IOException{
        StringBuilder tempList = new StringBuilder();
        for(int currentLine = 0; currentLine < listToPrint.size(); currentLine++) {
            tempList.append(listToPrint.get(currentLine));
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

    public void closeFile() throws IOException{
        fileScanner.close();
        //fileWriter.close();
    }

    public int returnBiggerInt(int int1, int int2) {
        if(int1 > int2) {
            return int1;
        }
        else {
            return int2;
        }
    }

    public void printByLine() throws IOException{
        fileScanner = resetFileScanner(fileScanner);
        System.out.println("-----------------------------------");
        while(fileScanner.hasNextLine()) {
            System.out.println(fileScanner.nextLine());
        }
        System.out.println("-----------------------------------");
    }

    //TODO public void printByElement()

    //TODO change oracles "feature" with something actually non braindead
    //Shitty way to reset the scanner nextLine to the first line. Change in the future
    private Scanner resetFileScanner(Scanner oldScanner) throws IOException{ //throws IOException if scanner cant close properly
        oldScanner.close();
        return new Scanner(this.file, StandardCharsets.UTF_8.name());
    }
    //Can only be called on initialized FileManipulator
    private void setDirectory() {
        //final directory also stored in current directory
        this.currentDirectory = System.getProperty("user.dir");
        this.currentDirectory += "\\test.txt";

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
            userInput = scanner.nextLine();
            }
            else {
                fileEndsInTxt = false; // we reset the value for the next iteration
            }

            //testing user input
            if(userInput.equals("no")){
                System.out.println("Please enter new directory: ");
                this.currentDirectory = scanner.nextLine();
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
