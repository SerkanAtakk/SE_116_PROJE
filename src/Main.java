import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        String version = "1.0";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy, HH:mm"));
        System.out.println("FSM DESIGNER " + version + " " + timestamp);

        Controller processor = new Controller();

        if (args.length == 0) {
            processor.starter();
        } else if (args.length == 1) {
            Executor.execute_File(args[0], processor);
        } else {
            System.out.println("Error: Too many arguments. Usage: java -jar fsm.jar [optional: filename]");
        }
    }
}