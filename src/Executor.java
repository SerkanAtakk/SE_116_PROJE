import java.io.*;

public class Executor {
    public static void execute_File(String filename, ModuleLayer.Controller processor) {
        if (filename.endsWith(".fs")) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
                FSM loadedFSM = (FSM) in.readObject();
                processor.setFSM(loadedFSM);
                System.out.println("The finite state machine (FSM) has been successfully loaded from the specified binary file: " + filename);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("An error occurred while attempting to load the FSM from the binary file. Detailed message: " + e.getMessage());
            }
        } else if (filename.endsWith(".txt")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                StringBuilder commandBuffer = new StringBuilder();
                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    line = line.trim();

                    if (line.isEmpty() || line.startsWith(";")) continue;

                    int semicolonIndex = line.indexOf(';');

                    if (semicolonIndex != -1) {
                        commandBuffer.append(line, 0, semicolonIndex);
                        String fullCommand = commandBuffer.toString().trim();

                        try {
                            processor.processer(fullCommand);
                        } catch (Exception e) {
                            System.out.println("An error occurred while processing the command on line " + lineNumber + ". Details: " + e.getMessage());
                        }

                        commandBuffer.setLength(0);
                    } else {
                        commandBuffer.append(line).append(" ");
                    }
                }

                if (commandBuffer.length() > 0) {
                    try {
                        processor.processer(commandBuffer.toString().trim());
                    } catch (Exception e) {
                        System.out.println("An error occurred while processing the final command at the end of the file. Details: " + e.getMessage());
                    }
                }

                System.out.println("All commands have been successfully read from the text file and executed sequentially: " + filename);
            } catch (IOException e) {
                System.out.println("An I/O error occurred while attempting to read the specified text file. Please ensure the file exists and is accessible. Details: " + e.getMessage());
            }
        } else {
            System.out.println("Error: The provided file must have either a '.txt' or '.fs' extension. Other file formats are not supported.");
        }
    }
}
