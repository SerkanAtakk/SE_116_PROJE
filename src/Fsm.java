import java.io.*;
import java.util.*;

public class FSM implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<String> symbols;
    private Set<String> states;
    private String initialState;
    private Set<String> finalStates;
    private Map<String, Map<String, String>> transitions;

    public FSM() {
        symbols = new LinkedHashSet<>();
        states = new LinkedHashSet<>();
        finalStates = new LinkedHashSet<>();
        transitions = new HashMap<>();
        initialState = null;
    }

    public boolean addSymbol(String symbol) throws NotAlphanumericException, AlreadyDeclaredException {
        if (!symbol.matches("[a-zA-Z0-9]"))
            throw new NotAlphanumericException(symbol);
        symbol = symbol.toLowerCase();
        if (!symbols.add(symbol))
            throw new AlreadyDeclaredException(symbol);
        return true;
    }

    public boolean addState(String state) throws NotAlphanumericException, AlreadyDeclaredException {
        if (!state.matches("[a-zA-Z0-9]+"))
            throw new NotAlphanumericException(state);
        state = state.toLowerCase();
        if (!states.add(state))
            throw new AlreadyDeclaredException(state);
        if (initialState == null) {
            initialState = state;
        }
        return true;
    }

    public void setInitialState(String state) throws NotAlphanumericException {
        if (!state.matches("[a-zA-Z0-9]+"))
            throw new NotAlphanumericException(state);
        state = state.toLowerCase();
        if (!states.contains(state)) {
            states.add(state);
            System.out.println(new HasNotBeenDeclaredBefore(state).getMessage());
        }
        initialState = state;
    }

    public void addFinalState(String state) throws NotAlphanumericException, AlreadyDeclaredException {
        if (!state.matches("[a-zA-Z0-9]+"))
            throw new NotAlphanumericException(state);
        state = state.toLowerCase();
        if (!states.contains(state)) {
            states.add(state);
            System.out.println(new HasNotBeenDeclaredBefore(state).getMessage());
        }
        if (!finalStates.add(state))
            throw new AlreadyDeclaredException(state);
    }

    public boolean addTransition(String symbol, String from, String to) throws InvalidInputException {
        symbol = symbol.toLowerCase();
        from = from.toLowerCase();
        to = to.toLowerCase();

        if (!symbols.contains(symbol))
            throw new InvalidInputException("Symbol not declared: " + symbol);
        if (!states.contains(from))
            throw new InvalidInputException("State not declared: " + from);
        if (!states.contains(to))
            throw new InvalidInputException("State not declared: " + to);

        transitions.putIfAbsent(from, new HashMap<>());
        boolean overridden = transitions.get(from).containsKey(symbol);
        transitions.get(from).put(symbol, to);
        return overridden;
    }

    public List<String> execute(String input) throws InvalidInputException {
        List<String> trace = new ArrayList<>();
        if (initialState == null)
            throw new InvalidInputException("Initial state not set");

        String current = initialState;
        trace.add(current);

        for (char ch : input.toCharArray()) {
            String symbol = String.valueOf(ch).toLowerCase();
            if (!symbols.contains(symbol))
                throw new InvalidInputException("Invalid symbol in input: " + symbol);

            if (!transitions.containsKey(current) || !transitions.get(current).containsKey(symbol)) {
                trace.add("NO");
                return trace;
            }

            current = transitions.get(current).get(symbol);
            trace.add(current);
        }

        trace.add(finalStates.contains(current) ? "YES" : "NO");
        return trace;
    }

    public void printTransitions() {
        for (String from : transitions.keySet()) {
            Map<String, String> map = transitions.get(from);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println(entry.getKey() + " " + from + " " + entry.getValue());
            }
        }
    }

    public String getTransitionsAsString() {
        StringBuilder sb = new StringBuilder();
        for (String from : transitions.keySet()) {
            Map<String, String> map = transitions.get(from);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(entry.getKey()).append(" ")
                        .append(from).append(" ")
                        .append(entry.getValue()).append("\n");
            }
        }
        return sb.toString();
    }

    public void clear() {
        symbols.clear();
        states.clear();
        finalStates.clear();
        transitions.clear();
        initialState = null;
    }

    public Set<String> getSymbols() { return symbols; }
    public Set<String> getStates() { return states; }
    public String getInitialState() { return initialState; }
    public Set<String> getFinalStates() { return finalStates; }
    public Map<String, Map<String, String>> getTransitions() { return transitions; }

    public void exportToTextFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("SYMBOLS");
            for (String symbol : symbols) {
                writer.write(" " + symbol);
            }
            writer.write(";\n");

            writer.write("STATES");
            for (String state : states) {
                writer.write(" " + state);
            }
            writer.write(";\n");

            writer.write("INITIAL-STATE " + initialState + ";\n");

            writer.write("FINAL-STATES");
            for (String state : finalStates) {
                writer.write(" " + state);
            }
            writer.write(";\n");

            writer.write("TRANSITIONS ");
            List<String> transitionsStr = new ArrayList<>();
            for (String from : transitions.keySet()) {
                Map<String, String> map = transitions.get(from);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    transitionsStr.add(entry.getKey() + " " + from + " " + entry.getValue());
                }
            }
            writer.write(String.join(", ", transitionsStr) + ";\n");
        }
    }

    public boolean hasTransition(String symbol, String fromState) {
        return transitions.containsKey(fromState) && transitions.get(fromState).containsKey(symbol);
    }
}