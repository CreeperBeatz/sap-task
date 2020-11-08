package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        getDirectory(scanner);
	    //get file name
        // pass it to fileManipulation class
        //  - TODO may trow exception that file wasnt found
        //  - TODO ask if user wants to create a file in that repository
        //ask user what type of manipulation he wants to do
        //switch case with
    }

    public static String getDirectory(Scanner scanner) {
        //final directory also stored in current directory
        //temp directory used for user input
        String currentDirectory = System.getProperty("user.dir");
        String tempDirectory;

        System.out.println("type \"...\") for going back a folder");
        System.out.println("type \"cd (folder name/file name)\" to change directory");
        System.out.println("type \"done\" to exit the cycle and pass the directory");

        while(true) {
            System.out.println("\nCurrent directory" + currentDirectory);
            tempDirectory = scanner.nextLine();

            //if we have "cd " in front, we add the statement of the user to the final directory
            if(tempDirectory.substring(0,2).equals("cd ")) {
                currentDirectory += "\\"+tempDirectory.substring(3);
            }
            else if (tempDirectory.equals("...")) {
                // we check the string for \ from the end to the beginning
                for(int indexToCut = currentDirectory.length()-1; indexToCut >= 0; indexToCut--) {
                    // when found, we cut the currentDirectory to the value without \
                    if(currentDirectory.charAt(indexToCut) == '\\') {
                        currentDirectory = currentDirectory.substring(0, indexToCut);
                        break;
                    }
                    // if not found, nothing happens
                    if(indexToCut == 0) {
                        System.out.println("Cant revert directory any further!");
                    }
                }
            } else if (tempDirectory.equals("done")) {
                return currentDirectory;
            }
            else if (tempDirectory.equals("exit")) {
                //TODO program exit statement
                System.out.println("put exit arguments here");
            }
        }
    }
}
