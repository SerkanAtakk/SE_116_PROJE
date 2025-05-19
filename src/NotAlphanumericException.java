class NotAlphanumericException extends Exception {
    public NotAlphanumericException(String item) {
        super("Warning: Input is not alphanumerical - " + item);
    }
}
