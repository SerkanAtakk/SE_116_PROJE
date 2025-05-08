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

        }
    }
}