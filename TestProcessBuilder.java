import java.io.*;
import java.util.*;

// Define a Java class named TestProcessBuilder
public class TestProcessBuilder {

    // Inner class to run processes
    public static class ProcessRunner implements Runnable {
        private String command;

        // Constructor to set the command
        public ProcessRunner(String command) {
            this.command = command;
        }

        // Implement the run method required by the Runnable interface
        @Override
        public void run() {
            try {
                // Split the command into a list of arguments
                List<String> input = Arrays.asList(command.split(" "));
                // Create a ProcessBuilder to execute the command
                ProcessBuilder processBuilder = new ProcessBuilder(input);
                BufferedReader bufferReader = null;

                try {
                    // Start a new process
                    Process proc = processBuilder.start();
                    // Get the process's standard output stream
                    InputStream inputStream = proc.getInputStream();
                    InputStreamReader isr = new InputStreamReader(inputStream);
                    // Create a buffered reader to read the output
                    bufferReader = new BufferedReader(isr);

                    String line;
                    // Read and print the output of the process line by line
                    while ((line = bufferReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ioe) {
                    // Handle error by logging the command
                    logErrorCommand(command);
                    // Print an error message and the exception
                    System.err.println("Error");
                    System.err.println(ioe);
                } finally {
                    // Close the buffered reader when done
                    if (bufferReader != null) {
                        bufferReader.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // List to store error commands
    private static List<String> errorLog = new ArrayList<>();

    // Method to log error commands
    private static void logErrorCommand(String command) {
        errorLog.add(command);
    }

    // Method to show the error log
    private static void showErrorLog() {
        // Print a header for the error log
        System.out.println("\n***** Error Log *****");
        // Iterate over the error log and print each error command
        for (String errorCommand : errorLog) {
            System.out.println(errorCommand);
        }
        // Print a footer for the error log
        System.out.println("*********************\n");
    }

    private static void fileDump(String filename) {
        BufferedReader reader = null;

        try {
            // Create a buffered reader to read the file
            reader = new BufferedReader(new FileReader(filename));
            String line;

            // Read and print the content of the file line by line
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            // Handle file not found error by logging the filename and printing an error message
            logErrorCommand("filedump " + filename);
            System.err.println("Error: File not found - " + filename);
        } catch (IOException e) {
            // Handle IO error by logging the filename and printing an error message
            logErrorCommand("filedump " + filename);
            System.err.println("Error reading from file - " + filename);
        } finally {
            // Close the buffered reader when done
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Ignored
                }
            }
        }
    }


    // Method to create and run a process
    public static void createProcess(String command) {
        // Check if the command is empty or null; if so, return without doing anything
        if (command == null || command.isEmpty()) {
            return;
        }

        // Check if the command is "end"; if so, exit the program
        if (command.toLowerCase().equals("end")) {
            System.out.println("\n***** Command Shell Terminated. See you next time. BYE for now. *****\n");
            System.exit(0);
        } else if (command.toLowerCase().equals("showerrlog")) {
            // If the command is "showerrlog," display the error log
            showErrorLog();
        } else if (command.toLowerCase().startsWith("filedump ")) {
            // If the command is "filedump," call the fileDump method with the filename parameter
            String filename = command.substring("filedump ".length());
            fileDump(filename);
        } else {
            // If not an internal command, try running it in a separate thread
            Thread processThread = new Thread(new ProcessRunner(command));
            processThread.start();
        }
    }

    // Main method
    public static void main(String[] args) {
        String commandLine;
        // Create a scanner object to read user input
        Scanner scanner = new Scanner(System.in);
        // Print a welcome message
        System.out.println("\n\n***** Welcome to the Java Command Shell *****");
        System.out.println("If you want to exit the shell, type END and press RETURN.");
        System.out.println("Type 'showerrlog' to display the error log.\n");

        while (true) {
            System.out.print("jsh>"); // Display the shell prompt
            // Read a line of user input
            commandLine = scanner.nextLine();
            // If the user entered an empty line, continue to the next iteration
            if (commandLine.equals("")) {
                continue;
            }
            // Create and run a process based on the user's input
            createProcess(commandLine);
        }
    }
}
