public class UnknownCommandException extends Exception {
    public UnknownCommandException(String command) {
        super("Warning: Unknown command - " + command);
    }
}
