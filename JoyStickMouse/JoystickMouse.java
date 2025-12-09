import java.awt.Robot;
import java.awt.MouseInfo;
import java.awt.event.InputEvent;
import com.fazecast.jSerialComm.SerialPort;

public class JoystickMouse {

    public static void main(String[] args) throws Exception {

        // -------------------------
        // SET YOUR ARDUINO COM PORT 
        // -------------------------
        SerialPort port = SerialPort.getCommPort("COM6");  // <<< CHANGE THIS
        port.setBaudRate(9600);

        if (!port.openPort()) {
            System.out.println("ERROR: Could not open serial port!");
            return;
        }

        System.out.println("Connected on " + port.getSystemPortName());

        Robot robot = new Robot();
        byte[] buffer = new byte[1024];
        StringBuilder sb = new StringBuilder();

        while (true) {
            int bytes = port.readBytes(buffer, buffer.length);

            if (bytes > 0) {
                sb.append(new String(buffer, 0, bytes));

                int newlineIndex;
                while ((newlineIndex = sb.indexOf("\n")) >= 0) {
                    String line = sb.substring(0, newlineIndex).trim();
                    sb.delete(0, newlineIndex + 1);

                    // Expecting: 
                    String[] parts = line.split(",");

                    if (parts.length == 4) { // must be 4 for scrollBtn
                        try {
                            int joyX = Integer.parseInt(parts[0]);
                            int joyY = Integer.parseInt(parts[1]);
                            int sw   = Integer.parseInt(parts[2]);
                            int scrollBtn = Integer.parseInt(parts[3]);
// Exchange joyY and joyX to cange movement
                            moveMouse(robot, joyY, joyX, sw, scrollBtn);

                        } catch (Exception e) {
                            // ignore corrupted chunks
                        }
                    }
                }
            }
        }
    }


    static void moveMouse(Robot robot, int joyX, int joyY, int sw, int scrollBtn) {

        int center = 512;  // joystick center
        int deadZone = 50; // ignore small movement
        int speed = 26;     // larger = slower cursor

        int dx = joyX - center;
        int dy = joyY - center;

        if (Math.abs(dx) < deadZone) dx = 0;
        if (Math.abs(dy) < deadZone) dy = 0;

     //invert y
        dy = dy;
        dx = -1 *dx;
        // Get mouse pos
        int mx = MouseInfo.getPointerInfo().getLocation().x;
        int my = MouseInfo.getPointerInfo().getLocation().y;

        // Move mouse only if middle click is NOT pressed
        if (scrollBtn != 1) {
            robot.mouseMove(mx + dx / speed, my + dy / speed);
        }

        // ---------------------------
        //  Click 
        // ---------------------------
        
        
        //BUTTON1_DOWN_MASK → left click

        //BUTTON2_DOWN_MASK → middle click

        //BUTTON3_DOWN_MASK → right click
        if (sw == 0) {
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
        // -------------------------
        // SCROLL (D3) – proportional to joystick Y
        // -------------------------
        if (scrollBtn == 1) { // HIGH = pressed
            if (dy != 0) {
              //  robot.mouseWheel(dy / speed);
            robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
            }
        }
    }
}
