package be.carmen.dfs;

import be.carmen.dfs.domain.MicrowaveController;
import be.carmen.dfs.domain.TimerUtil;
import be.carmen.dfs.stub.FakeMicrowave;

import java.util.List;

public class Main {
    static void main() {
        boolean run = true;
        FakeMicrowave microwave = new FakeMicrowave();
        MicrowaveController controller = new MicrowaveController(microwave, new TimerUtil(), 60);
        microwave.setEventListener(controller);

        IO.println("MICROWAVE CONTROLLER STARTED");
        IO.println("Use commands to controle this microwave");
        IO.println("* open  - to opens door");
        IO.println("* close - to close door");
        IO.println("* start - press the start button");
        IO.println("* exit  - to end the simulation");

        List<String> commands = List.of("open", "close", "close", "start", "exit");

        do {
            String input = IO.readln().toLowerCase();
            if (commands.contains(input)) {
                switch (input) {
                    case "open":
                        microwave.simulateDoor(true);
                        break;
                    case "close":
                        microwave.simulateDoor(false);
                        break;
                    case "start":
                        microwave.simulateStart();
                        break;
                    case "exit":
                        run = false;
                        break;
                }
            } else {
                IO.println("Invalid input");
            }

        } while (run);
    }
}
