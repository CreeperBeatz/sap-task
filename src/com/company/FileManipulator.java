package com.company;

import java.io.*;
import java.util.Scanner;

public class FileManipulator {
    File file;
    Scanner fileScanner;

    String currentDirectory;
    Scanner scanner;

    public FileManipulator()


    public void setDirectory() {
        //final directory also stored in current directory
        //temp directory used for user input
        String currentDirectory = System.getProperty("user.dir");
        currentDirectory += "\\test.txt";

        System.out.println("Current directory: " + currentDirectory);
        System.out.println("Do you want to change that?");
        System.out.println("(yes) - change directory      (no) - save current directory");
    }
}
