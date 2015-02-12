
import java.io.IOException;
import com.leapmotion.leap.*;
import java.sql.*;

class Runner

{
    public static void main(String[] args) 
    {
        
        
        // Create a sample listener and controller
        leapAPI listener = new leapAPI();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }
    
}

