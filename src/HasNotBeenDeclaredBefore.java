class HasNotBeenDeclaredBefore extends Exception {
    public HasNotBeenDeclaredBefore(String item) {
        super("Warning: " + item + " was not previously declared as a state or symbol.");
    }
}
