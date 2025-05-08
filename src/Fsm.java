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
        return true;
    }