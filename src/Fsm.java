import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;

import static com.sun.tools.jdeprscan.DeprDB.loadFromFile;//hatırlat


public class Fsm {
    private Set<String> symbols = new LinkedHashSet<>();
    private Set<String> states = new LinkedHashSet<>();
    private String initialState = null;
    private Set<String> finalStates = new LinkedHashSet<>();
    private Map<String, Map<String, String>> transitions = new HashMap<>();
    private boolean logging = false;
    private transient PrintWriter logWriter = null;
    public void start(String[] args) {
        System.out.println("FSM DESIGNER <1.0> " + LocalDateTime.now());
        if (args.length > 0) {
            loadFromFile(args[0]);
        }
        interactiveMode();
    }

    private void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        StringBuilder commandBuilder = new StringBuilder();
        while (true) {
            System.out.print("? ");
            String line = scanner.nextLine();

            if (line.trim().startsWith(";")) continue; // comment

            commandBuilder.append(" ").append(line.trim());
            if (line.contains(";")) {
                String command = commandBuilder.toString().split(";")[0].trim();
                handleCommand(command);
                commandBuilder.setLength(0);
            }
        }
    }
    private void Symbols(String[] args) {
        if (args.length == 0) {
            System.out.println("SYMBOLS: " + symbols);
        } else {
            for (String s : args) {
                if (!s.matches("[a-zA-Z0-9]")) {
                    System.out.println("Warning: invalid symbol " + s);
                    continue;
                }
                if (!symbols.add(s.toUpperCase())) {
                    System.out.println("Warning: symbol already declared " + s);
                }
            }
        }
    }

    private void States(String[] args) {
        if (args.length == 0) {
            System.out.println("STATES: " + states);
        } else {
            for (String s : args) {
                if (!s.matches("[a-zA-Z0-9]+")) {
                    System.out.println("Warning: invalid state " + s);
                    continue;
                }
                if (!states.add(s.toUpperCase())) {
                    System.out.println("Warning: state already declared " + s);
                }
            }
        }
    }
}
