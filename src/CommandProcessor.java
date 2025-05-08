import java.io.*;
import java.util.*;

public class CommandProcessor {
    private FSM fsm;
    private FileWriter logWriter = null;
    private boolean logging = false;

    public CommandProcessor() {
        this.fsm = new FSM();
    }

    public void startInteractive() {
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
                processCommand(commandBuffer.toString().trim());
                commandBuffer.setLength(0);
                System.out.print("? ");
            } else {
                commandBuffer.append(line).append(" ");
            }
        }
    }

    public void processCommand(String rawCommand) {
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
                case "EXIT":
                    System.out.println("TERMINATED BY USER");
                    System.exit(0);
                    break;

                case "LOG":
                    handleLog(params);
                    break;

                case "SYMBOLS":
                    handleSymbols(params);
                    break;

                case "STATES":
                    handleStates(params);
                    break;

                case "INITIAL-STATE":
                    handleInitial(params);
                    break;

                case "FINAL-STATES":
                    handleFinal(params);
                    break;

                case "TRANSITIONS":
                    handleTransitions(rawCommand.substring("TRANSITIONS".length()).trim());
                    break;

                case "PRINT":
                    handlePrint(params);
                    break;

                case "COMPILE":
                    handleCompile(params);
                    break;

                case "LOAD":
                    handleLoad(params);
                    break;

                case "CLEAR":
                    fsm.clear();
                    System.out.println("FSM cleared.");
                    break;

                case "EXECUTE":
                    handleExecute(params);
                    break;

                default:
                    System.out.println("Warning: Unknown command " + command);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleLog(String[] params) {
        try {
            if (params.length == 0) {
                if (logWriter != null) {
                    logWriter.close();
                    logWriter = null;
                    logging = false;
                    System.out.println("STOPPED LOGGING");
                } else {
                    System.out.println("LOGGING was not enabled");
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

    private void handleSymbols(String[] params) {
        if (params.length == 0) {
            System.out.println("SYMBOLS: " + fsm.getSymbols());
            return;
        }
        for (String s : params) {
            try {
                fsm.addSymbol(s);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void handleStates(String[] params) {
        if (params.length == 0) {
            System.out.println("STATES: " + fsm.getStates());
            return;
        }
        for (String s : params) {
            try {
                fsm.addState(s);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void handleInitial(String[] params) {
        if (params.length != 1) {
            System.out.println("Warning: invalid initial state declaration");
            return;
        }
        try {
            fsm.setInitialState(params[0]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleFinal(String[] params) {
        for (String s : params) {
            try {
                fsm.addFinalState(s);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void handleTransitions(String input) {
        String[] transitions = input.split(",");
        for (String t : transitions) {
            String[] parts = t.trim().split("\\s+");
            if (parts.length != 3) {
                System.out.println("Error: invalid transition format " + t.trim());
                continue;
            }
            try {
                boolean overridden = fsm.addTransition(parts[0], parts[1], parts[2]);
                if (overridden)
                    System.out.println("Warning: transition overridden for <" + parts[0] + "," + parts[1] + ">");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void handlePrint(String[] params) {
        if (params.length == 0) {
            System.out.println("SYMBOLS: " + fsm.getSymbols());
            System.out.println("STATES: " + fsm.getStates());
            System.out.println("INITIAL STATE: " + fsm.getInitialState());
            System.out.println("FINAL STATES: " + fsm.getFinalStates());
            System.out.println("TRANSITIONS:");
            fsm.printTransitions();
        } else if (params.length == 1) {
            try {
                fsm.exportToTextFile(params[0]);
                System.out.println("FSM printed to file: " + params[0]);
            } catch (IOException e) {
                System.out.println("Error writing file: " + e.getMessage());
            }
        } else {
            System.out.println("Error: invalid PRINT command");
        }
    }

    private void handleCompile(String[] params) {
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

    private void handleLoad(String[] params) {
        if (params.length != 1) {
            System.out.println("Error: LOAD requires one filename");
            return;
        }
        CommandFileExecutor.executeFile(params[0], this);
    }

    private void handleExecute(String[] params) {
        if (params.length != 1) {
            System.out.println("Error: EXECUTE requires a string");
            return;
        }
        try {
            List<String> trace = fsm.execute(params[0]);
            System.out.println(String.join(" ", trace));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setFSM(FSM fsm) {
        this.fsm = fsm;
    }
}