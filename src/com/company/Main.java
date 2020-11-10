package com.company;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {


        FileManipulator fileManipulator;
        Scanner scanner = new Scanner(System.in);

        //Main cycle -> program goes back here when user wants a new file
        while(true) {
            try {
                fileManipulator = new FileManipulator(scanner);
                fileManipulator.printByLine();
                //fileManipulator.switchLines(1,3);
                //fileManipulator.printByLine();
                fileManipulator.switchWords(1, 3, 2, 4);
                fileManipulator.printByLine();
                break;
            } catch (IOException e) {
                System.out.println("Please provide a valid Directory!");
                //e.printStackTrace();
            }
            //TODO rewrite this exception in the inner cycle
            //TODO write a good method to close file, since currently, if an exception is trown closefile isnt executed
            //catch (LineOutOfBoundsException e) { System.out.println("yayeet ya numbers wrong"); }
        }



        //ask user what type of manipulation he wants to do
        //switch case with
    }


}
