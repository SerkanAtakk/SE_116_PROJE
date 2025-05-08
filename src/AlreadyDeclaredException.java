public class AlreadyDeclaredException extends Exception {
    public AlreadyDeclaredException(String item) {
        super("Warning: " + item.toUpperCase() + " has already been declared.");
    }
}