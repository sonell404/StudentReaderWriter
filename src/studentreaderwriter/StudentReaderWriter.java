/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package studentreaderwriter;

import java.io.IOException;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;
import java.util.Calendar;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;


/**
 *  PROGRAM SUMMARY
 * 
 *  This program is made for processing text files. It deals with 2 forms of data.
 * 
 *  FORMAT 1.
 * 
 *  (First Name) (Second Name)
 *  (No. of Classes)
 *  (Student Number)
 * 
 *  FORMAT 2.
 * 
 *  (Student Number) - (Second Name)
 *  (Workload)
 * 
 *  FUNCTIONALITY.
 * 
 *  WRITE TO FILE: Write FORMAT 1. data to a new or existing file.
 * 
 *      - Create new file
 *          - When creating a new file, the user is asked to supply a file name.
 *            The program will create and place the file in the users home directory.
 *      - Write to existing file
 *          - The user will be prompted to place their text file in the home directory.
 *            They will then be asked to confirm that it is in the correct location.
 *            They are then prompted to input the name of their existing text file.
 *            The program will confirm the file has been found or in the case of the
 *            program not finding the file, the user will be informed and asked if 
 *            they would like to return to main menu.
 * 
 *  READ FROM FILE: Read a file containing data in FORMAT 1., which will automatically create a 
 *                  status file and error file once the data has been processed into FORMAT 2. 
 * 
 *      - The same as writing to existing file, the user is prompted to place their text
 *        file in the correct directory, confirm it is correct directory, and enter name of 
 *        their file. A status and error file are automatically generated. 
 *        Valid student entries are processed into FORMAT 2. and written to the status file.
 *        Invalid student entries are tagged with the specific error and written to the error
 *        file for analysis and correction. This file can then be put through the program again
 *        to be read and formatted. 
 *  
 *  @author Sonel Ali
 */
public class StudentReaderWriter 
{
    // Class members 
    static final Scanner in = new Scanner(System.in);
    
    static final DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH.mm");
    static final LocalDateTime now = LocalDateTime.now();
    static final String CURRENT_DATE = dateTime.format(now).replace("/", ".");
    static final String YEAR = Integer.toString(Calendar.getInstance().get(Calendar.YEAR)).substring(2, 4);
        
    static final String SR = System.getProperty("file.separator");
    static final File DIRECTORY = 
            new File(System.getProperty("user.home") + SR + "Student Reader Writer" + SR + "Student Files");
    
    static File newFile = null;
    static File userFile = null;
    static File currentFile = null;
    static File statusFile = null;
    static File errorFile = null;
    
    static int mainMenuSelection = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        // Create directory to place generated files
        DIRECTORY.mkdirs();
        
        // Try initialise global scanner for user input
        try (in) 
        {
            // Loop program while user is using program
            do
            {
                initialiseProgram();
            }
            while (yesOrNo("\nContinue to main menu?"));
        }
        catch (Exception e)
        {
            System.out.println("An error with the input scanner was found.");
        }
        
