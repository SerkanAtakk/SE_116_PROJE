import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Controller {
    private FSM fsm;
    private FileWriter logWriter = null;
    private boolean logging = false;

    public Controller() {
        this.fsm = new FSM();
    }

    public void starter() {
        Scanner scanner = new Scanner(System.in);
        StringBuilder commandBuffer = new StringBuilder();
        System.out.print("? ");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.startsWith(";")) {
                System.out.print("? ");
                continue;
            }

            int semicolonIndex = line.indexOf(';');
            if (semicolonIndex != -1) {
                commandBuffer.append(line, 0, semicolonIndex).append(" ");
                processer(commandBuffer.toString().trim());
                commandBuffer.setLength(0);
                System.out.print("? ");
            } else {
                commandBuffer.append(line).append(" ");
            }
        }
    }

    public void processer(String rawCommand) {
        if (logging && logWriter != null) {
            try {
                logWriter.write("> " + rawCommand + "\n");
            } catch (IOException e) {
                System.out.println("Error writing to log: " + e.getMessage());
            }
        }

        String[] parts = rawCommand.trim().split("\\s+");
        if (parts.length == 0) return;

        String command = parts[0].toUpperCase();
        String[] params = Arrays.copyOfRange(parts, 1, parts.length);

        try {
            switch (command) {

                case "COMPILE":
                    COMPLILE(params);
                    break;

                case "LOAD":
                    LOAD(params);
                    break;

                case "CLEAR":
                    fsm.clear();
                    System.out.println("FSM cleared.");
                    break;

                case "EXIT":
                    System.out.println("TERMINATED USER");
                    System.exit(0);
                    break;
                case "STATES":
                    STATEhand(params);
                    break;

                case "INITIAL-STATE":
                    INITIAL(params);
                    break;
                case "LOG":
                    LOGhend(params);
                    break;

                case "SYMBOLS":
                    SYMBOLhand(params);
                    break;
                case "FINAL-STATES":
                    FINAL(params);
                    break;

                case "TRANSITIONS":
                    TRANSITIONhand(rawCommand.substring("TRANSITIONS".length()).trim());
                    break;

                case "PRINT":
                    PRINT(params);
                    break;

                case "EXECUTE":
                    EXECUTE(params);
                    break;

                default:
                    System.out.println("Warning: Unknown command " + command);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void LOGhend(String[] params) {
        try {
            if (params.length == 0) {
                if (logWriter != null) {
                    logWriter.close();
                    logWriter = null;
                    logging = false;
                    System.out.println("STOP LOGGING...");
                } else {
                    System.out.println("LOGGING is not enabled");
                }
            } else {
                if (logWriter != null) logWriter.close();
                logWriter = new FileWriter(params[0]);
                logging = true;
                System.out.println("Logging to " + params[0]);
            }
        } catch (IOException e) {
            System.out.println("Error opening log file: " + e.getMessage());
        }
    }

    private void SYMBOLhand(String[] params) {
        if (params.length == 0) {
            System.out.println("SYMBOLS: " + fsm.getSymbols());
            return;
        }
        for (String s : params) {
            try {
                fsm.SYMBOL_OF_ADDED(s);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void STATEhand(String[] params) {
        if (params.length == 0) {
            System.out.println("STATES: " + fsm.getStates());
            return;
        }
        for (String s : params) {
            try {
                fsm.STATE_OF_ADDED(s);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void INITIAL(String[] params) {
        if (params.length != 1) {
            System.out.println("Warning: invalid initial state declaration");
            return;
        }
        try {
            fsm.setInitial(params[0]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void FINAL(String[] params) {
        for (String s : params) {
            try {
                fsm.addFinal(s);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void TRANSITIONhand(String input) {
        String[] transitions = input.split(",");
        for (String t : transitions) {
            String[] parts = t.trim().split("\\s+");
            if (parts.length != 3) {
                System.out.println("Error: invalid transition format " + t.trim());
                continue;
            }
            try {
                boolean overridden = fsm.TRANSITION_TorF(parts[0], parts[1], parts[2]);
                if (overridden)
                    System.out.println("Warning: transition overridden for <" + parts[0] + "," + parts[1] + ">");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void PRINT(String[] params) {
        if (params.length == 0) {
            System.out.println("SYMBOLS: " + fsm.getSymbols());
            System.out.println("STATES: " + fsm.getStates());
            System.out.println("INITIAL STATE: " + fsm.getInitialState());
            System.out.println("FINAL STATES: " + fsm.getFinalStates());
            System.out.println("TRANSITIONS:");
            fsm.Transitions_printer();
        } else if (params.length == 1) {
            try (FileWriter fw = new FileWriter(params[0])) {
                fw.write("SYMBOLS " + fsm.getSymbols() + "\n");
                fw.write("STATES " + fsm.getStates() + "\n");
                fw.write("INITIAL-STATE " + fsm.getInitialState() + "\n");
                fw.write("FINAL-STATES " + fsm.getFinalStates() + "\n");
                fw.write("TRANSITIONS\n" + fsm.getTransitionsAsString());
                System.out.println("FSM printed to file: " + params[0]);
            } catch (IOException e) {
                System.out.println("Error writing file: " + e.getMessage());
            }
        } else {
            System.out.println("Error: invalid PRINT command");
        }
    }

    private void COMPLILE(String[] params) {
        if (params.length != 1 || !params[0].endsWith(".fs")) {
            System.out.println("Error: filename must end with .fs");
            return;
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(params[0]))) {
            out.writeObject(fsm);
            System.out.println("Compile successful to " + params[0]);
        } catch (IOException e) {
            System.out.println("Error compiling: " + e.getMessage());
        }
    }

    private void LOAD(String[] params) {
        if (params.length != 1) {
            System.out.println("Error: LOAD requires one filename");
            return;
        }
        Executor.execute_File(params[0], this);
    }

    private void EXECUTE(String[] params) {
        if (params.length != 1) {
            System.out.println("Error: EXECUTE requires a string");
            return;
        }
        try {
            List<String> trace = fsm.EXECUTE(params[0]);
            System.out.println(String.join(" ", trace));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setFSM(FSM fsm) {
        this.fsm = fsm;
    }
}
class Executor {
    public static void execute_File(String filename, Controller processor) {
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
