public class InvalidInputException extends Exception {
    public InvalidInputException(String reason) {
        super("Warning: Invalid Input - " + reason);
    }