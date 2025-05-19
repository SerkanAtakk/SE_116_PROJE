import java.io.Serializable;
import java.util.*;

public class FSM implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<String> symbols;
    private Set<String> states;
    private String initialState;
    private Set<String> finalStates;
    private Map<String, Map<String, String>> transitions;

    public FSM() {
        initialState = null;
        states = new LinkedHashSet<>();
        finalStates = new LinkedHashSet<>();
        symbols = new LinkedHashSet<>();
        transitions = new HashMap<>();
    }

    public boolean SYMBOL_OF_ADDED(String symbol) throws NotAlphanumericException, AlreadyDeclaredException {
        if (!symbol.matches("[a-zA-Z0-9]"))
            throw new NotAlphanumericException(symbol);
        symbol = symbol.toLowerCase();
        if (!symbols.add(symbol))
            throw new AlreadyDeclaredException(symbol);
        return true;
    }

    public boolean STATE_OF_ADDED(String state) throws NotAlphanumericException, AlreadyDeclaredException {
        if (!state.matches("[a-zA-Z0-9]+"))
            throw new NotAlphanumericException(state);
        state = state.toLowerCase();
        if (!states.add(state))
            throw new AlreadyDeclaredException(state);
        return true;
    }

    public void setInitial(String state) throws NotAlphanumericException {
        if (!state.matches("[a-zA-Z0-9]+"))
            throw new NotAlphanumericException(state);
        initialState = state.toLowerCase();
        states.add(initialState);
    }

    public void addFinal(String state) throws NotAlphanumericException, AlreadyDeclaredException {
        if (!state.matches("[a-zA-Z0-9]+"))
            throw new NotAlphanumericException(state);
        state = state.toLowerCase();
        states.add(state);
        if (!finalStates.add(state))
            throw new AlreadyDeclaredException(state);
    }

    public boolean TRANSITION_TorF(String symbol, String from, String to) throws InvalidInputException {
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

    public List<String> EXECUTE(String input) throws InvalidInputException {
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

    public void Transitions_printer() {
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

    // Getter methods
    public Set<String> getSymbols() { return symbols; }
    public Set<String> getStates() { return states; }
    public String getInitialState() { return initialState; }
    public Set<String> getFinalStates() { return finalStates; }
    public Map<String, Map<String, String>> getTransitions() { return transitions; }
}
