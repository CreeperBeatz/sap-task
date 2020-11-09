package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        FileManipulator fileManipulator = new FileManipulator();

        Scanner scanner = new Scanner(System.in);
        fileManipulator.setDirectory(scanner);
	    //get file name
        // pass it to fileManipulation class
        //  - TODO may trow exception that file wasnt found
        //  - TODO ask if user wants to create a file in that repository
        //ask user what type of manipulation he wants to do
        //switch case with
    }


}