        // Confirm program is finished running
        System.out.println("\nGoodbye!");
    }
    
    
    
    /*
    *
    *    METHODS FOR INITIALISATION, DISPLAYING MENUS AND PROCESSING MENU SELECTIONS
    *
    */
    
    
    
    // Method for initialising program
    static void initialiseProgram()
    {
        displayOptions();
        mainMenuSelection = getMenuSelection();
        useSystem(mainMenuSelection);
    }
    
    // Methods for Main Menu display and Write Menu display
    static void displayOptions()
    {
        System.out.println("\n1. Read from file");
        System.out.println("2. Write to file");
    }
    static void displayWriteOptions()
    {
        System.out.println("\n1. Create new file");
        System.out.println("2. Write to existing file");
    }
    
    // Method for taking and processing users menu selection
    static int getMenuSelection()
    {
        int selection = 0;
        String strSelection;
        
        // Loop until valid selection has been retrieved
        while (selection == 0)
        {
            System.out.print("\nEnter operation number: ");
            strSelection = in.nextLine();
            // Confirm input is within range 
            if (isNumeric(strSelection) 
                    && Integer.parseInt(strSelection) > 0 
                    && Integer.parseInt(strSelection) < 3)
            {
                // If input is valid, assign to return variable
                selection = Integer.parseInt(strSelection);
            }
            else if (strSelection.equals("0"))
            {
                System.out.println("Invalid Input!");
            }
            else
            {
                System.out.println("Invalid Input!");
            }
        }
        
        return selection;
    }
    
    // Method to act on users menu selection
    static void useSystem(int n)
    {
        switch (n)
        {
            case 1 -> 
            { 
                getStudentData(getUserFile(yesOrNo("\nContinue to read existing file?")));
            }
            case 2 -> 
            { 
                getStudentData(yesOrNo("\nContinue to write file?"));
            }
        }
    }
    
    
    
    /*
    *
    *   METHODS FOR GETTING & CREATING FILES
    *
    */
    
    
    
    // Method to get users pre-existing file
    static boolean getUserFile(boolean getting)
    {
        while (getting)
        {
            // Ask user to place desired text file in given location
            System.out.println("\nPlace text file in the following location: \n");
            System.out.println(System.getProperty("user.home"));
            
            // Get confirmation from user that file has been placed in correct location
            if (yesOrNo("\nIs file in correct location?\n"))
            {   
                // Get file name from user
                userFile = 
                    new File(System.getProperty("user.home"), 
                            askForFileName("\nEnter file name: ") + ".txt");
                
                // Check file exists
                if (userFile.exists())
                {
                    System.out.println("\nSuccess!");
                    System.out.println("\nFound file: " + userFile.getName()+ "\n");
                    return true;
                }
                else
                {
                    // File not found, so return to start of method
                    System.out.println("\nFile not found. Please try again\n");
                    break;
                }
            } 
        }
        
        return false;
    }
    
    // Method to name and create new student data file
    static boolean createNewStudentFile(boolean creating)
    {
        while (creating)
        {
            try
            {
                //Get name for new file from user and assign value to static File variable, newFile.
                newFile = new File(DIRECTORY, askForFileName("\nEnter name for new text file: ") + ".txt");
            
                // Create file and check if it exists or not already. Inform user either way
                if (newFile.createNewFile())
                {
                    System.out.println("File Created: " + newFile.getName()+ "\n");
                    break;
                }
                else
                {
                    System.out.println("\nFile already exists.\n");
                    return false;
                }
            }
            catch (IOException e)
            {
                // Inform user of missing directory
                System.out.println("Missing directory!");
                System.out.println(DIRECTORY.getName());
                return false;
            }
        }
        
        return true;
    }
    
    // Method to name and create new error file
    static boolean createNewErrorFile()
    {
        try
        {
            /* 
                Get name for new file from user and assign value to static File variable, newFile.
                The directory is already set in static File variable 'directory'
            */
            errorFile = new File(DIRECTORY, "Error File - " + CURRENT_DATE + ".txt");
            
            // Create file and check if directory exists
            if (errorFile.createNewFile())
            {
                System.out.println("File Created: " + errorFile.getName()+ "\n");
                return true;
            }
        }
        catch (IOException e)
        {
            // Inform user of missing directory
            System.out.println("Could not create error file - Missing directory!");
            System.out.println(DIRECTORY.getName());
            return false;
        }
        
        return false;
    }
    
    // Method to create new status file
    static boolean createNewStatusFile()
    {
        try
        {
            /* 
                Get name for new file from user and assign value to static File variable, newFile.
                The directory is already set in static File variable 'directory'
            */
            statusFile = new File(DIRECTORY, "Status File - " + CURRENT_DATE + ".txt");
            
            // Create file and check if directory exists
            if (statusFile.createNewFile())
            {
                System.out.println("\nFile Created: " + statusFile.getName()+ "\n");
                return true;
            }
        }
        catch (IOException e)
        {
            // Inform user of missing directory
            System.out.println("Could not create status file - Missing directory!");
            System.out.println(DIRECTORY.getName());
            return false;
        }
        
        return false;
    }
    
    
    
    /*
    *
    *   METHODS FOR RETRIEVING AND FORMATTING DATA
    *
    */
    
    
    
    
    // Method to retrieve and process student data
    static void getStudentData(boolean retrieving)
    {
        Scanner reader = null;
        
        String firstName = "";
        String secondName = "";
        String numOfClasses = "";
        String studentNumber = "";
        boolean reading = false;
        boolean errorMade = false;
        
        String temp = "";
        int i = 4;
        
        
        /* 
            Once it has been determined whether the operation is read or write, and have assigned the correct
            file to currentFile variable, we can now move on to retrieving data. 
        */
        if (retrieving)
        {
            // Determine if the user is writing or reading
            // Reading logic
            if (mainMenuSelection == 1)
            {
                reading = true;
                
                if ((createNewStatusFile() && createNewErrorFile()) || statusFile.exists() && errorFile.exists())
                {
                    if (userFile.exists())
                    {
                        currentFile = userFile;
                        
                        try
                        {
                            reader = new Scanner(currentFile);
                        }
                        catch (FileNotFoundException e)
                        {
                            System.out.println("Missing file or directory");
                            System.out.println(DIRECTORY.getName() + currentFile.getName());
                            
                            reading = false;
                        }
                    }
                    else
                    {
                        System.out.println("File not found.");
                        
                        reading = false;
                        retrieving = false;
                    }
                }
                else
                {
                    if (!statusFile.exists())
                    {
                        System.out.println("Could not create status file - Missing directory!");
                        System.out.println(DIRECTORY.getName());
                        
                        System.out.println("Please check directory and try again.");
                        
                        reading = false;
                        retrieving = false;
                    }
                    if (!errorFile.exists())
                    {
                        System.out.println("Could not create error file - Missing directory!");
                        System.out.println(DIRECTORY.getName());
                    
                        System.out.println("Please check directory and try again.");
                        
                        reading = false;
                        retrieving = false;
                    }
                }
            
            }
            // Writing logic
            else if (mainMenuSelection == 2)
            {
                int writeSelection;
            
                displayWriteOptions();
                writeSelection = getMenuSelection();
            
                switch (writeSelection)
                {
                    case 1 -> 
                    {
                        if (createNewStudentFile(true)) 
                        {
                            currentFile = newFile;
                        } 
                        else if (yesOrNo("Would you like to write to existing file?")) 
                        {
                            currentFile = newFile;
                        } 
                        else 
                        {
                            retrieving = false;
                        }
                    }
                    case 2 -> 
                    {
                        if (getUserFile(true)) 
                        {
                            currentFile = userFile;
                        } 
                        else 
                        {
                            System.out.println("File not found.");
                            retrieving = false;
                        }
                    }
                }
            }
            // Loop while taking input and writing to file
            MainRetrievingLoop:
            while (i != 0 && retrieving)
            {
                /*
                    Each input requires different check performing code.
                    So instead of a for loop, a switch is used to execute
                    different checks at each step. A counter of 4 is used
                    as we are looking for 4 inputs
                */
                switch (i)
                {
                    case 4 : 
                        // Loop until valid input for first name has been retrieved
                        casefourloop:
                        while (true)
                        {
                            if (!reading)
                            {
                                System.out.print("\nEnter first name: "); 
                                temp = in.nextLine();
                            }
                            else if (reading && reader != null && reader.hasNext())
                            {
                                temp = reader.next();
                            }
                            
                            
                            // Check validity of input for first name
                            for (int j = 0 ; j < temp.length() ; j++)
                            {
                                if (isNumeric(Character.toString(temp.charAt(j))))
                                {
                                    if (!reading)
                                    {
                                        System.out.println("Name must not contain any numbers.");
                                        break;
                                    }
                                    else
                                    {
                                        firstName = temp + " - Name must not contain any numbers.";
                                        errorMade = true;
                                        
                                        break casefourloop;
                                    }
                                }
                                else if (Character.toString(temp.charAt(j)).contains(" "))
                                {
                                    if (!reading)
                                    {
                                        System.out.print("\nName must not contain spaces.");
                                        System.out.println(" Please use '-' for double-barrell names");
                                        break;
                                    }
                                    else
                                    {
                                        firstName = temp + " - Name must not contain spaces. Use '-' for double-barrell names.";
                                        errorMade = true;
                                        
                                        break casefourloop;
                                    }
                                }
                                else if (j == temp.length() - 1)
                                {
                                    firstName = temp;
                                    break casefourloop;
                                }
                            }
                        }
                        
                        i--;
                        
                    case 3 : 
                        /* 
                            Second name can contain both letters and numbers,
                            so no need for any validity checks
                        */
                        if (!reading)
                        {
                            System.out.print("Enter second name: ");
                            secondName = in.nextLine(); 
                        }
                        else
                        {
                            if (reader != null && reader.hasNext())
                            {
                                secondName = reader.nextLine().replace(" ", "");
                            }
                            else
                            {
                                break MainRetrievingLoop;
                            }
                        }
                        
                        i--;
                        
                    case 2 :
                        // Loop until valid input has been retrieved
                        while(true)
                        {
                            // Get input for number of classes
                            if (!reading)
                            {
                                System.out.print("Enter number of classes: ");
                                temp = in.nextLine();
                            }
                            else
                            {
                                if (reader != null && reader.hasNext())
                                {
                                    temp = reader.nextLine();
                                }
                                else
                                {
                                    break MainRetrievingLoop;
                                }
                            }
                            
                            // Check validity of input for number of classes
                            if (isNumeric(temp) && Integer.parseInt(temp) < 9 && Integer.parseInt(temp) > 0)
                            {
                                numOfClasses = temp;
                                break;
                            }
                            else
                            {
                                // Check if error is numeric, else inform user of invalid input if not numeric
                                if (isNumeric(temp))
                                {
                                    // Inform user of the error in numeric range
                                    if (Integer.parseInt(temp) == 0)
                                    {
                                        if (!reading)
                                        {
                                            System.out.println("Number of classes must be at least 1.");
                                        }
                                        else
                                        {
                                            numOfClasses = temp + " - Number of classes must be at least 1.";
                                            errorMade = true;
                                            
                                            break;
                                        } 
                                    } 
                                    else if (Integer.parseInt(temp) > 8)
                                    {
                                        if (!reading)
                                        {
                                            System.out.println("Number of classes must not exceed 8.");
                                        }
                                        else
                                        {
                                            numOfClasses = temp + " - Number of classes must not exceed 8.";
                                            errorMade = true;
                                            break;
                                        }
                                    }
                                }
                                else
                                {
                                    if (!reading)
                                    {
                                        System.out.println("Invalid input!");
                                    }
                                    else
                                    {
                                        numOfClasses = temp + " - Invalid Input!";
                                        errorMade = true;
                                        break;
                                    }
                                }
                            }
                        }
                        
                        i--;
                        
                    case 1 : 
                        // Loop until valid input has been retrieved
                        while (true)
                        {
                            // Get student number and remove any spaces
                            if (!reading)
                            {
                                System.out.print("Enter student number: ");
                                studentNumber = in.nextLine().replace(" ", "");
                            }
                            else
                            {
                                if (reader != null && reader.hasNext())
                                {
                                    studentNumber = reader.nextLine().replace(" ", "");
                                }
                                else
                                {
                                    break MainRetrievingLoop;
                                }
                            }
                            
                            // Check validity of student number
                            if (isValidStudentNumber(studentNumber))
                            {
                                break;
                            }
                            else
                            {
                                if(reading)
                                {
                                    studentNumber += " - Please check student number rules at bottom of error file.";
                                    errorMade = true;
                                    break;
                                }
                            }
                        }
                        
                        i--; 
                        
                }
                
                 
                // Once all input has been checked and retrieved, write information to correct file.
                    
                
                // If reading, write to file. If writing, ask if user would like to commit to write.
                if (reading)
                {
                    if (!errorMade)
                    {
                        writeToStatusFile(secondName, numOfClasses, studentNumber);
                        if (reader != null && !reader.hasNext())
                        {
                            writeToErrorFile(firstName, secondName, numOfClasses, studentNumber, errorMade);
                        }
                    }
                    else
                    {
                        writeToErrorFile(firstName, secondName, numOfClasses, studentNumber, errorMade);
                        errorMade = false;
                    }
                }
                else if (yesOrNo("\nWould you like to write data to file?"))
                {
                    System.out.println("\nWriting...");
                    if (writeToFile(firstName, secondName, numOfClasses, studentNumber, currentFile))
                    {
                        System.out.println("Write successful!");
                    }
                }
                
                
                /*
                Set counter back to 4 if user wants to make another entry, or break from loop; ending the method.
                If reading, check if file has more data. Set counter back to 4 if it does and go back to beginning of loop
                else break from the loop; ending the method.
                */
                if (reading)
                {
                    if (reader != null && reader.hasNext())
                    {
                        i = 4;
                        continue;
                    }
                    else 
                    {
                        break;
                    }
                }
                
                if (yesOrNo("\nWould you like to make another entry?"))
                { 
                    i = 4; 
                }
                else
                {
                    break;
                }
            }
            
            if (reading && statusFile.exists())
            {
                System.out.println("Successfully written to status file!\n");
                System.out.println("Please find generated files in the following location:\n");
                System.out.println(DIRECTORY);
            }
        }
    }
    
    
    
    /*
    *
    *    METHODS FOR WRITING TO FILES
    *
    */
    
    
    
    // Method to write data retrieved from user to file
    static boolean writeToFile(String fName, String sName, String classes, String studentNo, File f)
    {
        // Append has been set to 'true' to allow multiple entries.
        try (BufferedWriter myWriter = new BufferedWriter(new FileWriter(f, true)))
        {
            myWriter.write(fName + " " + sName);
            myWriter.newLine();
            myWriter.write(classes);
            myWriter.newLine();
            myWriter.write(studentNo);
            myWriter.newLine();
            myWriter.newLine();
                    
                     
            // Close BufferedWriter to ensure data is flushed from buffer and written to file
            myWriter.close();
            
            return true;
        }
        catch (IOException e)
        {
            // Inform user of missing file
            System.out.println("Missing file error");
            System.out.println("'" + DIRECTORY.getName() + "'");
            
            return false;
        }
    }
    
    // Method for writing to status file
    static boolean writeToStatusFile(String secondN, String classAmount, String sNum)
    {
        try (BufferedWriter statusWriter = new BufferedWriter(new FileWriter(statusFile, true)))
        {
            statusWriter.write(sNum + " - " + secondN);
            statusWriter.newLine();
            statusWriter.write(getWorkload(Integer.parseInt(classAmount)));
            statusWriter.newLine();
            statusWriter.newLine();
            
            // Close BufferedWriter to ensure data is flushed from buffer and written to file
            statusWriter.close();
        
            return true;
        }
        catch (IOException e)
        {
            // Inform user of missing file
            System.out.println("Missing file error");
            System.out.println("'" + DIRECTORY.getName() + "'");
            
            return false;
        }
    }
    
     // Method to write data retrieved from user to file
    static boolean writeToErrorFile(String fName, String sName, String classes, String studentNo, boolean hasError)
    {
        // Append has been set to 'true' to allow multiple entries.
        try (BufferedWriter errorWriter = new BufferedWriter(new FileWriter(errorFile, true)))
        {
            if(hasError)
            {
                errorWriter.write(fName + " " + sName);
                errorWriter.newLine();
                errorWriter.write(classes);
                errorWriter.newLine();
                errorWriter.write(studentNo);
                errorWriter.newLine();
                errorWriter.newLine();
                errorWriter.write("** DELETE THIS TEXT BEFORE YOU SAVE FOR PROCESSING **");
                errorWriter.newLine();
                errorWriter.newLine();
                errorWriter.write("** All text within this box and also any errors tagged to entries must be cleared before being saved and processed again **");
                errorWriter.newLine();
                errorWriter.newLine();
                errorWriter.write("** It is also advised to rename file when saving for further processing **");
                errorWriter.newLine();
                errorWriter.newLine();
                errorWriter.write("1. Student number must be at least 6 characters and no greater than 8 characters.");
                errorWriter.newLine();
                errorWriter.write("2. Student number must begin with 2 numbers and must correspond to year of enrolment.");
                errorWriter.newLine();
                errorWriter.write("3. The first 2 numbers of a student number must be followed by 2-3 letters.");
                errorWriter.newLine();
                errorWriter.write("4. The final 3 digits of student number must be between 1 and 200");
                errorWriter.newLine();
                errorWriter.newLine();
                errorWriter.write("** DELETE THIS TEXT BEFORE YOU SAVE FOR PROCESSING **");
            }
            else
            {
                errorWriter.write("** NO ERRORS FOUND! **");
            }
                    
                     
            // Close BufferedWriter to ensure data is flushed from buffer and written to file
            errorWriter.close();
            
            return true;
        }
        catch (IOException e)
        {
            // Inform user of missing file
            System.out.println("Missing file error");
            System.out.println("'" + DIRECTORY.getName() + "'");
            
            return false;
        }
    }
    
    
    
    /*
    *
    *   METHODS TO CHECK THINGS
    *
    */
    
    
    
    // Method to check if student number adheres to rules
    static boolean isValidStudentNumber(String s)
    {
        /*
            Checks on various substrings so we can inform the user of the specific error in
            their input
        */
        if (s.length() < 6) 
        {
            if (mainMenuSelection == 2)
            {
                System.out.println("\nStudent number must be at least 6 characters.\n");
                return false;
            }
            else
            {
                return false;
            }
        }
        else if (s.length() > 8)
        {
            if (mainMenuSelection == 2)
            {
                System.out.println("\nStudent number must be no greater than 8 characters.\n");
                return false;
            }
            else
            {
                return false;
            }
        }
        else if (!isNumeric(s.substring(0, 2))) 
        {
            if (mainMenuSelection == 2)
            {
                System.out.println("\nStudent number must begin with 2 numbers.\n");
                return false;
            }
            else
            {
                return false;
            }
        }
        else if (Integer.parseInt(s.substring(0, 2))  < 20 ||
                Integer.parseInt(s.substring(0, 2)) > Integer.parseInt(YEAR))
        {
            if (mainMenuSelection == 2)
            {
                System.out.println("First 2 digits of student number must correspond to year of enrolment.");
                return false;
            }
            else
            {
                return false;
            }
        }
        else if (isNumeric(s.substring(2, 4)) || 
                isNumeric(s.substring(2, 5)))
        {
            if (mainMenuSelection == 2)
            {
                System.out.println("\nThe first 2 numbers of a student number must be followed by 2-3 letters.\n");
                return false;
            }
            else
            {
                return false;
            }
        }
        else if (!isNumeric(s.substring(4, s.length())) &&
                !isNumeric(s.substring(5, s.length())))
        {
            if (mainMenuSelection == 2)
            {
                System.out.println("\nThe first 4-5 digits of student number must be followed by numbers.\n");
                return false;
            }
            else
            {
                return false;
            }
        }
        else if (isNumeric(s.substring(s.length() - 3, s.length())) && 
                Integer.parseInt(s.substring(s.length() - 3, s.length())) < 1)
        {
            if (mainMenuSelection == 2)
            {
                System.out.println("Last 3 digits of student number must be between 1 and 200"); 
                return false; 
            }
            else
            {
                return false;
            }
        }
        else if (isNumeric(s.substring(s.length() - 3, s.length())) && 
                Integer.parseInt(s.substring(s.length() - 3, s.length())) > 200)
        {
            if (mainMenuSelection == 2)
            {
                System.out.println("Last 3 digits of student number must be between 1 and 200"); 
                return false;
            }
            else
            {
                return false;
            }
        }
        else 
        {
            // Once all checks have been passed, it's safe to return true
            return true; 
        }
    }
    
    static String getWorkload(int num)
    {
        return switch (num) 
        {
            case 1 -> "Very Light";
            case 2 -> "Light";
            case 3 -> "Part Time";
            case 4 -> "Part Time";
            case 5 -> "Part Time";
            case 6 -> "Full Time";
            default -> "Full Time";
        };
    }
    
    // Method to check if value is numeric
    static boolean isNumeric(String s)
    {
        try
        {
            Integer.valueOf(s);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }
    
    // Method to get yes or no input
    static boolean yesOrNo(String question)
    {
        String response = "";
        
        // Loop until valid input has been retrieved
        while(true)
        {
            System.out.println(question + "\nY/N");
            response = in.nextLine().trim();
            
            if (response.equalsIgnoreCase("Y") || response.equalsIgnoreCase("N"))
            {
                break;
            }
            else
            {
                System.out.println("Invalid input!");
            }
        }
        
        // Check value of valid input
        if (response.equalsIgnoreCase("Y")){return true;}
        else {return false;}
    }
    
    // Method to get and format file name correctly
    static String askForFileName(String request)
    {
        String response;
        
        // Relay request to user, store response in string
        System.out.println(request);
        response = in.nextLine();
        
        // Check response for '.' or '.txt' file extension and remove
        if (response.contains("."))
        {
            
            return response.replace(
                    response.substring(response.indexOf('.'), response.length()), "");
        }
        else if (response.contains(".txt"))
        {
            return response.replaceAll(".txt", "");
        }
        else
        {
            return response;
        }
    }
}
